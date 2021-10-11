package ru.protei.portal.ui.delivery.client.activity.card.edit;

import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Абстракция вида карточки редактирования Поставки
 */
public interface AbstractCardEditView extends IsWidget {
    void setActivity(AbstractCardEditActivity activity);

    void setSerialNumber(String value);

    HasWidgets getNoteCommentContainer();

    HasWidgets getItemsContainer();

    HasWidgets getMetaContainer();

    HasVisibility backButtonVisibility();

    HasVisibility noteCommentEditButtonVisibility();

    void setCreatedBy(String value);

    void setPreviewStyles(boolean isPreview);
}
