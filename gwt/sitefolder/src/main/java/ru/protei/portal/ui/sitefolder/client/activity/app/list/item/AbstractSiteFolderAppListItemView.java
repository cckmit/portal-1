package ru.protei.portal.ui.sitefolder.client.activity.app.list.item;

import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractSiteFolderAppListItemView extends IsWidget {

    void setActivity(AbstractSiteFolderAppListItemActivity activity);

    void setName(String name);

    void setComment(String comment);

    void setEditVisible(boolean visible);
}