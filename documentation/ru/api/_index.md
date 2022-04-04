---
title: "Portal API"
type: docs
weight: 3
gitlab_project_path: "department-7/portal"
gitlab_project_name: "portal"
gitlab_docs_branch: "master"
gitlab_docs_path: "docs"
gitlab_docs_enable_edit: true
gitlab_docs_enable_edit_ide: true
gitlab_docs_enable_new_issue: false
---


## Синхронизация c Youtrack
### Авторизация

Авторизация Basic.

Для доступа к порталу со стороны Youtrack, на портал добавлен пользователь

- **login**: portal_api
- **password**: <задается в портале в настройках учетной записи>

**Базовый url portal api**

``` http
 http://<portal host>:<portal port>/portal/Portal/springApi/api
```

Методы в api для синхронизации с Youtrack:

#### Обновление списка номеров обращений, привязанных к YT-задаче

- **путь**: /updateYoutrackCrmNumbers/{youtrackId}
- **метод**: POST
- **авторизация**: Basic
- **тело запроса**: Строка, в которой через запятую указаны все номера обращений для данной задачи youtrack. Может быть пустой или null.
- **youtrackId** ***(обязательный параметр)*** - название задачи в youtrack для которой будут обновлен список обращений
- **описание**: Список обращений, привязанных к YT-задаче {youtrackId}, будет актуализирован на основе номеров обращений, пришедших в теле запроса. Для всех обращений, чьи номера указаны в теле запроса, будет проверено наличие ссылки на эту YT-задачу и при отсутствии ссылка будет создана. Для все обращений, в которых есть ссылка на эту YT-задачу, но в теле запроса номеров этих обращений нет, ссылка на YT-задачу будет удалена. Таким образом, при пустом теле запроса все ссылки из обращений на эту задачу будут удалены.

#### Изменение имени YT-задачи

- **путь**: /changeyoutrackid/{oldyoutrackid}/{newyoutrackid}
- **метод**: POST
- **авторизация**: Basic
- **тело запроса**: Без тела.
- **oldyoutrackid** ***(обязательный параметр)*** - текущее название YT-задачи
- **newyoutrackid** ***(обязательный параметр)*** - новое название YT-задачи
- **описание**: Во всех обращениях все ссылки на YT-задачу с именем {oldyoutrackid} переименуются на {newyoutrackid}.

## Авторизация
Данный запрос производится без авторизации

- **url** - host:port/{app_name}/api/authorization?login=<login>&password=<password>
- **запрос** - get 
- **ответ** - содержит статус выполнения и в поле data DevUnitInfo
- *атрибуты запроса* - login – имя пользователя, password - пароль пользователя 

``` json
"status":"OK",
   "data":{
      "ip":null,
      "userLoginId":1,
      "personId":1,
      "personDisplayShortName":"Васильев В.В.",
      "companyId":1,
      "companyAndChildIds":[
         1,
         2
      ],
      "roles":[
         {
            "id":16,
            "code":"Сотрудник (без доступа к обращениям)",
            "info":"Внутренний сотрудник без доступа к обращениям",
            "privileges":[
               "EMPLOYEE_VIEW",
               "PRODUCT_VIEW"
            ],
            "scope":"SYSTEM",
            "defaultForContact":false
         }
      ],
      "sessionId":"050428864626102772655747"
   }
}
```

**Пример:**
``` sh
`curl 'host:9007/Portal/springApi/api/authorization?login=user&password=password'`
```

## Обращения
### Получение списка обращений

- **путь**: /cases
- **метод**: POST
- **авторизация**: Basic
- **тело запроса**: json

*возможные параметры:*

- **managerIds** - список идентификаторов менеджеров обращения
- **managerCompanyIds** - список идентификаторов компаний менеджеров
- **stateIds** - список идентификаторов состояний
- **companyIds** - список идентификаторов компаний
- **productIds** - список идентификаторов проектов
- **caseTagsNames** - список названий тегов
- **limit** - количество элементов в запросе
- **offset** - пропуск первых нескольких элементов; отступ
- **from** - дата создания (от)
- **viewPrivate** - bool, приватность обращения. true – выборка _только_ приватных, false = только _публичных_. В случае, если параметр не укзаан – выгружаются все обращения без учета приватности. 
- **modifiedFrom** - date, дата изменения (от)
- **modifiedTo** - date, дата изменения (до)

**Пример запроса:**

``` json
    POST /Portal/springApi/api/cases HTTP/1.1
    Host: 127.0.0.1:8888
    Authorization: Basic ZnJvc3Q6YWxleGFfZg==
    Content-Type: application/json
    Cache-Control: no-cache
    Postman-Token: a19a593e-1251-6db7-24bb-9efe890c8c3d
   
    {
     	"stateIds": [ 1 ],
     	"managerIds": [ 670 ],
        "managerCompanyIds": [ 1 ],
        "companyIds": [ 777 ],
        "productIds": [ 1, 2, 999 ],
        "caseTagsNames" ["tag1", "tag2"],
        "limit" : 10,
     	"offset" : 10,
      	"from" : "2015-01-01",
     	"to" : "2019-08-05"
    }
```


### Создание обращения

- **путь**: /cases
- **метод**: POST
- **авторизация**: Basic
- **тело запроса**: json

*необходимые параметры:*

- **AuditType** - Тип объекта, должен быть «CaseObject»
- **name** - название обращения
- **stateId** - айди состояния обращения
- **impLevel** - уровень критичности обращения, статус
- **typeId** - айди типа обращения
- **initiatorCompanyId** - айди компании заявителя
- **managerCompanyId** - айди компании менеджера
- **creatorId** - айди создателя обращения

*возможные параметры:*

- **privateCase** - приватность обращения
- **initiatorId** - айди заявителя
- **productId** - айди продукта
- **managerId** - айди менеджера
- **info** - описание
- **pauseDate** - дата возобновления работ.



**Пример запроса:**

```
    POST /Portal/springApi/api/cases/create HTTP/1.1
    Host: 127.0.0.1:8888
    Authorization: Basic ZnJvc3Q6YWxleGFfZg==
    Content-Type: application/json
    Cache-Control: no-cache
    Postman-Token: a19a593e-1251-6db7-24bb-9efe890c8c3d
   
    {
        "AuditType" : "CaseObject",
        "name" : "create_issue_from_api",
        "stateId" : 1,
        "typeId" : 4,
        "impLevel": 3,
        "initiatorCompanyId" : 1,
        "managerCompanyId" : 1,
        "creatorId": 7925
    }
```


### Изменение обращения

- **путь**: /cases/update
- **метод**: POST
- **авторизация**: Basic
- **тело запроса**: json

*необходимые параметры:*

- **AuditType** - Тип объекта, должен быть «CaseObject»
- **id** - айди обращения
- **name** - название обращения
- **stateId** - айди состояния обращения
- **impLevel** - уровень критичности обращения, статус
- **typeId** - айди типа обращения
- **initiatorCompanyId** - айди компании заявителя
- **managerCompanyId** - айди компании менеджера
- **creatorId** - айди создателя обращения


*возможные параметры:*

- **privateCase** - приватность обращения
- **initiatorId** - айди заявителя
- **productId** - айди продукта
- **managerId** - айди менеджера
- **info** - описание
- **pauseDate** - дата возобновления работ.


**Пример запроса:**

```
    POST /Portal/springApi/api/cases/update HTTP/1.1
    Host: 127.0.0.1:8888
    Authorization: Basic ZnJvc3Q6YWxleGFfZg==
    Content-Type: application/json
    Cache-Control: no-cache
    Postman-Token: a19a593e-1251-6db7-24bb-9efe890c8c3d
   
    {
        "AuditType" : "CaseObject",
        "id" : 160037,
        "name" : "update_issue_from_api",
        "stateId" : 4,
        "typeId" : 4,
        "impLevel": 3,
        "initiatorCompanyId" : 1,
        "managerCompanyId" : 1,
        "creatorId": 7925,
        "pauseDate" : 1587646355           // 23-04-2020
    }
```


Создание/изменение обращения без менеджера возможно только со статусами CREATED и CANCELED


Заявитель должен принадлежать компании заявителя. Менеджер должен принадлежать компании менеджера. Менеджера можно не указывать. Если менеджер указан, то должен быть указан продукт


Параметр pauseDate имеет тип unix timestamp. Актуален и обязателен только для stateId = 4. Валидными датами считаются те, которые следуют после текущей даты. Текущая дата считается невалидной.


Если выбрана компания заявителя с автоматическим открытием задач, то, помимо обязательного указания продукта, этот продукт обязательно должен быть привязанным к проекту любой площадки компании заявителя, если площадка не была указана. Также, продукт может быть частью комплекса**


### Получение списка комментариев обращения

Комментарии бывают 3 типов:

1. Текстовые
2. Изменение состояния обращения
3. Изменение критичности


- **путь**: /comments
- **метод**: POST
- **авторизация**: Basic
- **тело запроса**: json

*возможные параметры:*

- **caseNumber** - Номер обращения
- **limit** - количество элементов в запросе
- **offset** - пропуск первых нескольких элементов; отступ

**Пример запроса:**

```
	POST /Portal/springApi/api/comments HTTP/1.1
	Host: 127.0.0.1:8888
	Authorization: Basic ZnJvc3Q6YWxleGFfZg==
	Content-Type: application/json
	Cache-Control: no-cache
	Postman-Token: 4152c793-c21a-5b7e-f285-21e66593294f
	{
		"caseNumber": 100001,
		"limit" : 2,
		"offset" : 0
	}
```

```
__auth147=`printf 'manager01:manager01' | base64`; curl -d '{ "caseNumber" : 100001, "limit" : 2,"offset" : 0 }' -H "Authorization: Basic ${__auth147}" -H 'Content-Type: application/json' -o - -D - 127.0.0.1:8888/Portal/springApi/api/comments; unset __auth147
```

