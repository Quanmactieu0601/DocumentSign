--  create authority category
INSERT INTO signserver2.jhi_authority (name, id) VALUES ('ROLE_ADMIN', null);
INSERT INTO signserver2.jhi_authority (name, id) VALUES ('ROLE_USER', null);

--  create admin user account
--admin | admin@123$%
INSERT INTO jhi_user (id, created_by, created_date, last_modified_by, last_modified_date, activated, activation_key, email, first_name, image_url, lang_key, last_name, login, password_hash, reset_date, reset_key, organization_name, common_name, organization_unit, locality_name, state_name, country, phone, csr_status, com_id) VALUES (1, 'thanhld', '2020-12-21 09:20:44.331000', 'thanhld', '2020-12-21 09:20:44.331000', true, '779ocAqcgQ5BpZHxJ5Y1', 'admin-signserver@softdreams.vn', 'Super Admin', null, 'vi', null, 'admin', '$2a$10$bJ.4IDe6SnyqHfcekn/Nx..4vObeRDb3eti6GcjNsAjT8xxauavg6', null, null, null, null, null, null, null, null, null, 0, 1);

-- grant permission to admin user
INSERT INTO jhi_user_authority (user_id, authority_name) VALUES (1, 'ROLE_ADMIN');
INSERT INTO jhi_user_authority (user_id, authority_name) VALUES (1, 'ROLE_USER');

-- init configuration (will be updated base on each client requirement)
-- -- symmetric key | used to encrypt sensitive data
INSERT INTO system_config (id, com_id, config_key, config_value, description, activated, data_type) VALUES (2, 1, 'SYMMETRIC_KEY', 'c2RzQHNpZ24yMDIw', '', true, 'STRING');
-- -- save token password | used to save token pin when its created and skip check token pin when signing
INSERT INTO system_config (id, com_id, config_key, config_value, description, activated, data_type) VALUES (3, 1, 'SAVE_TOKEN_PASSWORD', '0', 'save p12 PIN and skip authen by PIN (p12)', true, 'BOOLEAN');
-- -- use otp | enable option authenticate by OTP when signing
INSERT INTO system_config (id, com_id, config_key, config_value, description, activated, data_type) VALUES (4, 1, 'USE_OTP', '0', null, true, 'BOOLEAN');
