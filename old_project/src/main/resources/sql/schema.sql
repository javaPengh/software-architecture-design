SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE IF NOT EXISTS `user` (
  `uid` int NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(255) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `nickname` varchar(255) NOT NULL COMMENT '昵称',
  `phone` varchar(20) NOT NULL COMMENT '电话号码',
  `type` enum('normal','admin') NOT NULL COMMENT '类型',
  `register_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `version` int NULL DEFAULT 1 COMMENT '乐观锁',
  `is_deleted` int NULL DEFAULT 0 COMMENT '逻辑删除字段',
  `user_pic` varchar(255) NULL COMMENT '头像',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `movie` (
  `mid` int NOT NULL AUTO_INCREMENT COMMENT '电影ID',
  `mname` varchar(255) NOT NULL COMMENT '电影名',
  `director` varchar(255) NOT NULL COMMENT '导演',
  `release_date` date NOT NULL COMMENT '发行日期',
  `synopsis` text NULL COMMENT '简介',
  `poster` varchar(2048) NULL DEFAULT NULL COMMENT '海报URL',
  `type` varchar(255) NULL DEFAULT NULL COMMENT '类型',
  `runtime` int NULL DEFAULT NULL COMMENT '时长',
  `version` int NULL DEFAULT 1 COMMENT '乐观锁',
  `is_deleted` int NULL DEFAULT 0 COMMENT '逻辑删除字段',
  PRIMARY KEY (`mid`),
  UNIQUE KEY `mname` (`mname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `hall` (
  `hid` int NOT NULL AUTO_INCREMENT COMMENT '影厅ID',
  `hname` varchar(255) NOT NULL COMMENT '影厅名',
  `row_num` int NOT NULL COMMENT '影厅座位排数',
  `row_capacity` int NOT NULL COMMENT '影院每排座位数',
  `screen_type` varchar(50) NULL DEFAULT NULL COMMENT '荧幕类型',
  `version` int NULL DEFAULT 1 COMMENT '乐观锁',
  `is_deleted` int NULL DEFAULT 0 COMMENT '逻辑删除字段',
  PRIMARY KEY (`hid`),
  UNIQUE KEY `hname` (`hname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `screening` (
  `sid` int NOT NULL AUTO_INCREMENT COMMENT '放映场次ID',
  `hid` int NOT NULL COMMENT '影厅ID',
  `mid` int NOT NULL COMMENT '电影ID',
  `show_time` datetime NOT NULL COMMENT '放映时间',
  `end_time` datetime NOT NULL,
  `price` decimal(10,2) NOT NULL COMMENT '票价',
  `remaining_seats` int NOT NULL COMMENT '剩余座位数',
  `seat_count` int NOT NULL COMMENT '总票数',
  `version` int NULL DEFAULT 1 COMMENT '乐观锁',
  `is_deleted` int NULL DEFAULT 0 COMMENT '逻辑删除字段',
  PRIMARY KEY (`sid`),
  KEY `hid` (`hid`),
  KEY `mid` (`mid`),
  CONSTRAINT `screening_hall_fk` FOREIGN KEY (`hid`) REFERENCES `hall` (`hid`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `screening_movie_fk` FOREIGN KEY (`mid`) REFERENCES `movie` (`mid`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `seat_count_chk` CHECK (`remaining_seats` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE IF NOT EXISTS `ticket` (
  `tid` int NOT NULL AUTO_INCREMENT COMMENT '电影票ID',
  `sid` int NOT NULL COMMENT '放映场次ID',
  `uid` int NOT NULL COMMENT '所属用户ID',
  `seat_number` int NOT NULL COMMENT '座位号',
  `purchase_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  `price` decimal(10,2) NOT NULL COMMENT '票价',
  `order_status` enum('待支付','已支付','已完成','已取消') NOT NULL DEFAULT '待支付' COMMENT '订单状态',
  `version` int NULL DEFAULT 1 COMMENT '乐观锁',
  `is_deleted` int NULL DEFAULT 0 COMMENT '逻辑删除字段',
  PRIMARY KEY (`tid`),
  KEY `uid` (`uid`),
  KEY `sid` (`sid`),
  CONSTRAINT `ticket_screening_fk` FOREIGN KEY (`sid`) REFERENCES `screening` (`sid`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `ticket_user_fk` FOREIGN KEY (`uid`) REFERENCES `user` (`uid`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT IGNORE INTO `user` (`uid`,`username`,`password`,`nickname`,`phone`,`type`,`register_time`,`version`,`is_deleted`)
VALUES (1,'admin','3af92164920998eec3c415a361c32be2','admin','12345678910','admin',CURRENT_TIMESTAMP,1,0),
       (2,'user','3af92164920998eec3c415a361c32be2','Test666...','19711111111','normal',CURRENT_TIMESTAMP,1,0);

SET FOREIGN_KEY_CHECKS = 1;
