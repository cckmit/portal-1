package ru.protei.portal.ui.delivery.client.activity.cardbatch.edit;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractCardBatchEditView extends IsWidget {

    void setActivity(AbstractCardBatchEditActivity activity);

    HTMLPanel getCommonInfoEditContainer();

    HasVisibility commonInfoEditContainerVisibility();

    HasVisibility commonInfoContainerVisibility();

    HTMLPanel getMetaContainer();

    void setTypeRO(String value);

    void setArticleRO(String value);

    void setAmountRO(String value);

    void setParamsRO(String value);

    void setNumberRO(String value);

    HasVisibility backButtonVisibility();

    HasVisibility commonInfoEditButtonVisibility();

    void setCreatedBy(String value);

    void setPreviewStyles(boolean isPreview);
}
