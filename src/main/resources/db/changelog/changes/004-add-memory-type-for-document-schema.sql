--liquibase formatted sql

--changeset ekros:004-add-memory-type-for-document-schema

CREATE TYPE memory_type AS ENUM ('DECISION', 'RULE', 'FACT', 'OBSERVATION', 'PROCEDURE', 'PITFALL', 'STATUS');

ALTER TABLE document
    ADD COLUMN memory_type memory_type;

--rollback ALTER TABLE document DROP COLUMN IF EXISTS memory_type;
--rollback DROP TYPE IF EXISTS memory_type;
