INSERT INTO auth_type (id, at_code, at_info) VALUES (1, 'local', 'Local login and password stored in db');
INSERT INTO auth_type (id, at_code, at_info) VALUES (2, 'ldap', 'Local LDAP');

# INSERT INTO absence_reason (id, ar_code, ar_info, old_id, display_order) VALUES (1, 'business_trip', 'Командировка', 1, 2);
# INSERT INTO absence_reason (id, ar_code, ar_info, old_id, display_order) VALUES (2, 'vacation', 'Отпуск', 2, 5);
# INSERT INTO absence_reason (id, ar_code, ar_info, old_id, display_order) VALUES (3, 'illness', 'Болезнь', 3, 6);
# INSERT INTO absence_reason (id, ar_code, ar_info, old_id, display_order) VALUES (4, 'personal_reasons', 'Личные дела', 4, 1);
# INSERT INTO absence_reason (id, ar_code, ar_info, old_id, display_order) VALUES (5, 'local_trip', 'Местная командировка', 5, 3);
# INSERT INTO absence_reason (id, ar_code, ar_info, old_id, display_order) VALUES (6, 'studies', 'Учеба', 6, 4);
# INSERT INTO absence_reason (id, ar_code, ar_info, old_id, display_order) VALUES (7, 'sick_leave', 'Больничный лист', 7, 7);
# INSERT INTO absence_reason (id, ar_code, ar_info, old_id, display_order) VALUES (8, 'guest_card', 'Гостевая карта', 8, 8);
# INSERT INTO absence_reason (id, ar_code, ar_info, old_id, display_order) VALUES (9, 'night_work', 'Ночные работы', 9, 9);
# INSERT INTO absence_reason (id, ar_code, ar_info, old_id, display_order) VALUES (10, 'leave_without_pay', 'Отпуск за свой счет', 10, 10);
# INSERT INTO absence_reason (id, ar_code, ar_info, old_id, display_order) VALUES (11, 'schedule', 'Расписание', 11, 11);

# INSERT INTO admin_state (id, code) VALUES (1, 'locked');
# INSERT INTO admin_state (id, code) VALUES (2, 'unlocked');

