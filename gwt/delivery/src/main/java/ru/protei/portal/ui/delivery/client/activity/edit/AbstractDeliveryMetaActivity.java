package ru.protei.portal.ui.delivery.client.activity.edit;

/**
 * Абстракция активности карточки создания поставки
 */
public interface AbstractDeliveryMetaActivity {
    void onProjectChanged();
    void onAttributeChanged();
    void onDepartureDateChanged();

    void onStateChange();
    void onTypeChange();
    default void onCaseMetaNotifiersChanged() {}
}
