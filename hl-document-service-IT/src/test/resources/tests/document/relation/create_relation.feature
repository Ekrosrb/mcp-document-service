Feature: Create document relation

  Background:
    Given the table "project" contains the rows from "params/projects.json"
    And the table "document_content" contains the rows from "params/document_contents.json"
    And the table "document" contains the rows from "params/documents.json"

  Scenario: A relation is created and shown on both documents
    When I send a POST request to "/api/v1/document/relation" with body "input/relation.json"
    Then the response status is 201
    And the response body is empty
    And the rows of table "document_relations" where "document_id" is "${documentId}" match "expected/database/relation.json"
    When I send a GET request to "/api/v1/document/${documentId}"
    Then the response status is 200
    And the response body matches "expected/responses/document_with_relation.json"
    When I send a GET request to "/api/v1/document/${memoryId}"
    Then the response status is 200
    And the response body matches "expected/responses/referenced_document.json"

  Scenario: A relation between the same two documents cannot be created twice
    Given the table "document_relations" contains the rows from "params/relations.json"
    When I send a POST request to "/api/v1/document/relation" with body "input/relation.json"
    Then the response status is 409
    And the response body matches "expected/responses/relation_exists.json"
    And the rows of table "document_relations" where "document_id" is "${documentId}" match "expected/database/existing_relation.json"

  Scenario: A relation to an unknown document is rejected
    When I send a POST request to "/api/v1/document/relation" with body "input/relation_to_unknown_document.json"
    Then the response status is 404
    And the response body matches "expected/responses/related_document_not_found.json"
    And the rows of table "document_relations" where "document_id" is "${documentId}" match "expected/database/no_relations.json"

  Scenario: A relation from a document to itself is rejected
    When I send a POST request to "/api/v1/document/relation" with body "input/relation_to_itself.json"
    Then the response status is 400
    And the response body matches "expected/responses/relation_to_itself.json"

  Scenario: A relation without a type is rejected
    When I send a POST request to "/api/v1/document/relation" with body "input/relation_without_type.json"
    Then the response status is 400
    And the response body matches "expected/responses/relation_without_type.json"

  Scenario: Removing a related document removes the relation
    Given the table "document_relations" contains the rows from "params/relations.json"
    When I send a DELETE request to "/api/v1/document/${memoryId}"
    Then the response status is 200
    And the rows of table "document_relations" where "document_id" is "${documentId}" match "expected/database/no_relations.json"
