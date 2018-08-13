package ru.protei.portal.ui.sitefolder.client.view.server.listdetailed;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.widget.platelist.PlateList;
import ru.protei.portal.ui.sitefolder.client.activity.server.listdetailed.AbstractServerDetailedListActivity;
import ru.protei.portal.ui.sitefolder.client.activity.server.listdetailed.AbstractServerDetailedListView;

public class ServerDetailedListView extends Composite implements AbstractServerDetailedListView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractServerDetailedListActivity activity) {}

    @Override
    public HasWidgets getChildContainer() {
        return childContainer;
    }

    @UiField
    PlateList childContainer;

    interface ServerDetailedListViewUiBinder extends UiBinder<HTMLPanel, ServerDetailedListView> {}
    private static ServerDetailedListViewUiBinder ourUiBinder = GWT.create(ServerDetailedListViewUiBinder.class);
}
