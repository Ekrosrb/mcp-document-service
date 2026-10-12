--liquibase formatted sql

--changeset ekros:001-initialize-pgvector-database
--comment: Enable the extensions required by the Spring AI pgvector store
CREATE
EXTENSION IF NOT EXISTS vector;
CREATE
EXTENSION IF NOT EXISTS hstore;
CREATE
EXTENSION IF NOT EXISTS "uuid-ossp";

--rollback DROP EXTENSION IF EXISTS "uuid-ossp";
--rollback DROP EXTENSION IF EXISTS hstore;
--rollback DROP EXTENSION IF EXISTS vector;
