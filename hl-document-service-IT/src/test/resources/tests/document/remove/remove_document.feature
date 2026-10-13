Feature: Remove document

  Background:
    Given the table "project" contains the rows from "params/projects.json"
    And the table "document_content" contains the rows from "params/document_contents.json"
    And the table "document" contains the rows from "params/documents.json"

  Scenario: Only the requested document and its content are removed
    When I send a DELETE request to "/api/v1/document/${documentId}"
    Then the response status is 200
    And the response body is empty
    And the rows of table "document" where "author_name" is "${authorName}" match "expected/database/remaining_document.json"
    And the rows of table "document_content" where "id" is "${documentContentId}" match "expected/database/no_document_content.json"
    And the rows of table "document_content" where "id" is "${memoryContentId}" match "expected/database/remaining_document_content.json"

  Scenario: A malformed document id is rejected and nothing is removed
    When I send a DELETE request to "/api/v1/document/not-a-number"
    Then the response status is 400
    And the response body matches "expected/responses/invalid_document_id.json"
    And the rows of table "document" where "author_name" is "${authorName}" match "expected/database/all_documents.json"
    And the rows of table "document_content" where "id" is "${documentContentId}" match "expected/database/document_content.json"
