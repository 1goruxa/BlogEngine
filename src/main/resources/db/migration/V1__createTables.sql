--DROP TABLE IF EXISTS captcha_codes
--DROP TABLE IF EXISTS post_comments
--DROP TABLE IF EXISTS post_votes
--DROP TABLE IF EXISTS posts
--DROP TABLE IF EXISTS tag2post
--DROP TABLE IF EXISTS tags
--DROP TABLE IF EXISTS users

CREATE TABLE `captcha_codes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `code` varchar(255) DEFAULT NULL,
  `secret_code` varchar(255) DEFAULT NULL,
  `time` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `post_comments` (
  `id` int NOT NULL AUTO_INCREMENT,
  `parent_id` int NOT NULL,
  `post_id` int NOT NULL,
  `text` varchar(255) DEFAULT NULL,
  `time` datetime(6) DEFAULT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `post_votes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `post_id` varchar(255) DEFAULT NULL,
  `time` datetime(6) DEFAULT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  `value` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `posts` (
  `id` int NOT NULL AUTO_INCREMENT,
  `is_active` int NOT NULL,
  `moderator_id` int NOT NULL,
  `moderation_status` ENUM('NEW', 'ACCEPTED','DECLINED') NOT NULL,
  `text` varchar(255) DEFAULT NULL,
  `time` datetime(6) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `user_id` int NOT NULL,
  `view_count` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `tag2post` (
  `id` int NOT NULL AUTO_INCREMENT,
  `post_id` int NOT NULL,
  `tagid` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `tags` (
  `id` int NOT NULL AUTO_INCREMENT,
  `text` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `code` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `is_moderator` int NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `photo` varchar(255) DEFAULT NULL,
  `reg_time` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `global_settings` (
    `id` int NOT NULL AUTO_INCREMENT,
    `code` varchar(255) DEFAULT NULL,
    `name` varchar(255) DEFAULT NULL,
    `value` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
