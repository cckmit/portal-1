package ru.protei.portal.ui.common.client.common;

/**
 * 4.0.0.0 - начальная версия
 * 4.0.1.6 - freq-16226-16231 добавлены методы создания/редактирования компании на backend
 * 4.0.2.6 - freq-16227 добавление inputSelector и привязка с CompanyEdit
 * 4.0.2.7 - freq-16199 добавлено отображение категорий в списке
 * 4.0.2.8 - task-59116 создание/привязка группы в рамках создания/редактирования компании на backend
 * 4.0.3.8 - freq-16230 CRM: frontend по карточке создания продукта
 *           freq-16229 CRM: backend по карточке создания продукта
 *           freq-16234 CRM: frontend по карточке редактирования продукта
 *           freq-16233 CRM: backend по карточке редактирования продукта
 * 4.0.4.8 - freq-16235 карточка просмотра компании
 * 4.0.4.9 - task-59162 исправление inputSelector
 * 4.0.5.9 - error-border добавление рамки при валидации
 * 4.0.6.9 - freq-16341 превью карточки продукта
 * 4.0.7.9 - freq-16070 CRM: список клиентов
 * 4.0.7.10 - устранение замечаний по универсальному списку - 8ч
 * 4.0.8.10 - freq-16227 список valuecomment
 * 4.0.8.11 - 7-2 изменение UI сигнатуры методов
 * 4.0.9.11 - 7-3 в создание/редактирование компании добавлена категория
 * 4.0.10.11 - 7-6 в редактирование компании добавлены контактные лица
 * 4.0.11.11 - freq-16347 карточка просмотра клиента (front+back)
 * 4.0.11.12 - freq-16231 редактирование компании и error border
 * 4.0.11.13 - sprint-8-task-8 рефакторинг таблицы контактов
 * 4.0.12.13 - 8-3 доработка с сохранением данных сессии
 * 4.0.13.13 - доработка sidebar'а, перевод меню на page
 * 4.0.14.13 - sprint-8-task-9 карточка просмотра обращения
 * 4.0.15.13 - sprint-8-task-10 Дополнительные селекторы (статус, критичность, продукты, сотрудники, контакты)
 * 4.0.15.14 - sprint-8-task-9-2 внесены изменения в отображение статуса и критичности обращения
 * 4.0.15.15 - sprint-8-task-6 Проблемы с вёрсткой таблицы и карточки контактных лиц (и обращениях), fix style bugs
 * 4.0.16.15 - sprint-8-task-5 IssueEdit (frontend)
 * 4.0.17.15 - sprint-8-task-11 IssueEdit (backend)
 * 4.0.18.15 - sprint-8-task-14 замена звёздочек на красные рамки
 * 4.0.19.15 - sprint-9-task-3 Фильтры в обращениях - стили отображения статусов и критичности
 * 4.0.20.15 - sprint-10-task-13 - Реализация работы с комментариями в обращениях на back
 * 4.0.20.16 - sprint-10-task-13 - Реализация работы с комментариями в обращениях на back (create/update)
 * 4.0.20.17 - sprint-10-task-7 - Фильтры в обращениях - добавить фильтрацию по диапазону дат ( range picker ) с предустановленными промежутками + фильтрация по менеджеру
 * 4.0.21.17 - sprint-10-task-9 - Трансформация ValueComment в ContactItem, доработка виджета
 * 4.0.21.18 - sprint-9-task-7 - Рефакторинг таблицы обращений
 * 4.0.21.19 - sprint-10-task-11 - Замена списков для селекторов на EntityOption и ShortView
 * 4.0.21.20 - m1-t-7 - виджет телефонов - изменить порядок (тип,номер,коммент) + наименования (в селекторе убрать слово телефон)
 *             m1-t-6 - кнопки "сбросить" в фильтрах сделать серыми
 * 4.0.21.21 - m1-t-10 - поиск по номеру в фильтре обращений
 * 4.0.21.22 - m1-t-8 - фильтры в компаниях и продуктах переверстать и сделать как в обращениях ( справа )
 * 4.0.21.23 - m1-t-12 - в селекторах забаненный элемент отображать в выбранном состоянии забаненным
 * 4.0.21.24 - m1-t-19 - мелкие замечания к внешнему виду, поддержка unicode при отображении контакта
 * 4.0.22.24 - m1-t-25 - last update case object при добавлении/изменении/удалении коммента
 * 4.0.22.25 - m1-t-24 - Исправить создание обращения
 * 4.0.23.25 - m1-t-3 - Дашборд. доработки
 * 4.0.23.26 - m1-t-21 - доработать миграцию
 * 4.0.23.27 - m1-t-34 - доработка скролла у превью обращения
 * 4.0.24.27 - доработка по децимальным номерам
 * 4.0.25.27 - загрузка аттачментов
 * 4.0.26.27 - CRM-7 Добавить список уведомляемых в карточку компании
 * 4.0.27.27 - CRM-10 Добавить отображение списка подписчиков для рассылки в превью компании, обращение
 * 4.0.28.27 - CRM-11 Управление ролями
 * 4.0.29.27 - CRM-12 Управление аккаунтами
 * 4.0.29.28 - CRM-19 Не отображаются комментарии заказчиков
 * 4.0.30.28 - CRM-25 Селектор смены языка приложения в навбаре
 * 4.0.31.28 - CRM-16 Аудит действий
 * 4.0.31.29 - CRM-34 Замечания по реализации привилегий
 *             CRM-35 Замечания по интерфейсу
 *             CRM-15 Настройка оповещения
 * 4.0.31.30 - CRM-8 Фиксация изменения статуса в комментариях
 * 4.0.31.31 - CRM-41-45-46 Возможность создать обращение с заявителем ПРОТЕЙ + Обновлять адреса получателей при изменении компании + Предустановка компании требуется только для заказчика
 * 4.0.32.31 - CRM-38 Разделение учетных записей на клиентские и внутренние
 *             CRM-53 Сделать пингер, чтобы не выбрасывать пользователя по истечению сессии
 * 4.0.32.32 - CRM-54 Косметические замечания по версии 4.0.32.31 (prod)
 * 4.0.32.33 - CRM-55 В списке копаний на prod отсутствует компания НТЦ Протей
 * 4.0.32.34 - CRM-52 Переключение видов между таблицей и списком у компаний и продуктов
 * 4.0.32.35 - CRM-57 Замечания по результатам локального тестирования
 * 4.0.32.36 - CRM-59 ДН: при сохранении оборудования происходит перенаправление на страницу создания обращения
 * 4.0.33.36 - CRM-56 Замечания по ролям и учеткам
 * 4.0.34.36 - CRM-42 Добавление адресатов к обращению
 * 4.0.35.36 - CRM-26 Сервис взаимодействия с 1С
 * 4.0.35.37 - CRM-65 Замечания по версии 4.0.35.36 (шаблон рассылки уведомлений, управление подпиской на доп адреса)
 * 4.0.35.38 - CRM-68 Не изменяется статус обращения на открыто при выбранном менеджере
 * 4.0.36.38 - CRM-63 Сервис взаимодействия с 1С : журналирование действий
 * 4.0.36.39 - CRM-69 Некорректно работает проверка обращения на наличие изменений на клиенте. Добавление сохранения сообщения при сохранении обращения
 * 4.0.36.40 - CRM-66 Ошибки при создании/изменении списка адресатов компаний
 * 4.0.37.40 - CRM-71 Исправления в сервисе сборки событий. Добавление отложенных уведомлений
 * 4.0.37.41 - CRM-76 Устранение замечаний в email уведомлениях
 * 4.0.37.42 - Временно убрать ссылку на обращение при рассылке оповещений
 * 4.0.38.42 - CRM-85 Доработки/замечания по новому CRM (этап 2)
 * 4.0.38.43 - CRM-98 Увеличить высоту поля для комментария в обращении
 * 4.0.39.0 - CRM-93 Возможность сохранять фильтры для обращений
 * 4.0.39.1 - CRM-85 Доработки/замечания по новому CRM (этап 2) – реализация п1. Уведомление Заказчиков о смене логина при входе в CRM извне.
 * 4.0.39.2 - Фикс по отображению децимальных номеров, отображение общего количества записей в пагинаторе
 * 4.0.39.3 - CRM-99 Автоматически подставляется в новую задачу последний комментарий из предыдущей
 * 4.0.39.4 - CRM-104 Не соответствие CRM-36
 * 4.0.39.5 - CRM-175, а также исправлена проблема с redmine-plugin (это WA), исправлен формат даты (Саша)
 * 4.0.39.6 - CRM-123 исправлен редмайн плагин, см. описание задачи.
 * 4.0.39.7 - CRM-125 Невозможно поменять список уведомляемых по уже созданному обращению.
 * 4.0.39.8 - CRM-106 Добавить возможность фильтровать обращения с не выставленным продуктом.
 * 4.0.40.0 - CRM-126 Раздел "Виды документов"
 * 4.0.40.1 - CRM-103 Добавление контакта со страницы создания обращения.
 * 4.0.40.2 - CRM-127 При создании нового контакта через "Контактные лица", учетная запись создается без привязки к какой-либо "роли".
 * 4.0.40.3 - CRM-128 Ошибка при изменении варианта сортировки в форме "Учетные записи": Сортировать по ФИО.
 * 4.0.40.4 - CRM-108 Не могу исправить номер мобильного телефона у заказчика.
 * 4.0.40.5 - CRM-134 Адреса для получения уведомлений. Интерфейс заказчиков.
 * 4.0.41.5 - CRM-155 Создание/удаление аккаунта из API
 * 4.0.41.6 - CRM-130 Не меняется статус после получения "В работе".
 * 4.0.42.6 - CRM-145 сортировать созданные фильтры по алфавиту
 * 4.0.43.6 - CRM-146 поправить кнопки в поле ввода комментария
 * 4.0.44.6 - CRM-152 Поиск аккаунтов по компании
 * 4.0.44.7 - CRM-157 Не работает функция изменения фильтра
 * 4.0.44.8 - CRM-150 Ошибка при удалении вложения
 * 4.0.45.0 - CRM-148 Экспорт тикетов
 * 4.0.45.1 - CRM-163 Кодировка при добавлении файлов
 * 4.0.45.2 - CRM-114 Статус verified не перемещает задачу в "Мои завершенные"
 * 4.0.46.2 - CRM-139 Есть возможность создать задачу или перевести из статуса new в open без указания продукта.
 * 4.0.46.3 - CRM-150 Ошибка при удалении вложения. (поправлено обновление строки обращения при изменнии вложений, исправлена ошибка модификации списка)
 * 4.0.47.3 - CRM-116 В созданном заказчиком обращении на новом CRM не подставился заявитель
 * 4.0.48.3 - CRM-112 В новом CRM перестали отображаться нераспределенные обращения
 * 4.0.48.4 - CRM-143 Некорректное отображение таблицы контактных лиц на странице редактирования компании
 * 4.0.48.5 - CRM-144 добавить кнопки сохранить, отмена в верху страницы - удобно при длинных задачах
 * 4.0.48.6 - CRM-153 Возможность увольнять и удалять контактные лица
 * 4.0.48.7 - CRM-120 В списках отображаются удаленные сотрудники
 * 4.0.48.8 - CRM-159 при выставлении "замочка" при создании crm - указывать актуальный список рассылки
 * 4.0.48.9 - CRM-179 В дашборде показывать завершенные задачи за последние 30 дней
 * 4.0.49.0 - CRM-173 Улучшения юзабилити фильтра обращений
 * 4.0.49.1 - CRM-164 Дать возможность пользоваться полнотекстовым поиском по комментариям
 * 4.0.50.1 - CRM-168 Дополнительные статусы должны появляться только при выборе Заказчика, для которого они были сделаны
 * 4.0.50.2 - CRM-165 Привязка CRM друг к другу
 * 4.0.50.3 - CRM-140 Изменение ссылки на newporta
 * 4.0.50.4 - CRM-147 в почтовых уведомлениях сохранять разметку для возможности чтения задачи. (символ перевода строки не сохраняется)
 * 4.0.50.5 - CRM-174 Убрать красную строку в поле "Описание" для почтовой рассылки по обращению
 * 4.0.50.6 - CRM-183 Для менеджеров при редактировании контактного лица не отображается информация о заведенной учетной записи
 * 4.0.50.7 - CRM-180 Новый CRM: не корректно приходит e-mail уведомления при использовании разметки текста
 * 4.0.50.8 - CRM-160 По задачам из старого CRM не применяется в новом изменение критичности обращения.
 * 4.0.50.9 - CRM-170 Заменить иконки critical, basic, ... по аналогии с YT.
 * 4.0.51.0 - CRM-101 Логирование истории изменения критичности задачи
 * 4.0.52.0 - CRM-91 Полнотекстовый поиск
 * 4.0.52.1 - CRM-168 Дополнительные статусы должны появляться только при выборе Заказчика, для которого они были сделаны (убрано имя бд из запроса, отдельный запрос на получение статусов обращения )
 * 4.0.52.2 - CRM-111 Добавление заголовков In-Reply-To и References.
 * 4.0.52.3 - CRM-96 Быстрый поиск по номеру задачи и заказчику в активной доске задач
 * 4.0.52.4 - CRM-143 (еще раз) Некорректное отображение таблицы контактных лиц на странице редактирования компании
 * 4.0.52.5 - CRM-194 Дублируются кнопки в панели "Навигация"
 * 4.0.52.6 - CRM-189 Добавить возможность изменения компании в обращениях
 * 4.0.52.7 - CRM-186 добавить возможность скрывать панель с фильтром (которая справа) в разделе Обращения
 * 4.0.52.8 - CRM-184 отправка письма с паролем заказчику
 * 4.0.53.0 - CRM-195 Запретить возможность удаления видов документов и добавить поле с ГОСТом
 * 4.0.54.0 - CRM-176 Новый CRM: Возможность указания затраченного времени.
 * 4.0.54.1 - CRM-201 Заблокировать функцию добавления контакта со страницы создания обращения для заказчиков
 * 4.0.54.2 - CRM-166 Улучшение dashboard
 * 4.0.54.3 - CRM-185 Новый CRM: добавить возможность выделения номера обращения на странице редактирования обращения
 * 4.0.55.0 - CRM-188 В превью компании добавить информацию об адресах рассылки и список контактных лиц
 * 4.0.55.1 - CRM-181 Новый CRM: пустая гиперссылка при наведении курсора на автора комментария
 * 4.0.55.2 - CRM-189 Добавить возможность изменения компании в обращениях
 * 4.0.56.0 - CRM-196 Расширить список конфигурируемых полей для проектов
 * 4.0.56.1 - CRM-176 Новый CRM: Возможность указания затраченного времени. (исправления по визуализации)
 * 4.0.56.2 - CRM-176 Новый CRM: Возможность указания затраченного времени. (исправления по привилегиям на отображение данных по затраченным часам)
 *            CRM-211 Добавление вложения в не корректную CRM
 * 4.0.56.3 - CRM-202 При поиске по комментариям - игнорируется фильтр
 * 4.0.57.0 - CRM-187 Раздел Site Folder
 * 4.0.57.1 - CRM-147 (доработка) в почтовых уведомлениях сохранять разметку для возможности чтения задачи. (символ перевода строки не сохраняется)
 * 4.0.57.2 - CRM-225 Новый CRM: Добавление в отчеты доп. столбца с фактическим временем работы.
 * 4.0.57.3 - CRM-171 Добавить возможность очистки выбранных значений в фильтре обращений по компаниям
 * 4.0.58.3 - CRM-239 Добавить комментарии к проектам
 * 4.0.59.3 - CRM-242 Добавить новые статусы в проекты и регионы
 * 4.0.60.0 - CRM-212 Новый CRM: Можно открыть заявку без указания менеджера.
 *            CRM-206 Доработать механизм привязки crm
 *            CRM-207 При привязке crm - отображать статус привязанной заявки цветом
 *            CRM-208 Отображать привязанные задачи при предварительном просмотре
 * 4.0.60.1 - CRM-227 Невалидные значки контактной информации
 * 4.0.60.2 - CRM-223 Корректировка интерфейса базы децимальных номеров(1,2)
 * 4.0.61.0 - CRM-197 Расширить список конфигурируемых полей для документов
 * 4.0.62.0 - CRM-210 Изменить форматирование таблицы документов в разделе "Банк документов"
 * 4.0.62.3 — CRM-246 Не обновляется таблица с проектами при смене региона в проекте
 * 4.0.62.4 - CRM-226 Падение портала по OOM
 * 4.0.62.5 — CRM-219 В продуктах иметь возможность видеть компоненты
 * 4.0.62.6 — CRM-175 Новый CRM: Расширение функционала фильтра поиска обращений.
 * 4.0.62.7 — CRM-244 Сбрасываются роли у пользователей
 * 4.0.62.8 - CRM-231 Текст подсказки для логаута с помощью комбинации на английском языке
 *            CRM-230 Русский язык не с заглавной буквы в попапе со списком языков
 * 4.0.62.9 - CRM-253 DN. При открытии карточки оборудования на редактирование не заполняется менеджер
 * 4.0.62.10 - CRM-254 Не работает создание оборудования
 * 4.0.62.11 - CRM-243 Добавить debug-id для разработки автотестов
 * 4.0.62.12 - CRM-235 Некорректное суммирование затраченного времени
 * 4.0.62.13 - CRM-260 Новый CRM: ошибки при редактировании комментария в обращении
 * 4.0.62.14 - CRM-217 Новый CRM: не обновляется раздел "Мои завершённые" на доске задач
 * 4.0.63.0 - CRM-233 Новый статус заявки CRM
 * 4.0.63.1 - CRM-255 Замечания 1-го релиза по SiteFolder
 *            CRM-266 Ссылка с сайтом компании работает некорректно
 * 4.0.63.2 - CRM-247 Ноый CRM: не корректно сохраняется текст в описании
 * 4.0.63.3 - CRM-220 Для продукта и компонента иметь возможность указывать дочерние компоненты
 * 4.0.63.4 - CRM-237 При создании обращения не выставлять компанию "НТЦ Протей" по дефолту
 * 4.0.63.5 - CRM-258 Создание нескольких объектов и отключение сайта при неоднократном нажатии на кнопку создать
 * 4.0.63.6 - CRM-228 Ошибка в повторном добавлении файла после отмены
 * 4.0.63.7 - CRM-237 При создании обращения не выставлять компанию "НТЦ Протей" по дефолту
 *            (заполнение профиля сотрудника при выборе своей компании)
 * 4.0.63.8 - CRM-251 Добавить тултип в фильтр в таблице проектов для статусов
 * 4.0.63.9 - PORTAL-214 Новый CRM: не удаётся прикрепить файл без расширения к обращению
 * 4.0.63.10 - PORTAL-232 Новый CRM: не корректно сформировались комментарии в e-mail нотификации
 * 4.0.63.11 - PORTAL-224 Не работает хоткей для разлогинивания
 * 4.0.63.12 - PORTAL-289 В отчете по обращениям дублируются записи
 * 4.0.63.13 - PORTAL-250 Переработать секцию "Менеджеры" в проектах
 * 4.0.63.14 - PORTAL-256 Добавить в preview карточку площадки информацию о серверах и приложениях
 * 4.0.63.15 - PORTAL-288 Добавить вывод текущей версии в новом CRM.
 * 4.0.63.16 - PORTAL-275 Форматирование комментариев в разделе "Площадки Заказчиков"
 *             PORTAL-277 Убрать поле "Параметры удаленного доступа" для каждого Сервера в "Площадки Заказчиков"
 * 4.0.63.17 - PORTAL-279 Возможность копирования объектов в разделе "Площадки Заказчиков"
 * 4.0.63.18 - PORTAL-293 Добавить debug-id для разработки автотестов
 * 4.0.63.19 - PORTAL-278 Добавить окно подтверждения при удалении фильтра
 * 4.0.63.20 - PORTAL-249 При открытии проектов в первый раз после логина, показывать только "мои"
 * 4.0.63.21 - PORTAL-271 Добавить в карточку приложения выпадайку с компонентами
 * 4.0.63.22 - PORTAL-287 Доработки карточки компоненты
 * 4.0.63.22 - PORTAL-287 Доработки карточки компоненты
 * 4.0.63.23 - PORTAL-296 Не работает поиск (в оборудовании прим. ред.)
 * 4.0.63.24 - PORTAL-276 Добавление поля выбора "Менеджера проекта"
 * 4.0.63.25 - PORTAL-282 Ошибка при сортировке в базе децимальных номеров
 * 4.0.63.26 - PORTAL-286 Наименование DO_NOT_REPLY для crm@protei.ru
 * 4.0.63.27 - PORTAL-295 некорректное создание Пользователя с двумя ролями
 * 4.0.63.28 - PORTAL-305 Некорректный расчёт кол-ва завершённых задач.
 * 4.0.63.29 - PORTAL-304 404 по fontawesome.
 * 4.0.63.30 - PORTAL-236 Добавление гиперссылки (http[s]://) в почтовые уведомления.
 * 4.0.63.31 - PORTAL-300 Просьба в списке Компаний "НТЦ Протей" поставить на 1е место.
 * 4.0.63.32 - PORTAL-205 Новый CRM: Пропадают кнопки редактирования у кейсов при выборе фильтра.
 * 4.0.63.33 - PORTAL-273 Потеря фокуса в таблице обращений
 * 4.0.64.33 - PORTAL-274 Сократить нижнюю полосу
 * 4.0.65.33 - PORTAL-209 Сохранять курсор после выхода из режима редактирования
 * 4.0.65.34 - PORTAL-310 Непонятное поведение интерфейса при поиске по комментариям
 * 4.0.65.35 - PORTAL-236 (доработка) Добавление гиперссылки (http[s]://) в почтовые уведомления.
 * 4.0.65.36 - PORTAL-317 Реализовать функцию открытия отдельной позиции оборудования в новой вкладке / в новом окне
 * 4.0.66.0 - PORTAL-272 Отображение списка сотрудников Протей
 * 4.0.66.1 - PORTAL-312 "Затраченное время" считается с задержкой в 1 комментарий.
 * 4.0.66.2 - PORTAL-248 Корректировка интерфейса базы децимальных номеров
 * 4.0.66.3 - PORTAL-330 Личные данные (паспортные) в открытом доступе
 * 4.0.66.4 - PORTAL-333 Не скачиваются отчеты по обращениям
 * 4.0.66.5 - PORTAL-330 Личные данные (паспортные) в открытом доступе
 * 4.0.66.6 - PORTAL-248 (доработка) Корректировка интерфейса базы децимальных номеров
 * 4.0.67.6 - PORTAL-314 Доработка фильтра заказчика.
 * 4.0.69.0 - PORTAL-264 Новый CRM: проблема с кодировкой в имени прикреплённых файлов
 */
public class Version {
    public static String getVersion() {
        return "4.0.69.0";
    }
}
