-- Create orders and order_item tables
CREATE TABLE IF NOT EXISTS orders (
  id INT AUTO_INCREMENT PRIMARY KEY,
  account_id INT NOT NULL,
  total DECIMAL(18,3) DEFAULT 0,
  status VARCHAR(64),
  created_at DATETIME,
  CONSTRAINT fk_orders_account FOREIGN KEY (account_id) REFERENCES account(id)
);

CREATE TABLE IF NOT EXISTS order_item (
  id INT AUTO_INCREMENT PRIMARY KEY,
  order_id INT NOT NULL,
  product_id INT NOT NULL,
  unit_price DECIMAL(10,3) NOT NULL,
  quantity INT NOT NULL,
  CONSTRAINT fk_orderitem_order FOREIGN KEY (order_id) REFERENCES orders(id),
  CONSTRAINT fk_orderitem_product FOREIGN KEY (product_id) REFERENCES product(idProduct)
);
