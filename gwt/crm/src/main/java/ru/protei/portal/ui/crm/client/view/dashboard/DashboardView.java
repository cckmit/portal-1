package ru.protei.portal.ui.crm.client.view.dashboard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.protei.portal.ui.crm.client.activity.dashboard.AbstractDashboardActivity;
import ru.protei.portal.ui.crm.client.activity.dashboard.AbstractDashboardView;

/**
 * Created by bondarenko on 01.12.16.
 */
public class DashboardView extends Composite implements AbstractDashboardView{

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractDashboardActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasWidgets getActiveRecordsContainer() {
        return activeRecordsContainer;
    }

    @Override
    public HasWidgets getNewRecordsContainer() {
        return newRecordsContainer;
    }

    @Override
    public HasWidgets getInactiveRecordsContainer() {
        return inactiveRecordsContainer;
    }

    @UiField
    HTMLPanel activeRecordsContainer;
    @UiField
    HTMLPanel newRecordsContainer;
    @UiField
    HTMLPanel inactiveRecordsContainer;

    private AbstractDashboardActivity activity;

    interface DashboardViewUiBinder extends UiBinder<HTMLPanel, DashboardView> {}
    private static DashboardViewUiBinder ourUiBinder = GWT.create(DashboardViewUiBinder.class);
}