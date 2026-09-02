package com.cloud.publishing.backend.repository.implementation;

import static com.cloud.publishing.common.constants.EducationConstants.ED_ID;
import static com.cloud.publishing.common.constants.EducationConstants.ED_LABEL;
import static com.cloud.publishing.common.constants.EducationConstants.ED_NAME;
import static com.cloud.publishing.common.constants.employee.EmployeeField.ADDRESS;
import static com.cloud.publishing.common.constants.employee.EmployeeField.BIRTH_YEAR;
import static com.cloud.publishing.common.constants.employee.EmployeeField.EMAIL;
import static com.cloud.publishing.common.constants.employee.EmployeeField.FIRST_NAME;
import static com.cloud.publishing.common.constants.employee.EmployeeField.GENDER;
import static com.cloud.publishing.common.constants.employee.EmployeeField.ID;
import static com.cloud.publishing.common.constants.employee.EmployeeField.IS_CHIEF_EDITOR;
import static com.cloud.publishing.common.constants.employee.EmployeeField.LAST_NAME;
import static com.cloud.publishing.common.constants.employee.EmployeeField.MIDDLE_NAME;
import static com.cloud.publishing.common.constants.employee.EmployeeField.PASSWORD;
import static com.cloud.publishing.common.constants.employee.EmployeeField.TYPE;
import static com.cloud.publishing.common.constants.employee.EmployeeMessage.EMPLOYEE_NOT_FOUND_MSG;
import static com.cloud.publishing.common.constants.employee.EmployeeMessage.FAILED_TO_ADD_MSG;
import static com.cloud.publishing.common.constants.employee.EmployeeMessage.FAILED_TO_ADD_NO_ID;
import static com.cloud.publishing.common.constants.employee.EmployeeMessage.FAILED_TO_ADD_NO_ROWS;
import static com.cloud.publishing.common.constants.employee.EmployeeMessage.FAILED_TO_CHECK_MESSAGE;
import static com.cloud.publishing.common.constants.employee.EmployeeMessage.FAILED_TO_DELETE_MSG;
import static com.cloud.publishing.common.constants.employee.EmployeeMessage.FAILED_TO_GET_MSG;
import static com.cloud.publishing.common.constants.employee.EmployeeMessage.FAILED_TO_LIST_MSG;
import static com.cloud.publishing.common.constants.employee.EmployeeMessage.FAILED_TO_RESET_CHIEF_EDITOR;
import static com.cloud.publishing.common.constants.employee.EmployeeMessage.FAILED_TO_UPDATE_WITH_ID_MSG;
import static com.cloud.publishing.common.constants.employee.EmployeeSQL.SQL_DELETE;
import static com.cloud.publishing.common.constants.employee.EmployeeSQL.SQL_EXIST;
import static com.cloud.publishing.common.constants.employee.EmployeeSQL.SQL_FIND_BY_EMAIL;
import static com.cloud.publishing.common.constants.employee.EmployeeSQL.SQL_FIND_BY_IDS;
import static com.cloud.publishing.common.constants.employee.EmployeeSQL.SQL_GET;
import static com.cloud.publishing.common.constants.employee.EmployeeSQL.SQL_INSERT;
import static com.cloud.publishing.common.constants.employee.EmployeeSQL.SQL_LIST;
import static com.cloud.publishing.common.constants.employee.EmployeeSQL.SQL_RESET_CE;
import static com.cloud.publishing.common.constants.employee.EmployeeSQL.SQL_UPDATE_WITH_PASSWORD;
import static com.cloud.publishing.common.constants.employee.EmployeeSQL.SQL_UPDATE_WITHOUT_PASSWORD;
import static com.cloud.publishing.common.constants.employee.EmployeeSQL.SQL_FIND_CO_AUTHORS;

import com.cloud.publishing.backend.repository.EmployeeRepository;
import com.cloud.publishing.common.dto.response.EmployeeShort;
import com.cloud.publishing.backend.exception.ObjectNotFoundException;
import com.cloud.publishing.model.employee.Education;
import com.cloud.publishing.model.employee.Employee;
import com.cloud.publishing.model.employee.Gender;
import com.cloud.publishing.model.employee.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.springframework.stereotype.Repository;

