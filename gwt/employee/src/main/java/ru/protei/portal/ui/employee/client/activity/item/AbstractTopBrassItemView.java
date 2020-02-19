package ru.protei.portal.ui.employee.client.activity.item;

import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractTopBrassItemView extends IsWidget {
    void addRootStyle(String style);

    void setImage(String url);

    void setName(String name, String link);

    void setPosition(String position);
}
