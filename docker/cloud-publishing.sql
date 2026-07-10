SET NAMES utf8mb4;

USE cloud_publishing;

CREATE TABLE categories
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE education
(
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    label VARCHAR(50) NOT NULL
);

INSERT INTO education (name, label)
VALUES ('SECONDARY', 'Среднее'),
       ('VOCATIONAL', 'Среднее специальное'),
       ('INCOMPLETE_HIGHER', 'Неоконченное высшее'),
       ('HIGHER', 'Высшее'),
       ('BACHELOR', 'Бакалавр'),
       ('MASTER', 'Магистр');

CREATE TABLE employees
(
    id              INT AUTO_INCREMENT PRIMARY KEY,
    first_name      VARCHAR(100)                  NOT NULL,
    last_name       VARCHAR(100)                  NOT NULL,
    middle_name     VARCHAR(100)                          ,
    email           VARCHAR(100)                  NOT NULL,
    password        VARCHAR(100)                  NOT NULL,
    gender          ENUM ('male', 'female')       NOT NULL,
    birth_year      YEAR                          NOT NULL,
    address         VARCHAR(100)                  NOT NULL,
    education_id    INT                           NOT NULL,
    type            ENUM ('Journalist', 'Editor') NOT NULL,
    is_chief_editor BOOLEAN DEFAULT FALSE,
    UNIQUE KEY unique_email (email),
    FOREIGN KEY (education_id) REFERENCES education(id)

);

CREATE TABLE publications
(
    id               INT AUTO_INCREMENT PRIMARY KEY,
    name             VARCHAR(100)                   NOT NULL,
    publication_type ENUM ('Magazine', 'Newspaper') NOT NULL,
    theme            VARCHAR(100)                   NOT NULL
);

CREATE TABLE articles
(
    id               INT AUTO_INCREMENT PRIMARY KEY,
    publication_id   INT                            NOT NULL,
    category_id      INT                            NOT NULL,
    name             VARCHAR(100)                   NOT NULL,
    content          TEXT                           NOT NULL,
    author_id        INT                            NOT NULL,
    FOREIGN KEY (category_id) REFERENCES categories (id),
    FOREIGN KEY (author_id) REFERENCES employees (id),
    FOREIGN KEY (publication_id) REFERENCES publications (id)
);

CREATE TABLE publication_categories (
    publication_id INT,
    category_id INT,
    FOREIGN KEY (publication_id) REFERENCES publications(id),
    FOREIGN KEY (category_id) REFERENCES  categories(id)
);

CREATE TABLE publication_journalists (
    publication_id INT,
    employee_id INT,
    FOREIGN KEY (publication_id) REFERENCES publications(id),
    FOREIGN KEY (employee_id) REFERENCES employees(id)
);

CREATE TABLE publication_editors (
    publication_id INT,
    employee_id INT,
    FOREIGN KEY (publication_id) REFERENCES publications(id),
    FOREIGN KEY (employee_id) REFERENCES employees(id)
);

CREATE TABLE article_co_authors
(
    article_id  INT NOT NULL,
    employee_id INT NOT NULL,
    PRIMARY KEY (article_id, employee_id),
    FOREIGN KEY (article_id) REFERENCES articles (id),
    FOREIGN KEY (employee_id) REFERENCES employees (id)
);

CREATE TABLE reviews
(
    id INT AUTO_INCREMENT PRIMARY KEY,
    article_id INT NOT NULL,
    author INT NOT NULL,
    review_text TEXT NOT NULL,
    publish_flag BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (article_id) REFERENCES articles(id),
    FOREIGN KEY (author) REFERENCES employees(id)
);

INSERT INTO categories (name) VALUES ('Спорт');
INSERT INTO categories (name) VALUES ('Отдых');
INSERT INTO categories (name) VALUES ('Работа');
INSERT INTO categories (name) VALUES ('Финансы');
INSERT INTO categories (name) VALUES ('Погода');
INSERT INTO categories (name) VALUES ('Гороскоп');
INSERT INTO categories (name) VALUES ('Реклама');
INSERT INTO categories (name) VALUES ('Объявления');
INSERT INTO categories (name) VALUES ('Юмор');

INSERT INTO employees (
    first_name,
    last_name,
    middle_name,
    email,
    password,
    gender,
    birth_year,
    address,
    education_id,
    type,
    is_chief_editor
)
VALUES (
    'Петр',
    'Иванов',
    'Иванович',
    'test1@gmail.com',
    '$2a$10$XyVegfm.5LN0SVUM1YW44u.jwobaBMnlwQQRk3X0GAPRTnoiPl1ZC', -- password1
    'male',
    1985,
    'test',
    6,
    'Editor',
    TRUE
);

INSERT INTO employees
(
    first_name,
    last_name,
    middle_name,
    email,
    password,
    gender,
    birth_year,
    address,
    education_id,
    type,
    is_chief_editor
)
VALUES
(
    'Иван',
    'Петров',
    'Петрович',
    'test2@gmail.com',
    '$2a$10$bD6W9A4twF0ql.WPjqaoYuyny4LyoJhRhsT4YKGK2yar3koB8yrm.', -- password2
    'male',
    1993,
    'test',
    3,
    'Journalist',
    FALSE
);
