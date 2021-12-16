package ru.protei.portal.app.portal.client.activity.dashboardblocks.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.dto.CaseFilterDto;
import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.ent.UserDashboard;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.FilterShortView;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.DashboardEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CaseFilterControllerAsync;
import ru.protei.portal.ui.common.client.service.UserLoginControllerAsync;
import ru.protei.portal.ui.common.client.util.CaseStateUtils;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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

        loadFilters(this::autoHideExistingFiltersFromCreation);
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
    public void onFilterChanged(FilterShortView filterShortView) {
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
        CaseFilterDto<CaseQuery> caseFilterDto = makeFilterNewIssues();
        createNewFilter(caseFilterDto);
    }

    @Override
    public void onCreateFilterActiveIssues() {
        CaseFilterDto<CaseQuery> caseFilterDto = makeFilterActiveIssues();
        createNewFilter(caseFilterDto);
    }

    private void autoHideExistingFiltersFromCreation(List<FilterShortView> filters) {
        List<String> filterNames = CollectionUtils.stream(filters)
                .map(FilterShortView::getName)
                .collect(Collectors.toList());
        boolean shouldHideNewIssues = filterNames.contains(makeFilterNewIssues().getCaseFilter().getName());
        boolean shouldHideActiveIssues = filterNames.contains(makeFilterActiveIssues().getCaseFilter().getName());
        boolean shouldHideCreation = shouldHideNewIssues && shouldHideActiveIssues;
        view.filterCreateNewIssues().setVisible(!shouldHideNewIssues);
        view.filterCreateActiveIssues().setVisible(!shouldHideActiveIssues);
        view.filterCreateContainer().setVisible(!shouldHideCreation);
    }

    private void loadFilters(Consumer<List<FilterShortView>> onLoaded) {
        filterController.getCaseFilterShortViewList(En_CaseFilterType.CASE_OBJECTS, new FluentCallback<List<FilterShortView>>()
                .withError(throwable -> {})
                .withSuccess(onLoaded));
    }

    private void createNewFilter(CaseFilterDto<CaseQuery> caseFilterDto) {
        filterController.saveIssueFilter(caseFilterDto, new FluentCallback<CaseFilterDto<CaseQuery>>()
                .withSuccess(result -> {
                    view.updateFilterSelector();
                    view.filter().setValue(result.getCaseFilter().toShortView(), true);
                    loadFilters(this::autoHideExistingFiltersFromCreation);
                }));
    }

    private boolean validate() {
        if (view.filter().getValue() == null || view.filter().getValue().getId() == null) {
            fireEvent(new NotifyEvents.Show(lang.errDashboardChooseFilter(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        if (StringUtils.isBlank(view.name().getValue())) {
            fireEvent(new NotifyEvents.Show(lang.errDashboardTableNameEmpty(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        return true;
    }

    private CaseFilterDto<CaseQuery> makeFilterNewIssues() {
        CaseFilter filter = new CaseFilter();
        filter.setName(lang.dashboardTableFilterCreationNewIssues());
        filter.setType(En_CaseFilterType.CASE_OBJECTS);
        return new CaseFilterDto<>(filter, generateQueryNewIssues());
    }

    private CaseFilterDto<CaseQuery> makeFilterActiveIssues() {
        CaseFilter filter = new CaseFilter();
        filter.setName(lang.dashboardTableFilterCreationActiveIssues());
        filter.setType(En_CaseFilterType.CASE_OBJECTS);
        return new CaseFilterDto<>(filter, generateQueryActiveIssues());
    }

    private CaseQuery generateQueryNewIssues() {
        CaseQuery query = new CaseQuery(En_CaseType.CRM_SUPPORT, null, En_SortField.last_update, En_SortDir.DESC);
        query.setStateIds(CaseStateUtils.getNewStateIds());
        query.setManagerIds(Arrays.asList(CrmConstants.Employee.UNDEFINED, CrmConstants.Employee.GROUP_MANAGER));
        if (policyService.getProfile() != null) {
            query.setManagerCompanyIds(new ArrayList<>(Collections.singletonList(policyService.getUserCompany().getId())));
        }
        return query;
    }

    private CaseQuery generateQueryActiveIssues() {
        CaseQuery query = new CaseQuery(En_CaseType.CRM_SUPPORT, null, En_SortField.last_update, En_SortDir.DESC);
        query.setStateIds(CaseStateUtils.getActiveStateIds());
        List<Long> managerIds = new ArrayList<>();
        List<Long> managerCompanyIds = new ArrayList<>();
        if (policyService.getProfile() != null) {
            managerIds.add(policyService.getProfile().getId());
            managerCompanyIds.add(policyService.getUserCompany().getId());
        }
        query.setManagerIds(managerIds);
        query.setManagerCompanyIds(managerCompanyIds);
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
    CaseFilterControllerAsync filterController;
    @Inject
    PolicyService policyService;

    private UserDashboard dashboard;
    private String lastFilterName;
}
