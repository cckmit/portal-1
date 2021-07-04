package ru.protei.portal.ui.delivery.client.activity.edit;

import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.dict.En_CommentOrHistoryType;
import ru.protei.portal.ui.common.client.widget.tab.multi.MultiTabWidget;

/**
 * Абстракция вида карточки редактирования Поставки
 */
public interface AbstractDeliveryEditView extends IsWidget {

    void setActivity(AbstractDeliveryEditActivity activity);

    HasWidgets getNameContainer();

    HasWidgets getMetaContainer();

    HasWidgets getItemsContainer();

    MultiTabWidget<En_CommentOrHistoryType> getMultiTabWidget();

    HasVisibility backButtonVisibility();

    HasVisibility showEditViewButtonVisibility();

    void setPreviewStyles(boolean isPreview);

    HasVisibility nameAndDescriptionEditButtonVisibility();

    HasVisibility addKitsButtonVisibility();
}
