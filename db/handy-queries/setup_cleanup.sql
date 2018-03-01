update case_type set next_id=1000000 where next_id is null or id in (1,2,3,4,5,6,7,8);

--update user_login set ulogin='michael' where personId=18;

delete from case_comment where case_id in (select id from case_object where CASE_TYPE=4);
delete from case_object where CASE_TYPE=4;

delete from case_comment where case_id in (select id from case_object where CASE_TYPE=5);
delete from case_object where CASE_TYPE=5;

delete from dev_unit where utype_id in (1,2);

delete from attachment;
delete from user_session;
delete from person;

delete from user_login;
delete from companysubscription;
delete from company where id > 1;
delete from migrationentry;
insert into migrationentry(entry_code,lastUpdate,last_id) values ('Tm_Company', now(), 1);




delete from DATABASECHANGELOGLOCK;

select * from userroles;