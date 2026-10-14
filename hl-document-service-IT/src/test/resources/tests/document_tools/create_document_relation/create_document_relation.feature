Feature: MCP tool createDocumentRelation

  Background:
    Given the table "project" contains the rows from "params/projects.json"
    And the table "document_content" contains the rows from "params/document_contents.json"
    And the table "document" contains the rows from "params/documents.json"

  Scenario: A relation is created and shown on both documents
    Given the variable "relatedId" is "${memoryId}"
    When I call the MCP tool "createDocumentRelation" with arguments "input/relation.json"
    Then the MCP tool call succeeds
    And the rows of table "document_relations" where "document_id" is "${documentId}" match "expected/database/relation.json"
    And the variable "targetId" is "${documentId}"
    When I call the MCP tool "getDocument" with arguments "input/document.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON matches "expected/responses/document_with_relation.json"
    And the variable "targetId" is "${memoryId}"
    When I call the MCP tool "getDocument" with arguments "input/document.json"
    Then the MCP tool call succeeds
    And the MCP tool result decoded from TOON matches "expected/responses/referenced_document.json"

  Scenario: A relation between the same two documents cannot be created twice
    Given the variable "relatedId" is "${memoryId}"
    Given the table "document_relations" contains the rows from "params/relations.json"
    When I call the MCP tool "createDocumentRelation" with arguments "input/relation.json"
    Then the MCP tool call fails
    And the MCP tool result is the text from "expected/responses/relation_exists.txt"
    And the rows of table "document_relations" where "document_id" is "${documentId}" match "params/relations.json"

  Scenario: A relation to an unknown document is rejected
    Given the variable "relatedId" is "${unknownId}"
    When I call the MCP tool "createDocumentRelation" with arguments "input/relation.json"
    Then the MCP tool call fails
    And the MCP tool result is the text from "expected/responses/related_document_not_found.txt"
    And the rows of table "document_relations" where "document_id" is "${documentId}" match "expected/database/no_relations.json"

  Scenario: A relation from a document to itself is rejected
    Given the variable "relatedId" is "${documentId}"
    When I call the MCP tool "createDocumentRelation" with arguments "input/relation.json"
    Then the MCP tool call fails
    And the MCP tool result is the text from "expected/responses/relation_to_itself.txt"

  Scenario: A relation from a document that does not allow MCP writes is rejected
    Given the variable "relatedId" is "${memoryId}"
    When I send a PUT request to "/api/v1/document/${documentId}" with body "input/deny_mcp_write.json"
    Then the response status is 200
    When I call the MCP tool "createDocumentRelation" with arguments "input/relation.json"
    Then the MCP tool call fails
    And the MCP tool result is the text from "expected/responses/write_forbidden.txt"
    And the rows of table "document_relations" where "document_id" is "${documentId}" match "expected/database/no_relations.json"

  Scenario: A relation to a document that does not allow MCP writes is created
    Given the variable "relatedId" is "${memoryId}"
    When I send a PUT request to "/api/v1/document/${memoryId}" with body "input/deny_mcp_write.json"
    Then the response status is 200
    When I call the MCP tool "createDocumentRelation" with arguments "input/relation.json"
    Then the MCP tool call succeeds
    And the rows of table "document_relations" where "document_id" is "${documentId}" match "expected/database/relation.json"
