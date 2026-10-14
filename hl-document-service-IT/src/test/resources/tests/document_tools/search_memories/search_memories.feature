Feature: MCP tool searchMemories

  Background:
    Given the table "project" contains the rows from "params/projects.json"
    And the table "document_content" contains the rows from "params/document_contents.json"
    And the table "document" contains the rows from "params/documents.json"

  Scenario: Only memories are returned, never documents
    When I call the MCP tool "searchMemories" with arguments "input/by_author_name.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON matches "expected/responses/memory_without_text.json"

  Scenario: Memories are filtered by memory type
    When I call the MCP tool "searchMemories" with arguments "input/by_memory_type.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON matches "expected/responses/memory_without_text.json"

  Scenario: Memories are found by a word of their name
    When I call the MCP tool "searchMemories" with arguments "input/by_query_in_name.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON matches "expected/responses/memory_without_text.json"

  Scenario: A memory type that no memory has returns an empty list
    When I call the MCP tool "searchMemories" with arguments "input/by_unused_memory_type.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON matches "expected/responses/no_documents.json"

  Scenario: A query that matches only a document returns an empty list
    When I call the MCP tool "searchMemories" with arguments "input/by_document_query.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON matches "expected/responses/no_documents.json"

  Scenario: A superseded memory is left out by default
    Given the table "document_relations" contains the rows from "params/superseding_relation.json"
    When I call the MCP tool "searchMemories" with arguments "input/by_author_name.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON matches "expected/responses/no_documents.json"

  Scenario: A superseded memory is returned on request
    Given the table "document_relations" contains the rows from "params/superseding_relation.json"
    When I call the MCP tool "searchMemories" with arguments "input/by_author_name_with_superseded.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON matches "expected/responses/memory_without_text.json"

  Scenario: A page holds the most recently updated memories
    Given the table "document_content" contains the rows from "params/ranked_document_contents.json"
    And the table "document" contains the rows from "params/ranked_documents.json"
    When I call the MCP tool "searchMemories" with arguments "input/first_page_of_one.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON contains the fields from "expected/responses/newest_memory.json" in order

  Scenario: A deleted memory is left out
    Given I send a DELETE request to "/api/v1/document/${memoryId}"
    When I call the MCP tool "searchMemories" with arguments "input/by_author_name.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON matches "expected/responses/no_documents.json"
