Feature: Create document

  Scenario: A global document with a custom extension is created and stored
    When I send a POST request to "/api/v1/document" with body "input/global_document.json"
    Then the response status is 200
    And the response body matches "expected/responses/created_document.json"
    And I remember the response field "id" as "createdId"
    And the rows of table "document" where "author_name" is "{{authorName}}" match "expected/database/global_document.json"
    When I send a GET request to "/api/v1/document/{{createdId}}?includeContent=true"
    Then the response status is 200
    And the response body matches "expected/responses/stored_global_document.json"

  Scenario: A project document gets the default type and its project is created
    When I send a POST request to "/api/v1/document" with body "input/project_document.json"
    Then the response status is 200
    And the response body matches "expected/responses/created_document.json"
    And I remember the response field "id" as "createdId"
    And the rows of table "document" where "author_name" is "{{authorName}}" match "expected/database/project_document.json"
    And the rows of table "project" where "project_key" is "{{projectKey}}" match "expected/database/new_project.json"
    When I send a GET request to "/api/v1/document/{{createdId}}?includeContent=true"
    Then the response status is 200
    And the response body matches "expected/responses/stored_project_document.json"

  Scenario: A document for an existing project reuses that project
    Given the table "project" contains the rows from "params/projects.json"
    When I send a POST request to "/api/v1/document" with body "input/project_document.json"
    Then the response status is 200
    And I remember the response field "id" as "createdId"
    And the rows of table "document" where "author_name" is "{{authorName}}" match "expected/database/project_document.json"
    And the rows of table "project" where "project_key" is "{{projectKey}}" match "expected/database/existing_project.json"

  Scenario: A project document without a project key is rejected
    When I send a POST request to "/api/v1/document" with body "input/project_document_without_project_key.json"
    Then the response status is 400
    And the rows of table "document" where "author_name" is "{{authorName}}" match "expected/database/no_documents.json"

  Scenario: A document without a summary is rejected
    When I send a POST request to "/api/v1/document" with body "input/document_without_summary.json"
    Then the response status is 400
    And the rows of table "document" where "author_name" is "{{authorName}}" match "expected/database/no_documents.json"

  Scenario: A document with a name longer than 250 characters is rejected
    When I send a POST request to "/api/v1/document" with body "input/document_with_too_long_name.json"
    Then the response status is 400
    And the rows of table "document" where "author_name" is "{{authorName}}" match "expected/database/no_documents.json"

  Scenario: A custom type document without a custom extension is rejected
    When I send a POST request to "/api/v1/document" with body "input/custom_document_without_extension.json"
    Then the response status is 400
    And the rows of table "document" where "author_name" is "{{authorName}}" match "expected/database/no_documents.json"
