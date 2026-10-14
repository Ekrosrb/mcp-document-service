Feature: MCP tool removeDocument

  Background:
    Given the table "project" contains the rows from "params/projects.json"
    And the table "document_content" contains the rows from "params/document_contents.json"
    And the table "document" contains the rows from "params/documents.json"

  Scenario: A document that allows MCP writes is marked as deleted and keeps its content
    Given the variable "targetId" is "${memoryId}"
    When I call the MCP tool "removeDocument" with arguments "input/document.json"
    Then the MCP tool call succeeds
    And the rows of table "document" where "id" is "${memoryId}" match "expected/database/memory_marked_deleted.json"
    And the rows of table "document_content" where "id" is "${memoryContentId}" match "expected/database/memory_content.json"

  Scenario: A removed document can no longer be read
    Given the variable "targetId" is "${memoryId}"
    And I call the MCP tool "removeDocument" with arguments "input/document.json"
    When I call the MCP tool "getDocument" with arguments "input/document_with_content.json"
    Then the MCP tool call fails
    And the MCP tool result is the text from "expected/responses/document_not_found.txt"

  Scenario: A document that does not allow MCP writes is not removed
    Given the variable "targetId" is "${documentId}"
    When I call the MCP tool "removeDocument" with arguments "input/document.json"
    Then the MCP tool call fails
    And the MCP tool result is the text from "expected/responses/write_forbidden.txt"
    And the rows of table "document" where "id" is "${documentId}" match "expected/database/remaining_document.json"
