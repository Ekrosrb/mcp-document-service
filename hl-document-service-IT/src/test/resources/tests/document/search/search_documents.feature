Feature: Search documents

  Background:
    Given the table "document" contains the rows from "params/documents.json"

  Scenario: Documents are found by author name with summary and content
    When I send a POST request to "/api/v1/document/search" with body "input/by_author_name.json"
    Then the response status is 200
    And the response body matches "expected/responses/all_documents.json"

  Scenario: Documents are filtered by kind and returned without summary and content
    When I send a POST request to "/api/v1/document/search" with body "input/by_kind.json"
    Then the response status is 200
    And the response body matches "expected/responses/memory_without_text.json"

  Scenario: Documents are filtered by scope and returned with summary only
    When I send a POST request to "/api/v1/document/search" with body "input/by_scope.json"
    Then the response status is 200
    And the response body matches "expected/responses/global_document_with_summary.json"

  Scenario: Documents are filtered by author type and returned with content only
    When I send a POST request to "/api/v1/document/search" with body "input/by_author_type.json"
    Then the response status is 200
    And the response body matches "expected/responses/memory_with_content.json"

  Scenario: Documents are filtered by project key
    When I send a POST request to "/api/v1/document/search" with body "input/by_project_key.json"
    Then the response status is 200
    And the response body matches "expected/responses/project_document.json"

  Scenario: A search that matches nothing returns an empty list
    When I send a POST request to "/api/v1/document/search" with body "input/by_unused_author_name.json"
    Then the response status is 200
    And the response body matches "expected/responses/no_documents.json"
