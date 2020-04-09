
INSERT INTO case_state (ID, STATE, INFO, usage_in_companies) VALUES (1, 'created', 'Исходное состояние задачи при её создании.', 1);
INSERT INTO case_state (ID, STATE, INFO, usage_in_companies) VALUES (2, 'opened', 'Задача открыта и находится в работе. Обязательно должен быть назначен менеджер задачи.', 1);
INSERT INTO case_state (ID, STATE, INFO, usage_in_companies) VALUES (3, 'closed', 'closed', 1);
INSERT INTO case_state (ID, STATE, INFO, usage_in_companies) VALUES (4, 'paused', 'Актуально только дла задач синхронизированных с Jira. Работа над тикетом приостановлена.', 1);
INSERT INTO case_state (ID, STATE, INFO, usage_in_companies) VALUES (5, 'verified', 'Терминальное состояние задачи. Закрытие задачи проверено (подтверждено) Заказчиком. Переход в другие статусы не возможен.', 1);
INSERT INTO case_state (ID, STATE, INFO, usage_in_companies) VALUES (6, 'reopened', 'reopened', 1);
INSERT INTO case_state (ID, STATE, INFO, usage_in_companies) VALUES (7, 'solved.noap', 'solved: not a problem', 1);
INSERT INTO case_state (ID, STATE, INFO, usage_in_companies) VALUES (8, 'solved.fix', 'solved: fixed', 1);
INSERT INTO case_state (ID, STATE, INFO, usage_in_companies) VALUES (9, 'solved.dup', 'solved: duplicated', 1);
INSERT INTO case_state (ID, STATE, INFO, usage_in_companies) VALUES (10, 'ignored', 'ignored', 1);
INSERT INTO case_state (ID, STATE, INFO, usage_in_companies) VALUES (11, 'assigned', 'assigned', 1);
INSERT INTO case_state (ID, STATE, INFO, usage_in_companies) VALUES (12, 'estimated', 'estimated', 1);
INSERT INTO case_state (ID, STATE, INFO, usage_in_companies) VALUES (14, 'discuss', 'discuss', 1);
INSERT INTO case_state (ID, STATE, INFO, usage_in_companies) VALUES (15, 'planned', 'planned', 1);
INSERT INTO case_state (ID, STATE, INFO, usage_in_companies) VALUES (16, 'active', 'Задача находится в работе.', 1);
INSERT INTO case_state (ID, STATE, INFO, usage_in_companies) VALUES (17, 'done', 'Работа по кейсу завершена, ожидается подтверждение Заказчика (статус verified).', 1);
INSERT INTO case_state (ID, STATE, INFO, usage_in_companies) VALUES (18, 'test', 'test', 1);
INSERT INTO case_state (ID, STATE, INFO, usage_in_companies) VALUES (19, 'test-local', 'Проводится локальное тестирование.', 1);
INSERT INTO case_state (ID, STATE, INFO, usage_in_companies) VALUES (20, 'test-cust', 'Заказчик осуществляет проверку/тестирование предоставленного решения по кейсу.', 1);
INSERT INTO case_state (ID, STATE, INFO, usage_in_companies) VALUES (21, 'design', 'design', 1);
INSERT INTO case_state (ID, STATE, INFO, usage_in_companies) VALUES (22, 'unknown', 'unknown', 1);
INSERT INTO case_state (ID, STATE, INFO, usage_in_companies) VALUES (23, 'marketing', 'marketing', 1);
INSERT INTO case_state (ID, STATE, INFO, usage_in_companies) VALUES (24, 'presale', 'presale', 1);
INSERT INTO case_state (ID, STATE, INFO, usage_in_companies) VALUES (25, 'projecting', 'projecting', 1);
INSERT INTO case_state (ID, STATE, INFO, usage_in_companies) VALUES (26, 'development', 'development', 1);
INSERT INTO case_state (ID, STATE, INFO, usage_in_companies) VALUES (27, 'deployment', 'deployment', 1);
INSERT INTO case_state (ID, STATE, INFO, usage_in_companies) VALUES (28, 'testing', 'testing', 1);
INSERT INTO case_state (ID, STATE, INFO, usage_in_companies) VALUES (29, 'support', 'support', 1);
INSERT INTO case_state (ID, STATE, INFO, usage_in_companies) VALUES (30, 'workaround', 'Предоставлено временное решение по кейсу.', 1);
INSERT INTO case_state (ID, STATE, INFO, usage_in_companies) VALUES (31, 'info.request', 'Запрошена информация у Заказчика необходимая для дальнейшей работы по кейсу.', 1);
INSERT INTO case_state (ID, STATE, INFO, usage_in_companies) VALUES (32, 'finished', 'finished', 1);
INSERT INTO case_state (ID, STATE, INFO, usage_in_companies) VALUES (33, 'canceled', 'Задача потеряла актуальность и была отменена.', 1);
INSERT INTO case_state (ID, STATE, INFO, usage_in_companies) VALUES (34, 'cust.pending', 'Ожидание Заказчика. Например: ожидание продления договора ПСГО, ожидание предоставления доступа к сервера, ожидание согласования коммерческого заказа, ожидание закупки комплектующих для серверов и т.п.', 1);
INSERT INTO case_state (ID, STATE, INFO, usage_in_companies) VALUES (35, 'nx.request', 'Актуально только дла задач синхронизированных с Jira. Запрошена информация у Nexign, ожидание ответа.', 1);
INSERT INTO case_state (ID, STATE, INFO, usage_in_companies) VALUES (36, 'customer.request', 'Актуально только дла задач синхронизированных с Jira. Запрошена информация у конечного Заказчика, ожидание ответа.', 1);

