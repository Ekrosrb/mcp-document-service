Feature: MCP tool getDocument

  Background:
    Given the table "project" contains the rows from "params/projects.json"
    And the table "document_content" contains the rows from "params/document_contents.json"
    And the table "document" contains the rows from "params/documents.json"

  Scenario: An existing document is returned with its content in TOON
    Given the variable "targetId" is "${documentId}"
    When I call the MCP tool "getDocument" with arguments "input/document_with_content.json"
    Then the MCP tool call succeeds
    And the MCP tool result is the text from "expected/responses/document_with_content.toon"

  Scenario: An existing document is returned without its content
    When I call the MCP tool "getDocument" with arguments "input/existing_document_without_content.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON matches "expected/responses/document_without_content.json"

  Scenario: The content is left out when includeContent is omitted
    When I call the MCP tool "getDocument" with arguments "input/existing_document_by_id_only.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON matches "expected/responses/document_without_content.json"

  Scenario: An unknown document is reported as not found
    Given the variable "targetId" is "${unknownId}"
    When I call the MCP tool "getDocument" with arguments "input/document_with_content.json"
    Then the MCP tool call fails
    And the MCP tool result is the text from "expected/responses/document_not_found.txt"

  Scenario: A malformed document id is rejected
    Given the variable "targetId" is "not-a-number"
    When I call the MCP tool "getDocument" with arguments "input/document_with_content.json"
    Then the MCP tool call fails
    And the MCP tool result is the text from "expected/responses/invalid_document_id.txt"
