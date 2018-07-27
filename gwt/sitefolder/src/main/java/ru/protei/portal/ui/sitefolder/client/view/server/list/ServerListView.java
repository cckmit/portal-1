package ru.protei.portal.ui.sitefolder.client.view.server.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.widget.platelist.PlateList;
import ru.protei.portal.ui.sitefolder.client.activity.server.list.AbstractServerListActivity;
import ru.protei.portal.ui.sitefolder.client.activity.server.list.AbstractServerListView;

public class ServerListView extends Composite implements AbstractServerListView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractServerListActivity activity) {}

    @Override
    public HasWidgets getChildContainer() {
        return childContainer;
    }

    @UiField
    PlateList childContainer;

    interface SiteFolderServerListViewUiBinder extends UiBinder<HTMLPanel, ServerListView> {}
    private static SiteFolderServerListViewUiBinder ourUiBinder = GWT.create(SiteFolderServerListViewUiBinder.class);
}