INSERT INTO audit_type (id, code, info) VALUES (1, 'ISSUE_MODIFY', 'Изменение обращения');
INSERT INTO audit_type (id, code, info) VALUES (2, 'ISSUE_CREATE', 'Создание обращения');
INSERT INTO audit_type (id, code, info) VALUES (3, 'ISSUE_REPORT', 'Создание отчета по обращениям');
INSERT INTO audit_type (id, code, info) VALUES (4, 'ISSUE_EXPORT', 'Экспорт обращений');
INSERT INTO audit_type (id, code, info) VALUES (5, 'REGION_MODIFY', 'Изменение региона');
INSERT INTO audit_type (id, code, info) VALUES (6, 'REGION_REPORT', 'Создание отчета по регионам');
INSERT INTO audit_type (id, code, info) VALUES (7, 'REGION_EXPORT', 'Экспорт регионов');
INSERT INTO audit_type (id, code, info) VALUES (8, 'PROJECT_MODIFY', 'Изменение проекта');
INSERT INTO audit_type (id, code, info) VALUES (9, 'PROJECT_CREATE', 'Создание проекта');
INSERT INTO audit_type (id, code, info) VALUES (10, 'COMPANY_MODIFY', 'Изменение компании');
INSERT INTO audit_type (id, code, info) VALUES (11, 'COMPANY_CREATE', 'Создание компании');
INSERT INTO audit_type (id, code, info) VALUES (12, 'PRODUCT_MODIFY', 'Изменение продукта');
INSERT INTO audit_type (id, code, info) VALUES (13, 'PRODUCT_CREATE', 'Создание продукта');
INSERT INTO audit_type (id, code, info) VALUES (14, 'CONTACT_MODIFY', 'Изменение контактного лица');
INSERT INTO audit_type (id, code, info) VALUES (15, 'CONTACT_CREATE', 'Создание контактного лица');
INSERT INTO audit_type (id, code, info) VALUES (16, 'ACCOUNT_MODIFY', 'Изменение учетной записи');
INSERT INTO audit_type (id, code, info) VALUES (17, 'ACCOUNT_CREATE', 'Создание учетной записи');
INSERT INTO audit_type (id, code, info) VALUES (18, 'ACCOUNT_REMOVE', 'Удаление учетной записи');
INSERT INTO audit_type (id, code, info) VALUES (19, 'EQUIPMENT_MODIFY', 'Изменение оборудования');
INSERT INTO audit_type (id, code, info) VALUES (20, 'EQUIPMENT_CREATE', 'Создание оборудования');
INSERT INTO audit_type (id, code, info) VALUES (21, 'EQUIPMENT_REMOVE', 'Удаление оборудования');
INSERT INTO audit_type (id, code, info) VALUES (22, 'ROLE_MODIFY', 'Изменение роли');
INSERT INTO audit_type (id, code, info) VALUES (23, 'ROLE_CREATE', 'Создание роли');
INSERT INTO audit_type (id, code, info) VALUES (24, 'ISSUE_COMMENT_CREATE', 'Создание описания для обращения');
INSERT INTO audit_type (id, code, info) VALUES (25, 'ISSUE_COMMENT_MODIFY', 'Изменение описания для обращения');
INSERT INTO audit_type (id, code, info) VALUES (26, 'ISSUE_COMMENT_REMOVE', 'Удаление описания для обращения');
INSERT INTO audit_type (id, code, info) VALUES (27, 'ATTACHMENT_REMOVE', 'Удаление вложения');
INSERT INTO audit_type (id, code, info) VALUES (28, 'EQUIPMENT_COPY', 'Копирование оборудования');
INSERT INTO audit_type (id, code, info) VALUES (29, 'OFFICIAL_MODIFY', 'Изменение должностного лица');
INSERT INTO audit_type (id, code, info) VALUES (30, 'OFFICIAL_CREATE', 'Создание должностного лица');
INSERT INTO audit_type (id, code, info) VALUES (31, 'ROLE_REMOVE', 'Удаление роли');
INSERT INTO audit_type (id, code, info) VALUES (32, 'EMPLOYEE_MODIFY', 'Изменение сотрудника');
INSERT INTO audit_type (id, code, info) VALUES (33, 'EMPLOYEE_CREATE', 'Создание сотрудника');
INSERT INTO audit_type (id, code, info) VALUES (34, 'DEPARTMENT_MODIFY', 'Изменение отдела');
INSERT INTO audit_type (id, code, info) VALUES (35, 'DEPARTMENT_CREATE', 'Создание отдела');
INSERT INTO audit_type (id, code, info) VALUES (36, 'DEPARTMENT_REMOVE', 'Удаление отдела');
INSERT INTO audit_type (id, code, info) VALUES (37, 'WORKER_MODIFY', 'Изменение записи о сотруднике');
INSERT INTO audit_type (id, code, info) VALUES (38, 'WORKER_CREATE', 'Создание записи о сотруднике');
INSERT INTO audit_type (id, code, info) VALUES (39, 'WORKER_REMOVE', 'Удаление записи о сотруднике');
INSERT INTO audit_type (id, code, info) VALUES (40, 'POSITION_MODIFY', 'Изменение должности');
INSERT INTO audit_type (id, code, info) VALUES (41, 'POSITION_CREATE', 'Создание должности');
INSERT INTO audit_type (id, code, info) VALUES (42, 'POSITION_REMOVE', 'Удаление должности');
INSERT INTO audit_type (id, code, info) VALUES (43, 'PHOTO_UPLOAD', 'Загрузка фотографии сотрудника');
INSERT INTO audit_type (id, code, info) VALUES (44, 'DOCUMENT_MODITY', 'Изменение документа');
INSERT INTO audit_type (id, code, info) VALUES (45, 'DOCUMENT_REMOVE', 'Удаление документа');
INSERT INTO audit_type (id, code, info) VALUES (46, 'EMPLOYEE_REGISTRATION_CREATE', 'Создание анкеты нового сотрудника');
INSERT INTO audit_type (id, code, info) VALUES (47, 'PROJECT_REMOVE', 'Удаление проекта');
INSERT INTO audit_type (id, code, info) VALUES (48, 'EMPLOYEE_REGISTRATION_MODIFY', 'Изменение анкеты нового сотрудника');
INSERT INTO audit_type (id, code, info) VALUES (49, 'CONTRACT_MODIFY', 'Изменение договора');
INSERT INTO audit_type (id, code, info) VALUES (50, 'CONTRACT_CREATE', 'Создание договора');
INSERT INTO audit_type (id, code, info) VALUES (51, 'CONTACT_FIRE', 'Увольнение контактного лица');
INSERT INTO audit_type (id, code, info) VALUES (52, 'CONTACT_DELETE', 'Удаление контактного лица');
INSERT INTO audit_type (id, code, info) VALUES (53, 'LINK_CREATE', 'Создание ссылки');
INSERT INTO audit_type (id, code, info) VALUES (54, 'LINK_REMOVE', 'Удаление ссылки');
INSERT INTO audit_type (id, code, info) VALUES (55, 'DOCUMENT_CREATE', 'Создание документа');
INSERT INTO audit_type (id, code, info) VALUES (56, 'DOCUMENT_TYPE_CREATE', 'Создание типа документа');
INSERT INTO audit_type (id, code, info) VALUES (57, 'DOCUMENT_TYPE_REMOVE', 'Удаление типа документа');
INSERT INTO audit_type (id, code, info) VALUES (58, 'PLATFORM_CREATE', 'Создание площадки');
INSERT INTO audit_type (id, code, info) VALUES (59, 'PLATFORM_MODIFY', 'Изменение площадки');
INSERT INTO audit_type (id, code, info) VALUES (60, 'PLATFORM_REMOVE', 'Удаление площадки');
INSERT INTO audit_type (id, code, info) VALUES (61, 'SERVER_CREATE', 'Создание сервера');
INSERT INTO audit_type (id, code, info) VALUES (62, 'SERVER_MODIFY', 'Изменение сервера');
INSERT INTO audit_type (id, code, info) VALUES (63, 'SERVER_REMOVE', 'Удаление сервера');
INSERT INTO audit_type (id, code, info) VALUES (64, 'APPLICATION_CREATE', 'Создание приложения');
INSERT INTO audit_type (id, code, info) VALUES (65, 'APPLICATION_MODIFY', 'Изменение приложения');
INSERT INTO audit_type (id, code, info) VALUES (66, 'APPLICATION_REMOVE', 'Удаление приложения');

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
INSERT INTO case_type (ID, CT_CODE, CT_INFO, NEXT_ID) VALUES (4, 'crm.sr', 'CRM-обращение в техподдержку', 1019256);
INSERT INTO case_type (ID, CT_CODE, CT_INFO, NEXT_ID) VALUES (5, 'crm.mr', 'CRM-маркетинг', 1000000);
INSERT INTO case_type (ID, CT_CODE, CT_INFO, NEXT_ID) VALUES (6, 'sysadm', 'Обращение в службу системных администраторов', 1000000);
INSERT INTO case_type (ID, CT_CODE, CT_INFO, NEXT_ID) VALUES (7, 'plan', 'План', 1000000);
INSERT INTO case_type (ID, CT_CODE, CT_INFO, NEXT_ID) VALUES (8, 'order', 'Заказ', 1000000);
INSERT INTO case_type (ID, CT_CODE, CT_INFO, NEXT_ID) VALUES (9, 'project', 'Проект', 1000196);
INSERT INTO case_type (ID, CT_CODE, CT_INFO, NEXT_ID) VALUES (10, 'official', 'Должностное лицо', 100);
INSERT INTO case_type (ID, CT_CODE, CT_INFO, NEXT_ID) VALUES (11, 'employee-reg', 'Анкета нового сотрудника', 82);
INSERT INTO case_type (ID, CT_CODE, CT_INFO, NEXT_ID) VALUES (12, 'contract', 'Договора', 20);
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
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (91, 4, 35, 4, null, null, 'request to NX');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (92, 4, 36, 4, null, null, 'request to customer');
INSERT INTO case_state_matrix (ID, CASE_TYPE, CASE_STATE, view_order, OLD_ID, OLD_CODE, info) VALUES (93, 4, 4, 3, null, null, 'paused');

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

