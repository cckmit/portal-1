package ru.protei.portal.ui.delivery.client.activity.create;

/**
 * Абстракция активности карточки создания поставки
 */
public interface AbstractDeliveryCreateActivity {
    void onSaveClicked();
    void onCancelClicked();

    void onProjectChanged();
    void onAttributeChanged();
    void onDepartureDateChanged();
}
