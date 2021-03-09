create table otp_history
(
	id bigserial not null,
	user_id bigint,
	secret_key varchar(100),
	otp varchar(15),
	action_time timestamp,
	expire_time timestamp,
	com_id bigint
);

create unique index otp_history_id_uindex
	on otp_history (id);

alter table otp_history
	add constraint otp_history_pk
		primary key (id);

