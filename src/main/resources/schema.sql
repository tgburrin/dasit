CREATE SCHEMA IF NOT EXISTS dasit;
CREATE TABLE IF NOT EXISTS dasit.groups (
	id bigserial NOT NULL PRIMARY KEY,
	created timestamptz not null,
	updated timestamptz not null,
	name text NOT NULL,
	email text NOT NULL,
	status text NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS idx_uq1_groups_name on dasit.groups(name);

CREATE TABLE IF NOT EXISTS dasit.datasets (
	id bigserial NOT NULL PRIMARY KEY,
	created timestamptz not null,
	updated timestamptz not null,
	name text NOT NULL,
	owner_group bigint NOT NULL REFERENCES dasit.groups(id),
	status text NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_uq1_datasets_name on dasit.datasets(name);

CREATE TABLE IF NOT EXISTS dasit.datasets_published (
	dataset_id bigint not null REFERENCES dasit.datasets(id),
	created timestamptz not null,
	publish_start_dt timestamptz not null,
	publish_end_dt timestamptz not null,
	publish_range tstzrange not null,
	primary key (dataset_id, publish_start_dt),
	CHECK (publish_start_dt < publish_end_dt)
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_uq1_datasets_published_id_end_dt on dasit.datasets_published(dataset_id, publish_end_dt);

CREATE OR REPLACE FUNCTION dasit.set_timestatmps_trg()
RETURNS TRIGGER
AS $$
BEGIN
	IF TG_WHEN = 'BEFORE' THEN

		IF TG_OP = 'INSERT' THEN
			perform 1
			from information_schema.columns
			where table_schema = TG_TABLE_SCHEMA and
			table_name = TG_TABLE_NAME and
			column_name = 'created';
			IF FOUND THEN
				NEW.created := clock_timestamp();
			END IF;

			perform 1
			from information_schema.columns
			where table_schema = TG_TABLE_SCHEMA and
			table_name = TG_TABLE_NAME and
			column_name = 'updated';

			IF FOUND THEN
				NEW.updated := clock_timestamp();
			END IF;
		END IF;

		IF TG_OP = 'UPDATE' THEN
			perform 1
			from information_schema.columns
			where table_schema = TG_TABLE_SCHEMA and
			table_name = TG_TABLE_NAME and
			column_name = 'updated';

			IF FOUND THEN
				NEW.updated := clock_timestamp();
			END IF;
		END IF;
	
		IF TG_OP = 'DELETE' THEN
			RETURN OLD;
		ELSE
			RETURN NEW;
		END IF;
	ELSIF TG_WHEN = 'AFTER' THEN
		RETURN NULL;
	ELSE
		RAISE EXCEPTION 'Unhandled trigger lifecycle % in %.% on %.%', TG_OP, TG_WHEN, TG_NAME, TG_TABLE_SCHEMA, TG_TABLE_NAME;
	END IF;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS FF_groups_timestamps on dasit.groups;
CREATE TRIGGER FF_groups_timestamps
BEFORE INSERT OR UPDATE OR DELETE
ON dasit.groups
FOR EACH ROW
EXECUTE PROCEDURE dasit.set_timestatmps_trg();

DROP TRIGGER IF EXISTS FF_datasets_timestamps on dasit.datasets;
CREATE TRIGGER FF_datasets_timestamps
BEFORE INSERT OR UPDATE OR DELETE
ON dasit.datasets
FOR EACH ROW
EXECUTE PROCEDURE dasit.set_timestatmps_trg();

DROP TRIGGER IF EXISTS FF_datasets_published_timestamps on dasit.datasets_published;
CREATE TRIGGER FF_datasets_published_timestamps
BEFORE INSERT OR UPDATE OR DELETE
ON dasit.datasets_published
FOR EACH ROW
EXECUTE PROCEDURE dasit.set_timestatmps_trg();

CREATE OR REPLACE FUNCTION dasit.merge_dataset_published_trg()
RETURNS TRIGGER
AS $$
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

		IF TG_OP = 'DELETE' THEN
			RETURN OLD;
		ELSE
			RETURN NEW;
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
CREATE TRIGGER MN_merge_dataset_published
AFTER INSERT OR UPDATE OR DELETE
ON dasit.datasets_published
FOR EACH ROW
EXECUTE PROCEDURE dasit.merge_dataset_published_trg();

CREATE OR REPLACE FUNCTION dasit.check_published_window(dsid bigint, sd timestamptz, ed timestamptz)
RETURNS SETOF dasit.datasets_published
AS $$
DECLARE
	range tstzrange;
BEGIN
	range := '['||sd||','||ed||')';

	sd := null;
	ed := null;

	return query
	select
		*
	from
		dasit.datasets_published dp
	where
		dp.dataset_id = dsid
	and dp.publish_range @> range;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION dasit.add_published_window(dsid bigint, sd timestamptz, ed timestamptz)
RETURNS SETOF dasit.datasets_published
AS $$
DECLARE
	cnt bigint;
	start_rec dasit.datasets_published;
	end_rec   dasit.datasets_published;
	range tstzrange;
BEGIN
	-- Noop if the window already exists
	select * into start_rec from dasit.check_published_window(dsid, sd, ed);
	IF FOUND THEN
		return next start_rec;
		return;
	END IF;

	start_rec := null;

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
		-- sd -> start_rec.publish_end_dt ->  would be part of a window being re-published near the start
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
		-- end_rec.publish_start_dt -> ed would be a part of a window being re-published near the end
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
	-- records matching published_range <@ range would be those being completely re-publised 
	delete from dasit.datasets_published dp
	where dp.dataset_id = dsid and dp.publish_range && range;

	return query
	with ins as (
		insert into dasit.datasets_published (dataset_id, publish_start_dt, publish_end_dt)
		values (dsid, sd, ed) returning *
	) select * from ins;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION dasit.remove_published_window(dsid bigint, sd timestamptz, ed timestamptz)
RETURNS SETOF dasit.datasets_published
AS $$
DECLARE
	rec dasit.datasets_published;
	range tstzrange;
BEGIN
	range := '['||sd||','||ed||')';
	-- RAISE NOTICE 'Range is %', range;

	-- Noop if the window already does not exist
	perform 1 from dasit.datasets_published dp where dp.publish_range && range;
	IF NOT FOUND THEN
		return query
		select *
		from dasit.datasets_published dp
		where dp.dataset_id = dsid and dp.publish_range -|- range
		order by dp.publish_start_dt;
		RETURN;
	END IF;

	-- Remove records entirely encompassed by this range
	delete from dasit.datasets_published dp
	where dp.dataset_id = dsid and dp.publish_range <@ range;

    select * into rec from dasit.datasets_published dp
    where dp.dataset_id = dsid and dp.publish_range @> range;
	IF FOUND THEN
		-- This is a window split

		UPDATE dasit.datasets_published dp set
			publish_end_dt = sd
		where
			dp.dataset_id = dsid
		and dp.publish_start_dt = rec.publish_start_dt;

		insert into dasit.datasets_published (dataset_id, publish_start_dt, publish_end_dt)
		values (dsid, ed, rec.publish_end_dt);
	ELSE
		-- These are one or more intersecting windows on the upper and lower bounds

		FOR rec in select * from dasit.datasets_published dp where dp.publish_range && range order by dp.publish_start_dt
		LOOP
			IF sd >= rec.publish_start_dt and sd < rec.publish_end_dt THEN
				-- RAISE NOTICE 'Amending % for upper bound %', rec, sd;
				UPDATE dasit.datasets_published dp set
					publish_end_dt = sd
				where
					dp.dataset_id = dsid
				and dp.publish_start_dt = rec.publish_start_dt;
			ELSIF ed > rec.publish_start_dt and ed <= rec.publish_end_dt THEN
				-- RAISE NOTICE 'Amending % for lower bound %', rec, ed;
				UPDATE dasit.datasets_published dp set
					publish_start_dt = ed
				where
					dp.dataset_id = dsid
				and dp.publish_end_dt = rec.publish_end_dt;
			END IF;
		END LOOP;
	END IF;

	return query
	select *
	from dasit.datasets_published dp
	where dp.dataset_id = dsid and dp.publish_range -|- range
	order by dp.publish_start_dt;
END;
$$ LANGUAGE plpgsql;
