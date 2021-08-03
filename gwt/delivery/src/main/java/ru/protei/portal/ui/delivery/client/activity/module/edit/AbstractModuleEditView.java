package ru.protei.portal.ui.delivery.client.activity.module.edit;

import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractModuleEditView extends IsWidget {

    void setActivity(AbstractModuleEditActivity activity);

    HasWidgets getNameContainer();

    HasWidgets getMetaContainer();

    HasVisibility showEditViewButtonVisibility();

    HasVisibility nameAndDescriptionEditButtonVisibility();

    void setCreatedBy(String value);
}
