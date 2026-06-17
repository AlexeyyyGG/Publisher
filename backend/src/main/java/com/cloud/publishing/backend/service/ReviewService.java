package com.cloud.publishing.backend.service;

import com.cloud.publishing.common.dto.response.ReviewDetailsDTO;
import java.util.List;

/**
 * Service interface for managing reviews. Provides operations for retrieving review details.
 */
public interface ReviewService {
    /**
     * Returns list of review details for article.
     *
     * @param articleId identifier of the article
     * @return list of {@link ReviewDetailsDTO}
     */
    List<ReviewDetailsDTO> getByArticleId(int articleId);

    /**
     * Checks if article has reviews.
     *
     * @param articleId identifier of the article
     * @return true if reviews exist, false otherwise
     */
    boolean hasReviewsForArticle(int articleId);

    /**
     * Checks if article is published.
     *
     * @param articleId identifier of the article
     * @return true if published, false otherwise
     */
    boolean isArticlePublished(int articleId);
}