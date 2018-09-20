# Проект portal

## Настройка проекта

### Файлы настроек

Размещаются в каталоге `cfg`  
(`cfg` - позволяет отличать от каталога `conf` Tomcat)  

Путь к каталогу файлов определяется на старте приложения конфигурацией `MainConfiguration`  
читающей значение переменной `winter_file_path_prefix`  
из файла `portal/gwt/crm/src/main/resources/spring.properties`  

Путь к файлу `log4j2.xml` определяется на старте приложения  
чтением значения переменной `log4j.configurationFile`  
из файла `portal/gwt/crm/src/main/resources/log4j2.component.properties`  

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
Файлы настроек лежат в /home/user/Projects/java/portal/debug/cfg/  
тогда значение будет: `-Dcatalina.home=/home/user/Projects/java/portal/debug`  
>
> Примечание:  
системная переменная `сatalina.home` устанавливается Tomcat-ом при его старте  