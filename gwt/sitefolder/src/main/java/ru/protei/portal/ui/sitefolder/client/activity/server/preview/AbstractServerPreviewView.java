package ru.protei.portal.ui.sitefolder.client.activity.server.preview;

import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractServerPreviewView extends IsWidget {

    void setActivity(AbstractServerPreviewActivity activity);

    void setName(String value);

    void setPlatform(String value);

    void setIp(String value);

    void setParameters(String value);

    void setServerGroup(String value);

    void setComment(String value);
}
