package ru.protei.portal.ui.sitefolder.client.activity.server.list.item;

import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractSiteFolderServerListItemView extends IsWidget {

    void setActivity(AbstractSiteFolderServerListItemActivity activity);

    void setName(String name);

    void setIp(String ip);

    void setComment(String comment);

    void setEditVisible(boolean visible);
}
