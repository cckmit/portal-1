package ru.protei.portal.ui.delivery.client.activity.create;

import ru.protei.portal.core.model.ent.Kit;

/**
 * Абстракция активности карточки создания/редактирования доставки
 */
public interface AbstractDeliveryCreateActivity {
    void onSaveClicked();
    void onCancelClicked();

    void onProjectChanged();
    void onAttributeChanged();
    void onDepartureDateChanged();

    Kit createEmptyKit();
}