INSERT INTO company_category (id, created, category_name) VALUES (1, '2017-10-16 21:28:38', 'Заказчик');
INSERT INTO company_category (id, created, category_name) VALUES (2, '2017-10-16 21:28:38', 'Партнер');
INSERT INTO company_category (id, created, category_name) VALUES (3, '2017-10-16 21:28:38', 'Субподрядчик');
INSERT INTO company_category (id, created, category_name) VALUES (4, '2017-09-18 13:40:23', 'Должностное лицо');
INSERT INTO company_category (id, created, category_name) VALUES (5, '2017-11-24 15:34:14', 'Домашняя компания');

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

INSERT INTO dev_unit_type (ID, UNIT_TYPE, UNIT_INFO) VALUES (1, 'component', 'Компонента, бывший термин - проект');
INSERT INTO dev_unit_type (ID, UNIT_TYPE, UNIT_INFO) VALUES (2, 'product', 'Продукт');
INSERT INTO dev_unit_type (ID, UNIT_TYPE, UNIT_INFO) VALUES (3, 'product_direction', 'Направление');
INSERT INTO dev_unit_type (ID, UNIT_TYPE, UNIT_INFO) VALUES (4, 'complex', 'Комплекс');

INSERT INTO dev_unit_state (ID, UST_CODE, UST_INFO) VALUES (1, 'active', 'Активно');
INSERT INTO dev_unit_state (ID, UST_CODE, UST_INFO) VALUES (2, 'deprecated', 'Устарело, более не используется');

