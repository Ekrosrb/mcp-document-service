Feature: Get document

  Background:
    Given the table "project" contains the rows from "params/projects.json"
    And the table "document_content" contains the rows from "params/document_contents.json"
    And the table "document" contains the rows from "params/documents.json"

  Scenario: An existing document is returned without its content by default
    When I send a GET request to "/api/v1/document/{{documentId}}"
    Then the response status is 200
    And the response body matches "expected/responses/document_without_content.json"

  Scenario: An existing document is returned with its content on request
    When I send a GET request to "/api/v1/document/{{documentId}}?includeContent=true"
    Then the response status is 200
    And the response body matches "expected/responses/document_with_content.json"

  Scenario: An unknown document is reported as not found
    When I send a GET request to "/api/v1/document/{{unknownId}}"
    Then the response status is 404
    And the response body matches "expected/responses/document_not_found.json"

  Scenario: A malformed document id is rejected
    When I send a GET request to "/api/v1/document/not-a-uuid"
    Then the response status is 400
    And the response body matches "expected/responses/invalid_document_id.json"
