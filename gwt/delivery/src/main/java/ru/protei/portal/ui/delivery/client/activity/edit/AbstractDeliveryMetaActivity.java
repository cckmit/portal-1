package ru.protei.portal.ui.delivery.client.activity.edit;

/**
 * Абстракция активности карточки создания Поставки
 */
public interface AbstractDeliveryMetaActivity {

    default void onStateChange(){}

    default void onTypeChange(){}

    void onProjectChanged();

    default void onInitiatorChange() {}

    void onAttributeChanged();

    default void onContractChanged(){}

    void onDepartureDateChanged();

    default void onCaseMetaNotifiersChanged() {}
}
