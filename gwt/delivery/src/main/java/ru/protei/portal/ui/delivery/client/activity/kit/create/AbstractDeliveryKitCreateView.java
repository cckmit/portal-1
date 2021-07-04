package ru.protei.portal.ui.delivery.client.activity.kit.create;

import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.ent.Kit;

import java.util.List;

/**
 * Абстракция вида карточки редактирования Поставки
 */
public interface AbstractDeliveryKitCreateView extends IsWidget {

    void setActivity(AbstractDeliveryKitCreateActivity activity);

    HasWidgets getKitsContainer();
}
