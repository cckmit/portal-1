package ru.protei.portal.ui.sitefolder.client.activity.plaform.preview;

import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;

public interface AbstractPlatformPreviewView extends IsWidget {

    void setActivity(AbstractPlatformPreviewActivity activity);

    HasVisibility footerContainerVisibility();

    void setName(String value);

    void setCompany(String value);

    void setManager(String value);

    void setParameters(String value);

    void setTechnicalSupportValidity(String technicalSupportValidity);

    void setProject(String value, String link);

    void setComment(String value);

    HasWidgets contactsContainer();

    HasWidgets serversContainer();

    HasAttachments attachmentsContainer();

    void isFullScreen(boolean isFullScreen);
}
