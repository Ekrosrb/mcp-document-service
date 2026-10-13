Feature: MCP tool createMemory

  Scenario: A global memory is created with the calling agent as author and can be read back
    When I call the MCP tool "createMemory" with arguments "input/global_memory.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON matches "expected/responses/created_memory.json"
    And I remember the MCP tool result field "id" as "createdId"
    And the rows of table "document" where "author_name" is "${agentName}" match "expected/database/global_memory.json"
    When I call the MCP tool "getDocument" with arguments "input/created_memory.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON matches "expected/responses/stored_global_memory.json"

  Scenario: A project memory gets the default type and its project is created
    When I call the MCP tool "createMemory" with arguments "input/project_memory.json"
    Then the MCP tool call succeeds
    And I remember the MCP tool result field "id" as "createdId"
    And the rows of table "document" where "author_name" is "${agentName}" match "expected/database/project_memory.json"
    And the rows of table "project" where "project_key" is "${projectKey}" match "expected/database/new_project.json"

  Scenario: A project memory without a project key is rejected
    When I call the MCP tool "createMemory" with arguments "input/project_memory_without_project_key.json"
    Then the MCP tool call fails
    And the MCP tool result is the text from "expected/responses/missing_project_key.txt"
    And the rows of table "document" where "author_name" is "${agentName}" match "expected/database/no_documents.json"
