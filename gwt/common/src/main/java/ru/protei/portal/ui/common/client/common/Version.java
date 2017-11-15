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
 */
public class Version {
    public static String getVersion() {
        return "4.0.32.31";
    }
}
