CREATE TABLE IF NOT EXISTS role (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    role_name VARCHAR(255) NOT NULL,
    role_code VARCHAR(255) NOT NULL,
    UNIQUE KEY uk_role_code (role_code)
    );
-- ===============================
-- CREATE TABLE permissions
-- ===============================
CREATE TABLE IF NOT EXISTS permissions (
                                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                           name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    UNIQUE KEY uk_permission_name (name)
    );
-- =============================
-- CREATE TABLE role_permissions
-- =============================
CREATE TABLE IF NOT EXISTS role_permissions (
                                                role_id BIGINT NOT NULL,
                                                permission_id BIGINT NOT NULL,

                                                PRIMARY KEY (role_id, permission_id),

    CONSTRAINT fk_role_permission_role
    FOREIGN KEY (role_id)
    REFERENCES role(id)
    ON DELETE CASCADE,

    CONSTRAINT fk_role_permission_permission
    FOREIGN KEY (permission_id)
    REFERENCES permissions(id)
    ON DELETE CASCADE
    );

-- ===============================
-- CREATE TABLE user
-- ===============================
CREATE TABLE IF NOT EXISTS user (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,

                                    email VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    full_name VARCHAR(255),
    identify_number VARCHAR(20),
    gender VARCHAR(50),
    age INT,
    address VARCHAR(255),
    date_of_birth DATE,
    password VARCHAR(255),
    avatar_url VARCHAR(255),
    google_id VARCHAR(255),
    login_provider VARCHAR(255),

    role_id BIGINT NOT NULL,

    UNIQUE KEY uk_user_email (email),

    CONSTRAINT fk_user_role
    FOREIGN KEY (role_id)
    REFERENCES role(id)
    ON DELETE RESTRICT
    );