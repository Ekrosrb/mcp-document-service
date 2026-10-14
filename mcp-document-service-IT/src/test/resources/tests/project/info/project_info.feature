Feature: Projects info

  Background:
    Given the table "project" contains the rows from "params/projects.json"
    And the table "document_content" contains the rows from "params/document_contents.json"
    And the table "document" contains the rows from "params/documents.json"

  Scenario: Projects are listed with the number of their documents
    When I send a GET request to "/api/v1/project/info?size=1000"
    Then the response status is 200
    And the response body at "content" contains the element from "expected/responses/project_with_two_documents.json"
    And the response body at "content" contains the element from "expected/responses/project_with_one_document.json"

  Scenario: A project without documents is listed with zero documents
    When I send a GET request to "/api/v1/project/info?size=1000"
    Then the response status is 200
    And the response body at "content" contains the element from "expected/responses/project_without_documents.json"

  Scenario: Deleted documents are not counted
    Given I send a DELETE request to "/api/v1/document/${memoryId}"
    When I send a GET request to "/api/v1/project/info?size=1000"
    Then the response status is 200
    And the response body at "content" contains the element from "expected/responses/other_project_without_documents.json"