INSERT INTO location_type (ID, LOCATION_TYPE, LOCATION_INFO, WEIGHT) VALUES (1, 'DISTRICT', null, null);
INSERT INTO location_type (ID, LOCATION_TYPE, LOCATION_INFO, WEIGHT) VALUES (2, 'REGION', null, null);
INSERT INTO location_type (ID, LOCATION_TYPE, LOCATION_INFO, WEIGHT) VALUES (3, 'MUNICIPALITY', null, null);


INSERT INTO case_doc_type (ID, DT_CODE, DT_INFO, DT_LABEL, DisplayOrder) VALUES (1, 'reqdoc', 'Requirement Doc', 'Requirement', 1);
INSERT INTO case_doc_type (ID, DT_CODE, DT_INFO, DT_LABEL, DisplayOrder) VALUES (2, 'desdoc', 'Design Doc', 'Design', 2);
INSERT INTO case_doc_type (ID, DT_CODE, DT_INFO, DT_LABEL, DisplayOrder) VALUES (3, 'reldoc', 'Release Doc', 'Release', 3);

INSERT INTO user_role (id, role_code, role_info, privileges, scopes, is_default_for_contact) VALUES (1, 'Сотрудник', 'Внутренний сотрудник ', 'ISSUE_EDIT,ISSUE_CREATE,ISSUE_VIEW,ISSUE_REPORT,DASHBOARD_VIEW,CONTACT_VIEW,COMPANY_VIEW,COMMON_PROFILE_VIEW,ISSUE_EXPORT,EMPLOYEE_VIEW', 'SYSTEM', false);
INSERT INTO user_role (id, role_code, role_info, privileges, scopes, is_default_for_contact) VALUES (2, 'ДН : Администратор', 'Администратор системы "Децимальные номера"', 'ACCOUNT_EDIT,ACCOUNT_VIEW', 'ROLE', false);
INSERT INTO user_role (id, role_code, role_info, privileges, scopes, is_default_for_contact) VALUES (3, 'ДН : Редактор', 'Редактор системы "Децимальные номера"', 'EQUIPMENT_VIEW,EQUIPMENT_CREATE,EQUIPMENT_EDIT,EQUIPMENT_REMOVE', 'SYSTEM', false);
INSERT INTO user_role (id, role_code, role_info, privileges, scopes, is_default_for_contact) VALUES (4, 'ДН : Наблюдатель', 'Наблюдатель системы "Децимальные номера"', 'EQUIPMENT_VIEW', 'SYSTEM', false);
INSERT INTO user_role (id, role_code, role_info, privileges, scopes, is_default_for_contact) VALUES (5, 'ТПиМ : Заказчик', 'Сотрудники-заказчики', 'COMMON_PROFILE_VIEW,ISSUE_EDIT,ISSUE_VIEW,ISSUE_CREATE', 'COMPANY', true);
INSERT INTO user_role (id, role_code, role_info, privileges, scopes, is_default_for_contact) VALUES (6, 'Главный администратор', '', 'ISSUE_ASSIGNMENT_VIEW,SITE_FOLDER_REMOVE,CONTRACT_EDIT,CONTRACT_CREATE,EMPLOYEE_REGISTRATION_CREATE,ISSUE_CREATE,DOCUMENT_TYPE_EDIT,ISSUE_FILTER_PRODUCT_VIEW,PROJECT_CREATE,SITE_FOLDER_VIEW,COMPANY_EDIT,DASHBOARD_VIEW,PROJECT_EDIT,DOCUMENT_EDIT,ROLE_EDIT,CONTRACT_VIEW,CONTACT_REMOVE,EMPLOYEE_VIEW,ISSUE_EDIT,ACCOUNT_EDIT,OFFICIAL_VIEW,ISSUE_FILTER_COMPANY_VIEW,PRODUCT_CREATE,COMPANY_VIEW,ACCOUNT_VIEW,COMMON_PROFILE_EDIT,ACCOUNT_REMOVE,ROLE_REMOVE,REGION_REPORT,CASE_STATES_VIEW,ISSUE_VIEW,PRODUCT_VIEW,ROLE_CREATE,PROJECT_REMOVE,ISSUE_REPORT,CONTACT_VIEW,SITE_FOLDER_CREATE,CASE_STATES_CREATE,EMPLOYEE_REGISTRATION_VIEW,COMPANY_CREATE,PRODUCT_EDIT,ISSUE_FILTER_MANAGER_VIEW,SITE_FOLDER_EDIT,DOCUMENT_CREATE,ROLE_VIEW,DOCUMENT_REMOVE,OFFICIAL_EDIT,CASE_STATES_EDIT,ISSUE_WORK_TIME_VIEW,CASE_STATES_REMOVE,CONTACT_EDIT,ISSUE_EXPORT,EQUIPMENT_VIEW,REGION_EDIT,REGION_VIEW,DOCUMENT_TYPE_VIEW,EQUIPMENT_CREATE,PROJECT_VIEW,DOCUMENT_TYPE_CREATE,ACCOUNT_CREATE,COMMON_PROFILE_VIEW,REGION_EXPORT,EQUIPMENT_REMOVE,DOCUMENT_VIEW,EQUIPMENT_EDIT,CONTACT_CREATE', 'SYSTEM', false);
INSERT INTO user_role (id, role_code, role_info, privileges, scopes, is_default_for_contact) VALUES (7, 'ТПиМ : Администратор', 'Только для управления аккаунтами', 'SITE_FOLDER_EDIT,CONTACT_EDIT,ACCOUNT_REMOVE,CASE_STATES_CREATE,ACCOUNT_EDIT,PRODUCT_EDIT,CONTACT_REMOVE,PRODUCT_VIEW,SITE_FOLDER_REMOVE,COMPANY_EDIT,CASE_STATES_EDIT,SITE_FOLDER_VIEW,CONTACT_VIEW,ACCOUNT_CREATE,PRODUCT_CREATE,COMPANY_CREATE,SITE_FOLDER_CREATE,CASE_STATES_VIEW,CONTACT_CREATE,ACCOUNT_VIEW,COMPANY_VIEW', 'ROLE', false);
INSERT INTO user_role (id, role_code, role_info, privileges, scopes, is_default_for_contact) VALUES (8, 'ТПиМ : Менеджер', 'Менеджеры отделов Технической поддержки и Маркетинга', 'COMPANY_CREATE,COMPANY_VIEW,ISSUE_VIEW,ISSUE_CREATE,DASHBOARD_VIEW,CONTACT_CREATE,COMPANY_EDIT,CONTACT_VIEW,SITE_FOLDER_EDIT,ISSUE_EXPORT,PRODUCT_VIEW,PRODUCT_CREATE,SITE_FOLDER_REMOVE,ISSUE_REPORT,SITE_FOLDER_CREATE,SITE_FOLDER_VIEW,ISSUE_EDIT,CONTACT_EDIT,PRODUCT_EDIT', 'SYSTEM', false);
INSERT INTO user_role (id, role_code, role_info, privileges, scopes, is_default_for_contact) VALUES (9, 'ТПиМ : Управление проектами', 'Только для управления проектами', 'DOCUMENT_TYPE_EDIT,DOCUMENT_EDIT,REGION_VIEW,DOCUMENT_TYPE_VIEW,DOCUMENT_TYPE_CREATE,DOCUMENT_CREATE,DOCUMENT_VIEW,PROJECT_CREATE,PROJECT_EDIT,PROJECT_VIEW,REGION_EDIT', 'SYSTEM', false);
INSERT INTO user_role (id, role_code, role_info, privileges, scopes, is_default_for_contact) VALUES (10, 'Администратор банка документов', 'Для банка документов', 'PRODUCT_VIEW,PROJECT_VIEW,DOCUMENT_CREATE,PRODUCT_CREATE,DOCUMENT_TYPE_CREATE,DOCUMENT_VIEW,DOCUMENT_EDIT,PROJECT_CREATE,PROJECT_EDIT,DOCUMENT_TYPE_EDIT,PRODUCT_EDIT,DOCUMENT_TYPE_VIEW', 'SYSTEM', false);
INSERT INTO user_role (id, role_code, role_info, privileges, scopes, is_default_for_contact) VALUES (11, 'Главный администратор (Read Only)', 'Просмотр всех сущностей в системе без возможности редактирования', 'ISSUE_ASSIGNMENT_VIEW,DASHBOARD_VIEW,SITE_FOLDER_VIEW,REGION_VIEW,CONTACT_VIEW,ISSUE_VIEW,EQUIPMENT_VIEW,CASE_STATES_VIEW,ACCOUNT_VIEW,PRODUCT_VIEW,COMPANY_VIEW,COMMON_PROFILE_VIEW', 'SYSTEM', false);
INSERT INTO user_role (id, role_code, role_info, privileges, scopes, is_default_for_contact) VALUES (12, 'Офис-менеджер', '', 'EMPLOYEE_VIEW', 'SYSTEM', false);
INSERT INTO user_role (id, role_code, role_info, privileges, scopes, is_default_for_contact) VALUES (13, 'ТПиМ : Администратор менеджеров', 'Роль для управления учетными записями для менеджеров', 'ACCOUNT_CREATE,ACCOUNT_EDIT,ACCOUNT_VIEW', 'ROLE', false);
INSERT INTO user_role (id, role_code, role_info, privileges, scopes, is_default_for_contact) VALUES (14, 'Новые сотрудники', '', 'EMPLOYEE_REGISTRATION_CREATE,EMPLOYEE_REGISTRATION_VIEW', 'COMPANY', false);
INSERT INTO user_role (id, role_code, role_info, privileges, scopes, is_default_for_contact) VALUES (15, 'ТПиМ : Управление проектами (Read only)', 'Просмотр проектов', 'PROJECT_VIEW', 'SYSTEM', false);
INSERT INTO user_role (id, role_code, role_info, privileges, scopes, is_default_for_contact) VALUES (16, 'Сотрудник (без доступа к обращениям)', 'Без доступа к обращениям', 'EMPLOYEE_VIEW,PRODUCT_VIEW', 'SYSTEM', false);
INSERT INTO user_role (id, role_code, role_info, privileges, scopes, is_default_for_contact) VALUES (17, 'Новые сотрудники (ReadOnly)', 'Только для просмотра анкет сотрудников', 'EMPLOYEE_REGISTRATION_VIEW', 'SYSTEM', false);
INSERT INTO user_role (id, role_code, role_info, privileges, scopes, is_default_for_contact) VALUES (18, 'Разработчик', 'Редактирование продуктов', 'PRODUCT_EDIT,COMPANY_EDIT,PRODUCT_VIEW', 'SYSTEM', false);
INSERT INTO user_role (id, role_code, role_info, privileges, scopes, is_default_for_contact) VALUES (19, 'Cотрудник отдела договоров', 'Cотрудники отдела договоров', 'CONTRACT_CREATE,CONTRACT_VIEW,CONTRACT_EDIT', 'SYSTEM', false);
INSERT INTO user_role (id, role_code, role_info, privileges, scopes, is_default_for_contact) VALUES (20, 'ТПиМ : Внешний отчет', 'Отчеты для заказчика', 'ISSUE_REPORT', 'COMPANY', false);
INSERT INTO user_role (id, role_code, role_info, privileges, scopes, is_default_for_contact) VALUES (21, 'Банк документов (ReadOnly)', 'Только для просмотра документов', 'DOCUMENT_VIEW,DOCUMENT_EDIT,DOCUMENT_CREATE', 'SYSTEM', false);

