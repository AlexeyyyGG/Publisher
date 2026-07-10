package com.cloud.publishing.model;

public record Review(
        Integer id,
        Integer articleId,
        Integer authorId,
        String reviewText,
        boolean publishFlag
) {
}