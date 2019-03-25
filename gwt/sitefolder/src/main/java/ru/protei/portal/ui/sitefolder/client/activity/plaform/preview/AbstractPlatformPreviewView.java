package ru.protei.portal.ui.sitefolder.client.activity.plaform.preview;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.ui.common.client.widget.attachment.list.HasAttachments;

public interface AbstractPlatformPreviewView extends IsWidget {

    void setActivity(AbstractPlatformPreviewActivity activity);

    void setName(String value);

    void setCompany(String value);

    void setManager(String value);

    void setParameters(String value);

    void setComment(String value);

    HasWidgets contactsContainer();

    HasWidgets serversContainer();

    HasAttachments attachmentsContainer();
}
