package com.cloud.publishing.backend.steps;

import com.cloud.publishing.backend.config.RestUtils;
import com.cloud.publishing.backend.context.TestContext;
import com.cloud.publishing.backend.constants.ContextKeys;
import com.cloud.publishing.common.constants.Urls;
import com.cloud.publishing.common.dto.request.LoginRequest;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;

public class AuthSteps {
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String REFRESH_TOKEN = "refreshToken";

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