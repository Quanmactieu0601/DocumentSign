create table system_config_category
(
	id bigserial not null,
	config_key varchar(50),
	data_type varchar(50),
	description varchar(500)
);

create unique index system_config_category_id_uindex
	on system_config_category (id);

alter table system_config_category
	add constraint system_config_category_pk
		primary key (id);