**Ответ:**

```
	...
    {
      "id": 714905,
      "created": 1580798179000,
      "caseId": 158739,
      "authorId": 777,
      "authorName": "Иванов И.И.",
      "companyId": 1,
      "companyName": "НТЦ Протей",
      "companyCategoryId": 5,
      "text": "Текст комментария",
      "caseStateId": 1,
      "caseImpLevel": 3,
      "isPrivateComment": false
    } 
	...
```

где:

- **id** - идентификатор сообщения в системе
- **created** - время создания сообщения в Unix time формате
- **caseId** - идентификатор обращения, к которому относится комментарий
- **authorId** - идентификатор пользователя
- **authorName** - короткое имя пользователя
- **companyId** - идентификатор компании
- **companyName** - короткое имя компании
- **companyCategoryId** - категория компании
- **text** - текст комментария
- **caseStateId** - изменение состояния обращения
- **caseImpLevel** - изменение критичности обращения
- **isPrivateComment** - отметка о приватности комментария

### Получение истории обращения


- **путь**: /case/histories
- **метод**: POST
- **авторизация**: Basic
- **тело запроса**: json

*возможные параметры:*

- **caseNumber** - Номер обращения
- **limit** - количество элементов в запросе
- **offset** - пропуск первых нескольких элементов; отступ

**Пример запроса:**

```
	POST /Portal/springApi/api/case/histories HTTP/1.1
	Host: 127.0.0.1:8888
	Authorization: Basic ZnJvc3Q6YWxleGFfZg==
	Content-Type: application/json
	Cache-Control: no-cache
	Postman-Token: 4152c793-c21a-5b7e-f285-21e66593294f
	{
		"caseNumber": 100001,
		"limit" : 2,
		"offset" : 0
	}
```

```
__auth147=`printf 'manager01:manager01' | base64`; curl -d '{ "caseNumber" : 100001, "limit" : 2,"offset" : 0 }' -H "Authorization: Basic ${__auth147}" -H 'Content-Type: application/json' -o - -D - 127.0.0.1:8888/Portal/springApi/api/case/histories; unset __auth147
```

**Ответ:**

```
	...
    {
      "id":600403,
      "initiatorId":568,
      "initiatorShortName":"Иванов И.И.",
      "initiatorFullName":"Иванов Иван Иванович",
      "date":1617112877000,
      "caseObjectId":164888,
      "action":"ADD",
      "type":"TAG",
      "oldId":null,
      "oldValue":null,
      "newId":50,
      "newValue":"done",
      "oldColor":null,
      "newColor":"#2E7D32",
      "initiatorName":"Иванов Иван Иванович"
    } 
	...
```

где:

- **id** - идентификатор записи истории в системе
- **initiatorId** - идентификатор создателя записи
- **initiatorShortName** - фамилия и инициалы создателя записи
- **initiatorFullName** - полное имя создателя записи
- **date** - время создания записи в Unix time формате
- **caseObjectId** - идентификатор обращения
- **action** - действие historyActionId
- **type** - тип historyTypeId
- **oldId** - прошлый идентификатор
- **oldValue** - прошлое значение
- **newId** - новый идентификатор
- **newValue** - новое значение
- **oldColor** - старый цвет
- **newColor** - новый цвет
- **initiatorName** - имя создателя записи


### Получение записи по затраченному времени


- **путь**: /case/elapsedTimes
- **метод**: POST
- **авторизация**: Basic
- **тело запроса**: json

*возможные параметры:*

- **from** - дата создания (от)
- **to** - дата создания (до)
- **productIds** - список идентификаторов продуктов
- **companyIds** - список идентификаторов компаний
- **authorIds** - список идентификаторов авторов записей
- **offset** - пропуск первых нескольких элементов; отступ
- **limit** - количество элементов в запросе

**Пример запроса:**

```
POST /Portal/springApi/api/case/elapsedTimes
Host: 127.0.0.1:8888
Authorization: Basic ZnJvc3Q6YWxleGFfZg==
Content-Type: application/json
Cache-Control: no-cache
    
{
  "from": "2021-03-08",
  "to": "2022-02-23",
  "productIds": [ 1, 2, 3 ],
  "companyIds": [ 11, 22, 33 ],
  "authorIds": [ 7777 ],
  "limit" : 2,
  "offset" : 0
}
```

**Ответ:**

```
{
  "status": "OK",
  "data": [
    {
      "id": 821832,
      "date": 1579685719000,
      "elapsedTime": 480,
      "timeElapsedType": "WATCH",
      "authorId": 7777,
      "caseId": 168474,
      "caseNumber": 1027392,
      "caseImpLevel": 3,
      "caseStateId": 5,
      "caseStateName": "verified",
      "caseInitiatorCompanyId": 2934,
      "caseInitiatorCompanyName": "Департамент Всех Департаментов",
      "caseProductId": 6618,
      "caseProductName": "Система-112",
      "caseManagerId": 42,
      "caseManagerName": "Иванов Д.Е.",
      "caseManagerCompanyId": 1,
      "caseManagerCompanyName": "НТЦ Протей"
    }
  ]
}
```

где:

- **id** - идентификатор записи по затраченному времени
- **date** - дата создания записи (Unix time)
- **elapsedTime** - потраченное время (м.)
- **timeElapsedType** - тип записи
- **authorId** - автор записи
- **caseId** - идентификатор обращения
- **caseNumber** - номер обращения
- **caseImpLevel** - уровень критичности обращения, статус
- **caseStateId** - идентификатор состояния обращения
- **caseStateName** - название состояния обращения
- **caseInitiatorCompanyId** - идентификатор компании заявителя
- **caseInitiatorCompanyName** - название компании заявителя
- **caseProductId** - идентификатор продукта
- **caseProductName** - название продукта
- **caseManagerId** - идентификатор менеджера
- **caseManagerName** - имя менеджера
- **caseManagerCompanyId** - идентификатор компании менеджера
- **caseManagerCompanyName** - название компании менеджера


## Продукты / Компоненты

реализовано 4.1.57.3 Добавлены api методы: 

### Получение информации по продукту

- **url** - host:port/{app_name}/api/products/{id_dev_unit}
- **id_dev_unit** - идентификатор продукта или компонента 
- **запрос** - post 
- **ответ** : содержит статус выполнения и в поле data DevUnitInfo


``` json
{"status":"OK",
 "data":{
        "id":17765,
        "configuration":"Configuration content",
        "cdrDescription":"cdr Description some text",
        "historyVersion":"History version some text",
        "description":"Description of product"
  }
}
```

**Пример:**
``` sh
`curl -X POST -u user:password  'host:9007/Portal/springApi/api/products/17765'`
```

### Общий запрос для получения Комплекс, Продукт, Компонент, Направление

- **путь**: /case/getProductShortViews
- **метод**: POST
- **авторизация**: Basic
- **тело запроса**: json

*возможные параметры:*

- **ids** - список идентификаторов
- **state** - статус
- **types** -список типов
- **directionIds** - список идентификаторов направлений
- **offset** - пропуск первых нескольких элементов; отступ
- **limit** - количество элементов в запросе

**Пример запроса:**

```json
POST /Portal/springApi/api/getProductShortViews
Authorization: Basic porubov password
Content-Type: application/json
Cache-Control: no-cache

{
"ids" : [6683],
"state": "ACTIVE",
"types" : ["PRODUCT"],
"directionIds": [6779],
"limit" : 50,
"offset" : 0
}
```

**Ответ:**

```json
{
  "status": "OK",
  "data": [{
      "id": 6683,
      "name": "CAPL (Protei_CAPL)",
      "stateId": 1,
      "aliases": "",
      "type": "PRODUCT",
      "productDirection": [{
          "id": 6779,
          "name": "MOBILE"
        }]}
  ]}
```

где:

- **id** - идентификатор
- **name** - название
- **stateId** - статус продукта
- **aliases** - псевдонимы
- **type** - тип продукта
- **productDirection** - идентификатор и наименование направления


### Создание продукта

- **url** - host:port/{app_name}/api/products/create 
- **DevUnitInfo** - json представления DevUnit продукта или компонента. Передается в теле запроса.  


``` json
{
"AuditType":"DevUnitInfo",                // обязательное поле
"typeId":2,                               // обязательное поле
"name":"testProductFromApi",              // обязательное поле
"description":"Description of product",
"internalDocLink":"google.com",
"externalDocLink":"google.com",
"commonManagerId":1                       // общий менеджер
}
```

- **запрос** - post
- **ответ** : содержит статус выполнения и в поле data всю информацию по сохраненному объекту 

``` json
{"status":"OK",
 "data":{
        "id":6892,
        "typeId":2,
        "name":"testProductFromApi",
        "description":"Description of product",
        "internalDocLink":"google.com",
        "externalDocLink":"google.com"        
 }
}
```

**Пример:**
``` sh
`curl -X POST -u user:password 'host:9007/Portal/springApi/api/products/create' -H "Content-Type:application/json" -d '{"AuditType":"DevUnitInfo","typeId":2,"name":"testProductFromApi","description":"Description of product","configuration":"Configuration content","cdrDescription":"cdr Description some text","historyVersion":"History version some text","wikiLink":"google.com"}'`
```

### Изменение статуса продукта

- **url** - host:port/{app_name}/api/products/updateState/{productId}/{productState}
- **productId** - идентификатор продукта
- **productState** - состояние продукта: 1 - активный, 2 - устаревший
- **запрос** - post 
- **ответ** : содержит статус выполнения и текущий статус продукта

``` json
{"status":"OK",
 "data":"ACTIVE"}
```

