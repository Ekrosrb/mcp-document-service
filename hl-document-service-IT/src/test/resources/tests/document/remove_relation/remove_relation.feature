Feature: Remove document relation

  Background:
    Given the table "project" contains the rows from "params/projects.json"
    And the table "document_content" contains the rows from "params/document_contents.json"
    And the table "document" contains the rows from "params/documents.json"
    And the table "document_relations" contains the rows from "params/relations.json"

  Scenario: Only the relation in the requested direction is removed
    When I send a DELETE request to "/api/v1/document/relation/${documentId}/${memoryId}"
    Then the response status is 200
    And the response body is empty
    And the rows of table "document_relations" where "document_id" is "${documentId}" match "expected/database/no_relations.json"
    And the rows of table "document_relations" where "document_id" is "${memoryId}" match "expected/database/backward_relation.json"

  Scenario: Removing a relation that does not exist changes nothing
    When I send a DELETE request to "/api/v1/document/relation/${documentId}/${unrelatedId}"
    Then the response status is 200
    And the rows of table "document_relations" where "document_id" is "${documentId}" match "expected/database/forward_relation.json"
    And the rows of table "document_relations" where "document_id" is "${memoryId}" match "expected/database/backward_relation.json"

  Scenario: A relation from a document to itself is rejected
    When I send a DELETE request to "/api/v1/document/relation/${documentId}/${documentId}"
    Then the response status is 400
    And the response body matches "expected/responses/relation_to_itself.json"
    And the rows of table "document_relations" where "document_id" is "${documentId}" match "expected/database/forward_relation.json"
