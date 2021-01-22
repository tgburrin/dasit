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

CREATE UNIQUE INDEX IF NOT EXISTS idx_uq1_datasets_name_owner on dasit.datasets(name, owner_group);

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
		NEW.publish_range := '['||NEW.publish_start_dt||','||NEW.publish_end_dt||')';

		IF TG_OP = 'INSERT' THEN
			/*
			SELECT
				COUNT(*) INTO rows
			FROM
				dasit.datasets_published dp
			WHERE
				dp.dataset_id = NEW.dataset_id AND
				dp.
			*/
		END IF;

		RETURN NEW;
	ELSIF TG_WHEN = 'AFTER' THEN
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
