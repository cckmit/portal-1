package ru.protei.portal.ui.delivery.client.activity.edit;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.Kit;

import java.util.List;

/**
 * Абстракция вида карточки редактирования Поставки
 */
public interface AbstractDeliveryEditView extends IsWidget {

    void setActivity(AbstractDeliveryEditActivity activity);

    HasWidgets getNameContainer();

    HasValue<List<Kit>> kits();

    void updateKitByProject(boolean isArmyProject);

    HasWidgets getMetaContainer();

    HasVisibility backButtonVisibility();

    HasVisibility showEditViewButtonVisibility();

    void setPreviewStyles(boolean isPreview);
}
