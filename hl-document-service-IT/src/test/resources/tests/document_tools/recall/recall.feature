Feature: MCP tool recall

  Background:
    Given the table "project" contains the rows from "params/projects.json"
    And the table "document_content" contains the rows from "params/document_contents.json"
    And the table "document" contains the rows from "params/documents.json"
    And the table "document_relations" contains the rows from "params/relations.json"

  Scenario: Entries with any word of the task come with the entries linked to them
    When I call the MCP tool "recall" with arguments "input/project_task.json"
    Then the MCP tool call succeeds
    And the MCP tool result is the text from "expected/responses/project_context.toon"

  Scenario: Entries of every project are matched when no project is given
    When I call the MCP tool "recall" with arguments "input/any_project_task.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON contains the fields from "expected/responses/any_project_order.json" in order

  Scenario: Entries two links away are added at depth 2
    When I call the MCP tool "recall" with arguments "input/two_hops.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON contains the fields from "expected/responses/two_hops_order.json" in order

  Scenario: No linked entries are returned at depth 0
    When I call the MCP tool "recall" with arguments "input/no_links.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON contains the fields from "expected/responses/no_links_order.json" in order

  Scenario: The limit applies to matches and to linked entries separately
    When I call the MCP tool "recall" with arguments "input/one_of_each.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON contains the fields from "expected/responses/one_of_each_order.json" in order

  Scenario: The content is added on request
    When I call the MCP tool "recall" with arguments "input/best_match_with_content.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON contains the fields from "expected/responses/best_match_with_content.json" in order

  Scenario: Deleted entries are left out and the entry a deleted one replaced is current again
    Given I send a DELETE request to "/api/v1/document/${partialMatchId}"
    And I send a DELETE request to "/api/v1/document/${extensionId}"
    When I call the MCP tool "recall" with arguments "input/project_task.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON contains the fields from "expected/responses/without_deleted_order.json" in order

  Scenario: A task without words recalls nothing
    When I call the MCP tool "recall" with arguments "input/task_without_words.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON matches "expected/responses/nothing_recalled.json"

  Scenario: A blank task is rejected
    When I call the MCP tool "recall" with arguments "input/blank_task.json"
    Then the MCP tool call fails
    And the MCP tool result is the text from "expected/responses/blank_task.txt"

  Scenario: A depth above 2 is rejected
    When I call the MCP tool "recall" with arguments "input/too_deep.json"
    Then the MCP tool call fails
    And the MCP tool result is the text from "expected/responses/too_deep.txt"
