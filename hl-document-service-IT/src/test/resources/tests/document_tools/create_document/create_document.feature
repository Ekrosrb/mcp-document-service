Feature: MCP tool createDocument

  Scenario: A global document is created with the calling agent as author and can be read back
    When I call the MCP tool "createDocument" with arguments "input/global_document.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON matches "expected/responses/created_document.json"
    And I remember the MCP tool result field "id" as "createdId"
    And the rows of table "document" where "author_name" is "{{agentName}}" match "expected/database/global_document.json"
    When I call the MCP tool "getDocument" with arguments "input/created_document.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON matches "expected/responses/stored_global_document.json"

  Scenario: A project document gets the default type and its project is created
    When I call the MCP tool "createDocument" with arguments "input/project_document.json"
    Then the MCP tool call succeeds
    And I remember the MCP tool result field "id" as "createdId"
    And the rows of table "document" where "author_name" is "{{agentName}}" match "expected/database/project_document.json"
    And the rows of table "project" where "project_key" is "{{projectKey}}" match "expected/database/new_project.json"

  Scenario: A project document without a project key is rejected
    When I call the MCP tool "createDocument" with arguments "input/project_document_without_project_key.json"
    Then the MCP tool call fails
    And the MCP tool result is the text from "expected/responses/missing_project_key.txt"
    And the rows of table "document" where "author_name" is "{{agentName}}" match "expected/database/no_documents.json"

  Scenario: A document without a summary is rejected
    When I call the MCP tool "createDocument" with arguments "input/document_without_summary.json"
    Then the MCP tool call fails
    And the MCP tool result is the text from "expected/responses/missing_summary.txt"
    And the rows of table "document" where "author_name" is "{{agentName}}" match "expected/database/no_documents.json"

  Scenario: A document with a name longer than 250 characters is rejected
    When I call the MCP tool "createDocument" with arguments "input/document_with_too_long_name.json"
    Then the MCP tool call fails
    And the MCP tool result is the text from "expected/responses/too_long_name.txt"
    And the rows of table "document" where "author_name" is "{{agentName}}" match "expected/database/no_documents.json"

  Scenario: A custom type document without a custom extension is rejected
    When I call the MCP tool "createDocument" with arguments "input/custom_document_without_extension.json"
    Then the MCP tool call fails
    And the MCP tool result is the text from "expected/responses/missing_custom_extension.txt"
    And the rows of table "document" where "author_name" is "{{agentName}}" match "expected/database/no_documents.json"
