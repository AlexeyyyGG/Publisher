package com.cloud.publishing.itest.steps;

import com.cloud.publishing.itest.config.RestUtils;
import com.cloud.publishing.itest.constants.ContextKeys;
import com.cloud.publishing.itest.context.TestContext;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;

public class ResponseSteps {
    @Then("The response status code should be {int}")
    public void verifyStatusCode(int expectedStatusCode) {
        Response response = TestContext.getContext().get(ContextKeys.RESPONSE, Response.class);
        RestUtils.verifyStatus(response, expectedStatusCode);
    }
}