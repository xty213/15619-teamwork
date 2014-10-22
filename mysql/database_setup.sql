DROP DATABASE 15619project;

CREATE DATABASE 15619project CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

USE 15619project;

CREATE TABLE tweets(tweet_id BIGINT PRIMARY KEY, censored_text VARCHAR(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci, score INT, time CHAR(19), user_id BIGINT);