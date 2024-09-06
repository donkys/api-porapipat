-- ลบตารางที่มีอยู่แล้วหากมี
DROP TABLE IF EXISTS api_permissions;
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS user_details;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS users;

-- สร้างตารางสำหรับผู้ใช้
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       username VARCHAR(50) UNIQUE,
                       password VARCHAR(100),
                       email VARCHAR(100) UNIQUE NOT NULL,
                       provider VARCHAR(20), -- ระบุว่าเป็นการลงทะเบียนแบบปกติหรือ OAuth2
                       enabled BOOLEAN NOT NULL DEFAULT TRUE,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- สร้างตารางสำหรับบทบาท
CREATE TABLE roles (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(50) UNIQUE NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       created_by VARCHAR(50),
                       updated_by VARCHAR(50)
);

-- สร้างตารางสำหรับความสัมพันธ์ระหว่างผู้ใช้และบทบาท
CREATE TABLE user_roles (
                            user_id INT NOT NULL,
                            role_id INT NOT NULL,
                            PRIMARY KEY (user_id, role_id),
                            FOREIGN KEY (user_id) REFERENCES users(id),
                            FOREIGN KEY (role_id) REFERENCES roles(id),
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            created_by VARCHAR(50),
                            updated_by VARCHAR(50)
);

-- สร้างตารางสำหรับเก็บข้อมูลเพิ่มเติมของผู้ใช้
CREATE TABLE user_details (
                              user_id INT PRIMARY KEY,
                              first_name VARCHAR(100),
                              last_name VARCHAR(100),
                              address TEXT,
                              phone_number VARCHAR(15),
                              profile_picture_url VARCHAR(255),
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              created_by VARCHAR(50),
                              updated_by VARCHAR(50),
                              FOREIGN KEY (user_id) REFERENCES users(id)
);

-- สร้างตารางสำหรับเก็บข้อมูลสิทธิ์การเข้าถึงของผู้ใช้ในแต่ละ API
CREATE TABLE api_permissions (
                                 id SERIAL PRIMARY KEY,
                                 user_id INT NOT NULL,
                                 api_name VARCHAR(100) NOT NULL,
                                 permission VARCHAR(50) NOT NULL,
                                 FOREIGN KEY (user_id) REFERENCES users(id),
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 created_by VARCHAR(50),
                                 updated_by VARCHAR(50)
);

-- CREATE TYPE provider_type AS ENUM ('GOOGLE', 'LOCAL');
-- ALTER TABLE users
-- ALTER COLUMN provider TYPE provider_type
--    USING provider::provider_type;
-- ALTER TYPE provider_type ADD VALUE 'FACEBOOK' AFTER 'GOOGLE';

-- เพิ่มตัวอย่างข้อมูลในตาราง roles
INSERT INTO roles (name, created_by, updated_by) VALUES
                                                     ('ROLE_USER', 'system', 'system'),
                                                     ('ROLE_ADMIN', 'system', 'system');

-- เพิ่มตัวอย่างข้อมูลในตาราง users
INSERT INTO users (username, password, email, provider, enabled, created_by, updated_by) VALUES
                                                                                             ('john_doe', 'hashed_password_123', 'john@example.com', 'LOCAL', TRUE, 'system', 'system'),
                                                                                             ('jane_doe', 'hashed_password_456', 'jane@example.com', 'GOOGLE', TRUE, 'system', 'system');

-- เพิ่มตัวอย่างข้อมูลในตาราง user_details
INSERT INTO user_details (user_id, first_name, last_name, address, phone_number, profile_picture_url, created_by, updated_by) VALUES
                                                                                                                                  (1, 'John', 'Doe', '123 Main St', '123-456-7890', 'http://example.com/john.jpg', 'system', 'system'),
                                                                                                                                  (2, 'Jane', 'Doe', '456 Elm St', '987-654-3210', 'http://example.com/jane.jpg', 'system', 'system');

-- เพิ่มตัวอย่างข้อมูลในตาราง user_roles
INSERT INTO user_roles (user_id, role_id, created_by, updated_by) VALUES
                                                                      (1, 1, 'system', 'system'), -- john_doe เป็น ROLE_USER
                                                                      (2, 2, 'system', 'system'); -- jane_doe เป็น ROLE_ADMIN

-- เพิ่มตัวอย่างข้อมูลในตาราง api_permissions
INSERT INTO api_permissions (user_id, api_name, permission, created_by, updated_by) VALUES
                                                                                        (1, 'test_api', 'READ', 'system', 'system'),
                                                                                        (1, 'test_api', 'WRITE', 'system', 'system');

INSERT INTO api_permissions (user_id, api_name, permission, created_by, updated_by) VALUES
                                                                                        (6, 'test_api', 'READ', 'system', 'system'),
                                                                                        (6, 'test_api', 'WRITE', 'system', 'system');

INSERT INTO api_permissions (user_id, api_name, permission, created_by, updated_by) VALUES
                                                                                        (15, 'role_api', 'READ', 'system', 'system'),
                                                                                        (15, 'role_api', 'WRITE', 'system', 'system');