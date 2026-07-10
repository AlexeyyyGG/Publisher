package com.cloud.publishing.backend.repository.implementation;

import static com.cloud.publishing.common.constants.publication.PublicationField.CATEGORY_ID;
import static com.cloud.publishing.common.constants.publication.PublicationField.EMPLOYEE_ID;
import static com.cloud.publishing.common.constants.publication.PublicationField.ID;
import static com.cloud.publishing.common.constants.publication.PublicationField.NAME;
import static com.cloud.publishing.common.constants.publication.PublicationField.PUBLICATION_EDITORS;
import static com.cloud.publishing.common.constants.publication.PublicationField.PUBLICATION_ID;
import static com.cloud.publishing.common.constants.publication.PublicationField.PUBLICATION_JOURNALISTS;
import static com.cloud.publishing.common.constants.publication.PublicationField.PUBLICATION_TYPE;
import static com.cloud.publishing.common.constants.publication.PublicationField.THEME;
import static com.cloud.publishing.common.constants.publication.PublicationMessage.FAILED_CREATING_MSG;
import static com.cloud.publishing.common.constants.publication.PublicationMessage.FAILED_TO_ADD_MSG;
import static com.cloud.publishing.common.constants.publication.PublicationMessage.FAILED_TO_CHECK_MESSAGE;
import static com.cloud.publishing.common.constants.publication.PublicationMessage.FAILED_TO_DELETE_MSG;
import static com.cloud.publishing.common.constants.publication.PublicationMessage.FAILED_TO_GET_CATEGORIES;
import static com.cloud.publishing.common.constants.publication.PublicationMessage.FAILED_TO_GET_EMP_ID;
import static com.cloud.publishing.common.constants.publication.PublicationMessage.FAILED_TO_GET_MSG;
import static com.cloud.publishing.common.constants.publication.PublicationMessage.FAILED_TO_UPDATE;
import static com.cloud.publishing.common.constants.publication.PublicationMessage.PUBLICATION_NOT_FOUND_MSG;
import static com.cloud.publishing.common.constants.publication.PublicationSQL.DELETE_CATEGORY;
import static com.cloud.publishing.common.constants.publication.PublicationSQL.DELETE_EDITOR;
import static com.cloud.publishing.common.constants.publication.PublicationSQL.DELETE_JOURNALIST;
import static com.cloud.publishing.common.constants.publication.PublicationSQL.INSERT_EDITOR;
import static com.cloud.publishing.common.constants.publication.PublicationSQL.INSERT_JOURNALIST;
import static com.cloud.publishing.common.constants.publication.PublicationSQL.INSERT_PUB_CATEGORY;
import static com.cloud.publishing.common.constants.publication.PublicationSQL.SQL_DELETE;
import static com.cloud.publishing.common.constants.publication.PublicationSQL.SQL_EXIST;
import static com.cloud.publishing.common.constants.publication.PublicationSQL.SQL_FIND_BY_IDS;
import static com.cloud.publishing.common.constants.publication.PublicationSQL.SQL_GET_ALL_CATEGORIES;
import static com.cloud.publishing.common.constants.publication.PublicationSQL.SQL_GET_ALL_EDITORS;
import static com.cloud.publishing.common.constants.publication.PublicationSQL.SQL_GET_ALL_JOURNALISTS;
import static com.cloud.publishing.common.constants.publication.PublicationSQL.SQL_GET_BY_ID_PUBLICATION;
import static com.cloud.publishing.common.constants.publication.PublicationSQL.SQL_GET_CATEGORIES_BY_PUBLICATION;
import static com.cloud.publishing.common.constants.publication.PublicationSQL.SQL_GET_EMPLOYEE;
import static com.cloud.publishing.common.constants.publication.PublicationSQL.SQL_GET_PUBLICATION;
import static com.cloud.publishing.common.constants.publication.PublicationSQL.SQL_INSERT_PUBLICATION;
import static com.cloud.publishing.common.constants.publication.PublicationSQL.UPDATE_PUBLICATION;

