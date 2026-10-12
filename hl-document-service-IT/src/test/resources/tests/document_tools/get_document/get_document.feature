Feature: MCP tool getDocument

  Scenario: An existing document is returned
    Given the table "document" contains the rows from "params/document.json"
    When I call the MCP tool "getDocument" with arguments "input/existing_document.json"
    Then the MCP tool call succeeds
    And the MCP tool result is the text from "expected/responses/document.toon"

  Scenario: An unknown document is reported as not found
    When I call the MCP tool "getDocument" with arguments "input/unknown_document.json"
    Then the MCP tool call fails
    And the MCP tool result is the text from "expected/responses/document_not_found.txt"

  Scenario: A malformed document id is rejected
    When I call the MCP tool "getDocument" with arguments "input/malformed_id.json"
    Then the MCP tool call fails
    And the MCP tool result is the text from "expected/responses/invalid_document_id.txt"
