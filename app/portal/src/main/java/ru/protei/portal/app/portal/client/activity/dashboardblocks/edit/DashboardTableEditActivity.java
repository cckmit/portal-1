package ru.protei.portal.app.portal.client.activity.dashboardblocks.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.ent.UserDashboard;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.IssueStates;
import ru.protei.portal.ui.common.client.events.DashboardEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IssueFilterControllerAsync;
import ru.protei.portal.ui.common.client.service.UserLoginControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class DashboardTableEditActivity implements Activity, AbstractDashboardTableEditActivity, AbstractDialogDetailsActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        dialogView.setActivity(this);
        dialogView.getBodyContainer().add(view.asWidget());
    }

    @Event
    public void onShow(DashboardEvents.EditTable event) {

        dashboard = event.dashboard;
        boolean isNew = dashboard == null || dashboard.getId() == null;
        if (isNew) {
            dashboard = new UserDashboard();
        }

        view.name().setValue(dashboard.getName());
        view.filter().setValue(dashboard.getCaseFilter() == null ? null : dashboard.getCaseFilter().toShortView(), true);
        view.updateFilterSelector();

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

        userLoginController.saveUserDashboard(dashboard, new FluentCallback<Long>()
                .withSuccess(id -> {
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
        String currentDashboardName = view.name().getValue();
        String oldFilterName = lastFilterName == null ? "" : lastFilterName;
        String newFilterName = filterShortView == null ? "" : filterShortView.getName();
        boolean dashboardNameEmpty = StringUtils.isEmpty(currentDashboardName);
        boolean dashboardNameMatchedOldFilterName = Objects.equals(currentDashboardName, oldFilterName);
        if (dashboardNameEmpty || dashboardNameMatchedOldFilterName) {
            view.name().setValue(newFilterName);
        }
        lastFilterName = newFilterName;
    }

    @Override
    public void onCreateFilterNewIssuesClicked() {
        CaseFilter filter = new CaseFilter();
        filter.setName(lang.dashboardTableFilterCreationNewIssues());
        filter.setType(En_CaseFilterType.CASE_OBJECTS);
        filter.setParams(generateQueryNewIssues());
        createNewFilter(filter);
    }

    @Override
    public void onCreateFilterActiveIssues() {
        CaseFilter filter = new CaseFilter();
        filter.setName(lang.dashboardTableFilterCreationActiveIssues());
        filter.setType(En_CaseFilterType.CASE_OBJECTS);
        filter.setParams(generateQueryActiveIssues());
        createNewFilter(filter);
    }

    private void createNewFilter(CaseFilter filter) {
        filterController.saveIssueFilter(filter, new FluentCallback<CaseFilter>()
                .withSuccess(f -> {
                    view.updateFilterSelector();
                    view.filter().setValue(f.toShortView(), true);
                }));
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

    private CaseQuery generateQueryNewIssues() {
        CaseQuery query = new CaseQuery(En_CaseType.CRM_SUPPORT, null, En_SortField.last_update, En_SortDir.DESC);
        query.setStates(Arrays.asList(En_CaseState.CREATED, En_CaseState.OPENED, En_CaseState.ACTIVE));
        query.setWithoutManager(true);
        return query;
    }

    private CaseQuery generateQueryActiveIssues() {
        CaseQuery query = new CaseQuery(En_CaseType.CRM_SUPPORT, null, En_SortField.last_update, En_SortDir.DESC);
        query.setStates(issueStates.getActiveStates());
        List<Long> productIds = null;
        if (policyService.getProfile() != null) {
            productIds = new ArrayList<>();
            productIds.add(policyService.getProfile().getId());
        }
        query.setManagerIds(productIds);
        return query;
    }

    @Inject
    Lang lang;
    @Inject
    AbstractDashboardTableEditView view;
    @Inject
    AbstractDialogDetailsView dialogView;
    @Inject
    UserLoginControllerAsync userLoginController;
    @Inject
    IssueFilterControllerAsync filterController;
    @Inject
    IssueStates issueStates;
    @Inject
    PolicyService policyService;

    private UserDashboard dashboard;
    private String lastFilterName;
}