INSERT INTO importance_level (id, code, info) VALUES (1, 'critical', 'critical');
INSERT INTO importance_level (id, code, info) VALUES (2, 'important', 'important');
INSERT INTO importance_level (id, code, info) VALUES (3, 'basic', 'basic');
INSERT INTO importance_level (id, code, info) VALUES (4, 'cosmetic', 'cosmetic');

INSERT INTO case_stage_type (ID, STAGE_TYPE) VALUES (1, 'general_stage');

INSERT INTO case_term_type (ID, TERM_CODE, TERM_INFO) VALUES (1, 'deadline', 'Deadline');
INSERT INTO case_term_type (ID, TERM_CODE, TERM_INFO) VALUES (2, 'workaround', 'Промежуточное решение');
INSERT INTO case_term_type (ID, TERM_CODE, TERM_INFO) VALUES (3, 'final', 'Конечное решение');

INSERT INTO department_type (id, name, info) VALUES (1, 'отдел', '');

INSERT INTO time_unit (id, unit_code, unit_name, minutes) VALUES (1, 'm', 'минута', 1);
INSERT INTO time_unit (id, unit_code, unit_name, minutes) VALUES (2, 'h', 'час', 60);
INSERT INTO time_unit (id, unit_code, unit_name, minutes) VALUES (3, 'd', 'день', 1440);