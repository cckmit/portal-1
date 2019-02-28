package ru.protei.portal.ui.project.client.activity.list;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

public interface AbstractProjectDocumentsListView extends IsWidget {

    void setActivity(AbstractProjectDocumentsListActivity activity);

    HasWidgets documentsContainer();
}
