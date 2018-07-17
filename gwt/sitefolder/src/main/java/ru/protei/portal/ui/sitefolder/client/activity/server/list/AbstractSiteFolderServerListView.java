package ru.protei.portal.ui.sitefolder.client.activity.server.list;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractSiteFolderServerListView extends IsWidget {

    void setActivity(AbstractSiteFolderServerListActivity activity);

    HasWidgets getChildContainer();
}
