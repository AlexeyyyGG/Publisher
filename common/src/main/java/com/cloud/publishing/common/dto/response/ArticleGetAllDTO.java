package com.cloud.publishing.common.dto.response;

import java.util.List;

public record ArticleGetAllDTO(
        Integer id,
        String name,
        String publicationName,
        String categoryName,
        String author,
        List<String> coAuthors,
        boolean published,
        boolean hasReviews
) {
}