alter table certificate
    add signing_profile int default -1 not null;


alter table certificate
    add auth_mode varchar(50);

