package com.cloud.publishing.itest.steps;

import static com.cloud.publishing.common.constants.employee.EmployeeSQL.SQL_INSERT;

import com.cloud.publishing.itest.config.RestUtils;
import com.cloud.publishing.itest.config.TestConfig;
import com.cloud.publishing.itest.context.TestContext;
import com.cloud.publishing.itest.constants.ContextKeys;
import com.cloud.publishing.common.constants.Urls;
import com.cloud.publishing.common.dto.request.LoginRequest;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.assertj.core.api.Assertions;
import org.springframework.security.crypto.bcrypt.BCrypt;

public class AuthSteps {
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String REFRESH_TOKEN = "refreshToken";

    @Given("Create employee with email {string} and password {string}")
    public void employeeExists(String email, String password) {
        try (Connection connection = DriverManager.getConnection(
                TestConfig.getDbUrl(),
                TestConfig.getDbUser(),
                TestConfig.getDbPassword())
        ) {
            try (PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
                statement.setString(1, "Петр");
                statement.setString(2, "Иванов");
                statement.setString(3, "Иванович");
                statement.setString(4, email);
                statement.setString(5, BCrypt.hashpw(password, BCrypt.gensalt()));
                statement.setString(6, "MALE");
                statement.setInt(7, 1985);
                statement.setString(8, "test");
                statement.setInt(9, 6);
                statement.setString(10, "EDITOR");
                statement.setBoolean(11, true);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create employee for test", e);
        }
    }

    @When("The employee logs in with email {string} and password {string}")
    public void login(String email, String password) {
        LoginRequest request = new LoginRequest(email, password);
        Response response = RestUtils.post(Urls.AUTH + Urls.LOGIN, request, null);
        var context = TestContext.getContext();
        context.put(ContextKeys.RESPONSE, response);
        String token = response.jsonPath().getString(ACCESS_TOKEN);
        if (token != null) {
            context.put(ContextKeys.TOKEN, token);
        }
    }

    @Then("The response should contain a valid tokens")
    public void verifyTokensExist() {
        Response response = TestContext.getContext().get(ContextKeys.RESPONSE, Response.class);
        String actualAccessToken = response.jsonPath().getString(ACCESS_TOKEN);
        String actualRefreshToken = response.jsonPath().getString(REFRESH_TOKEN);
        Assertions.assertThat(actualAccessToken)
                .as("Access token is missing or empty")
                .isNotEmpty();
        Assertions.assertThat(actualRefreshToken)
                .as("Refresh token is missing or empty")
                .isNotEmpty();
    }

    @Then("The response error details should be match {string}, status {int} and message {string}")
    public void verifyErrorDetails(
            String expectedError,
            int expectedStatus,
            String expectedMessage
    ) {
        Response response = TestContext.getContext().get(ContextKeys.RESPONSE, Response.class);
        int actualStatus = response.jsonPath().getInt("status");
        String actualError = response.jsonPath().getString("error");
        String actualMessage = response.jsonPath().getString("message");
        Assertions.assertThat(actualStatus).isEqualTo(expectedStatus);
        Assertions.assertThat(actualError).isEqualTo(expectedError);
        Assertions.assertThat(actualMessage).isEqualTo(expectedMessage);
    }
}