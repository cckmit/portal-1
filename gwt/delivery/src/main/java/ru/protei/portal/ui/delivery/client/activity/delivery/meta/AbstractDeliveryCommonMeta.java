package ru.protei.portal.ui.delivery.client.activity.delivery.meta;

public interface AbstractDeliveryCommonMeta {

    void onProjectChanged();

    void onAttributeChanged();

    void onDepartureDateChanged();

    void onContractChanged();

    void clearProjectSpecificFields();

    String getValidationError();
}    
