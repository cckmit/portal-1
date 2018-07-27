package ru.protei.portal.ui.sitefolder.client.activity.app.list;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractApplicationListView extends IsWidget {

    void setActivity(AbstractApplicationListActivity activity);

    HasWidgets getChildContainer();
}
