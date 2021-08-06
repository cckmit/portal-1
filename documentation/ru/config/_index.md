---
title: "Конфигурация"
description: >
  Настройка конфигурации NewPortal
type: docs
weight: 1
gitlab_project_path: "department-7/portal"
gitlab_project_name: "portal"
gitlab_docs_branch: "master"
gitlab_docs_path: "docs"
gitlab_docs_enable_edit: true
gitlab_docs_enable_edit_ide: true
gitlab_docs_enable_new_issue: false
---

Проект работает на java 8
## Настройка JVM
Для поддержки ldap-авторизации по SSL необходимо зарегистрировать ldap-сертификат в java keystore:
``` sh
/usr/protei/shared/jdk/bin/keytool -keystore /usr/protei/shared/jdk/jre/lib/security/cacerts -import -alias ldap-certificate -file ca.crt
```

Так как на данный момент наш ldap-сертификат подписан с помощью слабого алгоритма подписи MD5withRSA, нужно отменить ограничения в /usr/protei/shared/jdk/jre/lib/security/java.security:
- jdk.certpath.disabledAlgorithms - удалить MD5
- jdk.tls.disabledAlgorithms - удалить MD5withRSA

## Настройка tomcat 8
### server.xml

Для корректной работы, необходимо настроить tomcat на использование кодировки UTF-8.
В тег <Connector> добавить атрибут URIEncoding=«UTF-8».

### catalina.sh

Добавить опции JVM, указывающие на расположение java keystore:
``` sh
export JAVA_OPTS=«-Djavax.net.ssl.keyStore=$JAVA_HOME/jre/lib/security/cacerts -Djavax.net.ssl.keyStorePassword=changeit -Dcom.sun.jndi.ldap.object.disableEndpointIdentification=true»
```

## Настройка MySql

Необходима версия MySQL 8.0 или выше.
Должно быть выставлено игнорирование регистра названий таблиц. Необходимо в файле mysql/my.cnf (или my.ini) выставить lower_case_table_names = 1

Кодировка при создании БД должна быть следующая: 
``` sql
CREATE DATABASE portal_dev DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
```


## Конфигурация
### Файлы настроек




Размещаются в каталоге **cfg** (**cfg** - позволяет отличать от каталога **conf** Tomcat)

Путь к каталогу файлов определяется на старте приложения конфигурацией "MainConfiguration", читающей значение переменной "winter_file_path_prefix" из файла "portal/app/portal/src/main/resources/spring.properties"

Путь к файлу **"log4j2.xml"** определяется на старте приложения чтением значения переменной "log4j.configurationFile" из файла "portal/app/portal/src/main/resources/log4j2.component.properties"

На Tomcat файлы настроек размещаются в каталоге «tomcat» в каталоге **cfg**

#### winter.properties

Размещения файла определяется значением переменной окружения "winter_file_path_prefix", задается в "spring.properties"

#### portal.properties

Размещение файла определяется значением переменной окружения "winter_file_path_prefix", задается в "spring.properties"

#### log4j2.xml

Размещения файла указывается в "log4j2.component.properties"


### portal.properties

