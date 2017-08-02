ALTER TABLE case_attachment ADD COLUMN CCOMMENT_ID bigint(20) DEFAULT NULL;
ALTER TABLE case_attachment ADD CONSTRAINT FK_PARENT_COMMENT FOREIGN KEY (CCOMMENT_ID) REFERENCES case_comment(ID) ON DELETE CASCADE;

insert into user_role (id,role_code,role_info,ca_role_name) values (2,'crm-admin','crm-role', 'crm-admin');
insert into user_role (id,role_code,role_info,ca_role_name) values (3,'crm-user','crm-role', 'crm-user');
insert into user_role (id,role_code,role_info,ca_role_name) values (4,'crm-client','crm-role', 'crm-client');
