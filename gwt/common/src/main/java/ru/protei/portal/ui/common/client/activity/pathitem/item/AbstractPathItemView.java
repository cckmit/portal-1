package ru.protei.portal.ui.common.client.activity.pathitem.item;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractPathItemView extends IsWidget {

    void setActivity(AbstractPathItemActivity activity);

    HasText path();

    HasText desc();

    void focused();
}
