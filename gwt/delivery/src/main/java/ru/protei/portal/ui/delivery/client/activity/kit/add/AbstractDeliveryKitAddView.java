package ru.protei.portal.ui.delivery.client.activity.kit.add;

import com.google.gwt.user.client.ui.*;

/**
 * Абстракция вида карточки редактирования Поставки
 */
public interface AbstractDeliveryKitAddView extends IsWidget {

    void setActivity(AbstractDeliveryKitAddActivity activity);

    HasWidgets getKitsContainer();
}
