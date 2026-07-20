Feature: Login employee

  Scenario: Successful login returns authentication token
    When The employee logs in with email "test1@gmail.com" and password "password1"
    Then The response status code should be 200
    And The response should contain a valid tokens

  Scenario Outline: Failed Login with invalid credentials
    When The employee logs in with email "<email>" and password "<password>"
    Then The response error details should be match "<error>", status <statusValue> and message "<message>"

    Examples:
      | email           | password      | error        | statusValue | message                   |
      | wrong@gmail.com | password1     | Unauthorized | 401         | Неверный логин или пароль |
      | test1@gmail.com | wrongpassword | Unauthorized | 401         | Неверный логин или пароль |