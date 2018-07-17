package ru.protei.portal.ui.sitefolder.client.view.server.list;

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
import ru.protei.portal.ui.sitefolder.client.activity.server.list.AbstractSiteFolderServerListActivity;
import ru.protei.portal.ui.sitefolder.client.activity.server.list.AbstractSiteFolderServerListView;

public class SiteFolderServerListView extends Composite implements AbstractSiteFolderServerListView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractSiteFolderServerListActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasWidgets getChildContainer() {
        return childContainer;
    }

    @UiHandler("childContainer")
    public void onAddClicked(AddEvent event) {
        if (activity != null) {
            activity.onCreateClicked();
        }
    }

    @UiField
    PlateList childContainer;

    private AbstractSiteFolderServerListActivity activity;

    interface SiteFolderServerListViewUiBinder extends UiBinder<HTMLPanel, SiteFolderServerListView> {}
    private static SiteFolderServerListViewUiBinder ourUiBinder = GWT.create(SiteFolderServerListViewUiBinder.class);
}
