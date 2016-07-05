SET NAMES 'utf8';

delete from admin_state;
insert into admin_state(id,code) values (1,'locked');
insert into admin_state(id,code) values (2,'unlocked');

insert into user_role (id,role_code,role_info,ca_role_name) values (1,'employee','common-role', 'portal_employee');
commit;

delete from auth_type;
insert into auth_type (id,at_code,at_info) values (1,'local','Local login and password stored in db');
insert into auth_type (id,at_code,at_info) values (2,'ldap','Local LDAP');
commit;

insert into case_stage_type(id, stage_type) values (1,'general_stage');
commit;

delete from time_unit;
insert into time_unit(id,unit_code,unit_name,minutes) values (1,'m','минута',1);
insert into time_unit(id,unit_code,unit_name,minutes) values (2,'h','час',60);
insert into time_unit(id,unit_code,unit_name,minutes) values (3,'d','день',1440);

delete from importance_level;
insert into importance_level (id,code,info) values (1,'critical','critical');
insert into importance_level (id,code,info) values (2,'important','important');
insert into importance_level (id,code,info) values (3,'basic','basic');
insert into importance_level (id,code,info) values (4,'cosmetic','cosmetic');

delete from dev_unit_type;
insert into dev_unit_type (id, unit_type, unit_info) values (1,'component', 'Компонента, бывший термин - проект');
insert into dev_unit_type (id, unit_type, unit_info) values (2,'product', 'Продукт');

delete from dev_unit_state;
insert into dev_unit_state(id,UST_CODE,UST_INFO) values (1,'active','Активно');
insert into dev_unit_state(id,UST_CODE,UST_INFO) values (2,'deprecated','Устарело, более не используется');

delete from department_type;
insert into department_type(id, name, info) values (1,'отдел','');

delete from case_doc_type;
insert into case_doc_type (id,dt_code,dt_info,dt_label,displayorder) values (1,'reqdoc','Requirement Doc','Requirement',1);
insert into case_doc_type (id,dt_code,dt_info,dt_label,displayorder) values (2,'desdoc','Design Doc','Design',2);
insert into case_doc_type (id,dt_code,dt_info,dt_label,displayorder) values (3,'reldoc','Release Doc','Release',3);


delete from Absence_Reason;
insert into Absence_Reason(id, ar_code, old_id, ar_info, display_order) values (1, 'business_trip', 1, 'Командировка', 2);
insert into Absence_Reason(id, ar_code, old_id, ar_info, display_order) values (2, 'vacation', 2, 'Отпуск', 5);
insert into Absence_Reason(id, ar_code, old_id, ar_info, display_order) values (3, 'illness', 3, 'Болезнь', 6);
insert into Absence_Reason(id, ar_code, old_id, ar_info, display_order) values (4, 'personal_reasons', 4, 'Личные дела', 1);
insert into Absence_Reason(id, ar_code, old_id, ar_info, display_order) values (5, 'local_trip', 5, 'Местная командировка', 3);
insert into Absence_Reason(id, ar_code, old_id, ar_info, display_order) values (6, 'studies', 6, 'Учеба', 4);
insert into Absence_Reason(id, ar_code, old_id, ar_info, display_order) values (7, 'sick_leave', 7, 'Больничный лист', 7);
insert into Absence_Reason(id, ar_code, old_id, ar_info, display_order) values (8, 'guest_card', 8, 'Гостевая карта', 8);
insert into Absence_Reason(id, ar_code, old_id, ar_info, display_order) values (9, 'night_work', 9, 'Ночные работы', 9);
insert into Absence_Reason(id, ar_code, old_id, ar_info, display_order) values (10, 'leave_without_pay', 10, 'Отпуск за свой счет', 10);


--insert into company(id,cname) values (-1,'no_company');


--
delete from case_state_matrix;
delete from case_type;

insert into case_type (id, ct_code, ct_info) values (1, 'bug', 'Проблема');
insert into case_type (id, ct_code, ct_info) values (2, 'task', 'Задача');
insert into case_type (id, ct_code, ct_info) values (3, 'freq', 'Запрос на доработку');
insert into case_type (id, ct_code, ct_info) values (4, 'crm.sr', 'CRM-обращение в техподдержку');
insert into case_type (id, ct_code, ct_info) values (5, 'crm.mr', 'CRM-маркетинг');
insert into case_type (id, ct_code, ct_info) values (6, 'sysadm', 'Обращение в службу системных администраторов');
insert into case_type (id, ct_code, ct_info) values (7, 'plan', 'План');
insert into case_type (id, ct_code, ct_info) values (8, 'order', 'Заказ');
insert into case_type (id, ct_code, ct_info) values (9, 'project', 'Проект');

delete from case_term_type;
insert into case_term_type(id, term_code, term_info) values (1,'deadline','Deadline');
insert into case_term_type(id, term_code, term_info) values (2,'workaround','Промежуточное решение');
insert into case_term_type(id, term_code, term_info) values (3,'final','Конечное решение');

