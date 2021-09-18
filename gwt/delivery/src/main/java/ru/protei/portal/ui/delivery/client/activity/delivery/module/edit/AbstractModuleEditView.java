package ru.protei.portal.ui.delivery.client.activity.delivery.module.edit;

import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.dict.En_CommentOrHistoryType;
import ru.protei.portal.ui.common.client.widget.tab.multi.MultiTabWidget;

public interface AbstractModuleEditView extends IsWidget {

    void setActivity(AbstractModuleEditActivity activity);

    HasWidgets getNameContainer();

    HasWidgets getMetaContainer();

    HasVisibility showEditViewButtonVisibility();

    HasVisibility nameAndDescriptionEditButtonVisibility();

    void setCreatedBy(String value);

    void setModuleNumber(String serialNumber);

    HasVisibility backButtonVisibility();

    MultiTabWidget<En_CommentOrHistoryType> getMultiTabWidget();

    HasWidgets getItemsContainer();
}
