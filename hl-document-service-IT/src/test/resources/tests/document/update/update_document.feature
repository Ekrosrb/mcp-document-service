Feature: Update document

  Background:
    Given the table "project" contains the rows from "params/projects.json"
    And the table "document_content" contains the rows from "params/document_contents.json"
    And the table "document" contains the rows from "params/documents.json"

  Scenario: All given fields of a document are updated
    When I send a PUT request to "/api/v1/document/${documentId}" with body "input/all_fields.json"
    Then the response status is 200
    And the response body is empty
    And the rows of table "document" where "id" is "${documentId}" match "expected/database/fully_updated_document.json"
    And the rows of table "document_content" where "id" is "${documentContentId}" match "expected/database/new_content.json"

  Scenario: Updating only the content keeps the other fields and refreshes the update time
    When I send a PUT request to "/api/v1/document/${documentId}" with body "input/content_only.json"
    Then the response status is 200
    And the rows of table "document" where "id" is "${documentId}" match "expected/database/document_with_new_content.json"
    And the rows of table "document_content" where "id" is "${documentContentId}" match "expected/database/new_content.json"

  Scenario: Write access from MCP is granted through the update
    When I send a PUT request to "/api/v1/document/${documentId}" with body "input/allow_mcp_write.json"
    Then the response status is 200
    And the rows of table "document" where "id" is "${documentId}" match "expected/database/document_writable_from_mcp.json"

  Scenario: Moving a document to a new project creates that project
    When I send a PUT request to "/api/v1/document/${documentId}" with body "input/new_project_key.json"
    Then the response status is 200
    And the rows of table "document" where "id" is "${documentId}" match "expected/database/document_in_new_project.json"
    And the rows of table "project" where "project_key" is "${newProjectKey}" match "expected/database/new_project.json"

  Scenario: Updating an unknown document is reported as not found
    When I send a PUT request to "/api/v1/document/${unknownId}" with body "input/content_only.json"
    Then the response status is 404
    And the response body matches "expected/responses/document_not_found.json"

  Scenario: A blank name is rejected and nothing is changed
    When I send a PUT request to "/api/v1/document/${documentId}" with body "input/blank_name.json"
    Then the response status is 400
    And the response body matches "expected/responses/blank_name.json"
    And the rows of table "document" where "id" is "${documentId}" match "expected/database/unchanged_document.json"

  Scenario: Changing the type to custom without a custom extension is rejected
    When I send a PUT request to "/api/v1/document/${documentId}" with body "input/custom_type_without_extension.json"
    Then the response status is 400
    And the response body matches "expected/responses/missing_custom_extension.json"
    And the rows of table "document" where "id" is "${documentId}" match "expected/database/unchanged_document.json"
