package ru.protei.portal.ui.sitefolder.client.view.app.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.events.AddEvent;
import ru.protei.portal.ui.common.client.widget.platelist.PlateList;
import ru.protei.portal.ui.sitefolder.client.activity.app.list.AbstractSiteFolderAppListActivity;
import ru.protei.portal.ui.sitefolder.client.activity.app.list.AbstractSiteFolderAppListView;

public class SiteFolderAppListView extends Composite implements AbstractSiteFolderAppListView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractSiteFolderAppListActivity activity) {}

    @Override
    public HasWidgets getChildContainer() {
        return childContainer;
    }

    @UiField
    PlateList childContainer;

    interface SiteFolderAppListViewUiBinder extends UiBinder<HTMLPanel, SiteFolderAppListView> {}
    private static SiteFolderAppListViewUiBinder ourUiBinder = GWT.create(SiteFolderAppListViewUiBinder.class);
}

