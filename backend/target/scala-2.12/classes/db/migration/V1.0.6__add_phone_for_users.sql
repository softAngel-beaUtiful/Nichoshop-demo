alter table users add phone varchar(64) null;
alter table users add account_type varchar(32) null;

update users set account_type='PERSONAL' where id > 0;