**Пример:**
``` sh
`curl -X POST -u user:password 'host:9007/Portal/springApi/api/products/updateState/100/1
```


### Получение списка продуктов по проектам компании-заказчика
Запрос выгружает все продукты(иерархию) по проектам, у которых в качестве компании-заказчика указана компания пользователя, осуществляющего запрос(актуально и для иерархии компаний).

- **url** - host:port/{app_name}/api/products/getByCompanyProjects
- **запрос** - get 
- **ответ** : содержит статус выполнения и в поле data DevUnitInfo


``` json
{"status":"OK",
 "data":[{
        "id":17765,        
        "description":"Description of product"
        },
        {
        "id":17766,        
        "description":"Description of product"]
        }     
}
```

**Пример:**
``` sh
`curl -X GET -u user:password  'host:9007/Portal/springApi/api/products/getByCompanyProjects'`
```


## Компании
### Создание компании

- **url** - host:port/{app_name}/api/companies/create
- **Company** - json-представление компании. Передается в теле запроса.  

- **метод**: POST
- **авторизация**: Basic
- **тело запроса**: json


*необходимые параметры:*

- **cname** - название компании

*возможные параметры:*

- **AuditType** - тип объекта. Должен быть "Company"
- **categoryId** - идентификатор категории
- **parentCompanyId** - идентификатор родительской компании
- **info** - комментарий
- **autoOpenIssue** - автоматическое открытие обращений
- **contactItems** - контактные данные
- **subscriptions** - подписки
- **platformId** - идентификатор платформы
- **productId** - идентификатор продукта


``` json
{"cname":"Gazprom",
 "AuditType":"Company",
 "categoryId":1,                                          // 1 - Заказчик, 2 - Партнер, 3 - Субподрядчик, 5 - Домашняя компания 
 "parentCompanyId":1,
 "info":"Company information",
 "autoOpenIssue":true,
 "contactItems" : [{"t":"EMAIL","a":"PUBLIC","v":"data"}],  // t - тип: EMAIL, ADDRESS, ADDRESS_LEGAL, FAX, MOBILE_PHONE, GENERAL_PHONE, WEB_SITE; 
                                                               a - тип доступа PUBLIC, PRIVATE, INTERNAL - для отображения на ui использовать PUBLIC; 
                                                               v - данные
 "subscriptions" : [
    {
      "email":"emergency.crm@protei.ru",
      "langCode":"ru",
      "platformId":1,
      "productId":1
    }]
}
```

- **ответ** : содержит статус выполнения и в поле data всю информацию по сохраненному объекту 

``` json
{
  "status": "OK",
  "data":{
    "id": 72192,
    "category": "CUSTOMER",
    "groupId": null,
    "companyGroup": null,
    "parentCompanyId": 1,
    "parentCompanyName": null,
    "childCompanies": null,
    "cname": "Gazprom",
    "contactItems":[
      {
        "t": "EMAIL",
        "a": "PUBLIC",
        "v": "data",
        "c": null…
      }
    ],
    "info": "Company information",
    "created": 1608314634528,
    "oldId": null,
    "subscriptions":[
      {
        "id": 5749,
        "companyId": 72192,
        "email": "emergency.crm@protei.ru",
        "langCode": "ru",
        …
      }
    ],
    "caseStates": null,
    "autoOpenIssue": true,
    "hidden": false,
    "archived": false,
    "categoryId": 1,
    "contactInfo":{
      "items":[
        {
          "t": "EMAIL",
          "a": "PUBLIC",
          "v": "data",
          "c": null…
        }
      ]
    },
    "companyAndChildIds":[
      72192
    ]
  }
}
```

**Пример:**
``` sh
`curl -X POST -u user:password 'host:9007/Portal/springApi/api/companies/create' -H "Content-Type:application/json" -d '{
{"cname":"Gazprom","AuditType":"Company","categoryId":1,"parentCompanyId":1,"info":"Company information","autoOpenIssue":true,"contactItems" : [{"t":"EMAIL","a":"PUBLIC","v":"data"}],"subscriptions" : [{"email":"emergency.crm@protei.ru","langCode":"ru","platformId":1,"productId":1}]}'`
```

### Изменение статуса компании

- **url** - host:port/{app_name}/api/companies/updateState/{companyId}/{isArchived}
- **companyId** - идентификатор компании
- **isArchived** - состояние компании: true - архивирована, false - активна
- **запрос** - post 
- **ответ** : содержит статус выполнения

``` json
{"status":"OK"}
```

**Пример:**
``` sh
`curl -X POST -u user:password 'host:9007/Portal/springApi/api/companies/updateState/100/true
```


## Площадки
### Создание площадки заказчиков

- **url** - host:port/{app_name}/api/platforms/create
- **Platform** - json-представление площадки. Передается в теле запроса.
- **метод**: POST
- **авторизация**: Basic
- **тело запроса**: json


*возможные параметры:*

- **AuditType** - тип объекта. Должен быть "Platform"
- **companyId** - идентификатор компании
- **name** - имя площадки
- **managerId** - идентификатор менеджера
- **params** - параметры удаленного доступа
- **comment** - комментарий


``` json
{"AuditType":"Platform",
 "companyId":1,
 "name":"name",
 "managerId":1,
 "params":"params",
 "comment":"comments"
}
```

- **ответ** : содержит статус выполнения и в поле data всю информацию по сохраненному объекту 

``` json
{
  "status":"OK",
  "data":{
    "id":1,
    "companyId":1,
    "name":"name",
    "params":"params",
    "comment":"comment",
    "manager":{
      "id":1,
      "companyId":5,
      "displayName":"Test_Person",
      "displayShortName":null,
      "name":"Test_Person",
      "fired":false
    },
    "managerId":1,
    "caseManagerShortName":null,
    "company":{
      "AuditType":"Company",
      "id":1,
      "category":"HOME",
      "groupId":null,
      "companyGroup":null,
      "parentCompanyId":null,
      "parentCompanyName":null,
      "childCompanies":null,
      "cname":"НТЦ Протей",
      "contactItems":null,
      "info":"",
      "created":1109323875000,
      "oldId":1,
      "subscriptions":null,
      "caseStates":null,
      "autoOpenIssue":false,
      "archived":false,
      "contactInfo":{
        "items":[
        ]
      },
      "categoryId":5,
      "hidden":false,
      "companyAndChildIds":[
        1]
    },
    "projectId":null,
    "caseId":1,
    "attachments":null,
    "serversCount":null
  }
}
```

**Пример:**
``` sh
`curl -X POST -u user:password 'host:9007/Portal/springApi/api/platforms/create' -H "Content-Type:application/json" -d '{"AuditType":"Platform","companyId":1,"name":"name","params":"params","comment":"comment","managerId":1}'`
```

### Удаление площадки заказчиков

- **url** - host:port/{app_name}/api/platforms/delete/{platformId}
- **platformId** - идентификатор площадки
- **запрос** - post
- **ответ** : содержит статус выполнения и идентификатор удалённой площадки

``` json
{"status":"OK",
 "data":1}
```

**Пример:**
``` sh
`curl -X POST -u user:password 'host:9007/Portal/springApi/api/platforms/delete/100'`
```

## Сотрудники
### Получение информации по сотрудникам

- **url** - host:port/{app_name}/api/employees
- **EmployeeApiQuery** - json представления EmployeeQuery для поиска сотрудника. Передается в теле запроса. 


``` json
{
"ids": [ 7920 ],
"name":"Фомин",
"workPhone":"5192",
"mobilePhone":"+79319661820",
"email":"fomin_e@protei.ru"
}
```

- **запрос** - post
- **ответ** :  содержит статус выполнения и в поле data список сотрудников  


``` json
{
   "status":"OK",
   "data":[
      {
         "id":7920,
         "displayName":"Фомин Евгений Викторович",
         "email":"fomin_e@protei.ru",
         "mobilePhone":"+79319661820",
         "workPhone":"5192",
         "ip":"192.168.100.192"
      }
   ]
}
```

**Пример:**
``` sh
`curl -X POST -u user:password 'host:9007/Portal/springApi/api/employees' -H "Content-Type:application/json" -d '{"name":"Фомин","workPhone":"5192","mobilePhone":"+79319661820","email":"fomin_e@protei.ru"}'`
```


## Тэги
### Создание тэга. 

- **url** - host:port/{app_name}/api/tags/create 
- **CaseTagInfo** - json представления CaseTag для создания тэга. Передается в теле запроса. 


``` json
{
"name":"test tag", // необходимое поле
"companyId":"72130", // необходимое поле
}
```

- **запрос** - post
- **ответ** :   содержит статус выполнения и в поле data созданный тэг  


``` json
{
   "status":"OK",
   "data":[
      {
          "id": 103,
          "caseType": "CRM_SUPPORT",
          "name": "Tag from api!",
          "color": "#e9edef",
          "companyId": 72130,
          "companyName": "outlook-test",
          "personId": 7920,
          "personName": "Фомин Евгений Викторович"
      }
   ]
}
```

**Пример:**
``` sh
`curl -X POST -u user:password 'host:9007/Portal/springApi/api/tags/create' -H "Content-Type:application/json" -d '{"name":"test tag","companyId":"72130"}'`
```


### Удаление тэга 

- **url** - host:port/{app_name}/api/tags/remove/{caseTagId} 


**caseTagId** -  идентификатор тэга 


Пустое тело запроса 



- **запрос** - post
- **ответ** :   содержит статус выполнения и в поле data id удаленного тэга 


``` json
{
   "status":"OK",
   "data":103
}
```

**Пример:**
``` sh
`curl -X POST -u user:password 'host:9007/Portal/springApi/api/tags/remove/103'`
```

## Отсутствия
### Создание

