package com.cloud.publishing.backend.service.implementation;

import com.cloud.publishing.backend.repository.ReviewRepository;
import com.cloud.publishing.backend.service.ReviewService;
import com.cloud.publishing.model.Review;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public List<Review> getByArticleId(int articleId) {
        return reviewRepository.getByArticleId(articleId);
    }

    @Override
    public boolean hasReviewsForArticle(int articleId) {
        return reviewRepository.hasReviewsForArticle(articleId);
    }

    @Override
    public boolean isArticlePublished(int articleId) {
        return reviewRepository.isArticlePublished(articleId);
    }
}