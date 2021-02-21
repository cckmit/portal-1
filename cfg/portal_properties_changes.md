# Изменения настроек

## PORTAL-1604
Удалить проперти  
```syb.import.enabled```  
```syb.import.employees```  
с внешнего и внутреннего серверов

## PORTAL-1158 отдельный адрес отправителя для отправки отчетов на почту
Добавить в portal.properties внутреннего и внешнего серверов
```
## алиас электронного адреса для отображения для отчетов
## default: DO_NOT_REPLY
smtp.from.report.alias=DO_NOT_REPLY

## часть электронного адреса (перед @) отправителя письма для отчетов
## default: REPORT
smtp.from.report=report
```

## PORTAL-1638 Обучение. Нотификации
Добавить в portal.properties внутреннего и внешнего серверов
```
## адреса для получения рассылки о создании заявки на учебные материалы - курсы
## default: ""
crm.education.request.course.recipients=

## адреса для получения рассылки о создании заявки на учебные материалы - конференции
## default: ""
crm.education.request.conference.recipients=

## адреса для получения рассылки о создании заявки на учебные материалы - литературу
## default: ""
crm.education.request.literature.recipients=

## адреса для получения рассылки об одобрении заявки на учебные материалы - курсы
## default: ""
crm.education.request.approved.course.recipients=

## адреса для получения рассылки об одобрении заявки на учебные материалы - конференции
## default: ""
crm.education.request.approved.conference.recipients=

## адреса для получения рассылки об одобрении заявки на учебные материалы - литературу
## default: ""
crm.education.request.approved.literature.recipients=
```