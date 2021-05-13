package ru.protei.portal.ui.delivery.client.activity.edit;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.Kit;

import java.util.List;

/**
 * Абстракция вида карточки создания/редактирования проекта
 */
public interface AbstractDeliveryEditView extends IsWidget {

    void setActivity(AbstractDeliveryEditActivity activity);

    HasWidgets getNameContainer();

    HasWidgets getKitsContainer();

    HasValue<List<Kit>> kits();

    HasWidgets getMetaContainer();
}