INSERT INTO case_type (ID, CT_CODE, CT_INFO, NEXT_ID) VALUES (1, 'bug', 'Проблема', 1000000);
INSERT INTO case_type (ID, CT_CODE, CT_INFO, NEXT_ID) VALUES (2, 'task', 'Задача', 1000000);
INSERT INTO case_type (ID, CT_CODE, CT_INFO, NEXT_ID) VALUES (3, 'freq', 'Запрос на доработку', 1000000);
INSERT INTO case_type (ID, CT_CODE, CT_INFO, NEXT_ID) VALUES (4, 'crm.sr', 'CRM-обращение в техподдержку', 1000000);
INSERT INTO case_type (ID, CT_CODE, CT_INFO, NEXT_ID) VALUES (5, 'crm.mr', 'CRM-маркетинг', 1000000);
INSERT INTO case_type (ID, CT_CODE, CT_INFO, NEXT_ID) VALUES (6, 'sysadm', 'Обращение в службу системных администраторов', 1000000);
INSERT INTO case_type (ID, CT_CODE, CT_INFO, NEXT_ID) VALUES (7, 'plan', 'План', 1000000);
INSERT INTO case_type (ID, CT_CODE, CT_INFO, NEXT_ID) VALUES (8, 'order', 'Заказ', 1000000);
INSERT INTO case_type (ID, CT_CODE, CT_INFO, NEXT_ID) VALUES (9, 'project', 'Проект', 1000000);
INSERT INTO case_type (ID, CT_CODE, CT_INFO, NEXT_ID) VALUES (10, 'official', 'Должностное лицо', 100);
INSERT INTO case_type (ID, CT_CODE, CT_INFO, NEXT_ID) VALUES (11, 'employee-reg', 'Анкета нового сотрудника', 0);
INSERT INTO case_type (ID, CT_CODE, CT_INFO, NEXT_ID) VALUES (12, 'contract', 'Договора', 0);
INSERT INTO case_type (ID, CT_CODE, CT_INFO, NEXT_ID) VALUES (13, 'sf-platform', 'Платформы', 0);

INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (1, 1, 1, 1, 1, 'submitted', 'submitted');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (2, 1, 2, 2, 2, 'opened', 'opened');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (3, 1, 3, 10, 3, 'closed', 'closed');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (4, 1, 4, 11, 4, 'paused', 'paused');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (5, 1, 5, 9, 6, 'verified', 'verified');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (6, 1, 6, 8, 8, 'reopened', 'reopened');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (7, 1, 7, 3, 9, 'resolved not a problem', 'solved: not a problem');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (8, 1, 8, 5, 11, 'resolved fixed', 'solved: fixed');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (9, 1, 9, 6, 12, 'resolved duplicated', 'solved: duplicated');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (10, 1, 10, 7, 13, 'resolved ignored', 'ignored');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (20, 2, 1, 1, 1, 'submitted', 'submitted');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (21, 2, 11, 2, 2, 'assigned', 'assigned');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (22, 2, 12, 3, 3, 'estimated', 'estimated');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (23, 2, 2, 4, 4, 'opened', 'opened');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (24, 2, 17, 6, 5, 'completed', 'completed');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (25, 2, 3, 7, 6, 'closed', 'closed');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (26, 2, 10, 8, 7, 'ignored', 'ignored');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (27, 2, 4, 5, 8, 'paused', 'paused');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (30, 3, 1, 1, 1, 'NEW', 'new');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (31, 3, 2, 3, 2, 'OPEN', 'opened');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (32, 3, 17, 5, 3, 'DONE', 'done');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (33, 3, 18, 6, 4, 'TEST', 'test');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (34, 3, 18, 7, 5, 'VERIFIED', 'verified');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (35, 3, 3, 8, 6, 'CLOSED', 'closed');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (36, 3, 10, 9, 7, 'IGNORED', 'ignored');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (37, 3, 4, 4, 8, 'PAUSED', 'paused');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (38, 3, 14, 2, 9, 'DISCUSS', 'discuss');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (40, 4, 1, 1, 1, 'NEW', 'new');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (41, 4, 17, 5, 2, 'DONE', 'done');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (42, 4, 2, 2, 3, 'open', 'open');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (43, 4, 5, 7, 4, 'verified', 'verified');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (44, 4, 16, 3, 5, 'IN_PROCESS', 'in process');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (45, 4, 19, 4, 6, 'LOCAL_TEST', 'local test');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (46, 4, 20, 6, 7, 'CUSTOMER_TEST', 'customer test');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (50, 5, 1, 1, 1, 'NEW', 'new');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (51, 5, 17, 5, 2, 'DONE', 'done');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (52, 5, 2, 2, 3, 'open', 'open');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (53, 5, 5, 7, 4, 'verified', 'verified');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (54, 5, 16, 3, 5, 'IN_PROCESS', 'in process');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (55, 5, 19, 4, 6, 'LOCAL_TEST', 'local test');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (56, 5, 20, 6, 7, 'CUSTOMER_TEST', 'customer test');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (60, 6, 1, 1, 1, 'new', 'new');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (61, 6, 17, 3, 2, 'complete', 'complete');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (62, 6, 16, 2, 3, 'in_process', 'in process');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (63, 6, 10, 4, 6, 'ignore', 'ignored');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (64, 6, 5, 5, 4, 'verified', 'verified');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (70, 7, 15, 1, 1, 'planned', 'planned');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (71, 7, 16, 2, 2, 'active', 'active');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (72, 7, 4, 3, 3, 'paused', 'paused');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (73, 7, 17, 4, 4, 'done', 'done');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (74, 7, 3, 5, 5, 'closed', 'closed');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (75, 9, 22, null, null, null, null);
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (76, 9, 23, null, null, null, null);
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (77, 9, 24, null, null, null, null);
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (78, 9, 25, null, null, null, null);
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (79, 9, 26, null, null, null, null);
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (80, 9, 27, null, null, null, null);
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (81, 9, 28, null, null, null, null);
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (82, 9, 29, null, null, null, null);
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (83, 4, 30, 4, null, null, 'workaround solution');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (84, 4, 31, 4, null, null, 'request information');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (85, 9, 32, null, null, null, null);
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (86, 9, 33, null, null, null, null);
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (87, 4, 34, 4, null, null, 'customer pending');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (88, 4, 33, 7, null, null, 'canceled');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (89, 4, 35, 4, null, null, 'request to NX');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (90, 4, 36, 4, null, null, 'request to customer');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (91, 4, 4, 3, null, null, 'paused');

INSERT INTO case_state_workflow (id, info) VALUES (0, 'NO_WORKFLOW');
INSERT INTO case_state_workflow (id, info) VALUES (1, 'NX_JIRA');

