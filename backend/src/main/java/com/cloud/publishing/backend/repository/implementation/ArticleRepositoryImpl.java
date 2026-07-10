package com.cloud.publishing.backend.repository.implementation;

import static com.cloud.publishing.common.constants.article.ArticleField.ARTICLE_ID;
import static com.cloud.publishing.common.constants.article.ArticleField.AUTHOR_ID;
import static com.cloud.publishing.common.constants.article.ArticleField.CATEGORY_ID;
import static com.cloud.publishing.common.constants.article.ArticleField.CONTENT;
import static com.cloud.publishing.common.constants.article.ArticleField.EMPLOYEE_ID;
import static com.cloud.publishing.common.constants.article.ArticleField.ID;
import static com.cloud.publishing.common.constants.article.ArticleField.NAME;
import static com.cloud.publishing.common.constants.article.ArticleField.PUBLICATION_ID;
import static com.cloud.publishing.common.constants.article.ArticleMessage.ARTICLE_NOT_FOUND;
import static com.cloud.publishing.common.constants.article.ArticleMessage.FAILED_CREATING;
import static com.cloud.publishing.common.constants.article.ArticleMessage.FAILED_TO_ADD;
import static com.cloud.publishing.common.constants.article.ArticleMessage.FAILED_TO_CHECK;
import static com.cloud.publishing.common.constants.article.ArticleMessage.FAILED_TO_DELETE;
import static com.cloud.publishing.common.constants.article.ArticleMessage.FAILED_TO_GET;
import static com.cloud.publishing.common.constants.article.ArticleMessage.FAILED_TO_GET_CO_AUTHOR;
import static com.cloud.publishing.common.constants.article.ArticleMessage.FAILED_TO_UPDATE_ARTICLE;
import static com.cloud.publishing.common.constants.article.ArticleSQL.DELETE_CO_AUTHORS;
import static com.cloud.publishing.common.constants.article.ArticleSQL.INSERT_CO_AUTHOR;
import static com.cloud.publishing.common.constants.article.ArticleSQL.SQL_DELETE;
import static com.cloud.publishing.common.constants.article.ArticleSQL.SQL_EXIST;
import static com.cloud.publishing.common.constants.article.ArticleSQL.SQL_GET_ALL_ARTICLES;
import static com.cloud.publishing.common.constants.article.ArticleSQL.SQL_GET_ARTICLE_BY_ID;
import static com.cloud.publishing.common.constants.article.ArticleSQL.SQL_GET_BY_AUTHOR_ID;
import static com.cloud.publishing.common.constants.article.ArticleSQL.SQL_GET_CO_AUTHOR;
import static com.cloud.publishing.common.constants.article.ArticleSQL.SQL_GET_CO_AUTHORS;
import static com.cloud.publishing.common.constants.article.ArticleSQL.SQL_INSERT;
import static com.cloud.publishing.common.constants.article.ArticleSQL.UPDATE_ARTICLE;

import com.cloud.publishing.backend.exception.ObjectNotFoundException;
import com.cloud.publishing.backend.repository.ArticleRepository;
import com.cloud.publishing.model.article.Article;
import com.cloud.publishing.model.article.ArticleShort;
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
import javax.sql.DataSource;
import org.springframework.stereotype.Repository;

@Repository
public class ArticleRepositoryImpl extends BaseRepository implements ArticleRepository {
    public ArticleRepositoryImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Article add(Article article) {
        return doTransactional((connection) -> {
            int articleId = insertArticle(connection, article);
            if (article.coAuthorsIds() != null && !article.coAuthorsIds().isEmpty()) {
                try (PreparedStatement statement = prepareInsertCoAuthors(
                        connection,
                        articleId,
                        article.coAuthorsIds()
                )) {
                    statement.executeBatch();
                }
            }
            return new Article(
                    articleId,
                    article.publicationId(),
                    article.categoryId(),
                    article.name(),
                    article.content(),
                    article.authorId(),
                    article.coAuthorsIds()
            );
        }, FAILED_TO_ADD);
    }

