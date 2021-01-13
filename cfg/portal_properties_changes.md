# Изменения настроек
PORTAL-1582 Вынести значение автооткрытия обращения в properties

Добавить в проперти прод сервера 
``` properties
#### Автооткрытие обращений
## включение сервиса
## boolean, default: false
autoopen.enable=true

## включение задержки
## boolean, default: true
autoopen.delay.enable=true
```

Добавить в проперти внешнего сервера
``` properties
#### Автооткрытие обращений
## включение сервиса
## boolean, default: false
autoopen.enable=false

## включение задержки
## boolean, default: true
autoopen.delay.enable=true
```

Добавить в проперти внутреннего и внешнего серверов
``` properties
#### Идентификатор системного пользователя
## default: ""
system.user.id=8023
```
