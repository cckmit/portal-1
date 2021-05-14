package ru.protei.portal.ui.delivery.client.activity.create;

import ru.protei.portal.ui.delivery.client.activity.edit.AbstractDeliveryMetaActivity;

/**
 * Абстракция активности карточки создания поставки
 */
public interface AbstractDeliveryCreateActivity extends AbstractDeliveryMetaActivity {

    void onSaveClicked();

    void onCancelClicked();

}
