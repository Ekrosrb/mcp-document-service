--liquibase formatted sql

--changeset ekros:006-add-document-relations-target-index

CREATE INDEX idx_document_relations_related_document_id ON document_relations (related_document_id);

--rollback DROP INDEX IF EXISTS idx_document_relations_related_document_id;
