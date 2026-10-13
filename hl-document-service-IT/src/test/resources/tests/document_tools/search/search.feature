Feature: MCP tool search

  Background:
    Given the table "project" contains the rows from "params/projects.json"
    And the table "document_content" contains the rows from "params/document_contents.json"
    And the table "document" contains the rows from "params/documents.json"

  Scenario: Documents are found by a word of their content
    When I call the MCP tool "search" with arguments "input/by_query_in_content.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON matches "expected/responses/project_document.json"

  Scenario: Documents are filtered by kind and returned without summary and content
    When I call the MCP tool "search" with arguments "input/by_kind.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON matches "expected/responses/memory_without_text.json"

  Scenario: A query that matches no document returns an empty list
    When I call the MCP tool "search" with arguments "input/by_unmatched_query.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON matches "expected/responses/no_documents.json"

  Scenario: A search without any filter or query is rejected
    When I call the MCP tool "search" with arguments "input/without_filters.json"
    Then the MCP tool call fails
    And the MCP tool result is the text from "expected/responses/missing_filters.txt"
