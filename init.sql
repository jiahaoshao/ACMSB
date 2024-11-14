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