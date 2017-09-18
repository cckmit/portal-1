insert into company_group (id, created, group_name) values (1, current_timestamp, "test-group");

UPDATE case_type SET NEXT_ID = 55 where CT_CODE = 'project';

update user_role SET privileges = 'EQUIPMENT_VIEW,EQUIPMENT_EDIT,EQUIPMENT_CREATE,EQUIPMENT_REMOVE' WHERE role_code = 'dn-admin';