commit;


delete from case_state;

insert into case_state (id,state,info) values (1,'created','created');
insert into case_state (id,state,info) values (2,'opened','opened');
insert into case_state (id,state,info) values (3,'closed','closed');
insert into case_state (id,state,info) values (4,'paused','paused');
insert into case_state (id,state,info) values (5,'verified','verified');
insert into case_state (id,state,info) values (6,'reopened','reopened');
insert into case_state (id,state,info) values (7,'solved.noap','solved: not a problem');
insert into case_state (id,state,info) values (8,'solved.fix','solved: fixed');
insert into case_state (id,state,info) values (9,'solved.dup','solved: duplicated');
insert into case_state (id,state,info) values (10,'ignored','ignored');
insert into case_state (id,state,info) values (11,'assigned','assigned');
insert into case_state (id,state,info) values (12,'estimated','estimated');

insert into case_state (id,state,info) values (14,'discuss','discuss');
insert into case_state (id,state,info) values (15,'planned','planned');
insert into case_state (id,state,info) values (16,'active','active / in-process');
insert into case_state (id,state,info) values (17,'done','done');
insert into case_state (id,state,info) values (18,'test','test');
insert into case_state (id,state,info) values (19,'test-local','local test');
insert into case_state (id,state,info) values (20,'test-cust','customer test');
insert into case_state (id,state,info) values (21,'design','design');
commit;


/** state-matrix **/

/** 
* Bug-states 
**/
insert into case_state_matrix (id, case_type,case_state,view_order,old_id,old_code,info)
values (1,1,1,1,1,'submitted','submitted');

insert into case_state_matrix (id, case_type,case_state,view_order,old_id,old_code,info)
values (2,1,2,2,2,'opened','opened');

insert into case_state_matrix (id, case_type,case_state,view_order,old_id,old_code,info)
values (3,1,3,10,3,'closed','closed');

insert into case_state_matrix (id, case_type,case_state,view_order,old_id,old_code,info)
values (4,1,4,11,4,'paused','paused');

insert into case_state_matrix (id, case_type,case_state,view_order,old_id,old_code,info)
values (5,1,5,9, 6,'verified','verified');

insert into case_state_matrix (id, case_type,case_state,view_order,old_id,old_code,info)
values (6,1,6,8, 8,'reopened','reopened');

insert into case_state_matrix (id, case_type,case_state,view_order,old_id,old_code,info)
values (7,1,7,3, 9,'resolved not a problem','solved: not a problem');

insert into case_state_matrix (id, case_type,case_state,view_order,old_id,old_code,info)
values (8,1,8,5, 11,'resolved fixed','solved: fixed');

insert into case_state_matrix (id, case_type,case_state,view_order,old_id,old_code,info)
values (9,1,9,6, 12,'resolved duplicated','solved: duplicated');

insert into case_state_matrix (id, case_type,case_state,view_order,old_id,old_code,info)
values (10,1,10,7, 13,'resolved ignored','ignored');

/**
TASK STATES
*/
insert into case_state_matrix (id, case_type,case_state,view_order,old_id,old_code,info)
values (20,2,1,1,1,'submitted','submitted');

insert into case_state_matrix (id, case_type,case_state,view_order,old_id,old_code,info)
values (21,2,11,2,2,'assigned','assigned');

insert into case_state_matrix (id, case_type,case_state,view_order,old_id,old_code,info)
values (22,2,12,3,3,'estimated','estimated');

insert into case_state_matrix (id, case_type,case_state,view_order,old_id,old_code,info)
values (23,2,2,4,4,'opened','opened');

insert into case_state_matrix (id, case_type,case_state,view_order,old_id,old_code,info)
values (24,2,17,6,5,'completed','completed');

insert into case_state_matrix (id, case_type,case_state,view_order,old_id,old_code,info)
values (25,2,3,7,6,'closed','closed');

insert into case_state_matrix (id, case_type,case_state,view_order,old_id,old_code,info)
values (26,2,10,8,7,'ignored','ignored');

insert into case_state_matrix (id, case_type,case_state,view_order,old_id,old_code,info)
values (27,2,4,5,8,'paused','paused');


/**
* FREQ
*/
insert into case_state_matrix (id, case_type, case_state, info, old_id, view_order, old_code)
values (30, 3, 1, 'new', 1, 1, 'NEW');

insert into case_state_matrix (id, case_type, case_state, info, old_id, view_order, old_code)
values (31, 3, 2, 'opened', 2, 3, 'OPEN');

insert into case_state_matrix (id, case_type, case_state, info, old_id, view_order, old_code)
values (32, 3, 17, 'done', 3, 5, 'DONE');

insert into case_state_matrix (id, case_type, case_state, info, old_id, view_order, old_code)
values (33, 3, 18, 'test', 4, 6, 'TEST');

