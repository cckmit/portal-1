package ru.protei.portal.app.portal.client.activity.dashboardblocks.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.UserDashboard;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.events.DashboardEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.UserLoginControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

public abstract class DashboardTableEditActivity implements Activity, AbstractDashboardTableEditActivity, AbstractDialogDetailsActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        dialogView.setActivity(this);
        dialogView.getBodyContainer().add(view.asWidget());
    }

    @Event
    public void onShow(DashboardEvents.EditTable event) {
        if (event.dashboard == null) {
            fireEvent(new NotifyEvents.Show(lang.error(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        this.dashboard = event.dashboard;
        boolean isNew = dashboard.getId() == null;

        view.name().setValue(dashboard.getName());
        view.filter().setValue(dashboard.getCaseFilter() == null ? null : dashboard.getCaseFilter().toShortView());

        dialogView.saveButtonVisibility().setVisible(true);
        dialogView.setHeader(isNew ? lang.dashboardTableCreate() : lang.dashboardTableEdit());
        dialogView.showPopup();
    }

    @Override
    public void onSaveClicked() {
        if (!validate()) {
            return;
        }

        dashboard.setName(view.name().getValue());
        dashboard.setCaseFilterId(view.filter().getValue().getId());

        userLoginController.saveUserDashboard(dashboard, new FluentCallback<UserDashboard>()
                .withSuccess(d -> {
                    dialogView.hidePopup();
                    fireEvent(new DashboardEvents.ChangeTableModel());
                }));
    }

    @Override
    public void onCancelClicked() {
        dashboard = null;
        dialogView.hidePopup();
    }

    @Override
    public void onFilterChanged(CaseFilterShortView filterShortView) {
        if (filterShortView == null) {
            return;
        }
        if (StringUtils.isNotEmpty(view.name().getValue())) {
            return;
        }
        view.name().setValue(filterShortView.getName());
    }

    private boolean validate() {
        if (StringUtils.isBlank(view.name().getValue())) {
            return false;
        }
        if (view.filter().getValue() == null || view.filter().getValue().getId() == null) {
            return false;
        }
        return true;
    }

    @Inject
    Lang lang;
    @Inject
    AbstractDashboardTableEditView view;
    @Inject
    AbstractDialogDetailsView dialogView;
    @Inject
    UserLoginControllerAsync userLoginController;

    private UserDashboard dashboard;
}
