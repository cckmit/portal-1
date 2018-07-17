package ru.protei.portal.ui.sitefolder.client.activity.server.preview;

import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractSiteFolderServerPreviewView extends IsWidget {

    void setActivity(AbstractSiteFolderServerPreviewActivity activity);

    void setName(String value);

    void setPlatform(String value);

    void setIp(String value);

    void setParameters(String value);

    void setComment(String value);
}
