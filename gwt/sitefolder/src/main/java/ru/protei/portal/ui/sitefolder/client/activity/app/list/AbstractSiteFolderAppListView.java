package ru.protei.portal.ui.sitefolder.client.activity.app.list;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractSiteFolderAppListView extends IsWidget {

    void setActivity(AbstractSiteFolderAppListActivity activity);

    HasWidgets getChildContainer();
}
