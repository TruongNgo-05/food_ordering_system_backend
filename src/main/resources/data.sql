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

    full_name   VARCHAR(255),
    phone       VARCHAR(20),
    avatar      TEXT,

    role        ENUM('ADMIN','STAFF','CUSTOMER') DEFAULT 'CUSTOMER',

    is_active   BOOLEAN  DEFAULT TRUE,

    status      ENUM('ACTIVED','LOCKED') DEFAULT 'ACTIVED',

    create_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    fail_count  INT      DEFAULT 0,
    lock_time   DATETIME
);
-- ================= USER ADDRESSES =================
CREATE TABLE user_addresses
(
    id             INT PRIMARY KEY AUTO_INCREMENT,
    user_id        INT,
    receiver_name  VARCHAR(255),
    receiver_phone VARCHAR(20),
    address        TEXT,
    is_default     BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
-- ================= CATEGORY =================
CREATE TABLE categories
(
    id   INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) UNIQUE
);

-- ================= TABLES =================
CREATE TABLE table_details
(
    id           INT PRIMARY KEY AUTO_INCREMENT,
    table_number VARCHAR(10) UNIQUE,
    capacity     INT,
    qr_code      TEXT,
    status       ENUM('AVAILABLE','OCCUPIED','RESERVED') DEFAULT 'AVAILABLE',
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
    description     TEXT,

    discount        DECIMAL(10, 2),
    min_order_value DECIMAL(10, 2),

    usage_limit     INT,
    used_count      INT      DEFAULT 0,

    start_date      DATETIME,
    end_date        DATETIME,
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ================= FOODS =================
CREATE TABLE foods
(
    id          INT PRIMARY KEY AUTO_INCREMENT,

    name        VARCHAR(255),
    description TEXT,

    price       DECIMAL(10, 2),

    image       VARCHAR(255), -- ảnh chính

    category_id INT,

    rating      DECIMAL(3, 2) DEFAULT 0,
    sold_count  INT           DEFAULT 0,

    status      BOOLEAN       DEFAULT TRUE,
    created_at  DATETIME      DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories (id)
);

-- ================= FOOD IMAGES (ảnh phụ) =================
CREATE TABLE food_images
(
    id        INT PRIMARY KEY AUTO_INCREMENT,
    food_id   INT,
    image_url VARCHAR(255),
    FOREIGN KEY (food_id) REFERENCES foods (id) ON DELETE CASCADE
);

-- ================= CART =================
CREATE TABLE carts
(
    id      INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT UNIQUE,
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
    customer_name     varchar(30),
    customer_phone    varchar(30),

    discount          DECIMAL(10, 2),
    total_price       DECIMAL(10, 2),

    status            ENUM('PENDING','CONFIRMED','PREPARING','DELIVERING','COMPLETED','CANCELED','REJECTED') DEFAULT 'PENDING',

    note              Text,

    payment_method_id INT,
    voucher_id        INT,
    table_id          INT,
    address_id        int,

    created_at        DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (payment_method_id) REFERENCES payment_methods (id),
    FOREIGN KEY (voucher_id) REFERENCES vouchers (id),
    FOREIGN KEY (table_id) REFERENCES table_details (id),
    FOREIGN KEY (address_id) REFERENCES user_addresses (id)
);

-- ================= ORDER DETAILS =================
CREATE TABLE order_details
(
    id       INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT,
    food_id  INT,
    quantity INT,
    price    DECIMAL(10, 2),
    FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
    FOREIGN KEY (food_id) REFERENCES foods (id)
);

-- ================= PAYMENTS =================
CREATE TABLE payments
(
    id                INT PRIMARY KEY AUTO_INCREMENT,
    order_id          INT unique,
    payment_method_id INT,
    status            ENUM('PENDING','PAID','FAILED'),
    paid_at           DATETIME,
    transaction_id    VARCHAR(255),
    created_at        DATETIME,
    FOREIGN KEY (order_id) REFERENCES orders (id),
    FOREIGN KEY (payment_method_id) REFERENCES payment_methods (id)
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

-- ================= ĐáNH GIÁ VÀ BÌNH LUẬN =================
CREATE TABLE reviews
(
    id         INT PRIMARY KEY AUTO_INCREMENT,
    food_id    INT NOT NULL,
    user_id    INT NOT NULL,
    rating     DECIMAL(3, 1) CHECK (rating >= 1 AND rating <= 5),
    comment    TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (food_id) REFERENCES foods (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    UNIQUE KEY unique_review (food_id, user_id)
);
CREATE TABLE banners
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    image_url   TEXT         NOT NULL,
    is_active   BOOLEAN DEFAULT TRUE
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

CREATE INDEX idx_orders_user_id ON orders (user_id);
CREATE INDEX idx_orders_status ON orders (status);
CREATE INDEX idx_order_details_order_id ON order_details (order_id);
CREATE INDEX idx_foods_category_id ON foods (category_id);
CREATE INDEX idx_reviews_food_id ON reviews (food_id);
-- ================= DỮ LIỆU MẪU =================
-- ================= PAYMENT METHODS =================
INSERT INTO payment_methods (name, code, is_active)
VALUES ('Thanh toán khi nhận hàng', 'COD', TRUE),
       ('Thanh toán online', 'ONLINE', TRUE),
       ('Thanh toán Tiền mặt tại bàn', 'AT_TABLE', TRUE);

-- ================= USERS =================
INSERT INTO users (email, username, password, full_name, role, phone)
VALUES ('ngoquangtruongjk05@gmail.com', 'admin', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne',
        'Ngo Truong', 'ADMIN', '0900000001'),
       ('user1@gmail.com', 'user1', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Le Van A',
        'CUSTOMER', '0900000002'),
       ('user2@gmail.com', 'user2', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Pham Van B',
        'CUSTOMER', '0900000003'),
       ('user3@gmail.com', 'user3', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Hoang Van C',
        'STAFF', '0900000004');

INSERT INTO user_addresses (user_id,
                            receiver_name,
                            receiver_phone,
                            address,
                            is_default)
VALUES (2, 'Lê Văn A', '0900000002',
        '12 Lý Thường Kiệt, Hoàn Kiếm, Hà Nội', TRUE),

       (2, 'Lê Văn A', '0900000002',
        '45 Trần Phú, Ba Đình, Hà Nội', FALSE),

       (3, 'Phạm Văn B', '0900000003',
        '88 Nguyễn Huệ, Quận 1, TP.HCM', TRUE);
-- ================= CATEGORIES =================
INSERT INTO categories (name)
VALUES ('Đồ ăn nhanh'),
       ('Đồ uống'),
       ('Món chính');

-- ================= TABLE =================
INSERT INTO table_details (table_number, capacity)
VALUES ('T01', 2),
       ('T02', 4),
       ('T03', 6);

-- ================= VOUCHERS =================
INSERT INTO vouchers (code, discount, min_order_value, usage_limit)
VALUES ('WELCOME10', 10.00, 100000, 100),
       ('GIAM50K', 50000, 200000, 50),
       ('SALE20', 20.00, 150000, 200);
-- ================= FOODS (10 món mới) =================
INSERT INTO foods (name, description, price, image, category_id, rating, sold_count, status)
VALUES

-- Món chính (category_id = 3)
('Bún bò Huế',
 'Bún bò cay đặc trưng xứ Huế, nước dùng đậm đà với sả và mắm ruốc',
 75000,
 'https://images.unsplash.com/photo-1555126634-323283e090fa?w=600&auto=format&fit=crop',
 3, 4.7, 180, TRUE),

('Bánh mì thịt',
 'Bánh mì giòn nhân thịt nguội, chả lụa, rau sống và tương ớt',
 35000,
 'https://images.unsplash.com/photo-1600628421066-f6bfd0aa1dbb?w=600&auto=format&fit=crop',
 3, 4.5, 420, false),

('Cơm chiên dương châu',
 'Cơm chiên với tôm, thịt, trứng và rau củ tươi ngon',
 65000,
 'https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=600&auto=format&fit=crop',
 3, 4.3, 150, TRUE),

('Mì Quảng',
 'Mì Quảng trứ danh với nước lèo đậm vị, tôm thịt và bánh đa',
 70000,
 'https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=600&auto=format&fit=crop',
 3, 4.6, 95, TRUE),

('Bún chả Hà Nội',
 'Bún chả đặc sản Hà Nội, chả nướng thơm ngon chấm nước mắm chua ngọt',
 80000,
 'https://images.unsplash.com/photo-1547592166-23ac45744acd?w=600&auto=format&fit=crop',
 3, 4.8, 260, TRUE),

-- Đồ ăn nhanh (category_id = 1)
('Gỏi cuốn tôm thịt',
 'Gỏi cuốn tươi nhân tôm, thịt heo, bún và rau thơm, chấm tương đậu phộng',
 45000,
 'https://images.unsplash.com/photo-1562802378-063ec186a863?w=600&auto=format&fit=crop',
 1, 4.4, 310, TRUE),

('Chả giò chiên',
 'Chả giò vàng giòn nhân thịt heo, miến, mộc nhĩ và rau củ',
 40000,
 'https://images.unsplash.com/photo-1607330289024-1535c6b4e1c1?w=600&auto=format&fit=crop',
 1, 4.2, 275, TRUE),

('Bánh xèo miền Nam',
 'Bánh xèo giòn rụm nhân tôm thịt, giá đỗ, cuốn với rau sống và nước chấm',
 55000,
 'https://images.unsplash.com/photo-1585032226651-759b368d7246?w=600&auto=format&fit=crop',
 1, 4.5, 130, TRUE),

-- Đồ uống (category_id = 2)
('Cà phê sữa đá',
 'Cà phê phin truyền thống pha với sữa đặc, phục vụ với đá viên',
 35000,
 'https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=600&auto=format&fit=crop',
 2, 4.9, 680, TRUE),

('Sinh tố bơ',
 'Sinh tố bơ béo ngậy xay kem với sữa đặc, thơm ngon bổ dưỡng',
 45000,
 'https://images.unsplash.com/photo-1638176066666-ffb2f013c7dd?w=600&auto=format&fit=crop',
 2, 4.6, 220, TRUE);
-- ================= FOOD IMAGES =================
-- ================= FOOD IMAGES (ảnh phụ) =================
-- Food ID 1: Bún bò Huế
INSERT INTO food_images (food_id, image_url)
VALUES (1, 'https://images.unsplash.com/photo-1582878826629-29b7ad1cdc43?w=600&auto=format&fit=crop'),
       (1, 'https://images.unsplash.com/photo-1569058242272-fb78b5bea6c0?w=600&auto=format&fit=crop'),

-- Food ID 2: Bánh mì thịt
       (2, 'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=600&auto=format&fit=crop'),
       (2, 'https://images.unsplash.com/photo-1509722747041-616f39b57569?w=600&auto=format&fit=crop'),

-- Food ID 3: Cơm chiên dương châu
       (3, 'https://images.unsplash.com/photo-1596560548464-f010c64e46c3?w=600&auto=format&fit=crop'),
       (3, 'https://images.unsplash.com/photo-1512058564366-18510be2db19?w=600&auto=format&fit=crop'),

-- Food ID 4: Mì Quảng
       (4, 'https://images.unsplash.com/photo-1614563637806-1d0e645e0940?w=600&auto=format&fit=crop'),
       (4, 'https://images.unsplash.com/photo-1527477396000-e27163b481c2?w=600&auto=format&fit=crop'),

-- Food ID 5: Bún chả Hà Nội
       (5, 'https://images.unsplash.com/photo-1585273931648-59b23b2f9a6b?w=600&auto=format&fit=crop'),
       (5, 'https://images.unsplash.com/photo-1519984388953-d2406bc725e1?w=600&auto=format&fit=crop'),

-- Food ID 6: Gỏi cuốn tôm thịt
       (6, 'https://images.unsplash.com/photo-1541696432-82c6da8ce7bf?w=600&auto=format&fit=crop'),
       (6, 'https://images.unsplash.com/photo-1600850056064-a8b29d3afbe6?w=600&auto=format&fit=crop'),

-- Food ID 7: Chả giò chiên
       (7, 'https://images.unsplash.com/photo-1616645258469-ec681c17f3ee?w=600&auto=format&fit=crop'),
       (7, 'https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=600&auto=format&fit=crop'),

-- Food ID 8: Bánh xèo miền Nam
       (8, 'https://images.unsplash.com/photo-1565557623262-b51c2513a641?w=600&auto=format&fit=crop'),
       (8, 'https://images.unsplash.com/photo-1498654896293-37aacf113fd9?w=600&auto=format&fit=crop'),

-- Food ID 9: Cà phê sữa đá
       (9, 'https://images.unsplash.com/photo-1461023058943-07fcbe16d735?w=600&auto=format&fit=crop'),
       (9, 'https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=600&auto=format&fit=crop'),

-- Food ID 10: Sinh tố bơ
       (10, 'https://images.unsplash.com/photo-1623065422902-30a2d299bbe4?w=600&auto=format&fit=crop'),
       (10, 'https://images.unsplash.com/photo-1553530666-ba11a7da3888?w=600&auto=format&fit=crop');

-- ================= CARTS =================
INSERT INTO carts (user_id)
VALUES (2),
       (3),
       (1);

-- ================= CART ITEMS =================
INSERT INTO cart_items (cart_id, food_id, quantity)
VALUES (1, 1, 2),
       (1, 3, 1),
       (2, 2, 3);

-- ================= ORDERS =================
INSERT INTO orders (order_code,
                    user_id,
                    address_id,
                    discount,
                    total_price,
                    status,
                    payment_method_id,
                    voucher_id,
                    table_id)
VALUES ('ORD-20240601-001', 2, 1, 0, 195000, 'COMPLETED', 1, NULL, NULL),

       ('ORD-20240601-002', 3, 3, 50000, 175000, 'CONFIRMED', 3, 2, 2),

       ('ORD-20240602-001', 2, 1, 17000, 178000, 'PENDING', 2, 1, NULL);
-- ================= ORDER DETAILS =================
INSERT INTO order_details (order_id, food_id, quantity, price)
VALUES (1, 1, 2, 85000),
       (1, 3, 1, 45000),
       (2, 2, 3, 75000);

-- ================= PAYMENTS =================
INSERT INTO payments (order_id, payment_method_id, status, paid_at)
VALUES (1, 1, 'PAID', '2024-06-01 12:30:00'),
       (2, 3, 'PAID', '2024-06-01 14:00:00'),
       (3, 2, 'PENDING', NULL);

-- ================= FAVORITES =================
INSERT INTO favorites (user_id, food_id)
VALUES (2, 1),
       (2, 3),
       (3, 2);

-- ================= REVIEWS =================
INSERT INTO reviews (food_id, user_id, rating, comment)
VALUES (1, 2, 5.0, 'Phở rất ngon, nước dùng đậm đà, sẽ quay lại!'),
       (2, 3, 3.0, 'Cơm tấm vừa miệng, sườn nướng thơm.'),
       (3, 2, 4.0, 'Trà sữa ngon nhưng hơi ngọt.'),
       (1, 1, 2.0, 'Phở rất ngon, nước dùng đậm đà, sẽ quay lại!');
-- Banner
INSERT INTO banners (id, title, description, image_url, is_active)
VALUES (1,
        'Ăn gì hôm nay?',
        'Khám phá thực đơn món Việt đa dạng, giao tận nơi chỉ trong vài phút.',
        'https://images.unsplash.com/photo-1504674900247-0877df9cc836?q=80&w=1920&auto=format&fit=crop',
        TRUE),

       (2,
        'Món ngon mỗi ngày',
        'Từ bữa sáng đến bữa tối, luôn có món hợp vị cho bạn.',
        'https://images.unsplash.com/photo-1498654896293-37aacf113fd9?q=80&w=1920&auto=format&fit=crop',
        TRUE),

       (3,
        'Đặt nhanh trong 1 chạm',
        'Ưu đãi hấp dẫn, thanh toán tiện lợi, giao hàng siêu tốc.',
        'https://images.unsplash.com/photo-1482049016688-2d3e1b311543?q=80&w=1920&auto=format&fit=crop',
        TRUE);

-- Xem user
SELECT *
FROM users;
-- select * from foods;
-- select * from food_images;
-- select * from vouchers;
SELECT *
FROM user_addresses;
select*
from carts;
select*
from cart_items;
-- select * from favorites
SELECT *
FROM ORDERS;
SELECT *
FROM payment_methods;
select *
from table_details