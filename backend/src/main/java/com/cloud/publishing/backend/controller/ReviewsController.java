package com.cloud.publishing.backend.controller;

import com.cloud.publishing.backend.service.ReviewService;
import com.cloud.publishing.common.constants.Urls;
import com.cloud.publishing.common.dto.response.ReviewDetailsDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Urls.REVIEWS)
public class ReviewsController {
    private final ReviewService reviewService;
    private static final Logger logger = LoggerFactory.getLogger(ReviewsController.class);

    @Autowired
    public ReviewsController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping(value = Urls.ARTICLES_BY_ID_FOR_REVIEWS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ReviewDetailsDTO>> getByArticleId(@PathVariable int articleId) {
        logger.info("getByArticleId called for articleId={}", articleId);
        List<ReviewDetailsDTO> reviews = reviewService.getByArticleId(articleId);
        return ResponseEntity.status(HttpStatus.OK).body(reviews);
    }
}