create user dasit with login password 'passw0rd';
create database dasit_prod with owner dasit;

\c dasit_prod dasit
\i /tmp/schema.sql
