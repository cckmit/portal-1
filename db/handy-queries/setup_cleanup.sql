update case_type set next_id=1000000 where next_id is null;
update user_login set ulogin='michael' where personId=18;



delete from dev_unit;
delete from person;
delete from user_login;
delete from companysubscription;
delete from company where id > 1;
delete from migrationentry;
insert into migrationentry(entry_code,lastUpdate,last_id) values ('Tm_Company', now(), 1);




delete from DATABASECHANGELOGLOCK;

select * from userroles;