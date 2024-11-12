DROP TABLE IF EXISTS user;
CREATE TABLE IF NOT EXISTS user (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50),
    password VARCHAR(50),
    email VARCHAR(50),
    phone VARCHAR(20)
);
Insert into user (username, password, email, phone) values ('admin', 'admin', 'admin@admin.com', '1234567890');