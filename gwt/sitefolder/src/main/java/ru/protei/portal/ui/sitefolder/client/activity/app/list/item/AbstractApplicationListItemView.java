package ru.protei.portal.ui.sitefolder.client.activity.app.list.item;

import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractApplicationListItemView extends IsWidget {

    void setActivity(AbstractApplicationListItemActivity activity);

    void setName(String name);

    void setComment(String comment);

    void setEditVisible(boolean visible);
}