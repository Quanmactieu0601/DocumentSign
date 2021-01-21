alter table system_config drop key system_config_config_key_uindex;

alter table system_config
	add constraint system_config_config_key_uindex
		unique (config_key, com_id);
