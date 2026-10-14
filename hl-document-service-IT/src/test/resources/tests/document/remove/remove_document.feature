Feature: Remove, restore and list deleted documents

  Background:
    Given the table "project" contains the rows from "params/projects.json"
    And the table "document_content" contains the rows from "params/document_contents.json"
    And the table "document" contains the rows from "params/documents.json"

  Scenario: A document is marked as deleted and keeps its content
    When I send a DELETE request to "/api/v1/document/${documentId}"
    Then the response status is 200
    And the response body is empty
    And the rows of table "document" where "author_name" is "${authorName}" match "expected/database/document_marked_deleted.json"
    And the rows of table "document_content" where "id" is "${documentContentId}" match "expected/database/document_content.json"

  Scenario: A deleted document can no longer be read
    Given I send a DELETE request to "/api/v1/document/${documentId}"
    And the variable "targetId" is "${documentId}"
    When I send a GET request to "/api/v1/document/${documentId}"
    Then the response status is 404
    And the response body matches "expected/responses/document_not_found.json"

  Scenario: Links to a deleted document are hidden
    Given the table "document_relations" contains the rows from "params/relations.json"
    And I send a DELETE request to "/api/v1/document/${memoryId}"
    When I send a GET request to "/api/v1/document/${documentId}"
    Then the response status is 200
    And the response body at "relations" matches "expected/responses/no_relations.json"

  Scenario: A deleted document is read with its content and the links to it
    Given the table "document_relations" contains the rows from "params/relations.json"
    And I send a DELETE request to "/api/v1/document/${memoryId}"
    When I send a GET request to "/api/v1/document/deleted/${memoryId}?includeContent=true"
    Then the response status is 200
    And the response body matches "expected/responses/deleted_memory.json"

  Scenario: A document that is not deleted is not found among the deleted ones
    Given the variable "targetId" is "${documentId}"
    When I send a GET request to "/api/v1/document/deleted/${documentId}"
    Then the response status is 404
    And the response body matches "expected/responses/document_not_found.json"

  Scenario: Deleted documents are listed, the most recently deleted first
    Given the table "document_content" contains the rows from "params/deleted_document_contents.json"
    And the table "document" contains the rows from "params/deleted_documents.json"
    When I send a GET request to "/api/v1/document/deleted?size=2"
    Then the response status is 200
    And the response body contains the fields from "expected/responses/latest_deleted_documents.json" in order

  Scenario: A deleted document is restored
    Given I send a DELETE request to "/api/v1/document/${documentId}"
    When I send a POST request to "/api/v1/document/${documentId}/restore"
    Then the response status is 200
    And the rows of table "document" where "author_name" is "${authorName}" match "expected/database/all_documents.json"

  Scenario: Restoring an unknown document is reported as not found
    Given the variable "targetId" is "${unknownId}"
    When I send a POST request to "/api/v1/document/${unknownId}/restore"
    Then the response status is 404
    And the response body matches "expected/responses/document_not_found.json"

  Scenario: Only the requested document and its content are removed permanently
    When I send a DELETE request to "/api/v1/document/${documentId}/permanent"
    Then the response status is 200
    And the response body is empty
    And the rows of table "document" where "author_name" is "${authorName}" match "expected/database/remaining_document.json"
    And the rows of table "document_content" where "id" is "${documentContentId}" match "expected/database/no_document_content.json"
    And the rows of table "document_content" where "id" is "${memoryContentId}" match "expected/database/remaining_document_content.json"

  Scenario: A deleted document is removed permanently
    Given I send a DELETE request to "/api/v1/document/${documentId}"
    When I send a DELETE request to "/api/v1/document/${documentId}/permanent"
    Then the response status is 200
    And the rows of table "document" where "author_name" is "${authorName}" match "expected/database/remaining_document.json"
    And the rows of table "document_content" where "id" is "${documentContentId}" match "expected/database/no_document_content.json"

  Scenario: A malformed document id is rejected and nothing is removed
    When I send a DELETE request to "/api/v1/document/not-a-number"
    Then the response status is 400
    And the response body matches "expected/responses/invalid_document_id.json"
    And the rows of table "document" where "author_name" is "${authorName}" match "expected/database/all_documents.json"
    And the rows of table "document_content" where "id" is "${documentContentId}" match "expected/database/document_content.json"
