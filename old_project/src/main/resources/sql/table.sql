/*
 Navicat Premium Dump SQL

 Source Server         : greatsql
 Source Server Type    : MySQL
 Source Server Version : 80032 (5.7)
 Source Host           : 192.168.44.135:3306
 Source Schema         : onlinebooking_system

 Target Server Type    : MySQL
 Target Server Version : 80032 (8.0.32-25)
 File Encoding         : 65001

 Date: 26/06/2025 09:36:30
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
                         `uid` int NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                         `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
                         `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
                         `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '昵称',
                         `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '电话号码',
                         `type` enum('normal','admin') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '类型',
                         `register_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
                         `version` int NULL DEFAULT 1 COMMENT '乐观锁',
                         `is_deleted` int NULL DEFAULT 0 COMMENT '逻辑删除字段 1 删除  0 未删除',
                         PRIMARY KEY (`uid`) USING BTREE,
                         UNIQUE INDEX `username`(`username` ASC) USING BTREE,
                         UNIQUE INDEX `phone`(`phone` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', '3af92164920998eec3c415a361c32be2', 'admin', '12345678910', 'admin', '2024-08-27 10:20:05', 6, 0);
INSERT INTO `user` VALUES (2, 'user', '3af92164920998eec3c415a361c32be2', 'Test666...', '19711111111', 'normal', '2024-08-27 10:26:02', 1, 0);

-- ----------------------------
-- Table structure for ticket
-- ----------------------------
DROP TABLE IF EXISTS `ticket`;
CREATE TABLE `ticket`  (
                           `tid` int NOT NULL AUTO_INCREMENT COMMENT '电影票ID',
                           `sid` int NOT NULL COMMENT '对应放映场次ID',
                           `uid` int NOT NULL COMMENT '所属用户ID',
                           `seat_number` int NOT NULL COMMENT '座位号',
                           `purchase_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
                           `price` decimal(10, 2) NOT NULL COMMENT '票价',
                           `order_status` enum('待支付','已支付','已完成','已取消') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '待支付' COMMENT '订单状态',
                           `version` int NULL DEFAULT 1 COMMENT '乐观锁',
                           `is_deleted` int NULL DEFAULT 0 COMMENT '逻辑删除字段 1 删除  0 未删除',
                           PRIMARY KEY (`tid`) USING BTREE,
                           INDEX `uid`(`uid` ASC) USING BTREE,
                           INDEX `ticket_ibfk_1`(`sid` ASC) USING BTREE,
                           CONSTRAINT `ticket_ibfk_1` FOREIGN KEY (`sid`) REFERENCES `screening` (`sid`) ON DELETE RESTRICT ON UPDATE CASCADE,
                           CONSTRAINT `ticket_ibfk_2` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 66 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ticket
-- ----------------------------
INSERT INTO `ticket` VALUES (60, 6, 2, 2, '2025-06-25 16:17:18', 60.00, '已取消', 1, 0);
INSERT INTO `ticket` VALUES (61, 1, 2, 1, '2025-06-25 16:41:05', 60.00, '已取消', 1, 0);
INSERT INTO `ticket` VALUES (62, 5, 2, 2, '2025-06-25 16:51:40', 60.00, '已取消', 1, 0);
INSERT INTO `ticket` VALUES (63, 1, 2, 5, '2025-06-25 17:17:36', 60.00, '已取消', 1, 0);
INSERT INTO `ticket` VALUES (64, 1, 2, 1, '2025-06-25 17:26:52', 60.00, '已支付', 2, 0);
INSERT INTO `ticket` VALUES (65, 1, 2, 2, '2025-06-26 09:10:43', 60.00, '已支付', 2, 0);

-- ----------------------------
-- Table structure for screening
-- ----------------------------
DROP TABLE IF EXISTS `screening`;
CREATE TABLE `screening`  (
                              `sid` int NOT NULL AUTO_INCREMENT COMMENT '放映场次ID',
                              `hid` int NOT NULL COMMENT '对应的影厅ID',
                              `mid` int NOT NULL COMMENT '对应的电影ID',
                              `show_time` datetime NOT NULL COMMENT '放映时间',
                              `end_time` datetime NOT NULL,
                              `price` decimal(10, 2) NOT NULL COMMENT '票价',
                              `remaining_seats` int NOT NULL COMMENT '剩余座位数',
                              `seat_count` int NOT NULL COMMENT '总票数',
                              `version` int NULL DEFAULT 1 COMMENT '乐观锁',
                              `is_deleted` int NULL DEFAULT 0 COMMENT '逻辑删除字段 1 删除  0 未删除',
                              PRIMARY KEY (`sid`) USING BTREE,
                              INDEX `hid`(`hid` ASC) USING BTREE,
                              INDEX `mid`(`mid` ASC) USING BTREE,
                              CONSTRAINT `screening_ibfk_1` FOREIGN KEY (`hid`) REFERENCES `hall` (`hid`) ON DELETE RESTRICT ON UPDATE CASCADE,
                              CONSTRAINT `screening_ibfk_2` FOREIGN KEY (`mid`) REFERENCES `movie` (`mid`) ON DELETE RESTRICT ON UPDATE CASCADE,
                              CONSTRAINT `seat_count` CHECK (`remaining_seats` >= 0)
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of screening
-- ----------------------------
INSERT INTO `screening` VALUES (1, 1, 1, '2025-07-04 07:00:00', '2025-07-04 08:50:00', 60.00, 133, 135, 22, 0);
INSERT INTO `screening` VALUES (2, 1, 2, '2025-07-04 09:00:00', '2025-07-04 11:33:00', 60.00, 135, 135, 17, 0);
INSERT INTO `screening` VALUES (3, 1, 3, '2025-07-04 14:00:00', '2025-07-04 16:04:00', 60.00, 135, 135, 12, 0);
INSERT INTO `screening` VALUES (4, 1, 1, '2025-07-05 16:10:00', '2025-07-05 18:00:00', 40.00, 134, 135, 8, 0);
INSERT INTO `screening` VALUES (5, 1, 2, '2025-07-05 18:30:00', '2025-07-05 21:03:00', 60.00, 135, 135, 9, 0);
INSERT INTO `screening` VALUES (6, 4, 3, '2025-07-05 08:00:00', '2025-07-05 10:04:00', 60.00, 149, 150, 9, 0);
INSERT INTO `screening` VALUES (7, 3, 6, '2025-07-05 08:10:00', '2025-07-05 10:37:00', 60.00, 120, 120, 10, 0);

-- ----------------------------
-- Table structure for movie
-- ----------------------------
DROP TABLE IF EXISTS `movie`;
CREATE TABLE `movie`  (
                          `mid` int NOT NULL AUTO_INCREMENT COMMENT '电影ID',
                          `mname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '电影名',
                          `director` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '导演',
                          `release_date` date NOT NULL COMMENT '发行日期',
                          `synopsis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '简介',
                          `poster` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '海报URL',
                          `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类型',
                          `runtime` int NULL DEFAULT NULL COMMENT '时长',
                          `version` int NULL DEFAULT 1 COMMENT '乐观锁',
                          `is_deleted` int NULL DEFAULT 0 COMMENT '逻辑删除字段 1 删除  0 未删除',
                          PRIMARY KEY (`mid`) USING BTREE,
                          UNIQUE INDEX `mname`(`mname` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of movie
-- ----------------------------
INSERT INTO `movie` VALUES (1, '哪吒重生', '赵霁', '2020-01-25', '讲述了在现代背景下，哪吒与敖丙的再次对决的故事。', 'https://example.com/poster/nezha-rebirth.jpg', '动画, 动作, 奇幻', 110, 2, 0);
INSERT INTO `movie` VALUES (2, '我和我的家乡', '宁浩, 徐峥, 陈思诚等', '2020-10-01', '通过五个短篇故事讲述中国不同地区的乡村生活变迁。', 'https://example.com/poster/my-hometown-and-me.jpg', '剧情, 喜剧', 153, 3, 0);
INSERT INTO `movie` VALUES (3, '刺杀小说家', '路阳', '2021-02-12', '一个作家的小说世界与现实世界的交错，展开了一场惊心动魄的冒险。', 'https://example.com/poster/assassin-novelist.jpg', '动作, 幻想', 124, 1, 0);
INSERT INTO `movie` VALUES (4, '寄生虫', '奉俊昊', '2019-05-30', '一家贫穷的家庭通过欺骗手段进入了一个富裕家庭的生活。', 'https://example.com/poster/parasite.jpg', '剧情, 黑色幽默, 悬疑', 131, 1, 0);
INSERT INTO `movie` VALUES (5, '1917', '萨姆·门德斯', '2020-01-10', '一战期间，两名士兵穿越敌区传递重要情报，以阻止一场致命的攻击。', 'https://example.com/poster/1917.jpg', '战争, 剧情', 119, 1, 0);
INSERT INTO `movie` VALUES (6, '八佰', '管虎', '2020-08-21', '淞沪会战中，四行仓库保卫战的真实历史事件。', 'https://example.com/poster/eight-hundred.jpg', '战争, 历史, 剧情', 147, 1, 0);
INSERT INTO `movie` VALUES (7, '黑寡妇', '凯特·肖特兰', '2021-07-09', '黑寡妇娜塔莎·罗曼诺夫面对过去的黑暗，与家人重逢并对抗敌人。', 'https://example.com/poster/black-widow.jpg', '动作, 冒险, 科幻', 134, 1, 0);
INSERT INTO `movie` VALUES (8, '夺冠', '陈可辛', '2020-09-25', '讲述中国女排从1981年首夺世界冠军到2016年里约奥运会的故事。', 'https://example.com/poster/champion.jpg', '体育, 剧情, 传记', 135, 1, 0);
INSERT INTO `movie` VALUES (9, '唐人街探案3', '陈思诚', '2021-02-12', '神探唐仁和秦风前往日本东京解决新的谜题。', 'https://example.com/poster/detective-chinatown-3.jpg', '喜剧, 探案, 动作', 136, 1, 0);
INSERT INTO `movie` VALUES (10, '信条', '克里斯托弗·诺兰', '2020-09-03', '一名特工通过逆向时间旅行来阻止第三次世界大战的发生。', 'https://example.com/poster/tenet.jpg', '科幻, 动作, 惊悚', 150, 1, 0);
INSERT INTO `movie` VALUES (11, '误杀', '柯汶利', '2019-12-13', '一位父亲为了保护家庭而制造了一起完美的犯罪。', 'https://example.com/poster/sheep-without-a-shepherd.jpg', '悬疑, 剧情, 犯罪', 112, 1, 0);
INSERT INTO `movie` VALUES (12, '姜子牙', '程腾, 李炜', '2020-10-01', '封神大战之后，被贬下凡间的姜子牙踏上寻找真相的旅途。', 'https://example.com/poster/legend-of-deification.jpg', '动画, 冒险, 奇幻', 110, 1, 0);
INSERT INTO `movie` VALUES (13, '疯狂原始人2', '乔尔·克劳福德', '2020-11-25', '原始人家族遇到另一个更先进的家族，开始了一系列冒险。', 'https://example.com/poster/the-croods-a-new-age.jpg', '动画, 喜剧, 冒险', 90, 1, 0);

-- ----------------------------
-- Table structure for hall
-- ----------------------------
DROP TABLE IF EXISTS `hall`;
CREATE TABLE `hall`  (
                         `hid` int NOT NULL AUTO_INCREMENT COMMENT '影厅ID',
                         `hname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '影厅名',
                         `row_num` int NOT NULL COMMENT '影厅座位排数',
                         `row_capacity` int NOT NULL COMMENT '影院每排座位数',
                         `screen_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '荧幕类型',
                         `version` int NULL DEFAULT 1 COMMENT '乐观锁',
                         `is_deleted` int NULL DEFAULT 0 COMMENT '逻辑删除字段 1 删除  0 未删除',
                         PRIMARY KEY (`hid`) USING BTREE,
                         UNIQUE INDEX `hname`(`hname` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of hall
-- ----------------------------
INSERT INTO `hall` VALUES (1, '一号影厅', 9, 15, '普通', 6, 0);
INSERT INTO `hall` VALUES (2, '二号影厅', 10, 15, 'IMAX', 1, 0);
INSERT INTO `hall` VALUES (3, '三号影厅', 10, 12, '普通', 1, 0);
INSERT INTO `hall` VALUES (4, '四号影厅', 10, 15, 'IMAX', 1, 0);
INSERT INTO `hall` VALUES (5, '五号影厅', 6, 15, '普通', 1, 0);

SET FOREIGN_KEY_CHECKS = 1;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
                         `uid` int NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                         `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
                         `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
                         `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '昵称',
                         `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '电话号码',
                         `type` enum('normal','admin') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '类型',
                         `register_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
                         `version` int NULL DEFAULT 1 COMMENT '乐观锁',
                         `is_deleted` int NULL DEFAULT 0 COMMENT '逻辑删除字段 1 删除  0 未删除',
                         PRIMARY KEY (`uid`) USING BTREE,
                         UNIQUE INDEX `username`(`username` ASC) USING BTREE,
                         UNIQUE INDEX `phone`(`phone` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', '3af92164920998eec3c415a361c32be2', 'admin', '12345678910', 'admin', '2024-08-27 10:20:05', 6, 0);
INSERT INTO `user` VALUES (2, 'user', '3af92164920998eec3c415a361c32be2', 'Test666...', '19711111111', 'normal', '2024-08-27 10:26:02', 1, 0);
ALTER TABLE `user` ADD COLUMN `user_pic` VARCHAR(255) COMMENT 'User profile picture path';
-- ----------------------------
-- Table structure for ticket
-- ----------------------------
DROP TABLE IF EXISTS `ticket`;
CREATE TABLE `ticket`  (
                           `tid` int NOT NULL AUTO_INCREMENT COMMENT '电影票ID',
                           `sid` int NOT NULL COMMENT '对应放映场次ID',
                           `uid` int NOT NULL COMMENT '所属用户ID',
                           `seat_number` int NOT NULL COMMENT '座位号',
                           `purchase_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
                           `price` decimal(10, 2) NOT NULL COMMENT '票价',
                           `order_status` enum('待支付','已支付','已完成','已取消') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '待支付' COMMENT '订单状态',
                           `version` int NULL DEFAULT 1 COMMENT '乐观锁',
                           `is_deleted` int NULL DEFAULT 0 COMMENT '逻辑删除字段 1 删除  0 未删除',
                           PRIMARY KEY (`tid`) USING BTREE,
                           INDEX `uid`(`uid` ASC) USING BTREE,
                           INDEX `ticket_ibfk_1`(`sid` ASC) USING BTREE,
                           CONSTRAINT `ticket_ibfk_1` FOREIGN KEY (`sid`) REFERENCES `screening` (`sid`) ON DELETE RESTRICT ON UPDATE CASCADE,
                           CONSTRAINT `ticket_ibfk_2` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 66 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ticket
-- ----------------------------
INSERT INTO `ticket` VALUES (60, 6, 2, 2, '2025-06-25 16:17:18', 60.00, '已取消', 1, 0);
INSERT INTO `ticket` VALUES (61, 1, 2, 1, '2025-06-25 16:41:05', 60.00, '已取消', 1, 0);
INSERT INTO `ticket` VALUES (62, 5, 2, 2, '2025-06-25 16:51:40', 60.00, '已取消', 1, 0);
INSERT INTO `ticket` VALUES (63, 1, 2, 5, '2025-06-25 17:17:36', 60.00, '已取消', 1, 0);
INSERT INTO `ticket` VALUES (64, 1, 2, 1, '2025-06-25 17:26:52', 60.00, '已支付', 2, 0);
INSERT INTO `ticket` VALUES (65, 1, 2, 2, '2025-06-26 09:10:43', 60.00, '已支付', 2, 0);

-- ----------------------------
-- Table structure for screening
-- ----------------------------
DROP TABLE IF EXISTS `screening`;
CREATE TABLE `screening`  (
                              `sid` int NOT NULL AUTO_INCREMENT COMMENT '放映场次ID',
                              `hid` int NOT NULL COMMENT '对应的影厅ID',
                              `mid` int NOT NULL COMMENT '对应的电影ID',
                              `show_time` datetime NOT NULL COMMENT '放映时间',
                              `end_time` datetime NOT NULL,
                              `price` decimal(10, 2) NOT NULL COMMENT '票价',
                              `remaining_seats` int NOT NULL COMMENT '剩余座位数',
                              `seat_count` int NOT NULL COMMENT '总票数',
                              `version` int NULL DEFAULT 1 COMMENT '乐观锁',
                              `is_deleted` int NULL DEFAULT 0 COMMENT '逻辑删除字段 1 删除  0 未删除',
                              PRIMARY KEY (`sid`) USING BTREE,
                              INDEX `hid`(`hid` ASC) USING BTREE,
                              INDEX `mid`(`mid` ASC) USING BTREE,
                              CONSTRAINT `screening_ibfk_1` FOREIGN KEY (`hid`) REFERENCES `hall` (`hid`) ON DELETE RESTRICT ON UPDATE CASCADE,
                              CONSTRAINT `screening_ibfk_2` FOREIGN KEY (`mid`) REFERENCES `movie` (`mid`) ON DELETE RESTRICT ON UPDATE CASCADE,
                              CONSTRAINT `seat_count` CHECK (`remaining_seats` >= 0)
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of screening
-- ----------------------------
INSERT INTO `screening` VALUES (1, 1, 1, '2025-07-04 07:00:00', '2025-07-04 08:50:00', 60.00, 133, 135, 22, 0);
INSERT INTO `screening` VALUES (2, 1, 2, '2025-07-04 09:00:00', '2025-07-04 11:33:00', 60.00, 135, 135, 17, 0);
INSERT INTO `screening` VALUES (3, 1, 3, '2025-07-04 14:00:00', '2025-07-04 16:04:00', 60.00, 135, 135, 12, 0);
INSERT INTO `screening` VALUES (4, 1, 1, '2025-07-05 16:10:00', '2025-07-05 18:00:00', 40.00, 134, 135, 8, 0);
INSERT INTO `screening` VALUES (5, 1, 2, '2025-07-05 18:30:00', '2025-07-05 21:03:00', 60.00, 135, 135, 9, 0);
INSERT INTO `screening` VALUES (6, 4, 3, '2025-07-05 08:00:00', '2025-07-05 10:04:00', 60.00, 149, 150, 9, 0);
INSERT INTO `screening` VALUES (7, 3, 6, '2025-07-05 08:10:00', '2025-07-05 10:37:00', 60.00, 120, 120, 10, 0);

-- ----------------------------
-- Table structure for movie
-- ----------------------------
DROP TABLE IF EXISTS `movie`;
CREATE TABLE `movie`  (
                          `mid` int NOT NULL AUTO_INCREMENT COMMENT '电影ID',
                          `mname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '电影名',
                          `director` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '导演',
                          `release_date` date NOT NULL COMMENT '发行日期',
                          `synopsis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '简介',
                          `poster` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '海报URL',
                          `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类型',
                          `runtime` int NULL DEFAULT NULL COMMENT '时长',
                          `version` int NULL DEFAULT 1 COMMENT '乐观锁',
                          `is_deleted` int NULL DEFAULT 0 COMMENT '逻辑删除字段 1 删除  0 未删除',
                          PRIMARY KEY (`mid`) USING BTREE,
                          UNIQUE INDEX `mname`(`mname` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of movie
-- ----------------------------
INSERT INTO `movie` VALUES (1, '哪吒重生', '赵霁', '2020-01-25', '讲述了在现代背景下，哪吒与敖丙的再次对决的故事。', 'https://example.com/poster/nezha-rebirth.jpg', '动画, 动作, 奇幻', 110, 2, 0);
INSERT INTO `movie` VALUES (2, '我和我的家乡', '宁浩, 徐峥, 陈思诚等', '2020-10-01', '通过五个短篇故事讲述中国不同地区的乡村生活变迁。', 'https://example.com/poster/my-hometown-and-me.jpg', '剧情, 喜剧', 153, 3, 0);
INSERT INTO `movie` VALUES (3, '刺杀小说家', '路阳', '2021-02-12', '一个作家的小说世界与现实世界的交错，展开了一场惊心动魄的冒险。', 'https://example.com/poster/assassin-novelist.jpg', '动作, 幻想', 124, 1, 0);
INSERT INTO `movie` VALUES (4, '寄生虫', '奉俊昊', '2019-05-30', '一家贫穷的家庭通过欺骗手段进入了一个富裕家庭的生活。', 'https://example.com/poster/parasite.jpg', '剧情, 黑色幽默, 悬疑', 131, 1, 0);
INSERT INTO `movie` VALUES (5, '1917', '萨姆·门德斯', '2020-01-10', '一战期间，两名士兵穿越敌区传递重要情报，以阻止一场致命的攻击。', 'https://example.com/poster/1917.jpg', '战争, 剧情', 119, 1, 0);
INSERT INTO `movie` VALUES (6, '八佰', '管虎', '2020-08-21', '淞沪会战中，四行仓库保卫战的真实历史事件。', 'https://example.com/poster/eight-hundred.jpg', '战争, 历史, 剧情', 147, 1, 0);
INSERT INTO `movie` VALUES (7, '黑寡妇', '凯特·肖特兰', '2021-07-09', '黑寡妇娜塔莎·罗曼诺夫面对过去的黑暗，与家人重逢并对抗敌人。', 'https://example.com/poster/black-widow.jpg', '动作, 冒险, 科幻', 134, 1, 0);
INSERT INTO `movie` VALUES (8, '夺冠', '陈可辛', '2020-09-25', '讲述中国女排从1981年首夺世界冠军到2016年里约奥运会的故事。', 'https://example.com/poster/champion.jpg', '体育, 剧情, 传记', 135, 1, 0);
INSERT INTO `movie` VALUES (9, '唐人街探案3', '陈思诚', '2021-02-12', '神探唐仁和秦风前往日本东京解决新的谜题。', 'https://example.com/poster/detective-chinatown-3.jpg', '喜剧, 探案, 动作', 136, 1, 0);
INSERT INTO `movie` VALUES (10, '信条', '克里斯托弗·诺兰', '2020-09-03', '一名特工通过逆向时间旅行来阻止第三次世界大战的发生。', 'https://example.com/poster/tenet.jpg', '科幻, 动作, 惊悚', 150, 1, 0);
INSERT INTO `movie` VALUES (11, '误杀', '柯汶利', '2019-12-13', '一位父亲为了保护家庭而制造了一起完美的犯罪。', 'https://example.com/poster/sheep-without-a-shepherd.jpg', '悬疑, 剧情, 犯罪', 112, 1, 0);
INSERT INTO `movie` VALUES (12, '姜子牙', '程腾, 李炜', '2020-10-01', '封神大战之后，被贬下凡间的姜子牙踏上寻找真相的旅途。', 'https://example.com/poster/legend-of-deification.jpg', '动画, 冒险, 奇幻', 110, 1, 0);
INSERT INTO `movie` VALUES (13, '疯狂原始人2', '乔尔·克劳福德', '2020-11-25', '原始人家族遇到另一个更先进的家族，开始了一系列冒险。', 'https://example.com/poster/the-croods-a-new-age.jpg', '动画, 喜剧, 冒险', 90, 1, 0);

-- ----------------------------
-- Table structure for hall
-- ----------------------------
DROP TABLE IF EXISTS `hall`;
CREATE TABLE `hall`  (
                         `hid` int NOT NULL AUTO_INCREMENT COMMENT '影厅ID',
                         `hname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '影厅名',
                         `row_num` int NOT NULL COMMENT '影厅座位排数',
                         `row_capacity` int NOT NULL COMMENT '影院每排座位数',
                         `screen_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '荧幕类型',
                         `version` int NULL DEFAULT 1 COMMENT '乐观锁',
                         `is_deleted` int NULL DEFAULT 0 COMMENT '逻辑删除字段 1 删除  0 未删除',
                         PRIMARY KEY (`hid`) USING BTREE,
                         UNIQUE INDEX `hname`(`hname` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of hall
-- ----------------------------
INSERT INTO `hall` VALUES (1, '一号影厅', 9, 15, '普通', 6, 0);
INSERT INTO `hall` VALUES (2, '二号影厅', 10, 15, 'IMAX', 1, 0);
INSERT INTO `hall` VALUES (3, '三号影厅', 10, 12, '普通', 1, 0);
INSERT INTO `hall` VALUES (4, '四号影厅', 10, 15, 'IMAX', 1, 0);
INSERT INTO `hall` VALUES (5, '五号影厅', 6, 15, '普通', 1, 0);

SET FOREIGN_KEY_CHECKS = 1;
