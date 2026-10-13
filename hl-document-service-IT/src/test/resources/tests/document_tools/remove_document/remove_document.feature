Feature: MCP tool removeDocument

  Background:
    Given the table "project" contains the rows from "params/projects.json"
    And the table "document_content" contains the rows from "params/document_contents.json"
    And the table "document" contains the rows from "params/documents.json"

  Scenario: A document that allows MCP writes is removed with its content
    When I call the MCP tool "removeDocument" with arguments "input/writable_memory.json"
    Then the MCP tool call succeeds
    And the rows of table "document" where "id" is "${memoryId}" match "expected/database/no_documents.json"
    And the rows of table "document_content" where "id" is "${memoryContentId}" match "expected/database/no_documents.json"

  Scenario: A document that does not allow MCP writes is not removed
    When I call the MCP tool "removeDocument" with arguments "input/protected_document.json"
    Then the MCP tool call fails
    And the MCP tool result is the text from "expected/responses/write_forbidden.txt"
    And the rows of table "document" where "id" is "${documentId}" match "expected/database/remaining_document.json"
