Feature: MCP tool updateDocument

  Background:
    Given the table "project" contains the rows from "params/projects.json"
    And the table "document_content" contains the rows from "params/document_contents.json"
    And the table "document" contains the rows from "params/documents.json"

  Scenario: A document that allows MCP writes is updated
    Given the variable "targetId" is "${memoryId}"
    When I call the MCP tool "updateDocument" with arguments "input/change.json"
    Then the MCP tool call succeeds
    And the rows of table "document" where "id" is "${memoryId}" match "expected/database/updated_memory.json"

  Scenario: A document created through MCP can be updated through MCP
    When I call the MCP tool "createDocument" with arguments "input/new_document.json"
    Then the MCP tool call succeeds
    And I remember the MCP tool result field "id" as "targetId"
    When I call the MCP tool "updateDocument" with arguments "input/change.json"
    Then the MCP tool call succeeds
    And the rows of table "document" where "id" is "${targetId}" match "expected/database/updated_created_document.json"

  Scenario: A document that does not allow MCP writes is not updated
    Given the variable "targetId" is "${documentId}"
    When I call the MCP tool "updateDocument" with arguments "input/change.json"
    Then the MCP tool call fails
    And the MCP tool result is the text from "expected/responses/write_forbidden.txt"
    And the rows of table "document" where "id" is "${documentId}" match "expected/database/unchanged_document.json"

  Scenario: A protected document can be updated after write access is granted through REST
    Given the variable "targetId" is "${documentId}"
    When I send a PUT request to "/api/v1/document/${documentId}" with body "input/allow_mcp_write.json"
    Then the response status is 200
    When I call the MCP tool "updateDocument" with arguments "input/change.json"
    Then the MCP tool call succeeds
    And the rows of table "document" where "id" is "${documentId}" match "expected/database/updated_document.json"

  Scenario: A write access flag sent through MCP is ignored
    When I call the MCP tool "updateDocument" with arguments "input/memory_with_write_flag.json"
    Then the MCP tool call succeeds
    And the rows of table "document" where "id" is "${memoryId}" match "expected/database/updated_memory.json"

  Scenario: Updating an unknown document is reported as not found
    Given the variable "targetId" is "${unknownId}"
    When I call the MCP tool "updateDocument" with arguments "input/change.json"
    Then the MCP tool call fails
    And the MCP tool result is the text from "expected/responses/document_not_found.txt"