- **url** - host:port/{app_name}/api/absence/1c/create
- **ApiAbsence** - json представления Absence для создания отсутствия. Передается в теле запроса. 


``` json
    {
        "from": "2021-04-10",
        "to": "2021-04-11",
        "company_code":"protei",
        "worker_ext_id": "0000000816",
        "person_id": 7777,
        "reason": 10
    }
```

|наименование поля | тип | описание|
|-------|----|---|
| from |date, mandatory| Отсутствие с |
| to | date, mandatory | Отсутствие по |
| company_code | string, mandatory | Код компании сотрудника |
| worker_ext_id | string, mandatory | Внешний идентификатор сотрудника из 1С |
| person_id | integer, mandatory | Идентификатор сотрудника  |
| reason | integer, mandatory | Причина отсутствия |

**Выбор сотрудника**
Выбор сотрудника происходит либо по "company_code" + "worker_ext_id", либо по "person_id".
При указании обоих параметов - выбор производится по "company_code" + "worker_ext_id"

- **запрос** - post
- **ответ** :   содержит статус выполнения и в поле data идентификатор созданного отсутствия    


``` json
{
    "status": "OK",
    "data": 7407
}
```

- **возможные ошибки**:
1. NOT_FOUND - Если не найден сотрудник по "company_code" + "worker_ext_id"
2. ABSENCE_HAS_INTERSECTIONS - Если есть пересечение отсутствий



**Пример:**
``` sh
`curl -X POST -u user:password 'host:9007/Portal/springApi/api/absence/1c/create' -H "Content-Type:application/json" -d '{"from":"2021-04-10","to":"2021-04-11","company_code":"protei","worker_ext_id":"0000000816","person_id":7777,"reason":10}'`
```

## Проекты
### Создание проекта. 

- **url** - host:port/{app_name}/api/projects/create
- **ApiProject** - json представления Project для создания проекта. Передается в теле запроса. 


``` json
{
  "creatorId": 1               // обязательное поле
  "name": "Test project",      // обязательное поле
  "description": "Project description",
  "slas":[
    {
      "importanceLevelId": 1,
      "reactionTime": 60,
      "temporarySolutionTime": 120,
      "fullSolutionTime": 180
    },
    {
      "importanceLevelId": 2,
      "reactionTime": 120,
      "temporarySolutionTime": 180,
      "fullSolutionTime": 240
    },
    {
      "importanceLevelId": 3,
      "reactionTime": 180,
      "temporarySolutionTime": 240,
      "fullSolutionTime": 300
    },
    {
      "importanceLevelId": 4,
      "reactionTime": 240,
      "temporarySolutionTime": 300,
      "fullSolutionTime": 360
    }
  ],
  "team":[
    {
      "id": 7879,
      "role": "HEAD_MANAGER"   // в команде проекта обязательно должен присутствовать Руководитель
    }
  ],
  "stateId": 4,
  "pauseDate": 1592168400000,  // обязательное поле при stateId = 4
  "regionId": 10,
  "companyId": 446,            // обязательное поле
  "customerTypeId": 1,         // обязательное поле
  "technicalSupportValidity":1592168400000,
  "workCompletionDate":1609102740000,
  "purchaseDate":1609102740001,
  "directionsIds":[            // обязательное поле
    6781
  ],
  "productsIds":[
    6683,
    6684
  ],
  "subcontractorsIds":[
    72151
  ],
  "plansIds":[
    4
  ]
}
```

- **запрос** - post
- **ответ** :   содержит статус выполнения и в поле data созданный проект  


``` json
{
  "status": "OK",
  "data":{
    "id": 164977,
    "name": "Test project",
    "description": "Project description",
    "stateId": 4,
    "stateName": null,
    "stateColor": null,
    "customerType": "MINISTRY_OF_DEFENCE",
    "customer":{
      "AuditType": "Company",
      "id": 446,
      "category": null,
      "groupId": null,
      "companyGroup": null,
      …
    },
    "created": 1620323925961,
    "creatorId": null,
    "members": null,
    "locations": null,
    "deleted": false,
    "creator": null,
    "managerId": null,
    "managerName": null,
    "platforms": null,
    "technicalSupportValidity": 1592168400000,
    "workCompletionDate": 1609102740000,
    "purchaseDate": 1609102740001,
    "projectSlas":[
      {
        "id": 411,
        "importanceLevelId": 1,
        "reactionTime": 60,
        "temporarySolutionTime": 120,
        …],
        "pauseDate": 1592168400000,
        "projectPlans":[
        {
        "AuditType": "Plan",
        "id": 4,
        "name": null,
        "created": null,
        "creatorId": null,
        …
      }
    ],
    "subcontractors":[
      {
        "AuditType": "Company",
        "id": 72151,
        "category": null,
        "groupId": null,
        "companyGroup": null,
        …
      }
    ],
    "team":[
      {
        "id": 7879,
        "companyId": null,
        "displayName": null,
        "displayShortName": null,
        "name": null,
        …
      }
    ],
    "region":{
      "displayText": null,
      "id": 10,
      "info": null
    },
    "links": null,
    "contracts": null,
    "productDirections":[
      {
        "AuditType": "DevUnit",
        "id": 6781,
        "created": null,
        "name": null,
        "info": null,
        …
      }
    ],
    "products":[
      {
        "AuditType": "DevUnit",
        "id": 6683,
        "created": null,
        "name": null,
        "info": null,
        …
      },
      {
        "AuditType": "DevUnit",
        "id": 6684,
        "created": null,
        "name": null,
        "info": null,
        …
      }
    ],
    "productDirectionEntityOptionList":[
      {
        "displayText": null,
        "id": 6781,
        "info": null
      }
    ],
    "customerId": 446,
    "leader":{
      "id": 7879,
      "companyId": null,
      "displayName": null,
      "displayShortName": null,
      "name": null,
      …
    },
    "productShortViewList":[
      {
        "id": 6683,
        "name": null,
        "stateId": 0,
        "aliases": "",
        "type": null,
        …
      },
      {
        "id": 6684,
        "name": null,
        "stateId": 0,
        "aliases": "",
        "type": null,
        …
      }
    ]
  }
}
```

**Пример:**
``` sh
`curl -X POST -u user:password 'host:port/Portal/springApi/api/projects/create' -H "Content-Type:application/json" -d '{"name":"Test project","description":"Project description","slas":[{"importanceLevelId":1,"reactionTime":60,"temporarySolutionTime":120,"fullSolutionTime":180},{"importanceLevelId":2,"reactionTime":120,"temporarySolutionTime":180,"fullSolutionTime":240},{"importanceLevelId":3,"reactionTime":180,"temporarySolutionTime":240,"fullSolutionTime":300},{"importanceLevelId":4,"reactionTime":240,"temporarySolutionTime":300,"fullSolutionTime":360}],"team":[{"id":7879,"role":0}],"stateId":4,"pauseDate":1592168400000,"regionId":10,"companyId":446,"customerTypeId":1,"technicalSupportValidity":1592168400000,"workCompletionDate":1609102740000,"purchaseDate":1609102740001,"directionsIds":[6781],"productsIds":[6683,6684],"subcontractorsIds":[72151],"plansIds":[4]}'`
```


### Удаление проекта 

- **url** - host:port/{app_name}/api/projects/delete/{projectId} 


**projectId** -  идентификатор проекта 


Пустое тело запроса 



- **запрос** - post
- **ответ** :   содержит статус выполнения и в поле data id удаленного проекта


``` json
{
   "status":"OK",
   "data":100
}
```

**Пример:**
``` sh
`curl -X POST -u user:password 'host:port/Portal/springApi/api/projects/delete/100'`
```

### Получение информации по сотрудникам

- **url** - host:port/{app_name}/api/absence/1c/get
- **AbsenceApiQuery** - json представления запрос по отсутствиям. Передается в теле запроса. 


``` json
    {
       "from":1592168400000,
       "to":1609102740000,
       "company_code":"protei",
       "worker_ext_ids":[
          "0000000147"
       ],
      "reasons":[
        10
      ]
    }
```

|наименование поля | тип | описание|
|-------|----|---|
| from |date, mandatory| Фильтр отсутствий с |
| to | date, mandatory | Фильтр отсутствий по |
| company_code | string, mandatory | Код компании сотрудника |
| worker_ext_ids | string array | Список внешних идентификаторов сотрудников из 1С|
| reasons | integer array | Список причин отсутствий |


- **запрос** - post
- **ответ** :   содержит статус выполнения и в поле data список отсутствий    


``` json
    {
       "status":"OK",
       "data":[
          {
             "from":1592168400000,
             "to":1593982740000,
             "worker_id":"0000000147",             
             "person_id":298,
             "reason":11
          },
          {
             "from":1608843600000,
             "to":1609102740000,
             "worker_id":"0000000147",
             "person_id":298,
             "reason":1
          }
       ]
    }
```
|наименование поля | тип | описание|
|-------|----|---|
| from |date| Отсутствие с |
| to | date | Отсутствие по |
| worker_id | string | Внешний идентификатор сотрудника из 1С |
| person_id | integer | Идентификатор сотрудника  |
| reason | integer | Причина отсутствия |


Так как в одной компании один и тот же человек может работать на разных должностях (по совместительству), записи по отсутствиям формируются на основании worker_id (если у сотрудника 2 должности = 2 worker_id – в итоговом списке отсутствий будет по 2 записи по каждому отсутствию с идентичными данными, но с различными worker_id)


**Пример:**
``` sh
`curl -X POST -u user:password 'host:9007/Portal/springApi//api/absence/1c/get' -H "Content-Type:application/json" -d '{"from":1592168400000,"to":1609102740000,"company_code":"protei","worker_ext_ids":["0000000147"],"reasons":[10]}'`
```

## Банк документов
### Создание

