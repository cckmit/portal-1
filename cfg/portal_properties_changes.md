# Изменения настроек

## "PORTAL-1959 Добавить отображение оплачиваемых дней отпуска"

Изменить на внутреннем и внешнем Портале в portal.properties:

1. #### Поправить название раздела: 1C API для отработанного времени -> 1C API для отработанного времени и остатка дней отпуска

2. Добавить в данный раздел параметры:

```
enterprise1c.api.work.rest_vacation_days.protei_url=http://srv-1cw/ziup/hs/ostatok
enterprise1c.api.work.rest_vacation_days.protei_st_url=http://srv-1cw/ziup_st/hs/ostatok
```