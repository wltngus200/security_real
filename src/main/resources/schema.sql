CREATE TABLE IF NOT EXISTS `user` (
    `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
    `uid` char(30) NOT NULL,
    `upw` char(70) NOT NULL,
    `nm` char(10) NOT NULL,
    `pic` char(70) DEFAULT NULL,
    `created_at` datetime NOT NULL DEFAULT current_timestamp(),
    `updated_at` datetime DEFAULT NULL ON UPDATE current_timestamp(),
    PRIMARY KEY (`user_id`) USING BTREE,
    UNIQUE KEY `uid` (`uid`) USING BTREE
    ) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
TRUNCATE TABLE  user;

CREATE TABLE IF NOT EXISTS `user_follow` (
    `from_user_id` bigint(20) NOT NULL,
    `to_user_id` bigint(20) NOT NULL,
    `created_at` datetime NOT NULL DEFAULT current_timestamp(),
    PRIMARY KEY (`from_user_id`,`to_user_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
TRUNCATE TABLE user_follow;

CREATE TABLE IF NOT EXISTS `feed` (
    `feed_id` bigint(20) NOT NULL AUTO_INCREMENT,
    `writer_id` bigint(20) NOT NULL,
    `contents` varchar(1000) DEFAULT NULL,
    `location` char(30) DEFAULT NULL,
    `created_at` datetime NOT NULL DEFAULT current_timestamp(),
    `updated_at` datetime DEFAULT NULL ON UPDATE current_timestamp(),
    PRIMARY KEY (`feed_id`) USING BTREE
    ) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
TRUNCATE TABLE feed;

CREATE TABLE IF NOT EXISTS `feed_comment` (
    `feed_comment_id` bigint(20) NOT NULL AUTO_INCREMENT,
    `feed_id` bigint(20) NOT NULL,
    `user_id` bigint(20) NOT NULL,
    `COMMENT` varchar(1000) NOT NULL,
    `created_at` datetime NOT NULL DEFAULT current_timestamp(),
    `updated_at` datetime DEFAULT NULL ON UPDATE current_timestamp(),
    PRIMARY KEY (`feed_comment_id`) USING BTREE
    ) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
TRUNCATE TABLE feed_comment;

CREATE TABLE IF NOT EXISTS `feed_favorite` (
    `feed_id` bigint(20) NOT NULL,
    `user_id` bigint(20) NOT NULL,
    `created_at` datetime NOT NULL DEFAULT current_timestamp(),
    PRIMARY KEY (`feed_id`,`user_id`) USING BTREE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
TRUNCATE TABLE feed_favorite;

CREATE TABLE IF NOT EXISTS `feed_pics` (
    `feed_pic_id` bigint(20) NOT NULL AUTO_INCREMENT,
    `feed_id` bigint(20) NOT NULL,
    `pic` varchar(70) NOT NULL,
    `created_at` datetime NOT NULL DEFAULT current_timestamp(),
    PRIMARY KEY (`feed_pic_id`));
TRUNCATE TABLE feed_pics;
-- 서버 내렸다 올릴 때 마다 같은 상태 유지