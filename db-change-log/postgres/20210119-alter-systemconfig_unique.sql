alter table system_config
	add constraint system_config_pk
		unique (config_key, com_id);

