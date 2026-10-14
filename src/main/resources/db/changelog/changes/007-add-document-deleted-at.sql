--liquibase formatted sql

--changeset ekros:007-add-document-deleted-at

ALTER TABLE document
    ADD COLUMN deleted_at timestamp;

--rollback ALTER TABLE document DROP COLUMN IF EXISTS deleted_at;
