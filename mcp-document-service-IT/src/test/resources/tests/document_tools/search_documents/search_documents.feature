Feature: MCP tool searchDocuments

  Background:
    Given the table "project" contains the rows from "params/projects.json"
    And the table "document_content" contains the rows from "params/document_contents.json"
    And the table "document" contains the rows from "params/documents.json"

  Scenario: Only documents are returned, never memories
    When I call the MCP tool "searchDocuments" with arguments "input/by_author_name.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON matches "expected/responses/documents.json"

  Scenario: Documents are found by a word of their content
    When I call the MCP tool "searchDocuments" with arguments "input/by_query_in_content.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON matches "expected/responses/project_document.json"

  Scenario: A query that matches no document returns an empty list
    When I call the MCP tool "searchDocuments" with arguments "input/by_unmatched_query.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON matches "expected/responses/no_documents.json"

  Scenario: A superseded document is returned on request
    Given the table "document_relations" contains the rows from "params/superseding_relation.json"
    When I call the MCP tool "searchDocuments" with arguments "input/by_author_name_with_superseded.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON matches "expected/responses/documents.json"

  Scenario: Results of a query come most relevant first
    Given the table "document_content" contains the rows from "params/ranked_document_contents.json"
    And the table "document" contains the rows from "params/ranked_documents.json"
    When I call the MCP tool "searchDocuments" with arguments "input/by_ranked_query.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON contains the fields from "expected/responses/most_relevant_first.json" in order

  Scenario: A later page holds the remaining results
    Given the table "document_content" contains the rows from "params/ranked_document_contents.json"
    And the table "document" contains the rows from "params/ranked_documents.json"
    When I call the MCP tool "searchDocuments" with arguments "input/second_page.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON contains the fields from "expected/responses/older_document.json" in order

  Scenario: A page size above 50 is rejected
    When I call the MCP tool "searchDocuments" with arguments "input/too_large_page.json"
    Then the MCP tool call fails
    And the MCP tool result is the text from "expected/responses/too_large_page.txt"

  Scenario: A query whose words never appear together falls back to any of the words
    Given the table "document_content" contains the rows from "params/ranked_document_contents.json"
    And the table "document" contains the rows from "params/ranked_documents.json"
    When I call the MCP tool "searchDocuments" with arguments "input/by_any_word_query.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON contains the fields from "expected/responses/most_relevant_first.json" in order
