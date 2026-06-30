package com.cloud.publishing.backend.repository;

import com.cloud.publishing.model.article.Article;
import com.cloud.publishing.model.article.ArticleShort;
import java.util.List;

/**
 * Repository interface for managing articles. Provides operations for database access.
 */
public interface ArticleRepository extends IRepository<Article, Integer> {
    /**
     * Returns list of all articles.
     *
     * @return list of {@link Article}
     */
    List<ArticleShort> getAll();

    /**
     * Returns list of articles for specific author.
     *
     * @param id identifier of the author
     * @return list of {@link Article}
     */
    List<ArticleShort> getByAuthorId(Integer id);
}