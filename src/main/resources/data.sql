DROP
DATABASE IF EXISTS base_system;
CREATE
DATABASE base_system;
USE
base_system;

-- ================= USERS =================
CREATE TABLE users
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    email       VARCHAR(255)                       NOT NULL UNIQUE,
    username    VARCHAR(255)                       NOT NULL UNIQUE,
    password    VARCHAR(255)                       NOT NULL,
    first_name  VARCHAR(255)                       NOT NULL,
    last_name   VARCHAR(255)                       NOT NULL,
    role        ENUM ('ADMIN','STAFF','CUSTOMER') NOT NULL,
    is_active   BOOLEAN                            NOT NULL DEFAULT TRUE,
    status      ENUM ('ACTIVED','LOCKED')          NOT NULL DEFAULT 'ACTIVED',
    create_date DATETIME,
    fail_count  INT                                         DEFAULT 0,
    lock_time   DATETIME
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