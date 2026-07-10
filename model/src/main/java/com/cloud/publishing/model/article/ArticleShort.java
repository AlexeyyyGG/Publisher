package com.cloud.publishing.model.article;

import java.util.Set;

public record ArticleShort(
        Integer id,
        Integer publicationId,
        Integer categoryId,
        String name,
        Integer authorId,
        Set<Integer> coAuthorsIds
) {
}