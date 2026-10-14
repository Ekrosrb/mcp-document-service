Feature: MCP tool getProjectsInfo

  Background:
    Given the table "project" contains the rows from "params/projects.json"
    And the table "document_content" contains the rows from "params/document_contents.json"
    And the table "document" contains the rows from "params/documents.json"

  Scenario: Projects are listed in TOON with the number of their documents
    When I call the MCP tool "getProjectsInfo" with arguments "input/first_page.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON contains the element from "expected/responses/project_with_two_documents.json"
    And the MCP tool result decoded from TOON contains the element from "expected/responses/project_without_documents.json"
