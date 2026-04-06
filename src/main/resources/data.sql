DROP
DATABASE IF EXISTS food_ordering_system;
CREATE
DATABASE food_ordering_system;
USE
food_ordering_system;
-- KHUYẾN MÃI
CREATE TABLE vouchers
(
    id         INT PRIMARY KEY AUTO_INCREMENT,
    code       VARCHAR(50) UNIQUE,
    discount   DECIMAL(5, 2),
    type       ENUM('PERCENT','FIXED'),
    expired_at DATE
);


CREATE TABLE branches
(
    id      INT PRIMARY KEY AUTO_INCREMENT,
    name    VARCHAR(100),
    address TEXT
);
-- ================= USERS =================
CREATE TABLE users
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    email       VARCHAR(255) NOT NULL UNIQUE,
    username    VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    first_name  VARCHAR(255) NOT NULL,
    last_name   VARCHAR(255) NOT NULL,
    role        ENUM ('ADMIN','STAFF','CUSTOMER') NOT NULL,
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    status      ENUM ('ACTIVED','LOCKED')          NOT NULL DEFAULT 'ACTIVED',
    create_date DATETIME,
    fail_count  INT                   DEFAULT 0,
    lock_time   DATETIME
);
CREATE TABLE categories
(
    id   INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100)
);

CREATE TABLE foods
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(100)   NOT NULL,
    description TEXT,
    price       DECIMAL(10, 2) NOT NULL,
    image       VARCHAR(255),
    category_id INT            NOT NULL,
    branch_id   INT            NOT NULL,
    status      BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (category_id) REFERENCES categories (id),
    FOREIGN KEY (branch_id) REFERENCES branches (id)
);

CREATE TABLE carts
(
    id      INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT UNIQUE,
    FOREIGN KEY (user_id) REFERENCES users (id)
);
CREATE TABLE cart_items
(
    id       INT PRIMARY KEY AUTO_INCREMENT,
    cart_id  INT,
    food_id  INT,
    quantity INT,
    FOREIGN KEY (cart_id) REFERENCES carts (id),
    FOREIGN KEY (food_id) REFERENCES foods (id)
);

CREATE TABLE orders
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    user_id     INT NOT NULL,
    branch_id   INT NOT NULL,
    total_price DECIMAL(10, 2) DEFAULT 0,
    status      ENUM('PENDING','PREPARING','DELIVERING','COMPLETED','CANCELED') DEFAULT 'PENDING',
    created_at  TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    voucher_id  INT,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (branch_id) REFERENCES branches (id),
    FOREIGN KEY (voucher_id) REFERENCES vouchers (id)
);
-- CHI TIẾT ĐƠN HÀNG
CREATE TABLE order_details
(
    id       INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT            NOT NULL,
    food_id  INT            NOT NULL,
    quantity INT            NOT NULL,
    price    DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
    FOREIGN KEY (food_id) REFERENCES foods (id)
);
-- THANH TOÁN
CREATE TABLE payments
(
    id       INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT,
    method   ENUM('COD','ONLINE'),
    status   ENUM('PENDING','PAID','FAILED'),
    paid_at  TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders (id)
);
-- YÊU THÍCH
CREATE TABLE favorites
(
    id      INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    food_id INT,
    UNIQUE (user_id, food_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (food_id) REFERENCES foods (id)
);
-- KHO VÀ NGUYÊN LIỆU
CREATE TABLE ingredients
(
    id   INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100)
);

CREATE TABLE inventory
(
    id            INT PRIMARY KEY AUTO_INCREMENT,
    ingredient_id INT,
    branch_id     INT,
    quantity      INT DEFAULT 0,
    UNIQUE (ingredient_id, branch_id),
    FOREIGN KEY (ingredient_id) REFERENCES ingredients (id),
    FOREIGN KEY (branch_id) REFERENCES branches (id)
);

-- ================= OTP TABLE =================
CREATE TABLE otps
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    email      VARCHAR(255) NOT NULL,
    otp        INT          NOT NULL,
    type       VARCHAR(50),
    expire_at  DATETIME,
    created_at DATETIME,
    FOREIGN KEY (email) REFERENCES users (email)
);

INSERT INTO vouchers (code, discount, type, expired_at)
VALUES ('SALE10', 10, 'PERCENT', '2026-12-31'),
       ('SALE20', 20, 'PERCENT', '2026-12-31'),
       ('FIX50', 50, 'FIXED', '2026-12-31'),
       ('FIX100', 100, 'FIXED', '2026-12-31'),
       ('NEWUSER', 15, 'PERCENT', '2026-12-31'),
       ('VIP', 25, 'PERCENT', '2026-12-31');