INSERT INTO case_state_workflow_link (id, workflow_id, state_from, state_to) VALUES (1, 1, 1, 2);
INSERT INTO case_state_workflow_link (id, workflow_id, state_from, state_to) VALUES (3, 1, 2, 4);
INSERT INTO case_state_workflow_link (id, workflow_id, state_from, state_to) VALUES (4, 1, 2, 5);
INSERT INTO case_state_workflow_link (id, workflow_id, state_from, state_to) VALUES (9, 1, 17, 2);
INSERT INTO case_state_workflow_link (id, workflow_id, state_from, state_to) VALUES (16, 1, 2, 35);
INSERT INTO case_state_workflow_link (id, workflow_id, state_from, state_to) VALUES (17, 1, 2, 36);
INSERT INTO case_state_workflow_link (id, workflow_id, state_from, state_to) VALUES (18, 1, 35, 2);
INSERT INTO case_state_workflow_link (id, workflow_id, state_from, state_to) VALUES (19, 1, 36, 2);

INSERT INTO company (id, created, cname, info, category_id, contactInfo, groupId, old_id, parent_company_id, is_hidden, is_deprecated) VALUES (-1, null, 'no_company', null, null, null, null, null, null, false, false);
INSERT INTO company (id, created, cname, info, category_id, contactInfo, groupId, old_id, parent_company_id, is_hidden, is_deprecated) VALUES (1, '2005-02-25 12:31:15', 'НТЦ Протей', '', 5, '{"items": [{"a": "PUBLIC", "t": "ADDRESS_LEGAL", "v": ""}, {"a": "PUBLIC", "t": "ADDRESS", "v": ""}, {"a": "PUBLIC", "t": "WEB_SITE", "v": ""}]}', null, 1, null, false, false);
INSERT INTO company (id, created, cname, info, category_id, contactInfo, groupId, old_id, parent_company_id, is_hidden, is_deprecated) VALUES (2, '2018-04-12 12:17:48', 'Протей СТ', '', 5, '{"items": []}', null, null, null, false, false);
INSERT INTO company (id, created, cname, info, category_id, contactInfo, groupId, old_id, parent_company_id, is_hidden, is_deprecated) VALUES (3, '2018-04-12 12:17:49', 'Протей', '', 5, '{"items": []}', null, null, null, false, false);
INSERT INTO company (id, created, cname, info, category_id, contactInfo, groupId, old_id, parent_company_id, is_hidden, is_deprecated) VALUES (4, '2018-04-12 12:17:49', 'Universum', '', 5, '{"items": [{"a": "PUBLIC", "t": "ADDRESS_LEGAL", "v": ""}, {"a": "PUBLIC", "t": "ADDRESS", "v": ""}, {"a": "PUBLIC", "t": "WEB_SITE", "v": ""}]}', null, 4267, null, true, false);

INSERT INTO company_group_home (companyId, external_code, mainId) VALUES (1, null, null);
INSERT INTO company_group_home (companyId, external_code, mainId) VALUES (2, 'protei-st', 1);
INSERT INTO company_group_home (companyId, external_code, mainId) VALUES (3, 'protei', 1);
INSERT INTO company_group_home (companyId, external_code, mainId) VALUES (4, 'universum', 1);