```properties
## Настройки PORTAL

## Идентиификатор системы (EXTERNAL / INTERNAL)
## default: ""
system.id=INTERNAL

#### Миграция данных

## должно быть запущено один раз и только на основном сервере. Заполняет новое поле на YT хранящее ссылки на задачи портала.
## boolean, default: false
migration.youtrack.links=false

## boolean, default: false
project.migration.youtrack.links=false

#### Общие настройки

## внешний портал параметер
## default: ""
auth.login.suffix=@crm.protei.ru

## Настройки аватарок сотрудников
## default: /usr/protei/shared/avatars/
employee.avatar.path=/usr/protei/shared/avatars/

## Настройка ldap-авторизации
## default: ldaps://ldap_1.protei
ldap.url=ldaps://ldap1.protei.ru

## Настройка планировщика задач
## default: false
task.scheduler.enabled=true

## Настройка максимально допустимого размера для загружаемого файла., megabytes (default 10)
max.file.size=25

## Ссылка на задачу JIRA Jira nexign issue link url
jira.url=https://jira.billing.ru/browse/

## список Jira-проектов для отображения ссылки в наименовании
jira.projects=CLM,CGTMR

## флаг основного рабочего сервера.  Должен быть true только на сервере, где развернуто рабочее приложение
## default: false
is.production.server=true

## адрес внутреннего портала
## default: http://newportal/crm/
crm.url.internal=https://newportal.protei.ru/portal/

## адрес внешнего портала
## default: http://newportal/crm/
crm.url.external=https://crm.protei.ru/crm/

## адрес внешнего портала
## default: http://newportal/crm/
crm.url.current=https://newportal.protei.ru/portal/

## адрес портала для скачивания файлов
## default: http://newportal/crm/
crm.url.files=https://newportal.protei.ru/portal/Portal/

#### Настройки smtp сервера

## Хост smtp сервера
## default: smtp.protei.ru
smtp.host=smtp.protei.ru

## Порт smtp сервера
## int, default: 2525
smtp.port=2525

## Энкодинг почтового сообщения
## default: utf-8
smtp.charset=utf-8

## часть электронного адреса (перед @) отправителя письма для обращений
## default: CRM
smtp.from.crm=crm

## часть электронного адреса (перед @) отправителя письма для остальных оповещений
## default: PORTAL
smtp.from.portal=portal

## часть электронного адреса (перед @) отправителя письма для отчетов по отсутствию
## default: ABSENCE
smtp.from.absence=absence

## алиас электронного адреса для отображения для обращений
## default: CRM
smtp.from.crm.alias=CRM

## алиас электронного адреса для отображения для остальных оповещений
## default: DO_NOT_REPLAY
smtp.from.portal.alias=DO_NOT_REPLY

## алиас электронного адреса для отображения для отчетов по отсутствию
## default: DO_NOT_REPLAY
smtp.from.absence.alias=DO_NOT_REPLY

## паттерн для формирования заголовков Message-ID, In-Reply-To и References. %id% будет заменен идентификатором.
## default: %id%@smtp.protei.ru
smtp.message_id_pattern=%id%@smtp.protei.ru

## блокировать отправку писем на адреса, отличные от *@protei.ru. true to block all external emails
## boolean, default: false
#smtp.block_external_recipients=false

#### Настройки imap сервера

## Хост imap сервера
## default: imap.protei.ru
imap.host=imap.protei.ru
imap.user=
imap.pass=

#### Настройки сервиса комментариев по почте

## default: false - включение сервиса
mail.comment.enable=true

## черный список в "Теме" письма
## default: ""
mail.comment.subject.black.list=

## включение форвардинга письма, если пользователь в портале не зарегистрирован
## default: false
mail.comment.forward.enable=true

## почтовый адрес для форвадинга
## default: support@protei.ru
mail.comment.forward.email=support@protei.ru

#### Настройки почтовых писем

## часть адреса до определенного обращения. %d будет заменен номером обращения
## default: #issues/issue:id=%d;
crm.case.url=#issues/issue:id=%d;

## часть адреса до определенного проекта. %d будет заменен номером проекта
## default: #project_preview:id=%d
crm.project.url=#project_preview:id=%d

## часть адреса до контракта. %d будет заменен id контракта
## default: #contracts/contract:id=%d
crm.contract.url=#contracts/contract:id=%d;

## часть адреса до превью документа. %d будет заменен id документа
## default: #doc_preview:id=%d
crm.document.url.preview=#doc_preview:id=%d

## часть адреса до определенной анкеты нового сотрудника. %d будет заменен id анкеты
## default: #employee_registration_preview:id=%d
crm.employee_registration.url=#employee_registration_preview:id=%d

## адреса для получения рассылки о создании анкет новых сотрудников и завершении подготовки ресурсов и оборудования по ним. Формат - адреса через запятую (без пробела). Кроме указанных адресов, в рассылку будет включен создатель анкеты.
## Пример: crm.employee_registration.recipients=name1@protei.ru,name2@protei.ru
crm.employee_registration.recipients=

## default: ""
crm.room_reservation.recipients=oo@protei.ru

## default: ""
crm.ip_reservation.recipients=sysadmin@protei.ru

## default: ""
crm.birthdays.recipients=bbs@protei.ru

#### Cloud Настройки облачного хранилища

## обязательный
cloud.user=

## обязательный
cloud.password=

## полный адрес до папки, в которой будет организовано хранилище
## обязательный, default: https://cloud.protei.ru/remote.php/webdav/crm/
cloud.path=https://cloud.protei.ru/remote.php/webdav/crm/

#### Настройки старого портала. legacy portal configuration

## default: net.sourceforge.jtds.jdbc.Driver
syb.jdbc.driver=net.sourceforge.jtds.jdbc.Driver

## default: jdbc:sybase:Tds:192.168.1.55:2638/PORTAL2017
#syb.jdbc.url=jdbc:jtds:sybase://192.168.101.140:2642/RESV3
syb.jdbc.url=jdbc:jtds:sybase://192.168.101.140:2638/RESV3

## default: dba
syb.jdbc.login=

## default: sql
syb.jdbc.pwd=

## boolean, default: true
syb.import.enabled=false

## boolean, default: false
syb.import.employees=true

## boolean, default: false
syb.export.enabled=true

## string, default: <текущий ip адрес>
syb.export.identity=portal-app

#### Настройки интеграций

## boolean, default: false
integration.redmine=true

## boolean, default: false
integration.redmine.backchannel=false

## boolean, default: false
integration.redmine.patch=true

## boolean, default: false
integration.youtrack=true

## синхронизация компаний с YT. Создание, изменение, архивирование
## boolean, default: false
integration.youtrack.companies=true

## автоматическое создание задач на YT по изменению фамилий и увольнению сотрудников
## boolean, default: false
integration.youtrack.employees=true

## boolean, default: false
integration.jira=true

## boolean, default: false
integration.jira.backchannel=false

## int, default: 0
integration.jira.queue.limit=0

#### Настройки системы

## период ожидания перед обработкой события. time to assembly case event (in seconds)
## секунды long, default: 30
core.waiting_period=30

#### Настройки svn

## string, обязательный, default: https://svn.protei.ru/svn/PortalDoc
svn.url=https://svn.protei.ru/svn/PortalDoc

## string, обязательный,
svn.username=

## string, обязательный,
svn.password=

## string, default: Add document №%2$s to project №%1$s
## %1$s - project id, %2$s - document id, %3$s - author. Everything optional
svn.commit_message=Add document №%2$s to project №%1$s (%3$s)

## string, default: Update document №%2$s at project №%1$s
svn.commit_message.update=Update document №%2$s at project №%1$s (%3$s)

## string, default: Remove document №%2$s at project №%1$s
svn.commit_message.remove=Remove document №%2$s at project №%1$s (%3$s)

#### Настройки полнотекстового поиска lucene (full-text search)

## директория, в которой будут храниться файлы индексирования документации, создаваемые lucene
## default: /tmp/crm-fulltextsearch-index
lucene.index_path=/opt/tomcat/apache-tomcat-9.0.19/index

#### Настройки генерации отчетов

## максимальное количество задействованных потоков
## int, default: 6
report.threads=6

## размер чанка обработки обращений
## int, default: 20
report.chunk.size=20

## время жизни созданного отчета, по истечению отчет будет удален автоматически
## <winter.core.duration>, default: 3d
report.live_time_duration=3d

## количество секунд, после которого будет предположено, что генерация отчета зависла, и отчет будет пересоздан
## long, default: 1800
report.hang_interval_sec=1800

## путь до папки хранения отчетов
## string, default: reports
report.storage.path=/opt/tomcat/latest/reports

#### Настройки связи обращений case linkage properties

## адрес обращения на портале с номером %id%
## string, default: http://newportal/crm/#issues/issue:id=%id%
case.link.internal=https://newportal.protei.ru/portal/#issues/issue:id=%id%

## адрес обращения на старом портале с номером %id%
## string, default: http://portal/crm/session/session_support.jsp?id=%id%&&action_ref=SessionManageBean_Support.applyFilterAction_Support
case.link.internal.old=http://portal/crm/session/session_support.jsp?id=%id%&&action_ref=SessionManageBean_Support.applyFilterAction_Support

## адрес обращения на портале УИТС с номером %id%
## string, default: https://support.uits.spb.ru/crm/deal/details/%id%/
case.link.uits=https://support.uits.spb.ru/crm/deal/details/%id%/

## адрес обращения на youtrack с номером %id%
## string, default: https://youtrack.protei.ru/issue/%id%
case.link.youtrack=https://youtrack.protei.ru/issue/%id%

##
case.crm.crosslink.youtrack=https://newportal.protei.ru/portal/#issues/issue:id=%id%

##
case.project.crosslink.youtrack=https://newportal.protei.ru/portal/#project_preview:id=%id%

#### Настройки синхронизации с порталом УИТС

## URL REST или API - сервиса УИТС
uits.api.url=https://support.uits.spb.ru/rest/347/*********/crm.deal.get

#### Настройки синхронизации с YouTrack

## URL REST или API - сервиса YouTrack
youtrack.api.baseurl=https://youtrack.protei.ru

## токен авторизации для доступа к созданию и просмотру задач YT (инструкция по созданию: https://www.jetbrains.com/help/youtrack/standalone/Manage-Permanent-Token.html)
## Пример: youtrack.api.auth_token=perm:cG9ydGFs.cG9ydGFs.IOOlyzNfo22S7FpoanxYQB7Ap....
youtrack.api.auth_token=perm:cG9ydGFs.cG9ydGFs.IOOlyzNfo22S7FpoanxYQB7Ap9FW7e

## логин пользователя на YT из токена авторизации
youtrack.api.login=portal

## cron-расписание синхронизации информации по анкетам с youtrack
youtrack.employee_registration.sync_schedule=0 */15 * * * *

## название YouTrack проекта для создания задач по подготовке оборудования для нового сотрудника
youtrack.employee_registration.equipment_project=HOZVOPROS

## название YouTrack проекта для создания задач по открытию доступа к внутренним ресурсам для нового сотрудника
youtrack.employee_registration.admin_project=ACRM

## название YouTrack проекта для создания задач по открытию доступа к телефонной связи
youtrack.employee_registration.phone_project=Office_Tel

## required, id какой-либо записи из таблицы person в БД. Этот id будет указан в качестве создателя генерируемых комментариев, аттачментов и изменений статусов
youtrack.user_id_for_synchronization=710

## id поля на YT, хранящее список компаний
youtrack.custom_field_company_id=34-53

#### Вспомогательные ссылки по разметке

markup.markdown=https://www.markdownguide.org/cheat-sheet/
markup.jira_markup=https://jira.atlassian.com/secure/WikiRendererHelpAction.jspa?section=all

#### UI свойства

## long, default: 200 - лимит количества обращений на дашборде
ui.issue-assignment.desk.limit=200

#### NRPE сервис

## включение NRPE сервиса
## boolean, default: false
nrpe.enable=true

## шаблон запроса к NRPE
## string, default: /usr/lib64/nagios/plugins/check_nrpe -H router.protei.ru -c check_arping_lan -a %s ; echo $?
nrpe.template=/usr/lib64/nagios/plugins/check_nrpe -H router.protei.ru -c check_arping_lan -a %s ; echo $?

## почтовые адреса через запятую для уведомления системных администраторов
## string, default: sysadmin@protei.ru
nrpe.admin.mails=sysadmin@protei.ru


#### Автооткрытие обращений

## включение сервиса
## boolean, default: false
autoopen.enable=false

## включение задержки
## boolean, default: true
autoopen.delay.enable=true

#### 1C API

enterprise1c.api.base_protei_url=http://srv-1cw/buh/odata/standard.odata
enterprise1c.api.base_protei_st_url=http://srv-1cw/buh_st/odata/standard.odata
enterprise1c.api.login=ExtUserPortal
enterprise1c.api.password=QuarZ4$rE
enterprise1c.api.parent_key_st=f37261f0-2fe0-11e7-a3b0-001e0b5da33e
enterprise1c.api.parent_key_resident=adf31e18-691c-11dd-9f1f-001f2908fbfa
enterprise1c.api.parent_key_not_resident=9bdc36f0-11b8-11e0-8438-001f2908fbfa
enterprise1c.api.contract.sync.enabled=false


```


### winter.properties

``` properties
jdbc.driver=<string> #драйвер для работы с БД
jdbc.url=<string> #url для подключения к БД
jdbc.user=<string>
jdbc.password=<string>
jdbc.sql_for_validate_connection=<sql> #sql-запрос, которым можно проверять валидность соединения (для mysql - можно select 1, для oracle - ничего писать не нужно, используется нативный ораклевый пингер)
jdbc.validate_connection_on_borrow=<boolean> #default=false - нужно ли проверять состояние соединение когда оно нективно
liquibase.enabled=<boolean> #default=false - используется ли liquibase, если true - то при старте приложения применяется changelog-файл liquibase/changelog.xml
```





