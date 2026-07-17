Feature: Get employees

  Scenario Outline: Authenticated employee get the employee list
    When The employee logs in with email "<email>" and password "<password>"
    And The user requests the list of all employees
    Then The response should contain exactly the following employees:
      | firstName | lastName | email           | chiefEditor |
      | Петр      | Иванов   | test1@gmail.com | true        |
      | Иван      | Петров   | test2@gmail.com | false       |

    Examples:
      | email           | password  |
      | test1@gmail.com | password1 |
      | test2@gmail.com | password2 |

  Scenario: Get employees without authentication
    When The user requests the list of all employees without authentication
    Then The response status code should be 401

  Scenario: Get employees with invalid token
    Given The user has an invalid access token
    When The user requests the list of all employees
    Then The response status code should be 401