INSERT INTO branches (name, address)
VALUES ('Chi nhánh Hà Nội', 'Hà Nội'),
       ('Chi nhánh Hồ Chí Minh', 'TP.HCM'),
       ('Chi nhánh Đà Nẵng', 'Đà Nẵng'),
       ('Chi nhánh Hải Phòng', 'Hải Phòng'),
       ('Chi nhánh Cần Thơ', 'Cần Thơ'),
       ('Chi nhánh Huế', 'Huế');

INSERT INTO users (email, username, password, first_name, last_name, role)
VALUES ('admin@gmail.com', 'admin', '1234', 'Ngo', 'Trupng', 'ADMIN'),
       ('staff1@gmail.com', 'staff1', '1234', 'Nguyen', 'Van A', 'STAFF'),
       ('staff2@gmail.com', 'staff2', '1234', 'Tran', 'Van B', 'STAFF'),
       ('user1@gmail.com', 'user1', '1234', 'Le', 'Van C', 'CUSTOMER'),
       ('user2@gmail.com', 'user2', '1234', 'Pham', 'Van D', 'CUSTOMER'),
       ('user3@gmail.com', 'user3', '1234', 'Hoang', 'Van E', 'CUSTOMER');

INSERT INTO categories (name)
VALUES ('Đồ ăn nhanh'),
       ('Đồ uống'),
       ('Món chính'),
       ('Tráng miệng'),
       ('Ăn vặt'),
       ('Combo');

INSERT INTO foods (name, description, price, category_id, branch_id)
VALUES ('Hamburger', 'Burger bò', 50000, 1, 1),
       ('Pizza', 'Pizza hải sản', 120000, 3, 1),
       ('Trà sữa', 'Trà sữa trân châu', 40000, 2, 2),
       ('Cơm gà', 'Cơm gà chiên', 60000, 3, 3),
       ('Bánh ngọt', 'Bánh kem', 45000, 4, 2),
       ('Khoai tây chiên', 'Snack', 30000, 5, 1);

INSERT INTO carts (user_id)
VALUES (4),
       (5),
       (6),
       (2),
       (3),
       (1);

INSERT INTO cart_items (cart_id, food_id, quantity)
VALUES (1, 1, 2),
       (1, 2, 1),
       (2, 3, 2),
       (3, 4, 1),
       (4, 5, 3),
       (5, 6, 2);

INSERT INTO orders (user_id, branch_id, total_price, status, voucher_id)
VALUES (4, 1, 100000, 'PENDING', 1),
       (5, 2, 150000, 'PREPARING', 2),
       (6, 3, 200000, 'DELIVERING', 3),
       (4, 1, 120000, 'COMPLETED', 4),
       (5, 2, 90000, 'CANCELED', NULL),
       (6, 3, 300000, 'PENDING', 5);

INSERT INTO order_details (order_id, food_id, quantity, price)
VALUES (1, 1, 2, 50000),
       (2, 2, 1, 120000),
       (3, 3, 2, 40000),
       (4, 4, 2, 60000),
       (5, 5, 1, 45000),
       (6, 6, 3, 30000);

INSERT INTO payments (order_id, method, status)
VALUES (1, 'COD', 'PENDING'),
       (2, 'ONLINE', 'PAID'),
       (3, 'ONLINE', 'PAID'),
       (4, 'COD', 'PAID'),
       (5, 'COD', 'FAILED'),
       (6, 'ONLINE', 'PENDING');

INSERT INTO favorites (user_id, food_id)
VALUES (4, 1),
       (4, 2),
       (5, 3),
       (5, 4),
       (6, 5),
       (6, 6);

INSERT INTO ingredients (name)
VALUES ('Thịt bò'),
       ('Bột mì'),
       ('Sữa'),
       ('Trà'),
       ('Gà'),
       ('Khoai tây');

INSERT INTO inventory (ingredient_id, branch_id, quantity)
VALUES (1, 1, 100),
       (2, 1, 200),
       (3, 2, 150),
       (4, 2, 120),
       (5, 3, 180),
       (6, 1, 300);


update users
set password ='$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne'
where id = 1; -- ADMIN:1234
update users
set password ='$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne'
where id = 2; -- STAFF:1234
update users
set password ='$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne'
where id = 3; -- STAFF:1234
update users
set password ='$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne'
where id = 4; -- CUSTOMER:1234
update users
set password ='$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne'
where id = 5; -- CUSTOMER:1234
update users
set password ='$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne'
where id = 6; -- CUSTOMER:1234


update users
set email ='ngoquangtruongjk05@gmail.com'
where id = 1;

select*
from users
