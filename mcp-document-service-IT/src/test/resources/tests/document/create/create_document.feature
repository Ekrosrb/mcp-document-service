Feature: Create document

  Scenario: A global document with a custom extension is created and stored
    When I send a POST request to "/api/v1/document" with body "input/global_document.json"
    Then the response status is 200
    And the response body matches "expected/responses/created_document.json"
    And I remember the response field "id" as "createdId"
    And the rows of table "document" where "author_name" is "${authorName}" match "expected/database/global_document.json"
    When I send a GET request to "/api/v1/document/${createdId}?includeContent=true"
    Then the response status is 200
    And the response body matches "expected/responses/stored_global_document.json"

  Scenario: A project document gets the default type and its project is created
    When I send a POST request to "/api/v1/document" with body "input/project_document.json"
    Then the response status is 200
    And the response body matches "expected/responses/created_document.json"
    And I remember the response field "id" as "createdId"
    And the rows of table "document" where "author_name" is "${authorName}" match "expected/database/project_document.json"
    And the rows of table "project" where "project_key" is "${projectKey}" match "expected/database/new_project.json"
    When I send a GET request to "/api/v1/document/${createdId}?includeContent=true"
    Then the response status is 200
    And the response body matches "expected/responses/stored_project_document.json"

  Scenario: A document for an existing project reuses that project
    Given the table "project" contains the rows from "params/projects.json"
    When I send a POST request to "/api/v1/document" with body "input/project_document.json"
    Then the response status is 200
    And I remember the response field "id" as "createdId"
    And the rows of table "document" where "author_name" is "${authorName}" match "expected/database/project_document.json"
    And the rows of table "project" where "project_key" is "${projectKey}" match "params/projects.json"

  Scenario: A project document without a project key is rejected
    When I send a POST request to "/api/v1/document" with body "input/project_document_without_project_key.json"
    Then the response status is 400
    And the rows of table "document" where "author_name" is "${authorName}" match "expected/database/no_documents.json"

  Scenario: A document without a summary is rejected
    When I send a POST request to "/api/v1/document" with body "input/document_without_summary.json"
    Then the response status is 400
    And the rows of table "document" where "author_name" is "${authorName}" match "expected/database/no_documents.json"

  Scenario: A document with a name longer than 250 characters is rejected
    When I send a POST request to "/api/v1/document" with body "input/document_with_too_long_name.json"
    Then the response status is 400
    And the rows of table "document" where "author_name" is "${authorName}" match "expected/database/no_documents.json"

  Scenario: A custom type document without a custom extension is rejected
    When I send a POST request to "/api/v1/document" with body "input/custom_document_without_extension.json"
    Then the response status is 400
    And the rows of table "document" where "author_name" is "${authorName}" match "expected/database/no_documents.json"

  Scenario: A memory without a memory type is stored as an observation
    When I send a POST request to "/api/v1/document" with body "input/memory_without_memory_type.json"
    Then the response status is 200
    And I remember the response field "id" as "createdId"
    And the rows of table "document" where "author_name" is "${authorName}" match "expected/database/observation_memory.json"
    When I send a GET request to "/api/v1/document/${createdId}?includeContent=true"
    Then the response status is 200
    And the response body matches "expected/responses/stored_observation_memory.json"

  Scenario: A memory is stored with the given memory type
    When I send a POST request to "/api/v1/document" with body "input/decision_memory.json"
    Then the response status is 200
    And I remember the response field "id" as "createdId"
    And the rows of table "document" where "author_name" is "${authorName}" match "expected/database/decision_memory.json"
    When I send a GET request to "/api/v1/document/${createdId}?includeContent=true"
    Then the response status is 200
    And the response body matches "expected/responses/stored_decision_memory.json"

  Scenario: A document with a memory type is rejected
    When I send a POST request to "/api/v1/document" with body "input/document_with_memory_type.json"
    Then the response status is 400
    And the response body matches "expected/responses/memory_type_for_document.json"
    And the rows of table "document" where "author_name" is "${authorName}" match "expected/database/no_documents.json"

  Scenario: A document can be created with write access from MCP
    When I send a POST request to "/api/v1/document" with body "input/document_writable_from_mcp.json"
    Then the response status is 200
    And I remember the response field "id" as "createdId"
    And the rows of table "document" where "author_name" is "${authorName}" match "expected/database/document_writable_from_mcp.json"

  Scenario: A document is created with relations to existing documents
    Given the table "document_content" contains the rows from "params/document_contents.json"
    And the table "document" contains the rows from "params/documents.json"
    When I send a POST request to "/api/v1/document" with body "input/document_with_relations.json"
    Then the response status is 200
    And I remember the response field "id" as "createdId"
    And the rows of table "document_relations" where "document_id" is "${createdId}" match "expected/database/created_relations.json"

  Scenario: A relation to an unknown document is rejected and nothing is created
    When I send a POST request to "/api/v1/document" with body "input/document_with_relation_to_unknown_document.json"
    Then the response status is 404
    And the response body matches "expected/responses/related_document_not_found.json"
    And the rows of table "document" where "author_name" is "${authorName}" match "expected/database/no_documents.json"

  Scenario: A relation without a type is rejected
    When I send a POST request to "/api/v1/document" with body "input/document_with_relation_without_type.json"
    Then the response status is 400
    And the response body matches "expected/responses/relation_without_type.json"
    And the rows of table "document" where "author_name" is "${authorName}" match "expected/database/no_documents.json"

  Scenario: The same related document listed twice is rejected
    When I send a POST request to "/api/v1/document" with body "input/document_with_duplicate_relations.json"
    Then the response status is 400
    And the response body matches "expected/responses/duplicate_relations.json"
    And the rows of table "document" where "author_name" is "${authorName}" match "expected/database/no_documents.json"

  Scenario: An unknown enum value is rejected
    When I send a POST request to "/api/v1/document" with body "input/document_with_unknown_kind.json"
    Then the response status is 400
    And the response body matches "expected/responses/unknown_kind.json"
    And the rows of table "document" where "author_name" is "${authorName}" match "expected/database/no_documents.json"

  Scenario: An unknown enum value inside a relation is rejected with its path
    When I send a POST request to "/api/v1/document" with body "input/document_with_unknown_relation_type.json"
    Then the response status is 400
    And the response body matches "expected/responses/unknown_relation_type.json"
    And the rows of table "document" where "author_name" is "${authorName}" match "expected/database/no_documents.json"
