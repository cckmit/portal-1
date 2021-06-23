package ru.protei.portal.ui.delivery.client.activity.meta;

/**
 * Абстракция активности карточки создания Меты Поставки
 */
public interface AbstractDeliveryMetaActivity extends AbstractDeliveryCommonMeta {

    void onStateChange();

    void onTypeChange();

    void onHwManagerChange();

    void onQcManagerChange();

    void onInitiatorChange();

    void onCaseMetaNotifiersChanged();
}
