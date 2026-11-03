Feature: Search documents

  Background:
    Given the table "project" contains the rows from "params/projects.json"
    And the table "document_content" contains the rows from "params/document_contents.json"
    And the table "document" contains the rows from "params/documents.json"

  Scenario: Documents are found by author name with summary and content
    When I send a POST request to "/api/v1/document/search" with body "input/by_author_name.json"
    Then the response status is 200
    And the response body at "content" matches "expected/responses/all_documents.json"

  Scenario: Documents are filtered by kind and returned without summary and content
    When I send a POST request to "/api/v1/document/search" with body "input/by_kind.json"
    Then the response status is 200
    And the response body at "content" matches "expected/responses/memory_without_text.json"

  Scenario: Documents are filtered by scope and returned with summary only
    When I send a POST request to "/api/v1/document/search" with body "input/by_scope.json"
    Then the response status is 200
    And the response body at "content" matches "expected/responses/global_document_with_summary.json"

  Scenario: Documents are filtered by author type and returned with content only
    When I send a POST request to "/api/v1/document/search" with body "input/by_author_type.json"
    Then the response status is 200
    And the response body at "content" matches "expected/responses/memory_with_content.json"

  Scenario: Documents are filtered by project key
    When I send a POST request to "/api/v1/document/search" with body "input/by_project_key.json"
    Then the response status is 200
    And the response body at "content" matches "expected/responses/project_document.json"

  Scenario: Relations are left out unless requested
    Given the table "document_relations" contains the rows from "params/relations.json"
    When I send a POST request to "/api/v1/document/search" with body "input/by_project_key.json"
    Then the response status is 200
    And the response body at "content" matches "expected/responses/project_document.json"

  Scenario: Relations in both directions are returned on request
    Given the table "document_relations" contains the rows from "params/relations.json"
    When I send a POST request to "/api/v1/document/search" with body "input/by_project_key_with_relations.json"
    Then the response status is 200
    And the response body at "content" matches "expected/responses/project_document_with_relations.json"

  Scenario: Documents are found by a word of their content
    When I send a POST request to "/api/v1/document/search" with body "input/by_query_in_content.json"
    Then the response status is 200
    And the response body at "content" matches "expected/responses/project_document.json"

  Scenario: Documents are found by a word of their name
    When I send a POST request to "/api/v1/document/search" with body "input/by_query_in_name.json"
    Then the response status is 200
    And the response body at "content" matches "expected/responses/memory_without_text.json"

  Scenario: A query is combined with the other filters
    When I send a POST request to "/api/v1/document/search" with body "input/by_query_and_kind.json"
    Then the response status is 200
    And the response body at "content" matches "expected/responses/memory_without_text.json"

  Scenario: A query that matches no document returns an empty list
    When I send a POST request to "/api/v1/document/search" with body "input/by_unmatched_query.json"
    Then the response status is 200
    And the response body at "content" matches "expected/responses/no_documents.json"

  Scenario: A search without any filter or query returns all entries newest first
    Given the table "document_content" contains the rows from "params/newest_document_contents.json"
    And the table "document" contains the rows from "params/newest_documents.json"
    When I send a POST request to "/api/v1/document/search?size=1" with body "input/without_filters.json"
    Then the response status is 200
    And the response body contains the fields from "expected/responses/newest_of_all.json" in order

  Scenario: Documents are filtered by type
    When I send a POST request to "/api/v1/document/search" with body "input/by_type.json"
    Then the response status is 200
    And the response body at "content" matches "expected/responses/project_document.json"

  Scenario: A type that no document has returns an empty list
    When I send a POST request to "/api/v1/document/search" with body "input/by_unused_type.json"
    Then the response status is 200
    And the response body at "content" matches "expected/responses/no_documents.json"

  Scenario: Memories are filtered by memory type
    When I send a POST request to "/api/v1/document/search" with body "input/by_memory_type.json"
    Then the response status is 200
    And the response body at "content" matches "expected/responses/memory_without_text.json"

  Scenario: A memory type that no memory has returns an empty list
    When I send a POST request to "/api/v1/document/search" with body "input/by_unused_memory_type.json"
    Then the response status is 200
    And the response body at "content" matches "expected/responses/no_documents.json"

  Scenario: A memory type combined with the document kind is rejected
    When I send a POST request to "/api/v1/document/search" with body "input/by_memory_type_and_document_kind.json"
    Then the response status is 400
    And the response body matches "expected/responses/memory_type_for_document_kind.json"

  Scenario: A search that matches nothing returns an empty list
    When I send a POST request to "/api/v1/document/search" with body "input/by_unused_author_name.json"
    Then the response status is 200
    And the response body at "content" matches "expected/responses/no_documents.json"

  Scenario: A superseded document is left out by default
    Given the table "document_relations" contains the rows from "params/superseding_relation.json"
    When I send a POST request to "/api/v1/document/search" with body "input/by_author_name.json"
    Then the response status is 200
    And the response body at "content" matches "expected/responses/current_documents.json"

  Scenario: A superseded document is returned on request
    Given the table "document_relations" contains the rows from "params/superseding_relation.json"
    When I send a POST request to "/api/v1/document/search" with body "input/by_author_name_with_superseded.json"
    Then the response status is 200
    And the response body at "content" matches "expected/responses/all_documents.json"

  Scenario: A query that matches only a superseded document returns an empty list
    Given the table "document_relations" contains the rows from "params/superseding_relation.json"
    When I send a POST request to "/api/v1/document/search" with body "input/by_query_in_content.json"
    Then the response status is 200
    And the response body at "content" matches "expected/responses/no_documents.json"

  Scenario: Results without a query come newest first in pages of 10
    Given the table "document_content" contains the rows from "params/ranked_document_contents.json"
    And the table "document" contains the rows from "params/ranked_documents.json"
    When I send a POST request to "/api/v1/document/search" with body "input/by_ranked_author_name.json"
    Then the response status is 200
    And the response body contains the fields from "expected/responses/newest_first.json" in order

  Scenario: Results of a query come most relevant first
    Given the table "document_content" contains the rows from "params/ranked_document_contents.json"
    And the table "document" contains the rows from "params/ranked_documents.json"
    When I send a POST request to "/api/v1/document/search" with body "input/by_ranked_query.json"
    Then the response status is 200
    And the response body contains the fields from "expected/responses/most_relevant_first.json" in order

  Scenario: A later page holds the remaining results
    Given the table "document_content" contains the rows from "params/ranked_document_contents.json"
    And the table "document" contains the rows from "params/ranked_documents.json"
    When I send a POST request to "/api/v1/document/search?page=1&size=1" with body "input/by_ranked_author_name.json"
    Then the response status is 200
    And the response body contains the fields from "expected/responses/second_page.json" in order

  Scenario: A query whose words never appear together falls back to any of the words
    Given the table "document_content" contains the rows from "params/ranked_document_contents.json"
    And the table "document" contains the rows from "params/ranked_documents.json"
    When I send a POST request to "/api/v1/document/search" with body "input/by_any_word_query.json"
    Then the response status is 200
    And the response body contains the fields from "expected/responses/most_relevant_first.json" in order

  Scenario: Entries with all words are returned without the fallback
    Given the table "document_content" contains the rows from "params/ranked_document_contents.json"
    And the table "document" contains the rows from "params/ranked_documents.json"
    When I send a POST request to "/api/v1/document/search" with body "input/by_all_words_query.json"
    Then the response status is 200
    And the response body contains the fields from "expected/responses/all_words_match.json" in order

  Scenario: A query with an excluded word does not fall back
    Given the table "document_content" contains the rows from "params/ranked_document_contents.json"
    And the table "document" contains the rows from "params/ranked_documents.json"
    When I send a POST request to "/api/v1/document/search" with body "input/by_query_with_excluded_word.json"
    Then the response status is 200
    And the response body at "content" matches "expected/responses/no_documents.json"
