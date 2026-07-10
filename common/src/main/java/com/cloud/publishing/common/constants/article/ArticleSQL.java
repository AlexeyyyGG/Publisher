package com.cloud.publishing.common.constants.article;

public class ArticleSQL {
    private ArticleSQL() {
    }

    public static final String SQL_INSERT =
            "INSERT INTO articles (publication_id, category_id, name, content, author_id) VALUES (?, ?, ?, ?, ?)";
    public static final String INSERT_CO_AUTHOR =
            "INSERT INTO article_co_authors (article_id, employee_id) VALUES (?, ?)";
    public static final String SQL_GET_CO_AUTHOR =
            "SELECT employee_id FROM article_co_authors WHERE article_id = ?";
    public static final String SQL_GET_ARTICLE_BY_ID =
            "SELECT id, publication_id, category_id, name, content, author_id FROM articles WHERE id = ?";
    public static final String UPDATE_ARTICLE =
            "UPDATE articles SET publication_id=?, category_id=?, name=?, content=? WHERE id=?";
    public static final String DELETE_CO_AUTHORS = "DELETE FROM article_co_authors WHERE article_id = ?";
    public static final String SQL_DELETE = "DELETE FROM articles WHERE id = ?";
    public static final String SQL_EXIST = "SELECT EXISTS(SELECT 1 FROM articles WHERE id = ?)";
    public static final String SQL_GET_ALL_ARTICLES =
            "SELECT id, publication_id, category_id, author_id, name FROM articles";
    public static final String SQL_GET_CO_AUTHORS =
            "SELECT article_id, employee_id FROM article_co_authors";
    public static final String SQL_GET_BY_AUTHOR_ID =
            "SELECT id, publication_id, category_id, author_id, name FROM articles WHERE author_id = ?";
}