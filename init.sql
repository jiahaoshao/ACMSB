#DROP TABLE IF EXISTS sign;
CREATE TABLE IF NOT EXISTS sign
(
    id       INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50),
    password VARCHAR(200),
    email    VARCHAR(50),
    phone    VARCHAR(20),
    salt     VARCHAR(200)
);

#DROP TABLE IF EXISTS user;
CREATE TABLE IF NOT EXISTS user
(
    id           INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    uid          INT UNSIGNED,
    user_account VARCHAR(50),
    username     VARCHAR(50),
    email        VARCHAR(50),
    phone        VARCHAR(20),
    create_time  DATETIME,
    role         VARCHAR(20),
    status       VARCHAR(20),
    avatar       VarChar(200)
);

#DROP TABLE IF EXISTS article;
CREATE TABLE IF NOT EXISTS article
(
    aid         INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(50),
    content     VARCHAR(10000),
    authorid    INT UNSIGNED,
    create_time DATETIME,
    classify    VARCHAR(200),
    tags        JSON,
    status      VARCHAR(20)
) CHARACTER SET utf8
  COLLATE utf8_general_ci;


DROP TABLE IF EXISTS chat_request_copy;
CREATE TABLE IF NOT EXISTS chat_request_copy
(
    id       INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    chatid   INT NOT NULL,
    uid      INT NOT NULL,
    messages TEXT
);

DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
                            `id` int(0) NOT NULL AUTO_INCREMENT COMMENT 'id',
                            `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评论内容',
                            `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户名称',
                            `user_id` int(0) NULL DEFAULT NULL COMMENT '用户id',
                            `rate` decimal(10, 1) NULL DEFAULT NULL COMMENT '评分',
                            `foreign_id` int(0) NULL DEFAULT NULL COMMENT '业务模块的id',
                            `pid` int(0) NULL DEFAULT NULL COMMENT '父级评论id',
                            `target` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '回复对象',
                            `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

INSERT INTO `comment` VALUES (1, 'eee', '张三', 1000001, 5.0, 1, NULL, NULL, '2023-04-21 22:18:45');
INSERT INTO `comment` VALUES (2, '3333', '张三', 1000001, 5.0, 1, NULL, NULL, '2023-04-22 11:35:22');
INSERT INTO `comment` VALUES (3, '7777', '张三', 1000001, 5.0, 1, NULL, NULL, '2023-04-22 11:35:42');
INSERT INTO `comment` VALUES (4, '66', '张三', 1000001, 5.0, 1, NULL, NULL, '2023-04-22 11:35:51');
INSERT INTO `comment` VALUES (5, '666', '张三', 1000001, 5.0, 1, NULL, NULL, '2023-04-22 11:35:56');
INSERT INTO `comment` VALUES (6, '1111', '李四', 1000002, NULL, 1, 1, '张三', '2023-04-22 14:50:33');
INSERT INTO `comment` VALUES (7, '456', '李四', 1000002, NULL, 1, 1, '张三', '2023-04-22 14:51:59');
INSERT INTO `comment` VALUES (8, '我在回复李四了', '李四', 1000002, NULL, 1, 1, '李四', '2023-04-22 15:25:26');
INSERT INTO `comment` VALUES (9, '555', '李四', 1000002, NULL, 1, 1, '李四', '2023-04-22 16:35:42');
INSERT INTO `comment` VALUES (10, '对对对', '张三', 1000001, 5.0, 1, NULL, NULL, '2023-04-22 16:35:48');
