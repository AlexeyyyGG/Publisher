package com.cloud.publishing.backend.repository.implementation;

import static com.cloud.publishing.common.constants.employee.EmployeeField.FIRST_NAME;
import static com.cloud.publishing.common.constants.employee.EmployeeField.LAST_NAME;
import static com.cloud.publishing.common.constants.employee.EmployeeField.MIDDLE_NAME;
import static com.cloud.publishing.common.constants.review.ReviewField.EMPLOYEE_ID;
import static com.cloud.publishing.common.constants.review.ReviewField.ID;
import static com.cloud.publishing.common.constants.review.ReviewField.REVIEW_TEXT;
import static com.cloud.publishing.common.constants.review.ReviewMessage.FAILED_TO_LOAD_REVIEWS;
import static com.cloud.publishing.common.constants.review.ReviewSQL.SQL_GET_BY_ARTICLE_ID;

import com.cloud.publishing.backend.repository.ReviewRepository;
import com.cloud.publishing.common.dto.response.EmployeeShort;
import com.cloud.publishing.common.dto.response.ReviewDetailsDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.stereotype.Repository;

@Repository
public class ReviewRepositoryImpl extends BaseRepository implements ReviewRepository {
    public ReviewRepositoryImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List<ReviewDetailsDTO> getByArticleId(int articleId) {
        List<ReviewDetailsDTO> reviews = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(SQL_GET_BY_ARTICLE_ID)) {
            statement.setInt(1, articleId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    EmployeeShort reviewer = new EmployeeShort(
                            resultSet.getInt(EMPLOYEE_ID),
                            resultSet.getString(FIRST_NAME),
                            resultSet.getString(LAST_NAME),
                            resultSet.getString(MIDDLE_NAME)
                    );
                    reviews.add(new ReviewDetailsDTO(
                            resultSet.getInt(ID),
                            reviewer.getShortName(),
                            resultSet.getString(REVIEW_TEXT)
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(FAILED_TO_LOAD_REVIEWS, e);
        }
        return reviews;
    }

    @Override
    public boolean checkArticleStatus(int articleId, String sql, String errorMessage) {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, articleId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBoolean(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(errorMessage, e);
        }
        return false;
    }
}