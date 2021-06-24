create table core_parser
(
	id bigserial,
	name varchar(100),
	description varchar(500)
);

create unique index core_parser_id_uindex
	on core_parser (id);

alter table core_parser
	add constraint core_parser_pk
		primary key (id);



INSERT INTO core_parser (id, name, description) VALUES (1, 'BV_Q11', null);
INSERT INTO core_parser (id, name, description) VALUES (2, 'BV_AnPhuoc', null);
