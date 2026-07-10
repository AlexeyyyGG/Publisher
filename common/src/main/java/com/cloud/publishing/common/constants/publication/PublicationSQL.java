package com.cloud.publishing.common.constants.publication;

public class PublicationSQL {
    private PublicationSQL() {
    }

    public static final String SQL_INSERT_PUBLICATION =
            "INSERT INTO publications (name, publication_type, theme) VALUES (?, ?, ?)";
    public static final String INSERT_PUB_CATEGORY =
            "INSERT INTO publication_categories (publication_id, category_id) VALUES (?, ?)";
    public static final String INSERT_JOURNALIST =
            "INSERT INTO publication_journalists (publication_id, employee_id) VALUES (?, ?)";
    public static final String INSERT_EDITOR =
            "INSERT INTO publication_editors (publication_id, employee_id) VALUES (?, ?)";
    public static final String UPDATE_PUBLICATION =
            "UPDATE publications SET name=?, publication_type=?, theme=? WHERE id=?";
    public static final String DELETE_CATEGORY =
            "DELETE FROM publication_categories WHERE publication_id = ?";
    public static final String DELETE_JOURNALIST =
            "DELETE FROM publication_journalists WHERE publication_id = ?";
    public static final String DELETE_EDITOR =
            "DELETE FROM publication_editors WHERE publication_id = ?";
    public static final String SQL_DELETE = "DELETE FROM publications WHERE id=?";
    public static final String SQL_GET_PUBLICATION = "SELECT id, name, publication_type, theme FROM publications";
    public static final String SQL_EXIST = "SELECT EXISTS(SELECT 1 FROM publications WHERE id = ?)";
    public static final String SQL_GET_EMPLOYEE = """
            SELECT e.id
            FROM employees e
            JOIN %s p ON e.id = p.employee_id WHERE p.publication_id = ?
            """;
    public static final String SQL_GET_CATEGORIES_BY_PUBLICATION = """
            SELECT c.id
            FROM categories c
            JOIN publication_categories pc ON c.id = pc.category_id WHERE pc.publication_id = ?
            """;
    public static final String SQL_GET_BY_ID_PUBLICATION =
            "SELECT id, name, publication_type, theme FROM publications WHERE id = ?";
    public static final String SQL_GET_ALL_CATEGORIES = "SELECT publication_id, category_id FROM publication_categories";
    public static final String SQL_GET_ALL_JOURNALISTS = "SELECT publication_id, employee_id FROM publication_journalists";
    public static final String SQL_GET_ALL_EDITORS = "SELECT publication_id, employee_id FROM publication_editors";
    public static final String SQL_FIND_BY_IDS = """
            SELECT id, name, publication_type, theme
            FROM publications
            WHERE id IN (%s)""";
}
