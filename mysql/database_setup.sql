DROP DATABASE 15619project;

CREATE DATABASE 15619project CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

USE 15619project;

create table q2(tweet_id bigint primary key, user_id bigint, `time` char(19), `timestamp` bigint, score int, censored_text VARCHAR(640) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci) engine=myisam;

create table q3(origin_user_id bigint, retweet_user_id bigint, primary key(origin_user_id, retweet_user_id)) engine=myisam;

create table q4(tweet_id bigint primary key, hashed_location bigint, location VARCHAR(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci, date int(8), hashtag VARCHAR(640) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci, start_at int) engine=myisam;