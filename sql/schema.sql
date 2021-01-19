CREATE SCHEMA IF NOT EXISTS dasit;
CREATE TABLE IF NOT EXISTS dasit.groups (
	id bigserial NOT NULL PRIMARY KEY,
	name text NOT NULL,
	email text NOT NULL,
	status text NOT NULL,
	unique(name)
);

CREATE TABLE IF NOT EXISTS dasit.datasets (
	id bigserial NOT NULL PRIMARY KEY,
	name text NOT NULL,
	owner_group bigint NOT NULL REFERENCES dasit.groups(id),
	status text NOT NULL,
	unique(name, owner_group)
);