import com.cloud.publishing.backend.repository.PublicationRepository;
import com.cloud.publishing.model.publication.Publication;
import com.cloud.publishing.model.publication.PublicationType;
import javax.sql.DataSource;
import com.cloud.publishing.backend.exception.ObjectNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public class PublicationRepositoryImpl extends BaseRepository implements PublicationRepository {
    public PublicationRepositoryImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Publication add(Publication publication) {
        return doTransactional((connection) -> {
            int publicationId = insertPublication(connection, publication);
            try (PreparedStatement stmtCategory = prepareInsertConnections(
                    connection,
                    INSERT_PUB_CATEGORY,
                    publicationId,
                    publication.categories()
            );
                    PreparedStatement stmtJournalist = prepareInsertConnections(
                            connection,
                            INSERT_JOURNALIST,
                            publicationId,
                            publication.journalists()
                    );
                    PreparedStatement stmtEditor = prepareInsertConnections(
                            connection,
                            INSERT_EDITOR,
                            publicationId,
                            publication.editors()
                    )
            ) {
                stmtCategory.executeBatch();
                stmtJournalist.executeBatch();
                stmtEditor.executeBatch();
            }
            return new Publication(
                    publicationId,
                    publication.name(),
                    publication.publicationType(),
                    publication.theme(),
                    publication.categories(),
                    publication.journalists(),
                    publication.editors()
            );
        }, FAILED_TO_ADD_MSG);
    }

    @Override
    public Publication get(Integer id) {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        SQL_GET_BY_ID_PUBLICATION)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String name = resultSet.getString(NAME);
                    PublicationType type = PublicationType.valueOf(
                            resultSet.getString(PUBLICATION_TYPE).toUpperCase());
                    String theme = resultSet.getString(THEME);
                    Set<Integer> categories = getCategories(connection, id);
                    Set<Integer> journalists = getEmployeeIdsByPublicationId(
                            connection,
                            id,
                            PUBLICATION_JOURNALISTS
                    );
                    Set<Integer> editors = getEmployeeIdsByPublicationId(
                            connection,
                            id,
                            PUBLICATION_EDITORS
                    );
                    return new Publication(
                            id,
                            name,
                            type,
                            theme,
                            categories,
                            journalists,
                            editors
                    );
                } else {
                    throw new ObjectNotFoundException(PUBLICATION_NOT_FOUND_MSG);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(FAILED_TO_GET_MSG, e);
        }
    }

    @Override
    public void update(Publication publication) {
        doTransactional((connection) -> {
            int publicationId = updatePublication(connection, publication);
            try (PreparedStatement stmtDeleteCategory = prepareDeleteConnections(
                    connection,
                    DELETE_CATEGORY,
                    publicationId
            );
                    PreparedStatement stmtDeleteJournalist = prepareDeleteConnections(
                            connection,
                            DELETE_JOURNALIST,
                            publicationId
                    );
                    PreparedStatement stmtDeleteEditor = prepareDeleteConnections(
                            connection,
                            DELETE_EDITOR,
                            publicationId
                    );
                    PreparedStatement stmtInsertCategory = prepareInsertConnections(
                            connection,
                            INSERT_PUB_CATEGORY,
                            publicationId,
                            publication.categories()
                    );
                    PreparedStatement stmtInsertJournalist = prepareInsertConnections(
                            connection,
                            INSERT_JOURNALIST,
                            publicationId,
                            publication.journalists()
                    );
                    PreparedStatement stmtInsertEditor = prepareInsertConnections(
                            connection,
                            INSERT_EDITOR,
                            publicationId,
                            publication.editors()
                    )
            ) {
                stmtDeleteCategory.executeUpdate();
                stmtDeleteJournalist.executeUpdate();
                stmtDeleteEditor.executeUpdate();
                stmtInsertCategory.executeBatch();
                stmtInsertJournalist.executeBatch();
                stmtInsertEditor.executeBatch();
            }
            return null;
        }, FAILED_TO_UPDATE);
    }

    @Override
    public void delete(Integer publicationId) {
        doTransactional((connection) -> {
            try (PreparedStatement stmtDeleteCategory = prepareDeleteConnections(
                    connection,
                    DELETE_CATEGORY,
                    publicationId
            );
                    PreparedStatement stmtDeleteJournalist = prepareDeleteConnections(
                            connection,
                            DELETE_JOURNALIST,
                            publicationId
                    );
                    PreparedStatement stmtDeleteEditor = prepareDeleteConnections(
                            connection,
                            DELETE_EDITOR,
                            publicationId
                    );
                    PreparedStatement stmtDeletePublication = prepareDeleteConnections(
                            connection,
                            SQL_DELETE,
                            publicationId
                    )
            ) {
                stmtDeleteCategory.executeUpdate();
                stmtDeleteJournalist.executeUpdate();
                stmtDeleteEditor.executeUpdate();
                stmtDeletePublication.executeUpdate();
            }
            return null;
        }, FAILED_TO_DELETE_MSG);
    }

    @Override
    public boolean exists(Integer id) {
        return super.exists(id, SQL_EXIST, FAILED_TO_CHECK_MESSAGE);
    }

    @Override
    public List<Publication> getAll() {
        try (Connection connection = dataSource.getConnection()) {
            List<Publication> publications = loadPublications(connection);
            Map<Integer, Set<Integer>> categories = loadRelations(
                    connection,
                    SQL_GET_ALL_CATEGORIES,
                    PUBLICATION_ID,
                    CATEGORY_ID
            );
            Map<Integer, Set<Integer>> journalists = loadRelations(
                    connection,
                    SQL_GET_ALL_JOURNALISTS,
                    PUBLICATION_ID,
                    EMPLOYEE_ID
            );
            Map<Integer, Set<Integer>> editors = loadRelations(
                    connection,
                    SQL_GET_ALL_EDITORS,
                    PUBLICATION_ID,
                    EMPLOYEE_ID
            );
            return publications.stream()
                    .map(publication -> new Publication(
                            publication.id(),
                            publication.name(),
                            publication.publicationType(),
                            publication.theme(),
                            categories.getOrDefault(publication.id(), Set.of()),
                            journalists.getOrDefault(publication.id(), Set.of()),
                            editors.getOrDefault(publication.id(), Set.of())
                    ))
                    .toList();
        } catch (SQLException e) {
            throw new RuntimeException(FAILED_TO_GET_MSG, e);
        }
    }

    @Override
    public List<Publication> getByIds(Set<Integer> ids) {
        return findById(
                ids,
                SQL_FIND_BY_IDS,
                resultSet -> new Publication(
                        resultSet.getInt(ID),
                        resultSet.getString(NAME),
                        PublicationType.valueOf(
                                resultSet.getString(PUBLICATION_TYPE).toUpperCase()),
                        resultSet.getString(THEME),
                        new HashSet<>(),
                        new HashSet<>(),
                        new HashSet<>()
                ),
                FAILED_TO_GET_MSG
        );
    }

    private List<Publication> loadPublications(Connection connection) throws SQLException {
        List<Publication> publications = new ArrayList<>();
        try (Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(SQL_GET_PUBLICATION)) {
            while (resultSet.next()) {
                publications.add(new Publication(
                        resultSet.getInt(ID),
                        resultSet.getString(NAME),
                        PublicationType.valueOf(
                                resultSet.getString(PUBLICATION_TYPE).toUpperCase()),
                        resultSet.getString(THEME),
                        new HashSet<>(),
                        new HashSet<>(),
                        new HashSet<>()
                ));
            }
        }
        return publications;
    }

    private PreparedStatement prepareDeleteConnections(
            Connection connection,
            String query,
            int publicationId
    ) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, publicationId);
        return statement;
    }

    private PreparedStatement prepareInsertConnections(
            Connection connection,
            String query,
            int publicationId,
            Set<Integer> ids
    ) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(query);
        for (Integer id : ids) {
            statement.setInt(1, publicationId);
            statement.setInt(2, id);
            statement.addBatch();
        }
        return statement;
    }

    private Set<Integer> getCategories(Connection connection, int publicationId) {
        Set<Integer> categories = new HashSet<>();
        try (PreparedStatement statement = connection.prepareStatement(
                SQL_GET_CATEGORIES_BY_PUBLICATION)) {
            statement.setInt(1, publicationId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    categories.add(resultSet.getInt(ID));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(FAILED_TO_GET_CATEGORIES, e);
        }
        return categories;
    }

    private Set<Integer> getEmployeeIdsByPublicationId(
            Connection connection,
            int publicationId,
            String tableName
    ) {
        Set<Integer> employees = new HashSet<>();
        String sql = String.format(SQL_GET_EMPLOYEE, tableName);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, publicationId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    employees.add(resultSet.getInt(ID));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(FAILED_TO_GET_EMP_ID + tableName, e);
        }
        return employees;
    }

    private Integer updatePublication(
            Connection connection,
            Publication publication
    ) throws SQLException {
        try (PreparedStatement updateStmt = connection.prepareStatement(UPDATE_PUBLICATION)) {
            updateStmt.setString(1, publication.name());
            updateStmt.setString(2, publication.publicationType().toString());
            updateStmt.setString(3, publication.theme());
            updateStmt.setInt(4, publication.id());
            updateStmt.executeUpdate();
        }
        return publication.id();
    }

    private Integer insertPublication(
            Connection connection,
            Publication publication
    ) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                SQL_INSERT_PUBLICATION,
                Statement.RETURN_GENERATED_KEYS
        )) {
            statement.setString(1, publication.name());
            statement.setString(2, publication.publicationType().toString());
            statement.setString(3, publication.theme());
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new RuntimeException(FAILED_CREATING_MSG);
                }
            }
        }
    }
}