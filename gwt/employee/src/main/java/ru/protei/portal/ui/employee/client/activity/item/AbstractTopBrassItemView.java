package ru.protei.portal.ui.employee.client.activity.item;

import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractTopBrassItemView extends IsWidget {
    void setActivity(AbstractTopBrassItemActivity activity);

    void setImage(String url);

    void setPosition(String position);
}
