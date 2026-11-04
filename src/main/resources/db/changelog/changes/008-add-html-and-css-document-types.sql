--liquibase formatted sql

--changeset ekros:008-add-html-and-css-document-types

ALTER TYPE document_type ADD VALUE 'HTML' BEFORE 'CUSTOM';
ALTER TYPE document_type ADD VALUE 'CSS' BEFORE 'CUSTOM';

-- Postgres cannot drop an enum value, so the rollback rebuilds the type without the two new ones.
-- It fails while any row still holds HTML or CSS, which is the point.
--rollback ALTER TABLE document ALTER COLUMN type DROP DEFAULT;
--rollback ALTER TYPE document_type RENAME TO document_type_old;
--rollback CREATE TYPE document_type AS ENUM ('TEXT', 'JSON', 'XML', 'MARKDOWN', 'TOON', 'CUSTOM');
--rollback ALTER TABLE document ALTER COLUMN type TYPE document_type USING type::text::document_type;
--rollback ALTER TABLE document ALTER COLUMN type SET DEFAULT 'TEXT';
--rollback DROP TYPE document_type_old;
