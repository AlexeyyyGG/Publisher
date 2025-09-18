CREATE TABLE employee (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          first_name VARCHAR(255) NOT NULL,
                          last_name VARCHAR(255) NOT NULL,
                          middle_name VARCHAR(255) NOT NULL,
                          email VARCHAR(255) NOT NULL,
                          password VARCHAR(255) NOT NULL,
                          gender ENUM('male', 'female') NOT NULL,
                          date_of_birth YEAR NOT NULL,
                          address VARCHAR(255) NOT NULL,
                          education VARCHAR(255) NOT NULL,
                          type ENUM('Journalist', 'Editor') NOT NULL,
                          is_chief_editor BOOLEAN DEFAULT FALSE,
                          UNIQUE KEY unique_email (email)
);

CREATE UNIQUE INDEX single_chief_editor ON employee (is_chief_editor) WHERE is_chief_editor = TRUE;