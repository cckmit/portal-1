# Изменения настроек
PORTAL-1582 Вынести значение автооткрытия обращения в properties

Добавить в проперти прод сервера 
``` properties
# Issue autoopen
autoopen.enable=true
autoopen.delay.enable=true
autoopen.delay.startup=60
autoopen.delay.runtime=180
autoopen.delay.random=120
```