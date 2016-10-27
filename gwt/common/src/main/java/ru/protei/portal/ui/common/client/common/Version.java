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
 *
 */
public class Version {
    public static String getVersion() {
        return "4.0.3.8";
    }
}
