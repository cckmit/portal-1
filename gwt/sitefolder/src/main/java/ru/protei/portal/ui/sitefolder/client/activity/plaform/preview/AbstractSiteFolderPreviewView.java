package ru.protei.portal.ui.sitefolder.client.activity.plaform.preview;

import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractSiteFolderPreviewView extends IsWidget {

    void setActivity(AbstractSiteFolderPreviewActivity activity);

    void setName(String value);

    void setCompany(String value);

    void setParameters(String value);

    void setComment(String value);
}
