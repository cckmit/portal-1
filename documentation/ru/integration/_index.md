---
title: "Интеграции Портала"
description: >
  Интеграции Портала
type: docs
weight: 4
gitlab_project_path: "department-7/portal"
gitlab_project_name: "portal"
gitlab_docs_branch: "master"
gitlab_docs_path: "docs"
gitlab_docs_enable_edit: true
gitlab_docs_enable_edit_ide: true
gitlab_docs_enable_new_issue: false
---

## Интеграция Портала с другими системами

### Тестовый стенд

**Стенд**: http://192.168.110.12:8080/portal-branch/

**Подключение**: ssh support@192.168.110.12 "Wehicfid3"

**Настройки приложения**: /tomcat/cfg2

**Логи**: /tomcat/logs/portal2

**Настройки Winter** - переписываются при деплое в Jenkins, чтобы указывалось на свой каталог "cfg2"
: /tomcat/webapps/portal-branch/WEB-INF/classes/
- spring.properties
- log4j2.component.properties

**Jenkins** :
- сборка : https://jenkins.protei.ru/job/team7/job/Portal/job/build_Portal_by_branch/
- деплой : https://jenkins.protei.ru/job/team7/job/Portal/job/deploy%20integrations%20(192.168.110.12)/


**Особенности**:
- Указывает на одну и ту же базу с тестовым стендом - изменения в базе повлияют на оригинальный тестовый стенд

### Jira

Задача: https://youtrack.protei.ru/issue/PORTAL-467

Отличия от Порталовских обращений

- Компании заказчик - группа компаний "Nexign *"
- Свой workflow статусов
- Форматировании комментариев - Jira Markup вместо Markdown
- Новый тип комментариев: полуприватный - виден нам и админам Nexign, не видны заказчикам Nexign
- Создаются **только** на стороне Jira

###### Тестирование

- Создать на стороне Jira, обязательно повесить **label "sandbox"**

##### Организация обмена:

Направление Jira - Portal (JiraIntegrationServiceImpl.class):

На каждое изменение со стороны Jira отправляются json web хуки.
Вебхуков много: отправляется как частичные изменения, так потом отправляется огромный json со всем обращением: все поля + комментарии.
Мы обрабатываем только крупные изменения.

Синхронизируются только часть изменений:
- название обращения
- описание
- статус
- критичность
- новые вложения
- новые комментарии

**Пример json web хука**: portal/module/jira/src/test/resources

**Nginx**

Так как что внутренний портал, что тестовый стенд не виден снаружи, 
но торчит наружу 10.0.0.18 и настраивается nginx в /etc/nginx/conf.d/crm2.protei.ru.conf

ssh frost@crm2.protei.ru "oXaidee4"

**Проекты Jira**

На стороне Jira есть пограничный проект PRT, который связан уже внутренними проектами Jira (CLM/UCL/CGTMR)
И изменения обращения проходят такой путь CLM/UCL/CGTMR -> PRT => Portal

https://jira.nexign.com/browse/PRT-566

https://jira.nexign.com/browse/CGTMR-20513

**Пользователи на стороне Jira**

Для технических пользователей не обязательны Cisco VPN и 2-х факторная авторизация

Связь: CLM/UCL/CGTMR -> PRT = protei_sync_user

Связь: PRT => Portal = protei_tech_user


**Определение Идентификатора обращения** (JiraIntegrationServiceImpl.selectEndpoint)

У Nexign есть группа компаний, для каждой есть точка доступа таблица "jira_endpoint"

В таблице "case_object" есть поля:
- EXT_APP = "jira"
- EXT_APP_ID = 2_PRT-646, где
  - 2 - id в таблице jira_endpoint
  - PRT-646 - id в пограничном проекте на стороне Jira
-EXT_APP_DATA - специфичная для интеграции информация

**Проблемы**
- Дублирование обращений на стороне jira

Глюки на стороне Jira приводят иногда к задублированию обращений на пограничном проекте PRT:
на один CLM может создаться несколько проектов PRT.
Хоть у нас все проекты помечены как PRT, в case_object.EXT_APP_DATA есть projectId с (CLM/UCL/CGTMR-*),
и обращение при поступлении еще проверяется на задублирование по названию Проекта.

**Маппинг статуса / критичности**

Таблицы:
- статус - jira_status_map_entry
- критичности - jira_priority_map_entry

**Обработка комментариев**
- игнорируются комментарии/вложения, прилетевшие со стороны Портала
- если полуприватные комментарии - то используются роли в jira комментариях "Project Customer Role", "Project Support Role"
- подмена ссылок вложенных изображений на Порталовское хранилище


### Redmine

Отличия от Порталовских обращений

- Компании заказчик "Департамент информатизации Тюменской области"
- Свой workflow статусов

##### Организация обмена
Необходимо установить крипто-ключи, для этого используется отдельный Chrominum GOST / Yandex browser

Пример настройки подключения
```
1) инструкция для доступа через сайт https://citto.ru/subsections/32
www.cryptopro.ru
porubov@protei.ru
cryptoproPASS#1
2) когда установишь, идти нужно сюда https://testagile.72to.ru/ 
3) логин/пароль SizkoDE/********
Там все просто - учетка прикреплена к тестовому проекту, мы видим и имеем доступ только к issue в рамках этого проекта.
```

**Тестовая площадка**: https://testagile.72to.ru

**Продашн** : https://agile.72to.ru/

Redmine - Portal (RedmineForwardChannel.class):

Получение новых данных происходит поллингом "RedmineIssuesCheckRunner.class"
Каждые 5 минут опрашивается сервер на наличие новых открытых обращений/обновлений

В таблице "redmine_endpoint" хранятся данные для подключения, а так же "last_created"/ "last_updated"
для определения новизны данных на стороне Redmine.

Таблицы маппинг статусов, критичностей
- redmine_status_map_entry
- redmine_priority_map_entry

**Маппинг статусов**

Наши статусы как сущности мапятся на переходы между статусами Redmine.

Граф переходов

![img.png](img.png)

| Redmine               | Portal         |
|-----------------------|----------------|
| новая                 | created        |
| в работе              | opened         |
| приёмка: в работе     | customer test  |
| открыта повторно      | active         |
| возвращена для работы | active         |
| закрыта               | verified       |
