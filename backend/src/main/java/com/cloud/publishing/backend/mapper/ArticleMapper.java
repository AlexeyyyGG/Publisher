package com.cloud.publishing.backend.mapper;

import com.cloud.publishing.common.dto.ArticleDTO;
import com.cloud.publishing.common.dto.response.ArticleGetAllDTO;
import com.cloud.publishing.model.Article;
import com.cloud.publishing.model.publication.Category;
import com.cloud.publishing.model.publication.Publication;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ArticleMapper {
    @Mapping(target = "id", ignore = true)
    Article toEntity(ArticleDTO request, Integer authorId);

    ArticleDTO toResponse(Article article);

    @Mapping(target = "id", source = "article.id")
    @Mapping(target = "name", source = "article.name")
    @Mapping(target = "publicationName", source = "publication.name")
    @Mapping(target = "categoryName", source = "category.name")
    ArticleGetAllDTO toGetDTO(
            Article article,
            Publication publication,
            Category category,
            String author,
            List<String> coAuthors,
            boolean published,
            boolean hasReviews
    );
}