- **url** - host:port/{app_name}/api/doc/create
- **DocumentApiInfo** - json представления документа для создания документа. Передается в теле запроса. 


``` json
{
  "name": "api doc",
  "decimalNumber": "40412735.АПКБГОО.14417.26",
  "inventoryNumber": 10110,
  "typeId": 3,
  "annotation": "annotation",
  "registrarId": 7777,
  "contractorId": 7777,
  "projectId": 166326,
  "equipmentId": 3358,
  "version": "000001",
  "keywords": [
    "word1",
    "word2"
  ],
  "approved": true,
  "approvedById": 7777,
  "approvalDate": "2021-04-30",
  "executionType": "ELECTRONIC",
  "memberIds": [
    7920
  ],
  "workDocFileExtension": "docx",
  "workDocFileBase64": "UEsDBBQACAgIABtAjVIAAAAAAAAAAAAAAAALAAAAX3JlbHMvLnJlbHOtkk1LA0EMhu/9FUPu3WwriMjO9iJCbyL1B4SZ7O7Qzgczaa3/3kEKulCKoMe8efPwHNJtzv6gTpyLi0HDqmlBcTDRujBqeNs9Lx9g0y+6Vz6Q1EqZXCqq3oSiYRJJj4jFTOypNDFxqJshZk9SxzxiIrOnkXHdtveYfzKgnzHV1mrIW7sCtftI/Dc2ehayJIQmZl6mXK+zOC4VTnlk0WCjealx+Wo0lQx4XWj9e6E4DM7wUzRHz0GuefFZOFi2t5UopVtGd/9pNG98y7zHbNFe4ovNosPZG/SfUEsHCOjQASPZAAAAPQIAAFBLAwQUAAgICAAbQI1SAAAAAAAAAAAAAAAAEQAAAGRvY1Byb3BzL2NvcmUueG1sjVLLTsMwELzzFZHvqfMoBVlJKgHqiUoVtAL1Zpxta0gcy3Zff4+dNG6BHrjt7ozH41ln40NdBTtQmjciR/EgQgEI1pRcrHO0mE/CexRoQ0VJq0ZAjo6g0bi4yZgkrFEwU40EZTjowAoJTZjM0cYYSTDWbAM11QPLEBZcNaqmxrZqjSVlX3QNOImiEa7B0JIaip1gKL0iOkmWzEvKrapagZJhqKAGYTSOBzE+cw2oWl890CIXzJqbo4Sr1B707IPmnrjf7wf7tKVa/zF+nz6/tk8NuXBRMUBFdjJCmAJqoAysAOmu65G39PFpPkFFEiVxGA3DaDSPY5Lek+RumeFf551gVzeqcOi5sXUJmikujd1hB/4Y2L6iYr21gRdqG74sWoofuVVWVJupXfqKQ/lwtBpXZqfRTHHhDPW249TZjiKSDpf+XE/yMdQnoX/k4AVvhxc59AKtDwU77j5skbQ3+tY9VW8/PoGZLgff2NpwU0E37ss/n7j4BlBLBwhSyePGeAEAABADAABQSwMEFAAICAgAG0CNUgAAAAAAAAAAAAAAABAAAABkb2NQcm9wcy9hcHAueG1snZFfa8IwFMXf9ylK8LVNLdKJpJH9YU/ChHVzb5Il1zajTUJyFf32izpq2ePezsm5/E5yw5bHvksO4IO2piLTLCcJGGmVNk1F3uuXdE6SgMIo0VkDFTlBIEt+x9beOvCoISSRYEJFWkS3oDTIFnoRshibmOys7wVG6xtqdzst4dnKfQ8GaZHnJYUjglGgUjcAyZW4OOB/ocrK8/3CR31ykcdZDb3rBAJn9CZri6KrdQ98Go8Hwx6c67QUGDfCV/rLw+ulgpbZLCuzYrLSZn/cfs7LbTlLRgPb+IRvkEhn+eRxrzuVFoyOYWwtGgjntqtgG+vVxV8Fe2qFFxLjb/DpPaMjO4o2Gts3JyT8HRoFscmLxgvX/tYNLpph0fwHUEsHCFEU9FIdAQAA/gEAAFBLAwQUAAgICAAbQI1SAAAAAAAAAAAAAAAAHAAAAHdvcmQvX3JlbHMvZG9jdW1lbnQueG1sLnJlbHOtkU0KwjAQhfeeIszeplUQkaZuRHAr9QAxnbbBNgnJKHp7A4paKOLC5fx97zEvX1/7jl3QB22NgCxJgaFRttKmEXAot9MlrItJvsdOUlwJrXaBxRsTBLREbsV5UC32MiTWoYmT2vpeUix9w51UJ9kgn6XpgvtPBhQDJttVAvyuyoCVN4e/sG1da4Ubq849GhqR4IFuHYZIlL5BEvCok8gBPi4/+6d8bQ2V8tjh28Gr9c3E/K8/QKKY5ecXnp2nhUnOB+EWd1BLBwj5LzDAxQAAABMCAABQSwMEFAAICAgAG0CNUgAAAAAAAAAAAAAAABEAAAB3b3JkL2RvY3VtZW50LnhtbKVV227bMAx931cYfk/ttEXRGU2KIUGL7pIFSwvsrVBk2dYqiQJFJ02/vlJ8azpgCNaXKDokD3lISb66ftYq2gh0EswkHp+kcSQMh1yachI/3N+MLuPIETM5U2DEJN4JF19PP11tsxx4rYWhyDMYl8EkrtFkjldCMzfSkiM4KGjEQWdQFJKLdonbCJzEFZHNkqQNOgErjLcVgJqR32KZNCHzNldymqYXCQrFyNfrKmldx7b5V/6NVp3f9pisW8DcInDhnG+EVk1ezaTpacbpEYIDTx9hj8mcI9u+SXlYyLwxDozuL8q+jBNfRtu9PYvnG6fv+FYVs2JgKz/GdotQ245N82PUaoZPtQ0ds36ia6kk7fbCh6LG5x+r6n3P/o8vnB/Ns7vSALK18hfBE0Whunjq78Ia8l1Y7f5niftlRTslom22YWoSL4JqFSd7b5nLDk8b6A/vACUKajAMPMmwtrzY2/xyA4acDxXM0RcnWUhEEK0EyiKaff0WrWaxN3N/UL5DJSmaiw0zrGQomyQcFGCXm9UEDfwk0HToaQO5lx4475CZe48pZsoOw3r06yE+qO6lGs0WAQo98IOQo7tFCEx6STSd/5z9DgA18KHmt46PR3l1b8cjs/IgIGnn5QSn1nln+4EZ8UxLVopGli1XQb5/PMbjz+F4brPK/7+4PLvsHH6w0McwvuB0dh58UJbVm20lWC4wDN1vCOxgKQCot6yBCPRgLGtqjW2qRa3vm1IL7elzwWV/uML1WiJQp6NgyrUiyEuaS/Ry/ePZnze8Xzdm/6Dfosyjpg+BtmC1olCBkkYsJfGgOd3XyCuGK8u4aMtKhj4m3X1Iho/E9BVQSwcIx5ostH0CAABpBgAAUEsDBBQACAgIABtAjVIAAAAAAAAAAAAAAAAPAAAAd29yZC9zdHlsZXMueG1sxVTdTtswFL7fU0S+L2mrAlVFQCwIjQ11Ez8P4DpO4+HYnu1QyuWuud8D7AXQpmkSSHuG8EY7SZMuNM0oKtMu0sbn2Mff+b4vZ2fvKubOJdWGSeGhzkYbOVQQGTAx9tD52WGrjxxjsQgwl4J6aEoN2tt9tTMZGDvl1DhwXpjBxEORtWrguoZENMZmQyoqIBdKHWMLSz12J1IHSktCjYHyMXe77faWG2MmUFmm06sVihnR0sjQbhAZuzIMGaF5KTjeaedvMS8LxGQVIDHWF4lqQT2FLRsxzuw0B4OcmAyOxkJqPOLQLeBBu9BrIMkBDXHCrcmW+oMulsUq/zuUwhpnMsCGMOahYzaiGspL4ZxSzUIEqWhfmIYUxcbuG4Y9NJRWzuKO//adc+pnaWLgmIyYdQ7oJRZ4jDVDbnbvBdUCNlxi7qHuLGSufTMP9WYxjsW4jOmkdXL++NLrqOUPs9CIBYAwYq2jYXbQLfpzF7tWi6v84kQpDfLuJ1a+maqIijkOqxNaFFRFwWoJt0Zy7i84bacKlFBY47HGKsow5qmjIKMKROW5RALHtLyrCOd9fzrMhXcrKCcskBMf1NKSuyvCnhFTRtsFzwoTltM6ouAvmiUyA4SW6vmmj6Q8xWloqxT8d+sQyaUu0WFo/i+OWvTTv/FYruyq6p9mL8UXWpU//ZLept/TX+k9PN/guSsUxIYG78Uylwh6Zcv4rOxmo30uKFXDyv4/Fqn7odurOqLTbT9bfyxMg/xFZlF9CK8kfkXS/hJJ++srs1lT5rUMps4ZcPekIOVAmTPKmaAnSTaUc6MWEQC6vYUaPsBObxnd6/W0VevpmJnl7TzyUWXmLRO+Sab1wG7XwPpYZeZ5kv4lti+H5DHQPkxi8KFpMH1m82eYvtmibPbrm5Vn0Hp89euj5Gt6B8PkZ3r78Dn9kd4/3LwcdS/hhvLN7P4GUEsHCIgT5VnKAgAAygkAAFBLAwQUAAgICAAbQI1SAAAAAAAAAAAAAAAAEgAAAHdvcmQvZm9udFRhYmxlLnhtbLVQQU7DMBC88wrLd+q0B4SiplUlxAn1QMsDtu6msWSvI69J6O9x3URCkANC5WbvzM7MznL94azoMLDxVMn5rJACSfujoVMl3/bP949ScAQ6gvWElTwjy/XqbtmXtafIIq0Tl30lmxjbUinWDTrgmW+RElb74CCmbzip3odjG7xG5qTurFoUxYNyYEgOMuE3Mr6ujcYnr98dUryKBLQQ0wXcmJblakgn+pLApdB745DFFnvx6h1QJugGAuOF04GtZFFIlffAGXsepyHTM9CaqJtx3kEwcLB4gdTV7Ifp7uwO3k56LW7ttUmUaavJs7g3zH+0ejEHDLlsscNg6uwKNm4TOup871tNJZvfuoSvyYB4Kti1p3+KMzx49QlQSwcIpfdKwhsBAABVAwAAUEsDBBQACAgIABtAjVIAAAAAAAAAAAAAAAARAAAAd29yZC9zZXR0aW5ncy54bWxFT8tOw0AMvPMV0d7pBoR4REkqLhUHbpQPcBM3WSlrr9ZOQ/l6DFHEbUbz0Ey9/4pTccEsgalxd7vSFUgd94GGxn0eD7fPrhAF6mFiwsZdUdy+vamXSlDVXFJYA0m1NG5UTZX30o0YQXackEw7c46gRvPgF859ytyhiEXj5O/L8tFHCORaq/xmjsVSJcwdktqch9L5X6HHM8yTHuH0oZzMcoGpcU/lyyrDrPx2TSMSqP3YdM0zroaOYwJta/8Pl0ptJh6Y9B1o2DLOAILoqwRY2Sn0wZD/S2+n2x9QSwcICe3toNsAAAA5AQAAUEsDBBQACAgIABtAjVIAAAAAAAAAAAAAAAATAAAAW0NvbnRlbnRfVHlwZXNdLnhtbL2Uy07DMBBF9/2KyFuUuLBACCXpgscSughrZOxJaogfst3S/j3jNKpQFZoChWU8c++ZuU6Sz9aqTVbgvDS6IOfZlCSguRFSNwV5qu7TKzIrJ3m1seAT7NW+IIsQ7DWlni9AMZ8ZCxortXGKBXx0DbWMv7EG6MV0ekm50QF0SEP0IGV+CzVbtiG5W+Pxlotyktxs+yKqIMzaVnIWsExjlQ7qHLT+gHClxd50aT9Zhsquxy+k9WdfE6xu9gBSxc3i+bDi1cKwpCug5hHjdlJAMmcuPDCFDfQ5bkKzE+8zRBKGz52xHq/FQXY4+AO8qE4tGoELEo4jovX3gaauJQf0WCqUZBCDFiCOZL8bJ/pwdxbY/h9Bd+jP0F/tHd1wZQ7e46eJG+wqikk9OocPmxb86afY+o7ia0RW7KX9wQs3NsHOejwDCAE1f5FC79yPMMlp978sPwBQSwcIC9URx1QBAABeBQAAUEsBAhQAFAAICAgAG0CNUujQASPZAAAAPQIAAAsAAAAAAAAAAAAAAAAAAAAAAF9yZWxzLy5yZWxzUEsBAhQAFAAICAgAG0CNUlLJ48Z4AQAAEAMAABEAAAAAAAAAAAAAAAAAEgEAAGRvY1Byb3BzL2NvcmUueG1sUEsBAhQAFAAICAgAG0CNUlEU9FIdAQAA/gEAABAAAAAAAAAAAAAAAAAAyQIAAGRvY1Byb3BzL2FwcC54bWxQSwECFAAUAAgICAAbQI1S+S8wwMUAAAATAgAAHAAAAAAAAAAAAAAAAAAkBAAAd29yZC9fcmVscy9kb2N1bWVudC54bWwucmVsc1BLAQIUABQACAgIABtAjVLHmiy0fQIAAGkGAAARAAAAAAAAAAAAAAAAADMFAAB3b3JkL2RvY3VtZW50LnhtbFBLAQIUABQACAgIABtAjVKIE+VZygIAAMoJAAAPAAAAAAAAAAAAAAAAAO8HAAB3b3JkL3N0eWxlcy54bWxQSwECFAAUAAgICAAbQI1SpfdKwhsBAABVAwAAEgAAAAAAAAAAAAAAAAD2CgAAd29yZC9mb250VGFibGUueG1sUEsBAhQAFAAICAgAG0CNUgnt7aDbAAAAOQEAABEAAAAAAAAAAAAAAAAAUQwAAHdvcmQvc2V0dGluZ3MueG1sUEsBAhQAFAAICAgAG0CNUgvVEcdUAQAAXgUAABMAAAAAAAAAAAAAAAAAaw0AAFtDb250ZW50X1R5cGVzXS54bWxQSwUGAAAAAAkACQA8AgAAAA8AAAAA",
  "archivePdfFileBase64": "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ZXJhdGlvblNlcmlmCi9GaXJzdENoYXIgMAovTGFzdENoYXIgMTUKL1dpZHRoc1s3NzcgMjUwIDU1NiA3MjIgNTU2IDUwMCA1MDAgNDQzIDUwMCA3NzcgNDQzIDUwMCAyNzcgNDQzIDUwMCAyNzcKXQovRm9udERlc2NyaXB0b3IgNyAwIFIKL1RvVW5pY29kZSA4IDAgUgo+PgplbmRvYmoKCjEwIDAgb2JqCjw8L0YxIDkgMCBSCj4+CmVuZG9iagoKMTEgMCBvYmoKPDwvRm9udCAxMCAwIFIKL1Byb2NTZXRbL1BERi9UZXh0XQo+PgplbmRvYmoKCjEgMCBvYmoKPDwvVHlwZS9QYWdlL1BhcmVudCA0IDAgUi9SZXNvdXJjZXMgMTEgMCBSL01lZGlhQm94WzAgMCA1OTUgODQyXS9Sb3RhdGUgMAovR3JvdXA8PC9TL1RyYW5zcGFyZW5jeS9DUy9EZXZpY2VSR0IvSSB0cnVlPj4vQ29udGVudHMgMiAwIFI+PgplbmRvYmoKCjQgMCBvYmoKPDwvVHlwZS9QYWdlcwovUmVzb3VyY2VzIDExIDAgUgovTWVkaWFCb3hbIDAgMCA1OTUgODQyIF0KL0tpZHNbIDEgMCBSIF0KL0NvdW50IDE+PgplbmRvYmoKCjEyIDAgb2JqCjw8L1R5cGUvQ2F0YWxvZy9QYWdlcyA0IDAgUgovVmlld2VyUHJlZmVyZW5jZXM8PC9EaXNwbGF5RG9jVGl0bGUgdHJ1ZQo+PgovTGFuZyhydS1SVSkKPj4KZW5kb2JqCgoxMyAwIG9iago8PC9UaXRsZTxGRUZGMDA2NDAwNkYwMDYzMDA3NTAwNkQwMDY1MDA2RTAwNzQwMDQxMDA3MDAwNjkwMDJFMDA2NDAwNkYwMDYzMDA3OD4KL0NyZWF0b3I8RkVGRjAwNEMwMDY5MDA2MjAwNzIwMDY1MDA0RjAwNjYwMDY2MDA2OTAwNjMwMDY1MDAyMDAwMzYwMDJFMDAzNDAwMkUwMDM2MDAyRTAwMzI+Ci9Qcm9kdWNlcjxGRUZGMDA0QzAwNjkwMDYyMDA3MjAwNjUwMDRGMDA2NjAwNjYwMDY5MDA2MzAwNjUwMDIwMDAzNjAwMkUwMDM0MDAyRTAwMzYwMDJFMDAzMj4KL0NyZWF0aW9uRGF0ZShEOjIwMjEwNDEzMTEwMDA5KzAzJzAwJyk+PgplbmRvYmoKCnhyZWYKMCAxNAowMDAwMDAwMDAwIDY1NTM1IGYgCjAwMDAwMDc0NTAgMDAwMDAgbiAKMDAwMDAwMDAxOSAwMDAwMCBuIAowMDAwMDAwMTk4IDAwMDAwIG4gCjAwMDAwMDc2MDMgMDAwMDAgbiAKMDAwMDAwMDIxOCAwMDAwMCBuIAowMDAwMDA2NTY4IDAwMDAwIG4gCjAwMDAwMDY1ODkgMDAwMDAgbiAKMDAwMDAwNjc4NCAwMDAwMCBuIAowMDAwMDA3MTQzIDAwMDAwIG4gCjAwMDAwMDczNjMgMDAwMDAgbiAKMDAwMDAwNzM5NSAwMDAwMCBuIAowMDAwMDA3NzAyIDAwMDAwIG4gCjAwMDAwMDc4MDggMDAwMDAgbiAKdHJhaWxlcgo8PC9TaXplIDE0L1Jvb3QgMTIgMCBSCi9JbmZvIDEzIDAgUgovSUQgWyA8MkUzNTdEM0Q1RjcyOERDQkY5NDRDNkJFODJEMkRFMTk+CjwyRTM1N0QzRDVGNzI4RENCRjk0NEM2QkU4MkQyREUxOT4gXQovRG9jQ2hlY2tzdW0gLzc2RTMyRTQwNDYyQzM2QjZGNkM0NzcyM0QxRjk2NDdFCj4+CnN0YXJ0eHJlZgo4MTI4CiUlRU9GCg==",
  "approvalSheetPdfBase64": "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dFsvUERGL1RleHRdCj4+CmVuZG9iagoKMSAwIG9iago8PC9UeXBlL1BhZ2UvUGFyZW50IDQgMCBSL1Jlc291cmNlcyAxMSAwIFIvTWVkaWFCb3hbMCAwIDU5NSA4NDJdL1JvdGF0ZSAwCi9Hcm91cDw8L1MvVHJhbnNwYXJlbmN5L0NTL0RldmljZVJHQi9JIHRydWU+Pi9Db250ZW50cyAyIDAgUj4+CmVuZG9iagoKNCAwIG9iago8PC9UeXBlL1BhZ2VzCi9SZXNvdXJjZXMgMTEgMCBSCi9NZWRpYUJveFsgMCAwIDU5NSA4NDIgXQovS2lkc1sgMSAwIFIgXQovQ291bnQgMT4+CmVuZG9iagoKMTIgMCBvYmoKPDwvVHlwZS9DYXRhbG9nL1BhZ2VzIDQgMCBSCi9WaWV3ZXJQcmVmZXJlbmNlczw8L0Rpc3BsYXlEb2NUaXRsZSB0cnVlCj4+Ci9MYW5nKHJ1LVJVKQo+PgplbmRvYmoKCjEzIDAgb2JqCjw8L1RpdGxlPEZFRkYwMDY0MDA2RjAwNjMwMDc1MDA2RDAwNjUwMDZFMDA3NDAwNDEwMDcwMDA2OTAwMkUwMDY0MDA2RjAwNjMwMDc4PgovQ3JlYXRvcjxGRUZGMDA0QzAwNjkwMDYyMDA3MjAwNjUwMDRGMDA2NjAwNjYwMDY5MDA2MzAwNjUwMDIwMDAzNjAwMkUwMDM0MDAyRTAwMzYwMDJFMDAzMj4KL1Byb2R1Y2VyPEZFRkYwMDRDMDA2OTAwNjIwMDcyMDA2NTAwNEYwMDY2MDA2NjAwNjkwMDYzMDA2NTAwMjAwMDM2MDAyRTAwMzQwMDJFMDAzNjAwMkUwMDMyPgovQ3JlYXRpb25EYXRlKEQ6MjAyMTA0MTMxMTAwMzQrMDMnMDAnKT4+CmVuZG9iagoKeHJlZgowIDE0CjAwMDAwMDAwMDAgNjU1MzUgZiAKMDAwMDAwODEyNCAwMDAwMCBuIAowMDAwMDAwMDE5IDAwMDAwIG4gCjAwMDAwMDAyMjggMDAwMDAgbiAKMDAwMDAwODI3NyAwMDAwMCBuIAowMDAwMDAwMjQ4IDAwMDAwIG4gCjAwMDAwMDcyMTUgMDAwMDAgbiAKMDAwMDAwNzIzNiAwMDAwMCBuIAowMDAwMDA3NDMxIDAwMDAwIG4gCjAwMDAwMDc4MDUgMDAwMDAgbiAKMDAwMDAwODAzNyAwMDAwMCBuIAowMDAwMDA4MDY5IDAwMDAwIG4gCjAwMDAwMDgzNzYgMDAwMDAgbiAKMDAwMDAwODQ4MiAwMDAwMCBuIAp0cmFpbGVyCjw8L1NpemUgMTQvUm9vdCAxMiAwIFIKL0luZm8gMTMgMCBSCi9JRCBbIDxBNTdEM0EwMjgzMEZBRTEyMkJFMzIxMzFFMDIxNTIzQT4KPEE1N0QzQTAyODMwRkFFMTIyQkUzMjEzMUUwMjE1MjNBPiBdCi9Eb2NDaGVja3N1bSAvRjVCMjcwMDgxN0RFMzdDRDQ1Mzc5NjY1QjQ3ODI4NDUKPj4Kc3RhcnR4cmVmCjg4MDIKJSVFT0YK"
}
```

