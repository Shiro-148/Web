-- Tạo bảng roles và user_roles để phân quyền nhiều-nhiều
CREATE TABLE IF NOT EXISTS roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES accounts(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Seed các quyền cơ bản
INSERT INTO roles (name, description) VALUES
    ('ADMIN', 'Quản trị viên'),
    ('USER', 'Khách hàng')
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- Gán quyền cho các tài khoản dựa trên cột role hiện có (nếu tồn tại)
INSERT INTO user_roles (user_id, role_id)
SELECT a.id, r.id
FROM accounts a
JOIN roles r ON r.name = a.role
ON DUPLICATE KEY UPDATE user_id = user_id;


