--liquibase formatted sql

--changeset ekros:002-create-document-schema

CREATE TYPE document_kind AS ENUM ('DOCUMENT', 'MEMORY');
CREATE TYPE document_scope AS ENUM ('GLOBAL', 'PROJECT', 'AGENT');
CREATE TYPE author_type AS ENUM ('HUMAN', 'AGENT');

CREATE TABLE document
(
    id          UUID PRIMARY KEY        DEFAULT gen_random_uuid(),
    kind        document_kind  NOT NULL DEFAULT 'DOCUMENT',
    scope       document_scope NOT NULL DEFAULT 'PROJECT',
    author      author_type    NOT NULL DEFAULT 'HUMAN',
    author_name varchar(250),
    project_key varchar(250),
    name        varchar(250),
    summary     varchar(1024),
    content     text,
    created_at  timestamp      NOT NULL DEFAULT now(),
    updated_at  timestamp      NOT NULL DEFAULT now()
);

--rollback DROP TABLE IF EXISTS document;
--rollback DROP TYPE IF EXISTS document_kind;
--rollback DROP TYPE IF EXISTS document_scope;
--rollback DROP TYPE IF EXISTS author_type;

