Feature: Remove document

  Scenario: Only the requested document is removed
    Given the table "document" contains the rows from "params/documents.json"
    When I send a DELETE request to "/api/v1/document/{{documentId}}"
    Then the response status is 200
    And the response body is empty
    And the rows of table "document" where "author_name" is "{{authorName}}" match "expected/database/remaining_document.json"

  Scenario: A malformed document id is rejected and nothing is removed
    Given the table "document" contains the rows from "params/documents.json"
    When I send a DELETE request to "/api/v1/document/not-a-uuid"
    Then the response status is 400
    And the response body matches "expected/responses/invalid_document_id.json"
    And the rows of table "document" where "author_name" is "{{authorName}}" match "expected/database/all_documents.json"
