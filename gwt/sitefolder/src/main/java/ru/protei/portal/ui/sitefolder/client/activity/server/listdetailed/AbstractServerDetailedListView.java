package ru.protei.portal.ui.sitefolder.client.activity.server.listdetailed;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractServerDetailedListView extends IsWidget {

    void setActivity(AbstractServerDetailedListActivity activity);

    HasWidgets getChildContainer();
}