insert into case_state_matrix (id, case_type, case_state, info, old_id, view_order, old_code)
values (34, 3, 18, 'verified', 5, 7, 'VERIFIED');

insert into case_state_matrix (id, case_type, case_state, info, old_id, view_order, old_code)
values (35, 3, 3, 'closed', 6, 8, 'CLOSED');

insert into case_state_matrix (id, case_type, case_state, info, old_id, view_order, old_code)
values (36, 3, 10, 'ignored', 7, 9, 'IGNORED');

insert into case_state_matrix (id, case_type, case_state, info, old_id, view_order, old_code)
values (37, 3, 4, 'paused', 8, 4, 'PAUSED');

insert into case_state_matrix (id, case_type, case_state, info, old_id, view_order, old_code)
values (38, 3, 14, 'discuss', 9, 2, 'DISCUSS');

/**
* CRM_SUPPORT_SESSION
*/
insert into case_state_matrix (id, case_type, case_state, info, old_id, view_order, old_code)
values (40, 4, 1, 'new', 1, 1, 'NEW');

insert into case_state_matrix (id, case_type, case_state, info, old_id, view_order, old_code)
values (41, 4, 17, 'done', 2, 5, 'DONE');

insert into case_state_matrix (id, case_type, case_state, info, old_id, view_order, old_code)
values (42, 4, 2, 'open', 3, 2, 'open');

insert into case_state_matrix (id, case_type, case_state, info, old_id, view_order, old_code)
values (43, 4, 5, 'verified', 4, 7, 'verified');

insert into case_state_matrix (id, case_type, case_state, info, old_id, view_order, old_code)
values (44, 4, 16, 'in process', 5, 3, 'IN_PROCESS');

insert into case_state_matrix (id, case_type, case_state, info, old_id, view_order, old_code)
values (45, 4, 19, 'local test', 6, 4, 'LOCAL_TEST');

insert into case_state_matrix (id, case_type, case_state, info, old_id, view_order, old_code)
values (46, 4, 20, 'customer test', 7, 6, 'CUSTOMER_TEST');


/**
* CRM_MARKETING_SESSION
*/
insert into case_state_matrix (id, case_type, case_state, info, old_id, view_order, old_code)
values (50, 5, 1, 'new', 1, 1, 'NEW');

insert into case_state_matrix (id, case_type, case_state, info, old_id, view_order, old_code)
values (51, 5, 17, 'done', 2, 5, 'DONE');

insert into case_state_matrix (id, case_type, case_state, info, old_id, view_order, old_code)
values (52, 5, 2, 'open', 3, 2, 'open');

insert into case_state_matrix (id, case_type, case_state, info, old_id, view_order, old_code)
values (53, 5, 5, 'verified', 4, 7, 'verified');

insert into case_state_matrix (id, case_type, case_state, info, old_id, view_order, old_code)
values (54, 5, 16, 'in process', 5, 3, 'IN_PROCESS');

insert into case_state_matrix (id, case_type, case_state, info, old_id, view_order, old_code)
values (55, 5, 19, 'local test', 6, 4, 'LOCAL_TEST');

insert into case_state_matrix (id, case_type, case_state, info, old_id, view_order, old_code)
values (56, 5, 20, 'customer test', 7, 6, 'CUSTOMER_TEST');

/**
* ADMIN-CRM-SESSION
*/
insert into case_state_matrix (id, case_type, case_state, info, old_id, view_order, old_code)
values (60, 6, 1, 'new', 1, 1, 'new');

insert into case_state_matrix (id, case_type, case_state, info, old_id, view_order, old_code)
values (61, 6, 17, 'complete', 2, 3, 'complete');

insert into case_state_matrix (id, case_type, case_state, info, old_id, view_order, old_code)
values (62, 6, 16, 'in process', 3, 2, 'in_process');

insert into case_state_matrix (id, case_type, case_state, info, old_id, view_order, old_code)
values (63, 6, 10, 'ignored', 6, 4, 'ignore');

insert into case_state_matrix (id, case_type, case_state, info, old_id, view_order, old_code)
values (64, 6, 5, 'verified', 4, 5, 'verified');

/**
* WORK-PLAN
*/
insert into case_state_matrix (id, case_type, case_state, info, old_id, view_order, old_code)
values (70, 7, 15, 'planned', 1, 1, 'planned');

insert into case_state_matrix (id, case_type, case_state, info, old_id, view_order, old_code)
values (71, 7, 16, 'active', 2, 2, 'active');

insert into case_state_matrix (id, case_type, case_state, info, old_id, view_order, old_code)
values (72, 7, 4, 'paused', 3, 3, 'paused');

insert into case_state_matrix (id, case_type, case_state, info, old_id, view_order, old_code)
values (73, 7, 17, 'done', 4, 4, 'done');

insert into case_state_matrix (id, case_type, case_state, info, old_id, view_order, old_code)
values (74, 7, 3, 'closed', 5, 5, 'closed');

