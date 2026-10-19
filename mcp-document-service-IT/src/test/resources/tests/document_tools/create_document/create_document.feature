Feature: MCP tool createDocument

  Scenario: A global document is created with the calling agent as author and can be read back
    When I call the MCP tool "createDocument" with arguments "input/global_document.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON matches "expected/responses/created_document.json"
    And I remember the MCP tool result field "id" as "createdId"
    And the rows of table "document" where "author_name" is "${agentName}" match "expected/database/global_document.json"
    When I call the MCP tool "getDocument" with arguments "input/created_document.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON matches "expected/responses/stored_global_document.json"

  Scenario: A project document gets the default type and its project is created
    When I call the MCP tool "createDocument" with arguments "input/project_document.json"
    Then the MCP tool call succeeds
    And I remember the MCP tool result field "id" as "createdId"
    And the rows of table "document" where "author_name" is "${agentName}" match "expected/database/project_document.json"
    And the rows of table "project" where "project_key" is "${projectKey}" match "expected/database/new_project.json"

  Scenario: An HTML document keeps its type and can be read back
    When I call the MCP tool "createDocument" with arguments "input/html_document.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON matches "expected/responses/created_document.json"
    And I remember the MCP tool result field "id" as "createdId"
    And the rows of table "document" where "author_name" is "${agentName}" match "expected/database/html_document.json"
    When I call the MCP tool "getDocument" with arguments "input/created_document.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON matches "expected/responses/stored_html_document.json"

  Scenario: A CSS document keeps its type and can be read back
    When I call the MCP tool "createDocument" with arguments "input/css_document.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON matches "expected/responses/created_document.json"
    And I remember the MCP tool result field "id" as "createdId"
    And the rows of table "document" where "author_name" is "${agentName}" match "expected/database/css_document.json"
    When I call the MCP tool "getDocument" with arguments "input/created_document.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON matches "expected/responses/stored_css_document.json"

  Scenario: A project document without a project key is rejected
    When I call the MCP tool "createDocument" with arguments "input/project_document_without_project_key.json"
    Then the MCP tool call fails
    And the MCP tool result is the text from "expected/responses/missing_project_key.txt"
    And the rows of table "document" where "author_name" is "${agentName}" match "expected/database/no_documents.json"

  Scenario: A document without a summary is rejected
    When I call the MCP tool "createDocument" with arguments "input/document_without_summary.json"
    Then the MCP tool call fails
    And the MCP tool result is the text from "expected/responses/missing_summary.txt"
    And the rows of table "document" where "author_name" is "${agentName}" match "expected/database/no_documents.json"

  Scenario: A document with a name longer than 250 characters is rejected
    When I call the MCP tool "createDocument" with arguments "input/document_with_too_long_name.json"
    Then the MCP tool call fails
    And the MCP tool result is the text from "expected/responses/too_long_name.txt"
    And the rows of table "document" where "author_name" is "${agentName}" match "expected/database/no_documents.json"

  Scenario: A custom type document without a custom extension is rejected
    When I call the MCP tool "createDocument" with arguments "input/custom_document_without_extension.json"
    Then the MCP tool call fails
    And the MCP tool result is the text from "expected/responses/missing_custom_extension.txt"
    And the rows of table "document" where "author_name" is "${agentName}" match "expected/database/no_documents.json"

  Scenario: A document is created with relations, even to documents that do not allow MCP writes
    Given the table "document_content" contains the rows from "params/document_contents.json"
    And the table "document" contains the rows from "params/documents.json"
    When I call the MCP tool "createDocument" with arguments "input/document_with_relations.json"
    Then the MCP tool call succeeds
    And I remember the MCP tool result field "id" as "createdId"
    And the rows of table "document_relations" where "document_id" is "${createdId}" match "expected/database/created_relations.json"

  Scenario: A relation to an unknown document is rejected and nothing is created
    When I call the MCP tool "createDocument" with arguments "input/document_with_relation_to_unknown_document.json"
    Then the MCP tool call fails
    And the MCP tool result is the text from "expected/responses/related_document_not_found.txt"
    And the rows of table "document" where "author_name" is "${agentName}" match "expected/database/no_documents.json"

  Scenario: A relation without a type is rejected
    When I call the MCP tool "createDocument" with arguments "input/document_with_relation_without_type.json"
    Then the MCP tool call fails
    And the MCP tool result is the text from "expected/responses/relation_without_type.txt"
    And the rows of table "document" where "author_name" is "${agentName}" match "expected/database/no_documents.json"

  Scenario: The same related document listed twice is rejected
    When I call the MCP tool "createDocument" with arguments "input/document_with_duplicate_relations.json"
    Then the MCP tool call fails
    And the MCP tool result is the text from "expected/responses/duplicate_relations.txt"
    And the rows of table "document" where "author_name" is "${agentName}" match "expected/database/no_documents.json"
