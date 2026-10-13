--liquibase formatted sql

--changeset ekros:002-create-document-schema

CREATE TYPE document_kind AS ENUM ('DOCUMENT', 'MEMORY');
CREATE TYPE document_scope AS ENUM ('GLOBAL', 'PROJECT', 'AGENT');
CREATE TYPE author_type AS ENUM ('HUMAN', 'AGENT');
CREATE TYPE document_type AS ENUM ('TEXT', 'JSON', 'HLD', 'XML', 'MARKDOWN', 'TOON', 'CUSTOM');
CREATE TYPE document_relation_type AS ENUM ('DEPENDS_ON', 'SUPERSEDES', 'EXTENDS', 'PART_OF', 'DERIVED_FROM', 'RELATED_TO');

CREATE TABLE project
(
    id          UUID PRIMARY KEY             DEFAULT gen_random_uuid(),
    project_key VARCHAR(250) UNIQUE NOT NULL,
    created_at  timestamp           NOT NULL DEFAULT now()
);

CREATE TABLE document_content
(
    id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    content text
);

CREATE TABLE document
(
    id               UUID PRIMARY KEY        DEFAULT gen_random_uuid(),
    kind             document_kind  NOT NULL DEFAULT 'DOCUMENT',
    scope            document_scope NOT NULL DEFAULT 'PROJECT',
    type             document_type  NOT NULL DEFAULT 'TEXT',
    custom_extension varchar(50)             DEFAULT NULL,
    author           author_type    NOT NULL DEFAULT 'HUMAN',
    author_name      varchar(250),
    project_key      varchar(250),
    name             varchar(250),
    summary          varchar(1024),
    content_id       UUID           NOT NULL,
    created_at       timestamp      NOT NULL DEFAULT now(),
    updated_at       timestamp      NOT NULL DEFAULT now(),

    CONSTRAINT fk_document_project_key
        FOREIGN KEY (project_key)
            REFERENCES project (project_key)
            ON DELETE CASCADE,

    CONSTRAINT fk_document_content_id FOREIGN KEY (content_id)
        REFERENCES document_content (id)
        ON DELETE RESTRICT
);

CREATE TABLE document_relations
(
    document_id         UUID,
    related_document_id UUID,
    type                document_relation_type NOT NULL DEFAULT 'RELATED_TO',

    PRIMARY KEY (document_id, related_document_id),

    CONSTRAINT fk_relation_source
        FOREIGN KEY (document_id) REFERENCES document (id) ON DELETE CASCADE,

    CONSTRAINT fk_relation_target
        FOREIGN KEY (related_document_id) REFERENCES document (id) ON DELETE CASCADE,

    CONSTRAINT chk_no_self_relation
        CHECK (document_id <> related_document_id)
);

CREATE INDEX idx_document_project_key ON document (project_key);
CREATE INDEX idx_document_scope ON document (scope);

--rollback DROP INDEX IF EXISTS idx_document_project_key;
--rollback DROP INDEX IF EXISTS idx_document_scope;
--rollback DROP TABLE IF EXISTS document_relations;
--rollback DROP TABLE IF EXISTS document;
--rollback DROP TABLE IF EXISTS document_content;
--rollback DROP TABLE IF EXISTS project;
--rollback DROP TYPE IF EXISTS document_kind;
--rollback DROP TYPE IF EXISTS document_scope;
--rollback DROP TYPE IF EXISTS author_type;
--rollback DROP TYPE IF EXISTS document_type;
--rollback DROP TYPE IF EXISTS document_relation_type;

