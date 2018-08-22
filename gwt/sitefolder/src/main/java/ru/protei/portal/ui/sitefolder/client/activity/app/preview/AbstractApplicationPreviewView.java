package ru.protei.portal.ui.sitefolder.client.activity.app.preview;

import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.struct.PathInfo;

public interface AbstractApplicationPreviewView extends IsWidget {

    void setActivity(AbstractApplicationPreviewActivity activity);

    void setName(String value);

    void setComponent(String value);

    void setServer(String value);

    void setComment(String value);

    void setPaths(PathInfo value);
}