    @Override
    public Article get(Integer id) {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(SQL_GET_ARTICLE_BY_ID)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Integer publicationId = resultSet.getInt(PUBLICATION_ID);
                    Integer categoryId = resultSet.getInt(CATEGORY_ID);
                    String name = resultSet.getString(NAME);
                    String content = resultSet.getString(CONTENT);
                    Integer authorId = resultSet.getInt(AUTHOR_ID);
                    Set<Integer> coAuthorsIds = getCoAuthorsIdsByArticleId(connection, id);
                    return new Article(
                            id,
                            publicationId,
                            categoryId,
                            name,
                            content,
                            authorId,
                            coAuthorsIds
                    );
                } else {
                    throw new ObjectNotFoundException(ARTICLE_NOT_FOUND);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(FAILED_TO_GET, e);
        }
    }

    @Override
    public List<ArticleShort> getAll() {
        List<ArticleShort> articles = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(SQL_GET_ALL_ARTICLES)) {
            Map<Integer, Set<Integer>> allCoAuthors = loadCoAuthorsRelations(connection);
            while (resultSet.next()) {
                int id = resultSet.getInt(ID);
                int publicationId = resultSet.getInt(PUBLICATION_ID);
                int categoryId = resultSet.getInt(CATEGORY_ID);
                String name = resultSet.getString(NAME);
                int authorId = resultSet.getInt(AUTHOR_ID);
                Set<Integer> coAuthorsIds = allCoAuthors.getOrDefault(id, Set.of());
                articles.add(new ArticleShort(
                        id,
                        publicationId,
                        categoryId,
                        name,
                        authorId,
                        coAuthorsIds
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(FAILED_TO_GET, e);
        }
        return articles;
    }

    @Override
    public List<ArticleShort> getByAuthorId(Integer authorId) {
        List<ArticleShort> articles = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(SQL_GET_BY_AUTHOR_ID)) {
            statement.setInt(1, authorId);
            try (ResultSet resultSet = statement.executeQuery()) {
                Map<Integer, Set<Integer>> allCoAuthors = loadCoAuthorsRelations(connection);
                while (resultSet.next()) {
                    int id = resultSet.getInt(ID);
                    int publicationId = resultSet.getInt(PUBLICATION_ID);
                    int categoryId = resultSet.getInt(CATEGORY_ID);
                    String name = resultSet.getString(NAME);
                    Set<Integer> coAuthorsIds = allCoAuthors.getOrDefault(id, Set.of());
                    articles.add(new ArticleShort(
                            id,
                            publicationId,
                            categoryId,
                            name,
                            authorId,
                            coAuthorsIds
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(FAILED_TO_GET, e);
        }
        return articles;
    }

    @Override
    public boolean exists(Integer id) {
        return super.exists(id, SQL_EXIST, FAILED_TO_CHECK);
    }

    @Override
    public void update(Article article) {
        doTransactional((connection) -> {
            int articleId = updateArticle(connection, article);
            try (PreparedStatement statement = connection.prepareStatement(DELETE_CO_AUTHORS)) {
                statement.setInt(1, articleId);
                statement.executeUpdate();
            }
            if (article.coAuthorsIds() != null && !article.coAuthorsIds().isEmpty()) {
                try (PreparedStatement statement = prepareInsertCoAuthors(
                        connection,
                        articleId,
                        article.coAuthorsIds()
                )) {
                    statement.executeBatch();
                }
            }
            return null;
        }, FAILED_TO_UPDATE_ARTICLE);
    }

    @Override
    public void delete(Integer id) {
        doTransactional((connection -> {
            try (PreparedStatement statement = connection.prepareStatement(DELETE_CO_AUTHORS)) {
                statement.setInt(1, id);
                statement.executeUpdate();
            }
            try (PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {
                statement.setInt(1, id);
                statement.executeUpdate();
            }
            return null;
        }), FAILED_TO_DELETE);
    }

    private Integer updateArticle(
            Connection connection,
            Article article
    ) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_ARTICLE)) {
            statement.setInt(1, article.publicationId());
            statement.setInt(2, article.categoryId());
            statement.setString(3, article.name());
            statement.setString(4, article.content());
            statement.setInt(5, article.id());
            statement.executeUpdate();
        }
        return article.id();
    }

    private Set<Integer> getCoAuthorsIdsByArticleId(
            Connection connection,
            int articleId
    ) {
        Set<Integer> coAuthors = new HashSet<>();
        try (PreparedStatement statement = connection.prepareStatement(SQL_GET_CO_AUTHOR)) {
            statement.setInt(1, articleId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    coAuthors.add(resultSet.getInt(EMPLOYEE_ID));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(FAILED_TO_GET_CO_AUTHOR);
        }
        return coAuthors;
    }

    private Integer insertArticle(Connection connection, Article article) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                SQL_INSERT,
                Statement.RETURN_GENERATED_KEYS
        )) {
            statement.setInt(1, article.publicationId());
            statement.setInt(2, article.categoryId());
            statement.setString(3, article.name());
            statement.setString(4, article.content());
            statement.setInt(5, article.authorId());
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new RuntimeException(FAILED_CREATING);
                }
            }
        }
    }

    private PreparedStatement prepareInsertCoAuthors(
            Connection connection,
            int articleId,
            Set<Integer> coAuthorsIds
    ) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(INSERT_CO_AUTHOR);
        for (Integer employeeId : coAuthorsIds) {
            statement.setInt(1, articleId);
            statement.setInt(2, employeeId);
            statement.addBatch();
        }
        return statement;
    }

    private Map<Integer, Set<Integer>> loadCoAuthorsRelations(Connection connection)
            throws SQLException {
        return loadRelations(
                connection,
                SQL_GET_CO_AUTHORS,
                ARTICLE_ID,
                EMPLOYEE_ID
        );
    }
}