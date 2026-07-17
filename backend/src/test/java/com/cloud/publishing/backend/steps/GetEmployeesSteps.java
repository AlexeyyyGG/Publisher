package com.cloud.publishing.backend.steps;

import com.cloud.publishing.backend.config.RestUtils;
import com.cloud.publishing.backend.context.TestContext;
import com.cloud.publishing.backend.constants.ContextKeys;
import com.cloud.publishing.backend.dto.EmployeeListItem;
import com.cloud.publishing.common.constants.Urls;
import com.cloud.publishing.common.dto.response.EmployeeResponse;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import java.util.List;
import org.assertj.core.api.Assertions;

public class GetEmployeesSteps {
    private static final String INVALID_TOKEN = "invalidToken";

    @When("The user requests the list of all employees")
    public void getAllEmployees() {
        String token = TestContext.getContext().get(ContextKeys.TOKEN, String.class);
        Response response = RestUtils.get(Urls.EMPLOYEES, token);
        TestContext.getContext().put(ContextKeys.RESPONSE, response);
    }

    @When("The user requests the list of all employees without authentication")
    public void getAllEmployeesWithoutAuth() {
        Response response = RestUtils.get(Urls.EMPLOYEES, null);
        TestContext.getContext().put(ContextKeys.RESPONSE, response);
    }

    @Given("The user has an invalid access token")
    public void setInvalidToken() {
        TestContext.getContext().put(ContextKeys.TOKEN, INVALID_TOKEN);
    }

    @Then("The response should contain exactly the following employees:")
    public void verifyEmployeeList(DataTable dataTable) {
        Response response = TestContext.getContext().get(ContextKeys.RESPONSE, Response.class);
        RestUtils.verifyStatus(response, 200);
        List<EmployeeListItem> expected = dataTable.entries().stream()
                .map(row -> new EmployeeListItem(
                        row.get("firstName"),
                        row.get("lastName"),
                        row.get("email"),
                        Boolean.parseBoolean(row.get("chiefEditor"))
                ))
                .toList();
        List<EmployeeListItem> actual = response.then()
                .extract()
                .jsonPath()
                .getList("", EmployeeResponse.class)
                .stream()
                .map(employee -> new EmployeeListItem(
                        employee.firstName(),
                        employee.lastName(),
                        employee.email(),
                        employee.chiefEditor()
                ))
                .toList();
        Assertions.assertThat(actual)
                .containsExactlyInAnyOrderElementsOf(expected);
    }
}