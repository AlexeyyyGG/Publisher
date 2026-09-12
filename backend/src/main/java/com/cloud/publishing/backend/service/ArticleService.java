package com.cloud.publishing.backend.service;

import com.cloud.publishing.common.dto.ArticleDTO;
import com.cloud.publishing.common.dto.response.EmployeeShort;
import com.cloud.publishing.model.article.Article;
import com.cloud.publishing.model.article.ArticleShort;
import java.util.List;

/**
 * Service interface for managing articles. Provides operations for creating, updating, retrieving
 * and deleting articles.
 */
public interface ArticleService {
    /**
     * Creates a new article.
     *
     * @param newArticle {@link ArticleDTO} containing article data
     * @return created {@link Article}
     */
    Article add(ArticleDTO newArticle);

    /**
     * Updates an existing article.
     *
     * @param id identifier of the article to update
     * @return updated {@link Article}
     */
    Article update(int id, ArticleDTO articleUpdate);

    /**
     * Returns article by id.
     *
     * @param id identifier of the article
     * @return {@link Article}
     */
    Article get(int id);

    /**
     * Returns list of all articles.
     *
     * @return list of {@link Article}
     */
    List<ArticleShort> getAll();

    /**
     * Deletes article by id.
     *
     * @param id identifier of the article to delete
     */
    void delete(int id);

    /**
     * Returns journalists associated with the publication.
     *
     * @param publicationId identifier of the publication
     * @return list of journalists
     */
    List<EmployeeShort> getCoAuthors(int publicationId);
}