package ru.protei.portal.ui.delivery.client.activity.meta;

/**
 * Абстракция активности карточки создания Поставки
 */
public interface AbstractDeliveryMetaActivity extends AbstractDeliveryCommonMeta {

    void onStateChange();

    void onTypeChange();

    void onInitiatorChange() ;
    
    void onContractChanged();

    void onCaseMetaNotifiersChanged() ;
}
