package com.cloud.publishing.backend.repository;

import com.cloud.publishing.model.Review;
import java.util.List;

/**
 * Repository interface for managing reviews. Provides operations for database access.
 */
public interface ReviewRepository {
    /**
     * Returns list of reviews for article.
     *
     * @param articleId identifier of the article
     * @return list of {@link Review}
     */
    List<Review> getByArticleId(int articleId);

    /**
     * Checks if article is published.
     *
     * @param articleId identifier of the article
     * @return true if published, false otherwise
     */
    boolean isArticlePublished(int articleId);

    /**
     * Checks if article has reviews.
     *
     * @param articleId identifier of the article
     * @return true if reviews exist, false otherwise
     */
    boolean hasReviewsForArticle(int articleId);
}