create table system_config_category
(
	id int auto_increment primary key ,
	config_key varchar(50) null,
	data_type varchar(30) null,
	description varchar(500) null
) charset = utf8mb4;
