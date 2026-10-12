Feature: MCP tool createMemory

  Scenario: An agent memory is created and stored with the calling agent as author
    When I call the MCP tool "createMemory" with arguments "input/agent_memory.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON matches "expected/responses/created_memory.json"
    And I remember the MCP tool result field "id" as "createdId"
    And the rows of table "document" where "author_name" is "{{agentName}}" match "expected/database/agent_memory.json"

  Scenario: A project memory is created and stored with its project key
    When I call the MCP tool "createMemory" with arguments "input/project_memory.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON matches "expected/responses/created_memory.json"
    And I remember the MCP tool result field "id" as "createdId"
    And the rows of table "document" where "author_name" is "{{agentName}}" match "expected/database/project_memory.json"

  Scenario: A project memory without a project key is rejected
    When I call the MCP tool "createMemory" with arguments "input/project_memory_without_project_key.json"
    Then the MCP tool call fails
    And the MCP tool result is the text from "expected/responses/missing_project_key.txt"
    And the rows of table "document" where "author_name" is "{{agentName}}" match "expected/database/no_documents.json"
