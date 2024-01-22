# 建表脚本
# @author ZunF

-- 创建库
create database if not exists oj_db;

-- 切换库
use oj_db;

-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id'
        primary key,
    userAccount  varchar(256)                             not null comment '账号',
    userPassword varchar(512)                             not null comment '密码',
    unionId      varchar(256)                             null comment '微信开放平台id',
    accessKey    varchar(512)                             null comment '访问密钥',
    secretKey    varchar(512)                             null comment '秘钥',
    mpOpenId     varchar(256)                             null comment '公众号openId',
    userName     varchar(256)                             null comment '用户昵称',
    userAvatar   varchar(1024)                            null comment '用户头像',
    userProfile  varchar(512)                             null comment '用户简介',
    userRole     varchar(256) default 'user'              not null comment '用户角色：user/admin/ban',
    createTime   datetime     default (CURRENT_TIMESTAMP) not null comment '创建时间',
    updateTime   datetime     default (CURRENT_TIMESTAMP) not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                   not null comment '是否删除',
    index idx_unionId (accessKey)
) comment '用户' collate = utf8mb4_unicode_ci;

-- 题目表
create table if not exists question
(
    id          bigint auto_increment comment 'id' primary key,
    title       varchar(512)                       null comment '标题',
    content     text                               null comment '内容',
    tags        varchar(1024)                      null comment '题目标签（json 数组）',
    answer      text                               null comment '题目答案',
    submitNum   int      default 0                 not null comment '题目提交数',
    acceptedNum int      default 0                 not null comment '题目通过数',
    judgeCase   text                               null comment '判题用例（json字符串）',
    judgeConfig text                               null comment '判题配置（json字符串）',
    thumbNum    int      default 0                 not null comment '点赞数',
    favourNum   int      default 0                 not null comment '收藏数',
    userId      bigint                             not null comment '创建用户 id',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment '题目' collate = utf8mb4_unicode_ci;


-- 开放接口信息表
create table if not exists interface_info
(
    id           bigint auto_increment comment 'id'
        primary key,
    name         varchar(256)                         null comment '接口名称',
    description  text                                 null comment '接口描述',
    url          varchar(512)                         null comment '接口路径',
    requestParam text                                 null comment '请求参数（json数组）',
    responseBody text                                 null comment '响应体示例(json)',
    status       tinyint  default 0                   not null comment '接口状态，0关闭，1开启',
    method       varchar(256)                         null comment '请求类型',
    userId       bigint                               null comment '创建人',
    createTime   datetime default (CURRENT_TIMESTAMP) not null comment '创建时间',
    updateTime   datetime default (CURRENT_TIMESTAMP) not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                   not null comment '是否删除',
    index idx_interfaceId (id)
) comment '开放接口信息表' collate = utf8mb4_unicode_ci;

-- 用户调用接口关系表
create table if not exists user_interface_info
(
    id          bigint                               not null auto_increment comment '主键' primary key,
    accessKey   varchar(512)                         not null comment '调用者用户Id',
    interfaceId bigint                               not null comment '接口Id',
    totalNum    int                                  not null default 0 comment '总调用次数',
    leftNum     int                                  not null default 0 comment '剩余调用次数',
    status      tinyint  default 0                   not null default 0 comment '状态，0正常，1禁用',
    createTime  datetime default (CURRENT_TIMESTAMP) not null comment '创建时间',
    updateTime  datetime default (CURRENT_TIMESTAMP) not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                   not null comment '是否删除',
    index idx_userId (accessKey),
    index idx_interfaceId (interfaceId)
) comment '用户调用接口关系表' collate = utf8mb4_unicode_ci;


-- 题目提交表
create table if not exists question_submit
(
    id         bigint auto_increment comment 'id' primary key,
    questionId bigint                             not null comment '帖子 id',
    userId     bigint                             not null comment '创建用户 id',
    language   varchar(128)                       not null comment '编程语言',
    code       text                               not null comment '提交代码',
    judgeInfo  text                               null comment '判题信息（json对象）',
    status     tinyint  default 0                 not null comment '判题状态，0 待判题、1 判题中、2 成功、3 失败',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    index idx_questionId (questionId),
    index idx_userId (userId)
) comment '题目提交';

-- Seata数据库undo日志
CREATE TABLE `undo_log`
(
    `id`            bigint(20)   NOT NULL AUTO_INCREMENT,
    `branch_id`     bigint(20)   NOT NULL,
    `xid`           varchar(100) NOT NULL,
    `context`       varchar(128) NOT NULL,
    `rollback_info` longblob     NOT NULL,
    `log_status`    int(11)      NOT NULL,
    `log_created`   datetime     NOT NULL,
    `log_modified`  datetime     NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `ux_undo_log` (`xid`, `branch_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;