|наименование поля | тип | описание|
|-------|----|---|
| name |string, mandatory| Наименование документа |
| decimalNumber |string, mandatory| Обозначение Документа, обязательно для некоторых типов документов |
| inventoryNumber |string| Инвентарный номер |
| typeId | long, mandatory | Идентификатор "вид документа", связка с таблице "document_type" |
| annotation | string | Аннотация |
| registrarId | long, mandatory | Идентификатор "Ответственный за регистрацию", связка с "person" |
| contractorId | long, mandatory | Идентификатор "Исполнитель", связка с "person" |
| projectId | long, mandatory | Идентификатор "Проект", связка с "project" |
| equipmentId | long, mandatory | Идентификатор "Оборудование", связка с "equipment" |
| version | string | Версия |
| keywords | array string | Ключевые слова для поиска|
| isApproved | boolean | Утвержденный |
| approvedById | long | Идентификатор "Утвердил", связка с "person" |
| approvalDate | date | Дата утверждения |
| executionType | En_DocumentExecutionType, mandatory | Вид документа |
| memberIds | array long | Сотрудники с доступом к исправлению рабочей документации, связка с "person" |
| workDocFileExtension | string | Расширение файла рабочей документации |
| workDocFileBase64 | string base64 | Файл рабочей документации |
| archivePdfFileBase64 | string base64 | Файл архивной документации |
| approvalSheetPdfBase64 | string base64 | Файл листа утверждения документации |

