DROP TABLE IF EXISTS sign;
CREATE TABLE IF NOT EXISTS sign (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50),
    password VARCHAR(200),
    email VARCHAR(50),
    phone VARCHAR(20),
    salt VARCHAR(200)
);
# Insert into sign (username, password, email, phone) values ('admin', '123456', 'admin@admin.com', '1234567890');

DROP TABLE IF EXISTS user;
CREATE TABLE IF NOT EXISTS user (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    uid INT UNSIGNED,
    user_account VARCHAR(50),
    username VARCHAR(50),
    email VARCHAR(50),
    phone VARCHAR(20),
    create_time DATETIME,
    role VARCHAR(20),
    status VARCHAR(20),
    avatar VarChar(200)
);

DROP TABLE IF EXISTS picture;
CREATE TABLE IF NOT EXISTS picture(
    id  INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    pid INT UNSIGNED,
    name VARCHAR(50),
    pic_data LONGBLOB
)