@Repository
public class EmployeeRepositoryImpl extends BaseRepository implements EmployeeRepository {
    public EmployeeRepositoryImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Employee add(Employee employee) {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        SQL_INSERT,
                        Statement.RETURN_GENERATED_KEYS
                )) {
            statement.setString(1, employee.firstName());
            statement.setString(2, employee.lastName());
            statement.setString(3, employee.middleName());
            statement.setString(4, employee.email());
            statement.setString(5, employee.password());
            statement.setString(6, employee.gender().name());
            statement.setInt(7, employee.birthYear());
            statement.setString(8, employee.address());
            statement.setInt(9, employee.education().id());
            statement.setString(10, employee.type().toString());
            statement.setBoolean(11, employee.chiefEditor());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException(FAILED_TO_ADD_NO_ROWS);
            }
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Integer id = generatedKeys.getInt(1);
                    return new Employee(
                            id,
                            employee.firstName(),
                            employee.lastName(),
                            employee.middleName(),
                            employee.email(),
                            employee.password(),
                            employee.gender(),
                            employee.birthYear(),
                            employee.address(),
                            employee.education(),
                            employee.type(),
                            employee.chiefEditor()
                    );
                } else {
                    throw new SQLException(FAILED_TO_ADD_NO_ID);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(FAILED_TO_ADD_MSG, e);
        }
    }

    @Override
    public void update(Employee employee) {
        boolean updatePassword =
                employee.password() != null && !employee.password().isEmpty();
        String sql;
        if (updatePassword) {
            sql = SQL_UPDATE_WITH_PASSWORD;
        } else {
            sql = SQL_UPDATE_WITHOUT_PASSWORD;
        }
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, employee.firstName());
            statement.setString(2, employee.lastName());
            statement.setString(3, employee.middleName());
            statement.setString(4, employee.email());
            if (updatePassword) {
                statement.setString(5, employee.password());
                statement.setString(6, employee.gender().name());
                statement.setInt(7, employee.birthYear());
                statement.setString(8, employee.address());
                statement.setInt(9, employee.education().id());
                statement.setString(10, employee.type().name());
                statement.setBoolean(11, employee.chiefEditor());
                statement.setInt(12, employee.id());
            } else {
                statement.setString(5, employee.gender().name());
                statement.setInt(6, employee.birthYear());
                statement.setString(7, employee.address());
                statement.setInt(8, employee.education().id());
                statement.setString(9, employee.type().name());
                statement.setBoolean(10, employee.chiefEditor());
                statement.setInt(11, employee.id());
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(FAILED_TO_UPDATE_WITH_ID_MSG + employee.id(), e);
        }
    }

    @Override
    public Employee get(Integer id) {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(SQL_GET)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSetToEmployee(resultSet);
                } else {
                    throw new ObjectNotFoundException(EMPLOYEE_NOT_FOUND_MSG);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(FAILED_TO_GET_MSG, e);
        }
    }

    @Override
    public List<Employee> getAll() {
        List<Employee> employees = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(SQL_LIST)) {
            while (resultSet.next()) {
                employees.add(resultSetToEmployee(resultSet));
            }
            return employees;
        } catch (SQLException e) {
            throw new RuntimeException(FAILED_TO_LIST_MSG, e);
        }
    }

    @Override
    public List<EmployeeShort> findByIds(Set<Integer> ids) {
        return findById(
                ids,
                SQL_FIND_BY_IDS,
                this::resultSetToShortEmployee,
                FAILED_TO_GET_MSG
        );
    }

    @Override
    public void delete(Integer id) {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(FAILED_TO_DELETE_MSG, e);
        }
    }

    @Override
    public boolean exists(Integer id) {
        return super.exists(id, SQL_EXIST, FAILED_TO_CHECK_MESSAGE);
    }

    @Override
    public void resetChiefEditor() {
        try (Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement()) {
            statement.executeUpdate(SQL_RESET_CE);
        } catch (SQLException e) {
            throw new RuntimeException(FAILED_TO_RESET_CHIEF_EDITOR, e);
        }
    }

    @Override
    public Optional<Employee> findByEmail(String email) {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(SQL_FIND_BY_EMAIL)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(resultSetToEmployeeForAuth(resultSet));
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(FAILED_TO_GET_MSG, e);
        }
    }

    @Override
    public List<EmployeeShort> findCoAuthors(Set<Integer> journalistIds, int currentUserId) {
        if (journalistIds.isEmpty()) {
            return List.of();
        }
        String placeholders = journalistIds.stream()
                .map(id -> "?")
                .collect(Collectors.joining(", "));
        String sql = SQL_FIND_CO_AUTHORS.formatted(placeholders);
        List<EmployeeShort> result = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, currentUserId);
            int parameterIndex = 2;
            for (Integer journalistId : journalistIds) {
                statement.setInt(parameterIndex++, journalistId);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(resultSetToShortEmployee(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка получения соавторов", e);
        }
        return result;
    }

    private EmployeeShort resultSetToShortEmployee(ResultSet resultSet) throws SQLException {
        return new EmployeeShort(
                resultSet.getInt(ID),
                resultSet.getString(FIRST_NAME),
                resultSet.getString(LAST_NAME),
                resultSet.getString(MIDDLE_NAME)
        );
    }

    private Employee resultSetToEmployee(ResultSet resultSet) throws SQLException {
        return new Employee(
                resultSet.getInt(ID),
                resultSet.getString(FIRST_NAME),
                resultSet.getString(LAST_NAME),
                resultSet.getString(MIDDLE_NAME),
                resultSet.getString(EMAIL),
                resultSet.getString(PASSWORD),
                Gender.valueOf(resultSet.getString(GENDER).toUpperCase()),
                resultSet.getInt(BIRTH_YEAR),
                resultSet.getString(ADDRESS),
                new Education(
                        resultSet.getInt(ED_ID),
                        resultSet.getString(ED_NAME),
                        resultSet.getString(ED_LABEL)
                ),
                Type.valueOf(resultSet.getString(TYPE).toUpperCase()),
                resultSet.getBoolean(IS_CHIEF_EDITOR)
        );
    }

    private Employee resultSetToEmployeeForAuth(ResultSet resultSet) throws SQLException {
        return new Employee(
                resultSet.getInt(ID),
                null,
                null,
                null,
                resultSet.getString(EMAIL),
                resultSet.getString(PASSWORD),
                null,
                0,
                null,
                null,
                Type.valueOf(resultSet.getString(TYPE).toUpperCase()),
                resultSet.getBoolean(IS_CHIEF_EDITOR)
        );
    }
}