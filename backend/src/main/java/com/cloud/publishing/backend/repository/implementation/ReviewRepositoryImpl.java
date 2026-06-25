package com.cloud.publishing.backend.repository.implementation;

import static com.cloud.publishing.common.constants.review.ReviewField.ARTICLE_ID;
import static com.cloud.publishing.common.constants.review.ReviewField.AUTHOR;
import static com.cloud.publishing.common.constants.review.ReviewField.ID;
import static com.cloud.publishing.common.constants.review.ReviewField.PUBLISH_FLAG;
import static com.cloud.publishing.common.constants.review.ReviewField.REVIEW_TEXT;
import static com.cloud.publishing.common.constants.review.ReviewMessage.FAILED_TO_CHECK_PUBLISHED;
import static com.cloud.publishing.common.constants.review.ReviewMessage.FAILED_TO_CHECK_REVIEWS;
import static com.cloud.publishing.common.constants.review.ReviewMessage.FAILED_TO_LOAD_REVIEWS;
import static com.cloud.publishing.common.constants.review.ReviewSQL.SQL_GET_BY_ARTICLE_ID;
import static com.cloud.publishing.common.constants.review.ReviewSQL.SQL_HAS_REVIEWS;
import static com.cloud.publishing.common.constants.review.ReviewSQL.SQL_IS_PUBLISHED;

import com.cloud.publishing.backend.repository.ReviewRepository;
import com.cloud.publishing.model.Review;
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
    public List<Review> getByArticleId(int articleId) {
        List<Review> reviews = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(SQL_GET_BY_ARTICLE_ID)) {
            statement.setInt(1, articleId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    reviews.add(new Review(
                            resultSet.getInt(ID),
                            resultSet.getInt(ARTICLE_ID),
                            resultSet.getInt(AUTHOR),
                            resultSet.getString(REVIEW_TEXT),
                            resultSet.getBoolean(PUBLISH_FLAG)
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(FAILED_TO_LOAD_REVIEWS, e);
        }
        return reviews;
    }

    @Override
    public boolean isArticlePublished(int articleId) {
        return exists(articleId, SQL_IS_PUBLISHED, FAILED_TO_CHECK_PUBLISHED);
    }

    @Override
    public boolean hasReviewsForArticle(int articleId) {
        return exists(articleId, SQL_HAS_REVIEWS, FAILED_TO_CHECK_REVIEWS);
    }
}