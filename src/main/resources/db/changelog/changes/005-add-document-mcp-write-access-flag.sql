--liquibase formatted sql

--changeset ekros:005-add-document-mcp-write-access-flag

ALTER TABLE document
    ADD COLUMN allow_mcp_write BOOL NOT NULL DEFAULT FALSE;

--rollback ALTER TABLE document DROP COLUMN IF EXISTS allow_mcp_write;
