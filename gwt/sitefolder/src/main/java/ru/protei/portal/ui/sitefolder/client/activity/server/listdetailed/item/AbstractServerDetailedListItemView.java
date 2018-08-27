package ru.protei.portal.ui.sitefolder.client.activity.server.listdetailed.item;

import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractServerDetailedListItemView extends IsWidget {

    void setActivity(AbstractServerDetailedListItemActivity activity);

    void setName(String name);

    void setParameters(String parameters);

    void setApps(String apps);

    void setComment(String comment);

    void setEditVisible(boolean visible);
}
