package com.cloud.publishing.itest.hooks;

import com.cloud.publishing.itest.config.TestConfig;
import io.cucumber.java.After;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBaseHooks {
    @After
    public void cleanUp() {
        try (Connection connection = DriverManager.getConnection(TestConfig.getDbUrl(),
                TestConfig.getDbUser(), TestConfig.getDbPassword());
                Statement statement = connection.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("TRUNCATE TABLE employees");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка очистки таблицы", e);
        }
    }
}