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
    role        ENUM('ADMIN','CUSTOMER') DEFAULT 'CUSTOMER',
    avatar      TEXT,
    phone       VARCHAR(20),
    is_active   BOOLEAN  DEFAULT TRUE,
    status      ENUM('ACTIVED','LOCKED') DEFAULT 'ACTIVED',
    create_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    fail_count  INT      DEFAULT 0,
    lock_time   DATETIME
);
-- ================= USER ADDRESSES =================
CREATE TABLE user_addresses
(
    id         INT PRIMARY KEY AUTO_INCREMENT,
    user_id    INT,
    address    TEXT,
    is_default BOOLEAN DEFAULT FALSE,
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
    usage_limit     INT,
    used_count      INT      DEFAULT 0,
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

    discount          DECIMAL(10, 2),
    shipping_fee      DECIMAL(10, 2),
    total_price       DECIMAL(10, 2),

    status            ENUM('PENDING','CONFIRMED','PREPARING','DELIVERING','COMPLETED','CANCELED','REJECTED') DEFAULT 'PENDING',

    payment_method_id INT,
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
-- ================= INVENTORY =================
CREATE TABLE inventory
(
    id         INT PRIMARY KEY AUTO_INCREMENT,
    food_id    INT NOT NULL UNIQUE,
    quantity   INT      DEFAULT 0 CHECK (quantity >= 0),
    status     ENUM('IN_STOCK','OUT_OF_STOCK') DEFAULT 'IN_STOCK',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (food_id) REFERENCES foods (id) ON DELETE CASCADE
);

-- ================= NHẬP KHO =================
CREATE TABLE stock_imports
(
    id           INT PRIMARY KEY AUTO_INCREMENT,
    food_id      INT NOT NULL,
    admin_id     INT NOT NULL,
    quantity     INT,
    import_price DECIMAL(10, 2),
    note         TEXT,
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (food_id) REFERENCES foods (id),
    FOREIGN KEY (admin_id) REFERENCES users (id)
);

-- ================= LOG KHO =================
CREATE TABLE stock_logs
(
    id              INT PRIMARY KEY AUTO_INCREMENT,
    food_id         INT NOT NULL,
    order_id        INT NULL,
    user_id         INT,
    change_quantity INT, -- + nhập, - bán
    type            ENUM('IMPORT','ORDER','CANCEL'),
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (food_id) REFERENCES foods (id) ON DELETE CASCADE,
    FOREIGN KEY (order_id) REFERENCES orders (id),
    FOREIGN KEY (user_id) REFERENCES users (id)
);
-- ================= PAYMENTS =================
CREATE TABLE payments
(
    id                INT PRIMARY KEY AUTO_INCREMENT,
    order_id          INT unique,
    payment_method_id INT,
    status            ENUM('PENDING','PAID','FAILED'),
    paid_at           DATETIME,
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
CREATE INDEX idx_stock_logs_food_id ON stock_logs (food_id);

-- ================= DỮ LIỆU MẪU =================
-- ================= PAYMENT METHODS =================
INSERT INTO payment_methods (name, code, is_active)
VALUES ('Thanh toán khi nhận hàng', 'COD', TRUE),
       ('Thanh toán online', 'ONLINE', TRUE),
       ('Thanh toán tại bàn', 'AT_TABLE', TRUE);

-- ================= USERS =================
INSERT INTO users (email, username, password, full_name, role, phone)
VALUES ('ngoquangtruongjk05@gmail.com', 'admin', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne',
        'Ngo Truong', 'ADMIN', '0900000001'),
       ('user1@gmail.com', 'user1', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Le Van A',
        'CUSTOMER', '0900000002'),
       ('user2@gmail.com', 'user2', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Pham Van B',
        'CUSTOMER', '0900000003'),
       ('user3@gmail.com', 'user3', '$2a$10$nlMnkBVDx81dyJ9puJyf8.FWUOiOjJTb4M4RggYlPDuxFDgtxb.ne', 'Hoang Van C',
        'CUSTOMER', '0900000004');

INSERT INTO user_addresses (user_id, address, is_default)
VALUES (2, '12 Lý Thường Kiệt, Hoàn Kiếm, Hà Nội', TRUE),
       (2, '45 Trần Phú, Ba Đình, Hà Nội', FALSE),
       (3, '88 Nguyễn Huệ, Quận 1, TP.HCM', TRUE);
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
INSERT INTO vouchers (code, discount, type, min_order_value, max_discount, usage_limit, expired_at)
VALUES ('WELCOME10', 10.00, 'PERCENT', 100000, 50000, 100, '2025-12-31 23:59:59'),
       ('GIAM50K', 50000, 'FIXED', 200000, 50000, 50, '2025-06-30 23:59:59'),
       ('SALE20', 20.00, 'PERCENT', 150000, 80000, 200, '2025-09-30 23:59:59');

-- ================= FOODS =================
INSERT INTO foods (name, description, price, image, category_id, rating, sold_count, status)
VALUES ('Phở bò đặc biệt', 'Phở bò tái chín với nước dùng hầm 12 tiếng', 85000, 'pho-bo.jpg', 1, 4.8, 320, TRUE),
       ('Cơm tấm sườn bì', 'Cơm tấm kèm sườn nướng, bì, chả trứng', 75000, 'com-tam.jpg', 1, 4.6, 210, TRUE),
       ('Trà sữa trân châu', 'Trà sữa Đài Loan, topping trân châu đen', 45000, 'tra-sua.jpg', 2, 4.7, 540, TRUE);

-- ================= FOOD IMAGES =================
INSERT INTO food_images (food_id, image_url, is_primary)
VALUES (1, 'pho-bo-1.jpg', TRUE),
       (1, 'pho-bo-2.jpg', FALSE),
       (2, 'com-tam-1.jpg', TRUE);

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
INSERT INTO orders (order_code, user_id, customer_name, customer_phone, delivery_address, order_type, discount,
                    shipping_fee, total_price, status, payment_method_id, voucher_id, table_id)
VALUES ('ORD-20240601-001', 2, 'Trần Thị Lan', '0902222222', '12 Lý Thường Kiệt, Hà Nội', 'DELIVERY', 0, 25000, 195000,
        'COMPLETED', 1, NULL, NULL),
       ('ORD-20240601-002', 3, 'Lê Minh Tuấn', '0903333333', NULL, 'DINE_IN', 50000, 0, 175000, 'CONFIRMED', 3, 2, 2),
       ('ORD-20240602-001', 2, 'Trần Thị Lan', '0902222222', '45 Trần Phú, Hà Nội', 'DELIVERY', 17000, 25000, 178000,
        'PENDING', 2, 1, NULL);

-- ================= ORDER DETAILS =================
INSERT INTO order_details (order_id, food_id, quantity, unit_price, subtotal)
VALUES (1, 1, 2, 85000, 170000),
       (1, 3, 1, 45000, 45000),
       (2, 2, 3, 75000, 225000);

-- ================= INVENTORY =================
INSERT INTO inventory (food_id, quantity, status)
VALUES (1, 50, 'IN_STOCK'),
       (2, 30, 'IN_STOCK'),
       (3, 0, 'OUT_OF_STOCK');

-- ================= STOCK IMPORTS =================
INSERT INTO stock_imports (food_id, admin_id, quantity, import_price, note)
VALUES (1, 1, 100, 30000, 'Nhập nguyên liệu phở đầu tháng 6'),
       (2, 1, 80, 25000, 'Nhập nguyên liệu cơm tấm'),
       (3, 1, 200, 10000, 'Nhập nguyên liệu trà sữa');

-- ================= STOCK LOGS =================
INSERT INTO stock_logs (food_id, order_id, user_id, change_quantity, type)
VALUES (1, NULL, 1, 100, 'IMPORT'),
       (1, 1, 2, -2, 'ORDER'),
       (2, 2, 3, -3, 'ORDER');

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
       (2, 3, 4.5, 'Cơm tấm vừa miệng, sườn nướng thơm.'),
       (3, 2, 4.0, 'Trà sữa ngon nhưng hơi ngọt.');

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
select *
from foods;
select *
from food_images;

select *
from reviews