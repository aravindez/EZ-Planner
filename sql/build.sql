DROP DATABASE IF EXISTS `cal`;
CREATE DATABASE `cal`;
USE cal;

-- ---
-- TABLE 'user'
-- ---

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
    id INTEGER NOT NULL AUTO_INCREMENT,
    username CHAR(32) NOT NULL UNIQUE,
    password CHAR(128) NOT NULL,
    email VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    primary key (id)
);

-- ---
-- TABLE 'calendar'
-- ---

DROP TABLE IF EXISTS `calendar`;

CREATE TABLE `calendar` (
    id INTEGER NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    color CHAR(9) NOT NULL,
    description TEXT DEFAULT NULL,
    primary key (id)
);

-- ---
-- TABLE 'task'
-- ---

DROP TABLE IF EXISTS `task`;

CREATE TABLE `task` (
    id INTEGER NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    created_on DATE NOT NULL,
    due DATE NOT NULL,
    content TEXT NOT NULL,
    complete BIT NOT NULL DEFAULT 0,
    primary key (id)
);

-- ---
-- TABLE 'user_calendar'
-- ---

DROP TABLE IF EXISTS `user_calendar`;

CREATE TABLE `user_calendar` (
    user_id INT NOT NULL,
    calendar_id INT NOT NULL,
    owner BIT NOT NULL,
    viewCal BIT NOT NULL,
    editCal BIT NOT NULL,
    foreign key (user_id) references `user` (id),
    foreign key (calendar_id) references `calendar` (id)
);

-- ---
-- TABLE 'calendar_task'
-- ---

DROP TABLE IF EXISTS `calendar_task`;

CREATE TABLE `calendar_task` (
    calendar_id INT NOT NULL,
    task_id int NOT NULL,
    foreign key (calendar_id) references `calendar` (id),
    foreign key (task_id) references `task` (id)
);