**Файлы**
- в АПИ добавлены поля для загрузки файлов (Base64)

**workDocFileExtension** - Расширение файла рабочей документации
- особенность реализации сохранения документов, для старых doc-документов необходимо указать "doc", 
  для новых docx - "docx" или можно опустить

**isApproved** - если документ утвержден, то необходимо указать следующие поля 
- isApproved
- approvedById
- approvalDate
- archivePdfFileBase64

- **запрос** - post
- **ответ** :   содержит статус выполнения и в поле data идентификатор созданного отсутствия    


``` json
{
    "status": "OK",
    "data": {
        "id": 550,
        "name": "api doc",
        "decimalNumber": "40412735.АПКБГОО.14417.26",
        "inventoryNumber": 10110,
        "state": "ACTIVE",
        "type": {
            "AuditType": "DocumentType",
            "id": 3,
            "name": "Пояснительная записка",
            "shortName": "ПЗ",
            "documentCategory": "TP",
            "gost": "ГОСТ Р 2.106-2019, ГОСТ 2.119, ГОСТ 2.120"
        },
        "annotation": "annotation",
        "registrar": {
            "id": 7777,
            "companyId": null,
            "displayName": null,
            "displayShortName": null,
            "name": null,
            "fired": false
        },
        "contractor": {
            "id": 7777,
            "companyId": null,
            "displayName": null,
            "displayShortName": null,
            "name": null,
            "fired": false
        },
        "projectId": 166326,
        "projectName": null,
        "projectLocation": null,
        "contragentName": null,
        "equipment": {
            "AuditType": "Equipment",
            "id": 3358,
            "type": null,
            "name": null,
            "nameSldWrks": null,
            "comment": null,
            "created": null,
            "authorId": null,
            "authorShortName": null,
            "managerId": null,
            "managerShortName": null,
            "projectId": null,
            "projectName": null,
            "decimalNumbers": null,
            "sortDecimal": null,
            "linkedEquipmentId": null,
            "linkedEquipmentDecimalNumbers": null
        },
        "version": "000001",
        "created": 1618301068164,
        "keywords": [
            "word1",
            "word2"
        ],
        "approvedBy": {
            "id": 7777,
            "companyId": null,
            "displayName": null,
            "displayShortName": null,
            "name": null,
            "fired": false
        },
        "approvalDate": 1619740800000,
        "executionType": "ELECTRONIC",
        "members": [
            {
                "id": 7920,
                "companyId": null,
                "displayName": null,
                "displayShortName": null,
                "name": null,
                "fired": false
            }
        ],
        "valid": true,
        "activeUnit": true,
        "deprecatedUnit": false,
        "approved": true
    }
}
```


**Пример:**
``` sh
`curl -X POST -u user:password 'host:9007/Portal/springApi/api/doc/create' -H "Content-Type:application/json" -d @json`
```

### Удаление документа 

- **url** - host:port/{app_name}/api/doc/remove/{id}


**id** -  идентификатор документа 


Пустое тело запроса 


- **запрос** - post
- **ответ** :   содержит статус выполнения и в поле data id удаленного документа 


``` json
{
   "status":"OK",
   "data":103
}
```

**Пример:**
``` sh
`curl -X POST -u user:password 'host:9007/Portal/springApi/api/doc/remove/103'`
```

## Договоры
### Получение информации по договорам

- **url** - host:port/{app_name}/api/contracts/1c/get
- **ContractApiQuery** - json представления ContractQuery для поиска договора. Передается в теле запроса.


``` json
{
  "refKeys": null,
  "openStateDate": "1646092800",
  "stateIds": [63, 64, 65, 66, 67, 68, 69],
  "organizationIds": [3083, 3084]
}
```

- **запрос** - post
- **ответ** :  содержит статус выполнения и в поле data список договоров


``` json
{
   "status":"OK",
   "data":[
    {
      "ref_key": null,
      "date_signing": null,
      "cost": 123456.77,
      "cost_currency": "EUR",
      "cost_vat": null,
      "subject": "!!!!! TEST !!!!",
      "directions": "MOBILE",
      "is_ministry_of_defence": false,
      "dates":[
        {
          "date": 1613422800000,
          "cost": null,
          "cost_percent": null,
          "cost_currency": "RUB",
          "comment": "",
          "type": "SUPPLY"
        }
      ]
    }
  ]
}
```

**Пример:**
``` sh
`curl -X POST -u user:password "host:9007/Portal/springApi/api/contracts/1c/get" -H "Content-Type:application/json" -d "{\"stateIds\": [63, 64, 65, 66, 67, 68, 69],\"organizationIds\": [3083, 3084]}"`
```

