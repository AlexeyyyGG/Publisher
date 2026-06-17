package com.cloud.publishing.backend.service;

import com.cloud.publishing.common.dto.ArticleDTO;
import com.cloud.publishing.model.Article;
import java.util.List;

/**
 * Service interface for managing articles. Provides operations for creating, updating, retrieving
 * and deleting articles.
 */
public interface ArticleService {
    /**
     * Creates a new article.
     *
     * @param request       {@link ArticleDTO} containing article data
     * @param currentUserId identifier of the current authenticated user who creates the article
     * @return created {@link Article}
     */
    Article add(ArticleDTO request, Integer currentUserId);

    /**
     * Updates an existing article.
     *
     * @param id            identifier of the article to update
     * @param request       {@link ArticleDTO} containing updated article data
     * @param currentUserId identifier of the current authenticated user who updates the article
     * @return updated {@link Article}
     */
    Article update(int id, ArticleDTO request, Integer currentUserId);

    /**
     * Returns article by id.
     *
     * @param id            identifier of the article
     * @param currentUserId identifier of the current authenticated user requesting the article
     * @param isChiefEditor flag indicating if the current user has the Chief Editor role
     * @return {@link Article}
     */
    Article get(int id, Integer currentUserId, boolean isChiefEditor);

    /**
     * Returns list of all articles.
     *
     * @param currentUserId identifier of the current authenticated user requesting the list
     * @param isChiefEditor flag indicating if the current user has the Chief Editor role
     * @return list of {@link Article}
     */
    List<Article> getAll(Integer currentUserId, boolean isChiefEditor);

    /**
     * Deletes article by id.
     *
     * @param id            identifier of the article to delete
     * @param currentUserId identifier of the current authenticated user who deletes the article
     */
    void delete(int id, Integer currentUserId);
}