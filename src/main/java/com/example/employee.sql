CREATE TABLE employee (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          surname VARCHAR(255) NOT NULL,
                          patronymic VARCHAR(255) NOT NULL,
                          email VARCHAR(255) NOT NULL,
                          password_hash VARCHAR(255) NOT NULL,
                          gender ENUM('male', 'female') NOT NULL,
                          date_of_birth DATE NOT NULL,
                          address VARCHAR(255) NOT NULL,
                          education VARCHAR(255) NOT NULL,
                          type ENUM('Journalist', 'Editor') NOT NULL,
                          is_editor_in_chief BOOLEAN DEFAULT FALSE,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          UNIQUE KEY unique_email (email)
);

CREATE UNIQUE INDEX single_editor_in_chief ON employee (is_editor_in_chief) WHERE is_editor_in_chief = TRUE;