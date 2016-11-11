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
 */
public class Version {
    public static String getVersion() {
        return "4.0.11.12";
    }
}
