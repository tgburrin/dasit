CREATE SCHEMA IF NOT EXISTS dasit;
CREATE TABLE IF NOT EXISTS dasit.groups (
	id bigserial NOT NULL PRIMARY KEY,
	name text NOT NULL,
	email text NOT NULL,
	status text NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS idx_uq1_groups_name on dasit.groups(name);

CREATE TABLE IF NOT EXISTS dasit.datasets (
	id bigserial NOT NULL PRIMARY KEY,
	name text NOT NULL,
	owner_group bigint NOT NULL REFERENCES dasit.groups(id),
	status text NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_uq1_datasets_name on dasit.datasets(name);

CREATE TABLE IF NOT EXISTS dasit.datasets_published (
	dataset_id bigint not null REFERENCES dasit.datasets(id),
	publish_start_dt timestamptz not null,
	publish_end_dt timestamptz not null,
	publish_range tstzrange not null,
	primary key (dataset_id, publish_start_dt),
	CHECK (publish_start_dt < publish_end_dt)
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_uq1_datasets_published_id_end_dt on dasit.datasets_published(dataset_id, publish_end_dt);

CREATE OR REPLACE FUNCTION dasit.merge_dataset_published_trg() RETURNS TRIGGER AS $$
DECLARE
	rows bigint;
BEGIN
	IF TG_WHEN = 'BEFORE' THEN
		IF NEW.dataset_id is not null THEN
			/* Ensure that publish_range is _always_ consistent with the rest of the record */
			NEW.publish_range := '['||NEW.publish_start_dt||','||NEW.publish_end_dt||')';
		END IF;

		IF TG_OP = 'INSERT' THEN
			perform
				1
			from
				dasit.datasets_published dp
			where
				dp.dataset_id = NEW.dataset_id AND
				dp.publish_range && NEW.publish_range;

			IF FOUND THEN
				RAISE EXCEPTION 'Overlapping record found while attempting insert of %', NEW;
			END IF;

		ELSIF TG_OP = 'UPDATE' THEN
			/* nothing */
		ELSIF TG_OP = 'DELETE' THEN
			/* nothing */
		ELSE
			RAISE EXCEPTION 'Unhandled trigger lifecycle % in %.% on %.%', TG_OP, TG_WHEN, TG_NAME, TG_TABLE_SCHEMA, TG_TABLE_NAME;
		END IF;

		/*
		RAISE NOTICE 'NEW %', NEW;
		RAISE NOTICE 'OLD %', OLD;
		*/

		IF NEW.dataset_id is not null THEN
			RETURN NEW;
		ELSE
			RETURN OLD;
		END IF;
	ELSIF TG_WHEN = 'AFTER' THEN
		RETURN NULL;
	ELSE
		RAISE EXCEPTION 'Unhandled trigger lifecycle % in %.% on %.%', TG_OP, TG_WHEN, TG_NAME, TG_TABLE_SCHEMA, TG_TABLE_NAME;
	END IF;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS MM_merge_dataset_published on dasit.datasets_published;
CREATE TRIGGER MM_merge_dataset_published
BEFORE INSERT OR UPDATE OR DELETE
ON dasit.datasets_published
FOR EACH ROW
EXECUTE PROCEDURE dasit.merge_dataset_published_trg();

DROP TRIGGER IF EXISTS MN_merge_dataset_published on dasit.datasets_published;
CREATE TRIGGER MM_merge_dataset_published
AFTER INSERT OR UPDATE OR DELETE
ON dasit.datasets_published
FOR EACH ROW
EXECUTE PROCEDURE dasit.merge_dataset_published_trg();

CREATE OR REPLACE FUNCTION dasit.add_published_window(dsid bigint, sd timestamptz, ed timestamptz) RETURNS void AS $$
DECLARE
	cnt bigint;
	start_rec dasit.datasets_published;
	end_rec   dasit.datasets_published;
	range tstzrange;
BEGIN
	/* Intersects on the lower bound */
	select
		*
	into start_rec
	from
		dasit.datasets_published dp
	WHERE
		dp.dataset_id = dsid AND
		dp.publish_start_dt <= sd AND
		dp.publish_end_dt > sd
	;
	IF FOUND THEN
		sd := start_rec.publish_start_dt;
	END IF;

	/* Our starting window is now the ending of another window */
	select
		*
	into start_rec
	from
		dasit.datasets_published dp
	WHERE
		dp.dataset_id = dsid AND
		dp.publish_end_dt = sd
	;
	IF FOUND THEN
		sd := start_rec.publish_start_dt;
	END IF;

	/* Intersects on the upper bound */
	select
		*
	into end_rec
	from
		dasit.datasets_published dp
	WHERE
		dp.dataset_id = dsid AND
		dp.publish_start_dt < ed AND
		dp.publish_end_dt >= ed
	;
	IF FOUND THEN
		ed := end_rec.publish_end_dt;
	END IF;

	/* Our ending window is now the start of another window */
	select
		*
	into end_rec
	from
		dasit.datasets_published dp
	WHERE
		dp.dataset_id = dsid AND
		dp.publish_start_dt = ed
	;
	IF FOUND THEN
		ed := end_rec.publish_end_dt;
	END IF;

	range := '['||sd||','||ed||')';

	/* This could be done better to identify a delete and an update for existing ranges */
	/* an update with identical details would be a noop */
	delete from dasit.datasets_published dp
	where dp.dataset_id = dsid and dp.publish_range && range;

	insert into dasit.datasets_published (dataset_id, publish_start_dt, publish_end_dt)
	values (dsid, sd, ed);
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION dasit.check_published_window(dsid bigint, sd timestamptz, ed timestamptz) RETURNS bool AS $$
DECLARE
	cnt bigint;
	range tstzrange;
	rv bool;
BEGIN
	rv := 'f';

	range := '['||sd||','||ed||')';
	perform
		1
	from
		dasit.datasets_published dp
	where
		dp.dataset_id = dsid
	and dp.publish_range @> range;
	IF FOUND THEN
		rv := 't';
	END IF;

	return rv;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION dasit.remove_published_window(dsid bigint, sd timestamptz, ed timestamptz) RETURNS void AS $$
DECLARE
	cnt bigint;
	start_rec dasit.datasets_published;
	end_rec   dasit.datasets_published;
	range tstzrange;
BEGIN
	range := '['||sd||','||ed||')';

	delete from dasit.datasets_published dp
	where dp.dataset_id = dsid and dp.publish_range <@ range;

	/* Intersects on the lower bound */
	select
		*
	into start_rec
	from
		dasit.datasets_published dp
	WHERE
		dp.dataset_id = dsid AND
		dp.publish_start_dt <= sd AND
		dp.publish_end_dt > sd
	;

	/* Intersects on the upper bound */
	select
		*
	into end_rec
	from
		dasit.datasets_published dp
	WHERE
		dp.dataset_id = dsid AND
		dp.publish_start_dt < ed AND
		dp.publish_end_dt >= ed
	;

	IF start_rec is not null and start_rec.publish_range @> range THEN
		/* start_rec and end_rec should be the same */
		update dasit.datasets_published set
			publish_end_dt = sd
		where dataset_id = dsid AND publish_start_dt = start_rec.publish_start_dt;

		insert into dasit.datasets_published (dataset_id, publish_start_dt, publish_end_dt)
		values (dsid, ed, start_rec.publish_end_dt);
	ELSE
		IF start_rec is not null THEN
			update dasit.datasets_published set
				publish_end_dt = sd
			where dataset_id = dsid AND publish_start_dt = start_rec.publish_start_dt;
		END IF;

		IF end_rec is not null THEN
			update dasit.datasets_published set
				publish_start_dt = ed
			where dataset_id = dsid AND publish_start_dt = end_rec.publish_start_dt;
		END IF;
	END IF;
END;
$$ LANGUAGE plpgsql;
