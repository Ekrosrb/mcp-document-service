@isolated
Feature: Count entries per type

  Background:
    Given the table "document_content" contains the rows from "params/document_contents.json"
    And the table "document" contains the rows from "params/documents.json"

  Scenario: Live entries of every kind are counted per type, deleted ones are left out
    When I send a GET request to "/api/v1/metric/count/type"
    Then the response status is 200
    And the response body matches "expected/responses/all_kinds.json"

  Scenario: Only documents are counted with kind DOCUMENT
    When I send a GET request to "/api/v1/metric/count/type?kind=DOCUMENT"
    Then the response status is 200
    And the response body matches "expected/responses/documents.json"

  Scenario: Only memories are counted with kind MEMORY
    When I send a GET request to "/api/v1/metric/count/type?kind=MEMORY"
    Then the response status is 200
    And the response body matches "expected/responses/memories.json"

  Scenario: A superseded entry is not counted
    Given the table "document_relations" contains the rows from "params/superseding_relation.json"
    When I send a GET request to "/api/v1/metric/count/type"
    Then the response status is 200
    And the response body matches "expected/responses/without_superseded.json"

  Scenario: An entry superseded only by a deleted entry is counted
    Given the table "document_relations" contains the rows from "params/deleted_superseding_relation.json"
    When I send a GET request to "/api/v1/metric/count/type"
    Then the response status is 200
    And the response body matches "expected/responses/all_kinds.json"

  Scenario: An unknown kind is rejected
    When I send a GET request to "/api/v1/metric/count/type?kind=NOTE"
    Then the response status is 400
    And the response body matches "expected/responses/invalid_kind.json"
