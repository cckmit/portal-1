package ru.protei.portal.ui.delivery.client.activity.pcborder.edit;

import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractPcbOrderEditView extends IsWidget {

    void setActivity(AbstractPcbOrderEditActivity activity);

    HasWidgets getCommonInfoEditContainer();

    HasVisibility commonInfoEditContainerVisibility();

    HasVisibility commonInfoContainerVisibility();

    HasWidgets getMetaContainer();

    void setCardTypeRO(String value);

    void setAmountRO(String value);

    void setModificationRO(String value);

    void setCommentRO(String value);

    HasVisibility backButtonVisibility();

    HasVisibility commonInfoEditButtonVisibility();

    void setCreatedBy(String value);

    void setPreviewStyles(boolean isPreview);
}
