create table if not exists db_authority_system.sys_department
(
    id              bigint auto_increment comment '部门编号'
        primary key,
    department_name varchar(50)       not null comment '部门名称',
    phone           varchar(50)       null comment '部门电话',
    address         varchar(255)      null comment '部门地址',
    pid             bigint            not null comment '所属部门编号',
    parent_name     varchar(50)       not null comment '所属部门名称',
    order_num       int               null comment '排序',
    is_delete       tinyint default 0 null comment '是否删除(0-未删除 1-已删除)'
);

create table if not exists db_authority_system.sys_permission
(
    id          bigint auto_increment comment '权限编号'
        primary key,
    label       varchar(50)       null comment '权限名称',
    parent_id   bigint            null comment '父权限ID',
    parent_name varchar(50)       null comment '父权限名称',
    code        varchar(50)       null comment '授权标识符',
    path        varchar(100)      null comment '路由地址',
    name        varchar(50)       null comment '路由名称',
    url         varchar(100)      null comment '授权路径',
    type        tinyint           null comment '权限类型(0-目录 1-菜单 2-按钮)',
    icon        varchar(50)       null comment '图标',
    create_time datetime          null comment '创建时间',
    update_time datetime          null comment '修改时间',
    remark      varchar(255)      null comment '备注',
    order_num   int               null comment '排序',
    is_delete   tinyint default 0 null comment '是否删除(0-未删除，1-已删除)'
);

create table if not exists db_authority_system.sys_role
(
    id          bigint auto_increment comment '角色编号'
        primary key,
    role_code   varchar(50)       not null comment '角色编码',
    role_name   varchar(50)       not null comment '角色名称',
    create_user bigint            null comment '创建人',
    create_time datetime          null comment '创建时间',
    update_time datetime          null comment '修改时间',
    remark      varchar(255)      null comment '备注',
    is_delete   tinyint default 0 null comment '是否删除(0-未删除，1-已删除)'
);

create table if not exists db_authority_system.sys_role_permission
(
    role_Id       bigint not null comment '角色ID',
    permission_Id bigint not null comment '权限ID'
);

create table if not exists db_authority_system.sys_user
(
    id                         bigint auto_increment comment '用户编号'
        primary key,
    username                   varchar(50)                                                                                             not null comment '登录名称(用户名)',
    password                   varchar(100)                                                                                            not null comment '登录密码',
    is_account_non_expired     tinyint                                                                                                 not null comment '帐户是否过期(1-未过期，0-已过期)',
    is_account_non_locked      tinyint                                                                                                 not null comment '帐户是否被锁定(1-未过期，0-已过期)',
    is_credentials_non_expired tinyint                                                                                                 not null comment '密码是否过期(1-未过期，0-已过期)',
    is_enabled                 tinyint                                                                                                 not null comment '帐户是否可用(1-可用，0-禁用)',
    real_name                  varchar(50)                                                                                             not null comment '真实姓名',
    nick_name                  varchar(50)                                                                                             null comment '昵称',
    department_id              bigint                                                                                                  null comment '所属部门ID',
    department_name            varchar(50)                                                                                             null comment '所属部门名称',
    gender                     tinyint                                                                                                 not null comment '性别(0-男，1-女)',
    phone                      varchar(50)                                                                                             not null comment '电话',
    email                      varchar(50)                                                                                             null comment '邮箱',
    avatar                     varchar(255) default 'https://manong-authority.oss-cn-guangzhou.aliyuncs.com/avatar/default-avatar.gif' null comment '用户头像',
    is_admin                   tinyint      default 0                                                                                  null comment '是否是管理员(1-管理员)',
    create_time                datetime                                                                                                null comment '创建时间',
    update_time                datetime                                                                                                null comment '修改时间',
    is_delete                  tinyint      default 0                                                                                  null comment '是否删除(0-未删除，1-已删除)'
);

create table if not exists db_authority_system.sys_user_role
(
    user_id bigint not null comment '用户编号',
    role_id bigint not null comment '角色编号'
);

