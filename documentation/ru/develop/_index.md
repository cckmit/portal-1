---
title: "Разработка"
description: >
  Информация по процессу разработки
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

### Запуск gwt с Java 9.
Встроенный в Idea плагин GWT контейнер сервлетов Jetty устарел и
не может обработать jar с Модулями Java 9.

Поэтому решено отказаться от Jetty в плагине, и использовать Jetty из самого GWT

##### Run/Debug конфигурация [DevMode.run.xml](DevMode.run.xml)


Это запуск classic DevMode и потом SuperDevMode через Code Server.

Импорт в Idea происходит простым копированием файла xml в корень проекта, но лучше в каталог ".idea/runConfigurations"

Idea сама поправит параметры при импорте конфигурации, но возможно придется поправить параметр "-war" на свой, где лежат файлы. И соответвенyо catalina.home

Перед запуском нужно скомпилировать проект: **maven[root]**: clean -> compile

Запустится отдельное приложение "GWT develope Mode". Если пройдет все хорошо, появится вкладка Jetty, где можно будет посмотреть логи сервера. Необходимо нажать "Launch Default Browser" и запустится приложение.

Нюанс - по рефрешу в браузере не происходит перекомпиляция проекта.
Поэтому для внесения изменений на gwt:

- делаем изменения в *.java, *.ui.xml
- компилируем проект ctrl-f9 (обновляем target из параметра -war)
- рефреш страницы в браузере