INSERT INTO dev_unit_mrole (ID, UROLE_CODE, UROLE_INFO) VALUES (1, 'head_manager', null);
INSERT INTO dev_unit_mrole (ID, UROLE_CODE, UROLE_INFO) VALUES (2, 'deploy_manager', null);
INSERT INTO dev_unit_mrole (ID, UROLE_CODE, UROLE_INFO) VALUES (3, 'decision_center', 'Центр принятия решений');
INSERT INTO dev_unit_mrole (ID, UROLE_CODE, UROLE_INFO) VALUES (4, 'chief_decision_maker', 'Главный ЛПР');
INSERT INTO dev_unit_mrole (ID, UROLE_CODE, UROLE_INFO) VALUES (5, 'decision_keeper', 'Привратник');
INSERT INTO dev_unit_mrole (ID, UROLE_CODE, UROLE_INFO) VALUES (6, 'tech_specialists', 'Технические специалисты');
INSERT INTO dev_unit_mrole (ID, UROLE_CODE, UROLE_INFO) VALUES (7, 'influence_maker', 'Лицо, влияющее на принятие решений');
INSERT INTO dev_unit_mrole (ID, UROLE_CODE, UROLE_INFO) VALUES (8, 'chief_influence_maker', 'Главное лицо, влияющее на принятие решений');
INSERT INTO dev_unit_mrole (ID, UROLE_CODE, UROLE_INFO) VALUES (9, 'economist', 'Экономист');
INSERT INTO dev_unit_mrole (ID, UROLE_CODE, UROLE_INFO) VALUES (10, 'well_wisher', 'Доброжелатель');
INSERT INTO dev_unit_mrole (ID, UROLE_CODE, UROLE_INFO) VALUES (11, 'receptivity_center', 'Центр восприимчивости');
INSERT INTO dev_unit_mrole (ID, UROLE_CODE, UROLE_INFO) VALUES (12, 'hardware_curator', 'АО - Курирование вопросов разработки аппаратного обеспечения');
INSERT INTO dev_unit_mrole (ID, UROLE_CODE, UROLE_INFO) VALUES (13, 'software_curator', 'ПО - Курирование вопросов разработки программного обеспечения');
INSERT INTO dev_unit_mrole (ID, UROLE_CODE, UROLE_INFO) VALUES (14, 'intro_new_tech_solutions', 'В - Внедрение новых технологических решений');
INSERT INTO dev_unit_mrole (ID, UROLE_CODE, UROLE_INFO) VALUES (15, 'liable_for_auto_testing', 'ОТК - Ответственный за автоматизированное тестирование компонентов комплекса');
INSERT INTO dev_unit_mrole (ID, UROLE_CODE, UROLE_INFO) VALUES (16, 'tech_support_curator', 'ТП - Курирует все вопросы технической поддержки комплекса');
INSERT INTO dev_unit_mrole (ID, UROLE_CODE, UROLE_INFO) VALUES (17, 'product_assembler', 'СБ - Сборка изделия из комплектующих изделий');
INSERT INTO dev_unit_mrole (ID, UROLE_CODE, UROLE_INFO) VALUES (18, 'supply_preparation', 'ПП - Подготовка поставки. Производство комплектующих изделий');
INSERT INTO dev_unit_mrole (ID, UROLE_CODE, UROLE_INFO) VALUES (19, 'engineer_doc_dev', 'КД - Разработка конструкторской документации');
INSERT INTO dev_unit_mrole (ID, UROLE_CODE, UROLE_INFO) VALUES (20, 'tech_doc_dev', 'ТД - Разработка технологической документации');
INSERT INTO dev_unit_mrole (ID, UROLE_CODE, UROLE_INFO) VALUES (21, 'software_doc_dev', 'ПД - Разработка программной документации');
INSERT INTO dev_unit_mrole (ID, UROLE_CODE, UROLE_INFO) VALUES (22, 'liable_for_certification', 'С - Сертификационная деятельность');
INSERT INTO dev_unit_mrole (ID, UROLE_CODE, UROLE_INFO) VALUES (23, 'okr_escort', 'ОКР - Сопровождение ОКР');
INSERT INTO dev_unit_mrole (ID, UROLE_CODE, UROLE_INFO) VALUES (24, 'quality_control_smk', 'КК - Контроль качества в рамках СМК, учет движения комплектующих изделий на всех этапах производства');
INSERT INTO dev_unit_mrole (ID, UROLE_CODE, UROLE_INFO) VALUES (25, 'customer_integration', 'РП - Проектная деятельность. Взаимодействие с заказчиком');

INSERT INTO importance_level (id, code, info) VALUES (1, 'critical', 'critical');
INSERT INTO importance_level (id, code, info) VALUES (2, 'important', 'important');
INSERT INTO importance_level (id, code, info) VALUES (3, 'basic', 'basic');
INSERT INTO importance_level (id, code, info) VALUES (4, 'cosmetic', 'cosmetic');
