package com.cloud.publishing.model.article;

import java.util.Set;

public record Article(
        Integer id,
        Integer publicationId,
        Integer categoryId,
        String name,
        String content,
        Integer authorId,
        Set<Integer> coAuthorsIds
) {
}