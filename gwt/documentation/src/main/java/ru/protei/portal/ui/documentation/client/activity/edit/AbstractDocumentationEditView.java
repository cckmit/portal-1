package ru.protei.portal.ui.documentation.client.activity.edit;

import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractDocumentationEditView extends IsWidget {

    void setActivity(AbstractDocumentationEditActivity activity);

    void setVisibilitySettingsForCreated(boolean isVisible);
}
