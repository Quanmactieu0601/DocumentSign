create table system_config_category
(
	id int identity primary key ,
	config_key varchar(50) null,
	data_type varchar(30) null,
	description varchar(500) null
);
