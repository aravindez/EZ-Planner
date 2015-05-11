drop database if exists `cal`;
create database `cal`;
use cal;

-- ---
-- Table 'user'
-- ---

drop table if exists `user`;

create table `user` (
    id integer not null auto_increment,
    username char(32) not null unique,
    password char(128) not null,
    email varchar(255) not null,
    first_name varchar(255) not null,
    last_name varchar(255) not null,
    primary key (id)
);

-- ---
-- Table 'calendar'
-- ---

drop table if exists `calendar`;

create table `calendar` (
    id integer not null auto_increment,
    name varchar(255) not null,
    description text default null,
    primary key (id)
);

-- ---
-- Table 'task'
-- ---

drop table if exists `task`;

create table `task` (
    id integer not null auto_increment,
    name varchar(255) not null,
    curdate date not null,
    due date not null,
    content text not null,
    complete bit not null default 0,
    primary key (id)
);

-- ---
-- Table 'user_calendar'
-- ---

drop table if exists `user_calendar`;

create table `user_calendar` (
    user_id int not null,
    calendar_id int not null,
    owner bit not null,
    viewCal bit not null,
    editCal bit not null,
    foreign key (user_id) references `user` (id),
    foreign key (calendar_id) references `calendar` (id)
);

-- ---
-- Table 'calendar_task'
-- ---

drop table if exists `calendar_task`;

create table `calendar_task` (
    calendar_id int not null,
    task_id int not null,
    foreign key (calendar_id) references `calendar` (id),
    foreign key (task_id) references `task` (id)
);
