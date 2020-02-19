package ru.protei.portal.ui.employee.client.activity.item;

import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractTopBrassItemView extends IsWidget {
    void setActivity(AbstractTopBrassItemActivity activity);

    void addRootStyle(String style);

    void setImage(String url);

    void setName(String name);

    void setPosition(String position);
}
