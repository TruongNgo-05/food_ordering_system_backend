DROP
DATABASE IF EXISTS food_ordering_system;
CREATE
DATABASE food_ordering_system;
USE
food_ordering_system;

-- ================= USERS =================
CREATE TABLE users
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    email       VARCHAR(255) NOT NULL UNIQUE,
    username    VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    first_name  VARCHAR(255),
    last_name   VARCHAR(255),
    role        ENUM('ADMIN','CUSTOMER') DEFAULT 'CUSTOMER',
    avatar      VARCHAR(255),
    phone       VARCHAR(20),
    is_active   BOOLEAN  DEFAULT TRUE,
    status      ENUM('ACTIVED','LOCKED') DEFAULT 'ACTIVED',
    create_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    fail_count  INT      DEFAULT 0,
    lock_time   DATETIME
);

-- ================= CATEGORY =================
CREATE TABLE categories
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(100) UNIQUE,
    description TEXT,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ================= TABLE =================
CREATE TABLE table_details
(
    id           INT PRIMARY KEY AUTO_INCREMENT,
    table_number VARCHAR(10) UNIQUE,
    capacity     INT,
    location     VARCHAR(100),
    qr_code      TEXT,
    status       ENUM('AVAILABLE','OCCUPIED','RESERVED','MAINTENANCE') DEFAULT 'AVAILABLE',
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ================= PAYMENT METHOD =================
CREATE TABLE payment_methods
(
    id        INT PRIMARY KEY AUTO_INCREMENT,
    name      VARCHAR(50),
    code      ENUM('COD','ONLINE','AT_TABLE'),
    is_active BOOLEAN DEFAULT TRUE
);

-- ================= VOUCHERS =================
CREATE TABLE vouchers
(
    id              INT PRIMARY KEY AUTO_INCREMENT,
    code            VARCHAR(50) UNIQUE,
    discount        DECIMAL(10, 2),
    type            ENUM('PERCENT','FIXED'),
    min_order_value DECIMAL(10, 2),
    max_discount    DECIMAL(10, 2),
    expired_at      DATETIME,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ================= FOODS =================
CREATE TABLE foods
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(255),
    description TEXT,
    price       DECIMAL(10, 2),
    image       VARCHAR(255),
    category_id INT,
    rating      DECIMAL(3, 2) DEFAULT 0,
    sold_count  INT           DEFAULT 0,
    status      BOOLEAN       DEFAULT TRUE,
    created_at  DATETIME      DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories (id)
);

-- ================= FOOD IMAGES =================
CREATE TABLE food_images
(
    id         INT PRIMARY KEY AUTO_INCREMENT,
    food_id    INT,
    image_url  VARCHAR(255),
    is_primary BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (food_id) REFERENCES foods (id) ON DELETE CASCADE
);

-- ================= CART =================
CREATE TABLE carts
(
    id         INT PRIMARY KEY AUTO_INCREMENT,
    user_id    INT UNIQUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE cart_items
(
    id       INT PRIMARY KEY AUTO_INCREMENT,
    cart_id  INT,
    food_id  INT,
    quantity INT DEFAULT 1,
    UNIQUE (cart_id, food_id),
    FOREIGN KEY (cart_id) REFERENCES carts (id) ON DELETE CASCADE,
    FOREIGN KEY (food_id) REFERENCES foods (id)
);

-- ================= ORDERS =================
CREATE TABLE orders
(
    id                INT PRIMARY KEY AUTO_INCREMENT,
    order_code        VARCHAR(50) UNIQUE,
    user_id           INT,

    customer_name     VARCHAR(255),
    customer_phone    VARCHAR(20),
    delivery_address  TEXT,

    order_type        ENUM('DELIVERY','DINE_IN') DEFAULT 'DELIVERY',

    subtotal          DECIMAL(10, 2),
    discount          DECIMAL(10, 2),
    shipping_fee      DECIMAL(10, 2),
    total_price       DECIMAL(10, 2),

    status            ENUM('PENDING','PREPARING','DELIVERING','COMPLETED','CANCELED') DEFAULT 'PENDING',

    payment_method_id INT,
    payment_status    ENUM('PENDING','PAID','FAILED') DEFAULT 'PENDING',

    voucher_id        INT,
    table_id          INT,

    created_at        DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (payment_method_id) REFERENCES payment_methods (id),
    FOREIGN KEY (voucher_id) REFERENCES vouchers (id),
    FOREIGN KEY (table_id) REFERENCES table_details (id)
);

-- ================= ORDER DETAILS =================
CREATE TABLE order_details
(
    id         INT PRIMARY KEY AUTO_INCREMENT,
    order_id   INT,
    food_id    INT,
    quantity   INT,
    unit_price DECIMAL(10, 2),
    subtotal   DECIMAL(10, 2),
    FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
    FOREIGN KEY (food_id) REFERENCES foods (id)
);

-- ================= PAYMENTS =================
CREATE TABLE payments
(
    id       INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT,
    method   ENUM('COD','ONLINE','AT_TABLE'),
    status   ENUM('PENDING','PAID','FAILED'),
    paid_at  DATETIME,
    FOREIGN KEY (order_id) REFERENCES orders (id)
);

-- ================= FAVORITES =================
CREATE TABLE favorites
(
    id      INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    food_id INT,
    UNIQUE (user_id, food_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (food_id) REFERENCES foods (id)
);

-- ================= INGREDIENTS =================
CREATE TABLE ingredients
(
    id   INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100)
);

CREATE TABLE inventory
(
    id            INT PRIMARY KEY AUTO_INCREMENT,
    ingredient_id INT UNIQUE,
    quantity      INT DEFAULT 0,
    FOREIGN KEY (ingredient_id) REFERENCES ingredients (id)
);

-- ================= OTP =================
CREATE TABLE otps
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    email      VARCHAR(255),
    otp        VARCHAR(10),
    type       VARCHAR(50),
    expire_at  DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (email) REFERENCES users (email)
);

-- ================= DỮ LIỆU MẪU =================
-- ================= PAYMENT METHODS =================
INSERT INTO payment_methods (name, code)
VALUES ('Tiền mặt', 'COD'),
       ('Chuyển khoản', 'ONLINE'),
       ('Tại bàn', 'AT_TABLE');

-- ================= USERS =================
INSERT INTO users (email, username, password, first_name, last_name, role, phone)
VALUES ('admin@gmail.com', 'admin', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Ngo', 'Truong',
        'ADMIN', '0900000001'),
       ('user1@gmail.com', 'user1', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Le', 'Van A',
        'CUSTOMER', '0900000002'),
       ('user2@gmail.com', 'user2', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Pham', 'Van B',
        'CUSTOMER', '0900000003'),
       ('user3@gmail.com', 'user3', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Hoang', 'Van C',
        'CUSTOMER', '0900000004');

-- ================= CATEGORIES =================
INSERT INTO categories (name)
VALUES ('Đồ ăn nhanh'),
       ('Đồ uống'),
       ('Món chính');

-- ================= TABLE =================
INSERT INTO table_details (table_number, capacity, location)
VALUES ('T01', 2, 'Cửa sổ'),
       ('T02', 4, 'Giữa phòng'),
       ('T03', 6, 'Phòng VIP');

-- ================= VOUCHERS =================
INSERT INTO vouchers (code, discount, type, min_order_value, max_discount, expired_at)
VALUES ('SALE10', 10, 'PERCENT', 50000, 50000, '2026-12-31'),
       ('SALE20', 20, 'PERCENT', 100000, 100000, '2026-12-31'),
       ('FIX50', 50000, 'FIXED', 150000, 50000, '2026-12-31');

-- ================= FOODS =================
INSERT INTO foods (name, description, price, category_id, rating, sold_count)
VALUES ('Hamburger', 'Burger bò ngon', 50000, 1, 4.5, 100),
       ('Pizza Hải Sản', 'Pizza tôm mực', 120000, 3, 4.8, 200),
       ('Trà sữa', 'Trà sữa trân châu', 40000, 2, 4.7, 300),
       ('Cơm gà', 'Cơm gà chiên', 60000, 3, 4.6, 150);

-- ================= FOOD IMAGES =================
INSERT INTO food_images (food_id, image_url, is_primary)
VALUES (1, 'burger.jpg', TRUE),
       (2, 'pizza.jpg', TRUE),
       (3, 'trasua.jpg', TRUE),
       (4, 'comga.jpg', TRUE);

-- ================= CART =================
INSERT INTO carts (user_id)
VALUES (2),
       (3),
       (4),
       (1);

INSERT INTO cart_items (cart_id, food_id, quantity)
VALUES (1, 1, 2),
       (1, 2, 1),
       (2, 2, 2),
       (2, 3, 1),
       (3, 1, 1),
       (3, 4, 2);

-- ================= ORDERS =================
INSERT INTO orders
(order_code, user_id, subtotal, discount, shipping_fee, total_price, status, payment_method_id, payment_status,
 voucher_id)
VALUES ('ORD001', 2, 150000, 15000, 10000, 145000, 'COMPLETED', 1, 'PAID', 1),
       ('ORD002', 3, 200000, 40000, 10000, 170000, 'DELIVERING', 2, 'PAID', 2),
       ('ORD003', 4, 100000, 0, 10000, 110000, 'PENDING', 1, 'PENDING', NULL),
       ('ORD004', 2, 120000, 50000, 0, 70000, 'COMPLETED', 3, 'PAID', 3);

-- ================= ORDER DETAILS =================
INSERT INTO order_details (order_id, food_id, quantity, unit_price, subtotal)
VALUES (1, 1, 2, 50000, 100000),
       (1, 3, 1, 50000, 50000),

       (2, 2, 1, 120000, 120000),
       (2, 3, 2, 40000, 80000),

       (3, 4, 1, 60000, 60000),
       (3, 1, 1, 50000, 50000),

       (4, 2, 1, 120000, 120000);

-- ================= PAYMENTS =================
INSERT INTO payments (order_id, method, status, paid_at)
VALUES (1, 'COD', 'PAID', NOW()),
       (2, 'ONLINE', 'PAID', NOW()),
       (3, 'COD', 'PENDING', NULL),
       (4, 'AT_TABLE', 'PAID', NOW());

-- ================= FAVORITES =================
INSERT INTO favorites (user_id, food_id)
VALUES (2, 1),
       (2, 2),
       (3, 2),
       (3, 3),
       (4, 1),
       (4, 4);

-- ================= INGREDIENTS =================
INSERT INTO ingredients (name)
VALUES ('Thịt bò'),
       ('Bột mì'),
       ('Sữa'),
       ('Trà'),
       ('Gà');

-- ================= INVENTORY =================
INSERT INTO inventory (ingredient_id, quantity)
VALUES (1, 100),
       (2, 200),
       (3, 150),
       (4, 120),
       (5, 180);

-- Xem user
SELECT *
FROM users;

-- Xem đơn hàng + user
SELECT o.order_code, u.username, o.total_price, o.status
FROM orders o
         JOIN users u ON o.user_id = u.id;

-- Xem giỏ hàng
SELECT u.username, f.name, ci.quantity
FROM cart_items ci
         JOIN carts c ON ci.cart_id = c.id
         JOIN users u ON c.user_id = u.id
         JOIN foods f ON ci.food_id = f.id;