Feature: Create document

  Scenario: A global document is created and stored
    When I send a POST request to "/api/v1/document" with body "input/global_document.json"
    Then the response status is 200
    And the response body matches "expected/responses/created_document.json"
    And I remember the response field "id" as "createdId"
    And the rows of table "document" where "author_name" is "{{authorName}}" match "expected/database/global_document.json"

  Scenario: A project document is created and stored with its project key
    When I send a POST request to "/api/v1/document" with body "input/project_document.json"
    Then the response status is 200
    And the response body matches "expected/responses/created_document.json"
    And I remember the response field "id" as "createdId"
    And the rows of table "document" where "author_name" is "{{authorName}}" match "expected/database/project_document.json"

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
