package ru.protei.portal.ui.sitefolder.client.view.app.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.widget.platelist.PlateList;
import ru.protei.portal.ui.sitefolder.client.activity.app.list.AbstractApplicationListActivity;
import ru.protei.portal.ui.sitefolder.client.activity.app.list.AbstractApplicationListView;

public class ApplicationListView extends Composite implements AbstractApplicationListView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractApplicationListActivity activity) {}

    @Override
    public HasWidgets getChildContainer() {
        return childContainer;
    }

    @UiField
    PlateList childContainer;

    interface SiteFolderAppListViewUiBinder extends UiBinder<HTMLPanel, ApplicationListView> {}
    private static SiteFolderAppListViewUiBinder ourUiBinder = GWT.create(SiteFolderAppListViewUiBinder.class);
}

