package ru.protei.portal.ui.delivery.client.activity.cardbatch.edit;

import com.google.gwt.user.client.ui.*;
import ru.protei.portal.core.model.dict.En_CommentOrHistoryType;
import ru.protei.portal.ui.common.client.widget.tab.multi.MultiTabWidget;

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

    void setContractorsRO(String value);

    HasVisibility backButtonVisibility();

    HasVisibility commonInfoEditButtonVisibility();

    void setCreatedBy(String value);

    void setPreviewStyles(boolean isPreview);

    HasWidgets getItemsContainer();

    MultiTabWidget<En_CommentOrHistoryType> getMultiTabWidget();
}
