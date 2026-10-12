Feature: Get document

  Scenario: An existing document is returned
    Given the table "document" contains the rows from "params/document.json"
    When I send a GET request to "/api/v1/document/{{documentId}}"
    Then the response status is 200
    And the response body matches "expected/responses/document.json"

  Scenario: An unknown document is reported as not found
    When I send a GET request to "/api/v1/document/{{unknownId}}"
    Then the response status is 404
    And the response body matches "expected/responses/document_not_found.json"

  Scenario: A malformed document id is rejected
    When I send a GET request to "/api/v1/document/not-a-uuid"
    Then the response status is 400
    And the response body matches "expected/responses/invalid_document_id.json"
