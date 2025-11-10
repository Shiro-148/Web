-- Migration: tạo bảng nối account_favorite cho relation many-to-many
-- Thực thi file này trên database MySQL của bạn (ví dụ qua client hoặc Flyway nếu cấu hình)

CREATE TABLE IF NOT EXISTS account_favorite (
  account_id INT NOT NULL,
  product_id INT NOT NULL,
  PRIMARY KEY (account_id, product_id),
  CONSTRAINT fk_account_favorite_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
  CONSTRAINT fk_account_favorite_product FOREIGN KEY (product_id) REFERENCES product(idProduct) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
