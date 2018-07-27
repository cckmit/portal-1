package ru.protei.portal.ui.sitefolder.client.activity.server.list;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractServerListView extends IsWidget {

    void setActivity(AbstractServerListActivity activity);

    HasWidgets getChildContainer();
}
