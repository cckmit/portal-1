# Изменения настроек

## PORTAL-1520 Заменить проект equipment на HOZVOPROS при создании Анкеты

В portal.properties установить значение youtrack.employee_registration.equipment_project=HOZVOPROS

## PORTAL-1523 Поправить заголовок и адрес from в уведомлении о грядущих днях рождениях

Заменить в portal.properties
    
``` properties
smtp.from=crm
smtp.from.absence=absence
smtp.from.report=report@protei.ru
smtp.from.alias=
```
на
``` properties
smtp.from.crm=crm
smtp.from.portal=portal
smtp.from.absence=absence
smtp.from.crm.alias=CRM
smtp.from.portal.alias=DO_NOT_REPLAY
smtp.from.absence.alias=DO_NOT_REPLAY
```