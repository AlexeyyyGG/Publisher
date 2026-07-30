USE cloud_publishing;

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
VALUES
(
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