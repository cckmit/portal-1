package ru.protei.portal.ui.sitefolder.client.activity.plaform.preview;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractPlatformPreviewView extends IsWidget {

    void setActivity(AbstractPlatformPreviewActivity activity);

    void setName(String value);

    void setCompany(String value);

    void setParameters(String value);

    void setComment(String value);

    HasWidgets contactsContainer();

    HasWidgets serversContainer();
}
