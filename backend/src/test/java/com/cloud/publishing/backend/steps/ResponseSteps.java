package com.cloud.publishing.backend.steps;

import com.cloud.publishing.backend.config.RestUtils;
import com.cloud.publishing.backend.constants.ContextKeys;
import com.cloud.publishing.backend.context.TestContext;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;

public class ResponseSteps {
    @Then("The response status code should be {int}")
    public void verifyStatusCode(int expectedStatusCode) {
        Response response = TestContext.getContext().get(ContextKeys.RESPONSE, Response.class);
        RestUtils.verifyStatus(response, expectedStatusCode);
    }
}