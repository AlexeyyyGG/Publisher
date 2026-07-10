package com.cloud.publishing.backend.repository.implementation;

import com.cloud.publishing.backend.repository.CategoryRepository;
import com.cloud.publishing.model.publication.Category;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryRepositoryImpl extends BaseRepository implements CategoryRepository {
    public static final String FAILED_TO_LOAD_CATEGORIES = "Не удалось загрузить категории";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String SQL_GET_ALL = "SELECT id, name FROM categories";
    public static final String SQL_FIND_BY_IDS = "SELECT id, name FROM categories WHERE id IN (%s)";

    public CategoryRepositoryImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List<Category> getAll() {
        List<Category> categories = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(SQL_GET_ALL)) {
            while (resultSet.next()) {
                categories.add(new Category(
                        resultSet.getInt(ID),
                        resultSet.getString(NAME)
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(FAILED_TO_LOAD_CATEGORIES, e);
        }
        return categories;
    }

    @Override
    public List<Category> getByIds(Set<Integer> ids) {
        return findById(
                ids,
                SQL_FIND_BY_IDS,
                resultSet -> new Category(resultSet.getInt(ID), resultSet.getString(NAME)),
                FAILED_TO_LOAD_CATEGORIES
        );
    }
}