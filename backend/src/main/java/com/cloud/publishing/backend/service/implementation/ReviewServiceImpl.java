package com.cloud.publishing.backend.service.implementation;

import static com.cloud.publishing.common.constants.review.ReviewMessage.FAILED_TO_CHECK_PUBLISHED;
import static com.cloud.publishing.common.constants.review.ReviewMessage.FAILED_TO_CHECK_REVIEWS;
import static com.cloud.publishing.common.constants.review.ReviewSQL.SQL_HAS_REVIEWS;
import static com.cloud.publishing.common.constants.review.ReviewSQL.SQL_IS_PUBLISHED;

import com.cloud.publishing.backend.repository.implementation.ReviewRepositoryImpl;
import com.cloud.publishing.backend.service.ReviewService;
import com.cloud.publishing.common.dto.response.ReviewDetailsDTO;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepositoryImpl reviewRepository;

    @Autowired
    public ReviewServiceImpl(ReviewRepositoryImpl reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public List<ReviewDetailsDTO> getByArticleId(int articleId) {
        return reviewRepository.getByArticleId(articleId);
    }

    @Override
    public boolean hasReviewsForArticle(int articleId) {
        return reviewRepository.checkArticleStatus(
                articleId,
                SQL_HAS_REVIEWS,
                FAILED_TO_CHECK_REVIEWS
        );
    }

    @Override
    public boolean isArticlePublished(int articleId) {
        return reviewRepository.checkArticleStatus(
                articleId,
                SQL_IS_PUBLISHED,
                FAILED_TO_CHECK_PUBLISHED
        );
    }
}