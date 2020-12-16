--alter table transaction change code status bit null;
alter table transaction_log
	add status bit null;

alter table transaction_log drop column code;
