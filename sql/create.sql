create table user
(
    id           bigint auto_increment comment 'id'
        primary key,
    userAccount  varchar(256)                           null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    userRole     varchar(256) default 'user'            not null comment '用户角色 user-普通用户 admin-管理员',
    userName     varchar(256)                           null comment '用户昵称',
    avatarUrl    varchar(1024)                          null comment '用户头像',
    gender       tinyint      default 1                 null comment '性别',
    phone        varchar(128)                           null comment '电话',
    email        varchar(512)                           null comment '邮箱',
    userStatus   int          default 0                 not null comment '状态 0 - 正常',
    createTime   datetime     default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete     tinyint      default 0                 not null comment '是否删除'
)
    comment '用户';

INSERT INTO demo.user (id, userAccount, userPassword, userRole, userName, avatarUrl, gender, phone, email, userStatus, createTime, updateTime, isDelete) VALUES (1, 'admin', '3014dcb9ee3639535d5d9301b32c840c', 'admin', '管理员', 'http://niu.liangtianyu.space/user.png', 1, null, null, 0, '2023-03-19 09:12:41', '2023-06-04 19:00:36', 0);
INSERT INTO demo.user (id, userAccount, userPassword, userRole, userName, avatarUrl, gender, phone, email, userStatus, createTime, updateTime, isDelete) VALUES (2, 'test', '3014dcb9ee3639535d5d9301b32c840c', 'user', '测试账户', 'http://niu.liangtianyu.space/user.png', 0, null, null, 0, '2023-03-19 09:13:05', '2023-06-27 14:13:34', 0);

create table book
(
    id         bigint auto_increment comment 'id'
        primary key,
    bookName   varchar(256)                       null comment '书名称',
    type       varchar(256)                       null comment '类型',
    price      decimal(10, 2)                     null comment '价格',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
)
    comment '书';

-- 插入20条模拟数据
INSERT INTO book (bookName, type, price, createTime, updateTime, isDelete)
VALUES ('Java编程思想', '计算机', 89.99, '2023-10-01 10:00:00', '2023-10-01 10:00:00', 0),
       ('Effective Java', '计算机', 69.99, '2023-10-02 11:00:00', '2023-10-02 11:00:00', 0),
       ('Spring实战', '计算机', 79.99, '2023-10-03 12:00:00', '2023-10-03 12:00:00', 0),
       ('深入理解计算机系统', '计算机', 99.99, '2023-10-04 13:00:00', '2023-10-04 13:00:00', 0),
       ('算法导论', '计算机', 129.99, '2023-10-05 14:00:00', '2023-10-05 14:00:00', 0),
       ('计算机网络', '计算机', 85.99, '2023-10-06 15:00:00', '2023-10-06 15:00:00', 0),
       ('操作系统', '计算机', 95.99, '2023-10-07 16:00:00', '2023-10-07 16:00:00', 0),
       ('数据库系统概论', '数据库', 75.99, '2023-10-08 17:00:00', '2023-10-08 17:00:00', 0),
       ('软件工程', '计算机', 65.99, '2023-10-09 18:00:00', '2023-10-09 18:00:00', 0),
       ('编译原理', '计算机', 105.99, '2023-10-10 19:00:00', '2023-10-10 19:00:00', 0),
       ('计算机组成原理', '计算机', 90.99, '2023-10-11 20:00:00', '2023-10-11 20:00:00', 0),
       ('数据结构', '计算机', 80.99, '2023-10-12 21:00:00', '2023-10-12 21:00:00', 0),
       ('设计模式', '计算机', 70.99, '2023-10-13 22:00:00', '2023-10-13 22:00:00', 0),
       ('计算机图形学', '计算机', 110.99, '2023-10-14 23:00:00', '2023-10-14 23:00:00', 0),
       ('人机交互', '计算机', 88.99, '2023-10-15 00:00:00', '2023-10-15 00:00:00', 0),
       ('软件测试', '计算机', 78.99, '2023-10-16 01:00:00', '2023-10-16 01:00:00', 0),
       ('编程珠玑', '计算机', 98.99, '2023-10-17 02:00:00', '2023-10-17 02:00:00', 0),
       ('计算机视觉', '计算机', 108.99, '2023-10-18 03:00:00', '2023-10-18 03:00:00', 0),
       ('软件架构', '计算机', 87.99, '2023-10-19 04:00:00', '2023-10-19 04:00:00', 0),
       ('数据挖掘', '计算机', 97.99, '2023-10-20 05:00:00', '2023-10-20 05:00:00', 0),
       ('Python编程', '编程语言', 59.99, '2023-10-21 06:00:00', '2023-10-21 06:00:00', 0),
       ('JavaScript权威指南', '编程语言', 64.99, '2023-10-22 07:00:00', '2023-10-22 07:00:00', 0),
       ('C++ Primer', '编程语言', 74.99, '2023-10-23 08:00:00', '2023-10-23 08:00:00', 0);