Feature: MCP tool removeDocumentRelation

  Background:
    Given the table "project" contains the rows from "params/projects.json"
    And the table "document_content" contains the rows from "params/document_contents.json"
    And the table "document" contains the rows from "params/documents.json"
    And the table "document_relations" contains the rows from "params/relations.json"

  Scenario: A relation is removed when its source allows MCP writes, even if its target does not
    Given the variable "sourceId" is "${documentId}"
    And the variable "relatedId" is "${memoryId}"
    When I call the MCP tool "removeDocumentRelation" with arguments "input/relation.json"
    Then the MCP tool call succeeds
    And the rows of table "document_relations" where "document_id" is "${documentId}" match "expected/database/no_relations.json"
    And the rows of table "document_relations" where "document_id" is "${memoryId}" match "expected/database/backward_relation.json"

  Scenario: A relation is not removed when its source does not allow MCP writes
    Given the variable "sourceId" is "${memoryId}"
    And the variable "relatedId" is "${documentId}"
    When I call the MCP tool "removeDocumentRelation" with arguments "input/relation.json"
    Then the MCP tool call fails
    And the MCP tool result is the text from "expected/responses/write_forbidden.txt"
    And the rows of table "document_relations" where "document_id" is "${memoryId}" match "expected/database/backward_relation.json"
