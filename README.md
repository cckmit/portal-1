# Проект portal

## Настройка проекта

### Файлы настроек

Размещаются в каталоге `cfg`  
(`cfg` - позволяет отличать от каталога `conf` Tomcat)  

Путь к каталогу файлов определяется на старте приложения конфигурацией `MainConfiguration`  
читающей значение переменной `winter_file_path_prefix`  
из файла `app/portal/src/main/resources/spring.properties`  

Путь к файлу `log4j2.xml` определяется на старте приложения  
чтением значения переменной `log4j.configurationFile`  
из файла `app/portal/src/main/resources/log4j2.component.properties`  

На Tomcat файлы настроек размещаются в каталоге "tomcat" в каталоге `cfg`  

#### winter.properties

Размещения файла определяется значением переменной окружения `winter_file_path_prefix`,  
задается в `spring.properties`  

#### portal.properties

Размещение файла определяется значением переменной окружения `winter_file_path_prefix`,  
задается в `spring.properties`  

#### log4j2.xml

Размещения файла указывается в `log4j2.component.properties`  

### Запуск из IDEA

Необходимо установить системную переменную `сatalina.home`  
на каталог в котором размещается каталог `cfg` с файлами конфигурации проекта  
IDEA->Конфигурация запуска->VM options добавить:
`-Dcatalina.home={путь до каталога}`
> Пример:  
Для использования индивидуальных параметров запуска приложения  
файлы настроек лежат в /home/user/Projects/java/portal/debug/cfg/  
тогда значение будет: `-Dcatalina.home=/home/user/Projects/java/portal/debug`  
>
> Примечание:  
Не рекомендуется изменять параметры запуска в каталоге `cfg` для индивидуальных нужд  
Следует использовать отдельный каталог с настройками (см. пример выше)  
>
> Примечание:  
Системная переменная `сatalina.home` устанавливается Tomcat-ом при его старте  
>

### Настройка базы данных в docker

После установки docker, завести dockerfile примерно со следующим содержимым: 
> FROM mysql:8.0.18 
>
> RUN apt-get update && apt-get install -y nano mc
>
> ENV MYSQL_ROOT_PASSWORD=my_root_pw

В папке с докер файлом выполнить команду:

`docker build -t my_img_name -f my_dockerfile_name .`

Создать папку, например, data, в которой будут лежать файлы mysql из контейнера и выполнить команду:

`docker run -d --name container_name -v /path/to/data:/var/lib/mysql -p 3000:3306 my_img_name`

После этого, контейнер запустится и БД будет доступна по localhost:3000. 

Вход в контейнер:

`docker exec -it container_name /bin/bash`

Создание бд для разработки и тестов:

`create database portal_dev`

`create database portal_test`

Желательно, сначала создать бд для разработки внутри контейнера, потом запустить портал, чтобы отработал Liquibase
и уже после этого применять дамп базы данных для разработки.

Дамп базы данных для разработки можно положить в папку data и применить дамп из контенера.

Теперь в файлах winter.properties с тестами можно подменять проперти на свои.
Как вариант, создать патч файл с изменениям в файлах winter.properties и применять его перед тестами.
Не забыть отменить патч перед пушем.

Последующие запуск/остановка контейнера:

`docker start container_name`

`docker stop container_name`

Image - https://hub.docker.com/_/mysql
