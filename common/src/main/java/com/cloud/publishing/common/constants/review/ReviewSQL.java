package com.cloud.publishing.common.constants.review;

public class ReviewSQL {
    private ReviewSQL() {
    }

    public static final String SQL_GET_BY_ARTICLE_ID = """
            SELECT r.id, r.review_text, r.publish_flag, e.id AS employee_id, e.first_name, e.last_name, e.middle_name
            FROM reviews r
            JOIN employees e ON r.author = e.id
            WHERE r.article_id = ?
            """;
    public static final String SQL_HAS_REVIEWS =
            "SELECT EXISTS(SELECT 1 FROM reviews WHERE article_id = ?)";
    public static final String SQL_IS_PUBLISHED =
            "SELECT EXISTS(SELECT 1 FROM reviews WHERE article_id = ? AND publish_flag = TRUE)";
}