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
    author     varchar(256)                       null comment '作者',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
)
    comment '书';