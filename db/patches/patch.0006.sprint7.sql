alter table person add contactInfo JSON;
alter table company add contactInfo JSON;

alter table company add (groupId bigint);
create index ix_company_group on company(groupId);
alter table company drop foreign key FK_PARENT_COMPANY;
alter table company drop key FK_PARENT_COMPANY;
alter table company drop column parent_company;

SET foreign_key_checks = 0;
alter table company add constraint fk_compgroup_smp foreign key (groupId) references CompanyGroup(id) on delete set null;
SET foreign_key_checks = 1;

alter table case_object add (initiator_company bigint);
alter table case_object add constraint fk_caseobj_initcomp foreign key (initiator_company) references Company(id);

/*
Insert into user_login (ulogin,upass,created,lastPwdChange,pwdExpired,astate,roleId,personId,authType,ipMaskAllow,ipMaskDeny,info) values ('mike','ac5c74b64b4b8352ef2f181affb5ac2a','2016-11-04 11:30:35.0',null,null,2,1,18,1,null,null,'Michael Zavedeev - dev account');
Insert into user_login (ulogin,upass,created,lastPwdChange,pwdExpired,astate,roleId,personId,authType,ipMaskAllow,ipMaskDeny,info) values ('nady','3949c66fdc4066377cdbd5f4a2f6c3f8','2016-11-06 20:55:17.0',null,null,2,1,411,1,null,null,'Ponomareva - dev account');
*/