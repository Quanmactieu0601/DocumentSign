create table otp_history
(
    id          bigint auto_increment
        primary key,
    user_id     bigint       not null,
    secret_key  varchar(100) not null,
    otp         varchar(15)  not null,
    action_time datetime     null,
    expire_time datetime     null,
    com_id      bigint       null
);

