package com.cloud.publishing.common.constants.employee;

public class EmployeeSQL {
    private EmployeeSQL() {
    }

    public static final String SQL_INSERT = """
            INSERT INTO employees(first_name, last_name, middle_name, email, password, gender,
            birth_year, address, education_id, type, is_chief_editor) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""";
    public static final String SQL_UPDATE_WITH_PASSWORD = """
            UPDATE employees SET first_name=?, last_name=?, middle_name=?, email=?, password=?,
            gender=?, birth_year=?, address=?, education_id=?, type=?, is_chief_editor=? WHERE id=?""";
    public static final String SQL_UPDATE_WITHOUT_PASSWORD = """
            UPDATE employees SET first_name=?, last_name=?, middle_name=?, email=?,
            gender=?, birth_year=?, address=?, education_id=?, type=?, is_chief_editor=? WHERE id=?""";
    public static final String SQL_GET = """
            SELECT e.*,
                   ed.id AS ed_id,
                   ed.name AS ed_name,
                   ed.label AS ed_label
            FROM employees AS e
            JOIN education ed ON e.education_id = ed.id
            WHERE e.id = ?""";
    public static final String SQL_LIST = """
            SELECT e.*,
                   ed.id AS ed_id,
                   ed.name AS ed_name,
                   ed.label AS ed_label
            FROM employees e
            JOIN education ed ON e.education_id = ed.id""";
    public static final String SQL_DELETE = "DELETE FROM employees WHERE id=?";
    public static final String SQL_EXIST = "SELECT EXISTS(SELECT 1 FROM employees WHERE id = ?)";
    public static final String SQL_FIND_BY_EMAIL = """
            SELECT e.id, e.email, e.password, e.type, e.is_chief_editor
            FROM employees e
            WHERE e.email = ?""";
    public static final String SQL_RESET_CE = "UPDATE employees SET is_chief_editor = false WHERE is_chief_editor = true";
    public static final String SQL_FIND_BY_IDS = """
            SELECT id, first_name, last_name, middle_name
            FROM employees
            WHERE id IN (%s)""";
    public static final String SQL_FIND_CO_AUTHORS = """
            SELECT id, first_name, last_name, middle_name
            FROM employees
            WHERE type = 'JOURNALIST'
              AND id <> ?
              AND id IN (%s)
            """;
}