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


INSERT INTO system_config_category (id, config_key, data_type, description) VALUES (1, 'SYMMETRIC_KEY', 'STRING', 'Khóa mã hóa các dữ liệu đặc biệt');
INSERT INTO system_config_category (id, config_key, data_type, description) VALUES (2, 'SAVE_TOKEN_PASSWORD', 'BOOLEAN', 'Lưu mật khẩu P12 (1: có lưu | 0: không lưu)');
INSERT INTO system_config_category (id, config_key, data_type, description) VALUES (3, 'USE_OTP', 'BOOLEAN', 'Sử dụng OTP để xác thực mỗi khi ký số (1: có sử dụng | 0: không sử dụng)');
INSERT INTO system_config_category (id, config_key, data_type, description) VALUES (4, 'OTP_LIFE_TIME_SECOND', 'NUMERIC', 'Lifetime của OTP (đơn vị: giây - Min 60s)');
