--liquibase formatted sql

--changeset ekros:003-add-search-vector-columns

-- 1. Add the vector column to the main document table
ALTER TABLE document
    ADD COLUMN search_vector tsvector;

-- 2. Create GIN index for fast searching
CREATE INDEX idx_document_search_vector ON document USING GIN(search_vector);

--rollback DROP INDEX IF EXISTS idx_document_search_vector;
--rollback ALTER TABLE document DROP COLUMN IF EXISTS search_vector;

--changeset ekros:003-add-search-vector-function splitStatements:false

-- 3. Create the function that calculates the vector
-- This function joins the document and content tables to build the full text index
CREATE
OR REPLACE FUNCTION doc_search_vector_update() RETURNS TRIGGER AS $$
BEGIN
    IF
TG_TABLE_NAME = 'document' THEN
        -- If the document metadata changed, update its own vector
UPDATE document d
SET search_vector =
        setweight(to_tsvector('english'::regconfig, COALESCE(d.name, '')), 'A') ||
        setweight(to_tsvector('english'::regconfig, COALESCE(d.summary, '')), 'B') ||
        setweight(to_tsvector('english'::regconfig, COALESCE(c.content, '')), 'C') FROM document_content c
WHERE d.content_id = c.id AND d.id = NEW.id;

ELSIF
TG_TABLE_NAME = 'document_content' THEN
        -- If the content changed, find ALL documents pointing to this content and update them
UPDATE document d
SET search_vector =
        setweight(to_tsvector('english'::regconfig, COALESCE(d.name, '')), 'A') ||
        setweight(to_tsvector('english'::regconfig, COALESCE(d.summary, '')), 'B') ||
        setweight(to_tsvector('english'::regconfig, COALESCE(NEW.content, '')), 'C')
WHERE d.content_id = NEW.id;
END IF;
RETURN NULL;
END;
$$
LANGUAGE plpgsql;

--rollback DROP FUNCTION IF EXISTS doc_search_vector_update;

--changeset ekros:003-add-search-vector-triggers

-- 4. Trigger for the 'document' table
-- Fires when name, summary, or the content link changes
CREATE TRIGGER trg_document_search_update
    AFTER INSERT OR
UPDATE OF name, summary, content_id
ON document
    FOR EACH ROW
    EXECUTE FUNCTION doc_search_vector_update();

-- 5. Trigger for the 'document_content' table
-- Fires when the actual text content changes
CREATE TRIGGER trg_document_content_search_update
    AFTER UPDATE OF content
    ON document_content
    FOR EACH ROW
    EXECUTE FUNCTION doc_search_vector_update();

-- 6. Backfill existing data
-- This ensures current records get a vector immediately
UPDATE document d
SET search_vector =
        setweight(to_tsvector('english'::regconfig, COALESCE(d.name, '')), 'A') ||
        setweight(to_tsvector('english'::regconfig, COALESCE(d.summary, '')), 'B') ||
        setweight(to_tsvector('english'::regconfig, COALESCE(c.content, '')), 'C') FROM document_content c
WHERE d.content_id = c.id;

--rollback DROP TRIGGER IF EXISTS trg_document_content_search_update ON document_content;
--rollback DROP TRIGGER IF EXISTS trg_document_search_update ON document;
