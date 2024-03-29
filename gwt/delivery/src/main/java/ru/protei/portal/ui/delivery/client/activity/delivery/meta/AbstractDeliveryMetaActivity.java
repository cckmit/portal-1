package ru.protei.portal.ui.delivery.client.activity.delivery.meta;

/**
 * Абстракция активности карточки создания Меты Поставки
 */
public interface AbstractDeliveryMetaActivity extends AbstractDeliveryCommonMeta {

    void onStateChange();

    void onTypeChange();

    void onInitiatorChange();

    void onCaseMetaNotifiersChanged();
}
