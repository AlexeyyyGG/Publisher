package com.cloud.publishing.itest.steps;

import static com.cloud.publishing.common.constants.employee.EmployeeSQL.SQL_INSERT;

import com.cloud.publishing.itest.config.RestUtils;
import com.cloud.publishing.itest.config.TestConfig;
import com.cloud.publishing.itest.context.TestContext;
import com.cloud.publishing.itest.constants.ContextKeys;
import com.cloud.publishing.itest.dto.EmployeeListItem;
import com.cloud.publishing.common.constants.Urls;
import com.cloud.publishing.common.dto.response.EmployeeResponse;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.springframework.security.crypto.bcrypt.BCrypt;

public class EmployeesSteps {
    private static final String INVALID_TOKEN = "invalidToken";

    @Given("Create employees in the system:")
    public void theFollowingEmployeesExist(List<Map<String, String>> employeesTable) {
        try (Connection conn = DriverManager.getConnection(
                TestConfig.getDbUrl(),
                TestConfig.getDbUser(),
                TestConfig.getDbPassword());
                PreparedStatement statement = conn.prepareStatement(SQL_INSERT)) {
            for (Map<String, String> row : employeesTable) {
                statement.setString(1, row.get("firstName"));
                statement.setString(2, row.get("lastName"));
                statement.setString(3, row.get("middleName"));
                statement.setString(4, row.get("email"));
                statement.setString(5, BCrypt.hashpw(row.get("password"), BCrypt.gensalt()));
                statement.setString(6, row.get("gender").toLowerCase());
                statement.setInt(7, Integer.parseInt(row.get("birthYear")));
                statement.setString(8, row.get("address"));
                statement.setInt(9, Integer.parseInt(row.get("educationId")));
                statement.setString(10, row.get("type"));
                statement.setBoolean(11, Boolean.parseBoolean(row.get("chiefEditor")));
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось заполнить таблицу сотрудников", e);
        }
    }

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