# WS API
## Сущности
 
1. WorkerRecord

- **id** \<Long\> - идентификатор работника
- **firstName** \<String\> - имя работника
- **lastName** \<String\> - фамилия работника
- **secondName** \<String\> - отчество работника
- **sex** \<Integer\> - пол
- **birthday** \<String\> - день рождения
- **phoneWork** \<String\> - рабочий телефон
- **phoneHome** \<String\> - домашний телефон
- **email** \<String\> - электронная почта
- **emailOwn** \<String\> - личная электронная почта
- **fax** \<String\> - факс
- **address** \<String\> - адрес
- **addressHome** \<String\> - домашний адрес
- **passportInfo** \<String\> - информация паспорта
- **info** \<String\> - информация
- **ipAddress** \<String\> - ip адрес
- **isDeleted** \<boolean\> - флаг удаленного сотрудника
- **isFired** \<boolean\> - флаг уволенного сотрудника
- **fireDate** \<String\> - дата увольнения
- **workerId** \<String\> - идентификатор работника в 1С
- **departmentId** \<String\> - идентификатор департамента работника в 1C
- **registrationId** \<Long\> - идентификатор регистрации пользователя
- **hireDate** \<String\> - дата принятия на работу
- **hireOrderNo** \<String\> - номер приказа о принятии на работу
- **active** \<int\> - флаг активности пользователя
- **positionName** \<String\> - должность

2. WorkerRecordList

- **workerRecords** \<List\<WorkerRecord\>\> - список записей работников

3. DepartmentRecord

- **companyCode** \<String\> - код компании
- **departmentId** \<String\> - идентификатор департамента в 1C
- **departmentName** \<String\> - название департамента
- **parentId** \<String\> - идентификатор родительского департамента в 1С
- **headId** \<String\> - идентификатор главы департамента в 1С

# Словари
## Portal API
**caseStateId** - айди состояния обращения:

id | Статус | Значение | Описание
--- | --- | --- | ---
|1|created|CREATED|Исходное состояние задачи при её создании.|
|2|opened|OPENED|Задача открыта и находится в работе. Обязательно должен быть назначен менеджер задачи.|
|4|paused|PAUSED|Работа над тикетом приостановлена.|
|5|verified|VERIFIED|Терминальное состояние задачи. Закрытие задачи проверено (подтверждено) Заказчиком. Переход в другие статусы не возможен.|
|16|active|ACTIVE|Задача находится в работе.|
|17|done|DONE|Работа по кейсу завершена, ожидается подтверждение Заказчика (статус verified).|
|19|local test|TEST_LOCAL|Проводится локальное тестирование.|
|20|customer test|TEST_CUST|Заказчик осуществляет проверку/тестирование предоставленного решения по кейсу.|
|30|workaround|WORKAROUND|Предоставлено временное решение по кейсу.|
|31|info request|INFO_REQUEST|Запрошена информация у Заказчика необходимая для дальнейшей работы по кейсу.|
|33|canceled|CANCELED|Задача потеряла актуальность и была отменена.|
|34|customer pending|CUST_PENDING|Ожидание Заказчика. Например: ожидание продления договора ПСГО, ожидание предоставления доступа к сервера, ожидание согласования коммерческого заказа, ожидание закупки комплектующих для серверов и т.п.|
|35|request to NX|NX_REQUEST|Актуально только дла задач синхронизированных с Jira. Запрошена информация у Nexign, ожидание ответа.|
|36|request to customer|CUST_REQUEST|Актуально только дла задач синхронизированных с Jira. Запрошена информация у конечного Заказчика, ожидание ответа.|
|37|customer responsibility||Задача находится в зоне ответственности Субподрядчика.|


**caseImpLevel** - уровень критичности обращения:


id | Код | Описание
--- | --- | --- 
|1|critical|critical|
|2|important|important|
|3|basic|basic|
|4|cosmetic|cosmetic|
|5|medium|medium|


**typeId** - айди типа обращения:


id | Код | Описание
--- | --- | --- 
|1|bug|Проблема|
|2|task|Задача|
|3|freq|Запрос на доработку|
|4|crm.sr|CRM-обращение в техподдержку|
|5|crm.mr|CRM-маркетинг|
|6|sysadm|Обращение в службу системных администраторов|
|7|plan|План|
|8|order|Заказ|
|9|project|Проект|
|10|official|Должностное лицо|
|11|employee-reg|Анкета нового сотрудника|
|12|contract|Договора|
|13|sf-platform|Платформы|

**historyTypeId** - айди типа истории:


id | Код | Описание
--- | --- | --- 
|0|PLAN|история по плану|
|1|TAG|история по тэгу|
|2|CONTRACT_STATE|история по статусу договора|
|3|CASE_STATE|история по статусу|
|4|CASE_MANAGER|история по менеджеру|
|5|CASE_IMPORTANCE|история по критичности|

**historyActionId** - айди действия истории:


id | Код | Описание
--- | --- | --- 
|0|ADD|добавление|
|1|CHANGE|изменение|
|2|REMOVE|удаление|


**timeElapsedType** - тип записи по затраченному времени:


 id  | Код | Описание   
-----| --- |------------
| 0   |NONE| Без типа   |
| 1   |WATCH| Дежурство |
| 2   |NIGHT_WORK| Ночные работы |
| 3   |SOFT_INSTALL| установка ПО |
| 4   |SOFT_UPDATE| обновление ПО |
| 5   |SOFT_CONFIG| настройка ПО |
| 6   |TESTING| тестирование |
| 7   |CONSULTATION| консультация |
| 8   |MEETING| совещание/конференц колл |
| 9   |DISCUSSION_OF_IMPROVEMENTS| обсуждение доработок |
| 10  |LOG_ANALYSIS| анализ логов |
| 11  |SOLVE_PROBLEMS| решение проблем |

**companyCategoryId** - категория компании:

id | Код | Описание
--- | --- | --- 
|1|customer|Заказчик|
|2|partner|Партнер|
|3|subcontractor|Субподрядчик|
|4|official|Должностное лицо|
|5|home|Домашняя компания|


**product.state** - айди статуса продукта:
id | Код | Описание
--- | --- | --- 
|1|ACTIVE|Активный|
|2|DEPRECATED|Устаревший|


**typeId** - айди типа продукта:


id | Код | Описание
--- | --- | --- 
|1|component|Компонент|
|2|product|Продукт|
|3|direction|Направление|
|4|complex|Комплекс|


**Absense.reason** - Словарь причин отсутствий:

| Id | Описание
--- | --- 
|1|Личные дела
|2|Гостевой пропуск
|3|Командировка
|4|Местная командировка
|5|Болезнь
|6|Больничный лист
|7|Ночные работы
|8|Дежурство
|9|Учеба
|10|Удаленная работа
|11|Отпуск
|12|Отпуск за свой счет

**En_DocumentExecutionType** - Вид документа:

| Type | Описание
--- | --- 
|ELECTRONIC|электронный
|PAPER|бумажный
|TYPOGRAPHIC|типографский

**En_DevUnitPersonRoleType** - Роль человека в команде:

| Код | Описание
--- | --- 
|HEAD_MANAGER|Руководитель
|DEPLOY_MANAGER|Менеджер внедрения (МВ)
|HARDWARE_CURATOR|Аппаратное обеспечение (АО)
|SOFTWARE_CURATOR|Программное обеспечение (ПО)
|INTRO_NEW_TECH_SOLUTIONS|Внедрение решений (В)
|LIABLE_FOR_AUTO_TESTING|Тестирование комплекса (ОТК)
|TECH_SUPPORT_CURATOR|Техническая поддержка (ТП)
|PRODUCT_ASSEMBLER|Сборка изделия (СБ)
|SUPPLY_PREPARATION|Подготовка поставки (ПП)
|ENGINEER_DOC_DEV|Конструкторская документация (КД)
|TECH_DOC_DEV|Технологическая документация (ТД)
|SOFTWARE_DOC_DEV|Программная документация (ПД)
|LIABLE_FOR_CERTIFICATION|Сертификация (С)
|OKR_ESCORT|Сопровождение ОКР (ОКР)
|QUALITY_CONTROL_SMK|Контроль качества СМК (КК)
|CUSTOMER_INTEGRATION|Взаимодействие с заказчиком (РП)
|PRESALE_MANAGER|Менеджер пресейла (МПС)
|BUSINESS_ANALYTICS_ARCHITECTURE|Бизнес-аналитика/архитектура (БА)
|PROJECT_DOCUMENTATION|Проектная документация (ПКД)
|PRODUCT_MANAGER|Менеджер продукта (МП)
|DEVELOPMENT|Разработка (Р)
|PRESALE_HEAD_MANAGER|Руководитель группы пресейла (РГП)
|DEPLOY_HEAD_MANAGER|Руководитель группы внедрения (РГВ)

**Project.stateId** - id состояния проекта:

| Id | Описание
--- | --- 
|4|Приостановлено
|22|Неизвестно
|23|Маркетинг
|24|Пресейл
|25|Проектирование
|26|Разработка
|27|Пусконаладка
|28|Тестирование
|29|Поддержка
|32|Завершено
|33|Отменено

**customerTypeId** - id типа заказчика:

| Id | Описание
--- | --- 
|1|Министерство обороны
|2|Госбюджет
|3|Коммерческое РФ
|4|Коммерческое ближнее зарубежье
|5|Коммерческое дальнее зарубежье
|6|Коммерческое ПРОТЕЙ

## WS API
**sex** - пол

id | Код | Описание
--- | --- | --- 
|1|M|Мужской|
|2|F|Женский|
|null|-|Не определен|

**companyCode** - код компании

external_code| Описание
--- | ----
|protei-st|1С Протей|
|protei|1С Протей СТ|
