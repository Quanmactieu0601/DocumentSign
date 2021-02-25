create table otp_history
(
    id          bigint identity
        primary key,
    user_id     bigint       not null,
    secret_key  varchar(100) not null,
    otp         varchar(15)  not null,
    action_time datetime2(0)     null,
    expire_time datetime2(0)     null,
    com_id      bigint       null
);
