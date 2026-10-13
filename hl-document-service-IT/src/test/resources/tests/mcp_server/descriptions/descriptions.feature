Feature: MCP server descriptions

  Scenario: The server tells the agent how to use it
    Then the MCP server provides instructions

  Scenario: Every tool and every tool parameter is described
    When I list the MCP tools
    Then every MCP tool and every tool parameter has a description
