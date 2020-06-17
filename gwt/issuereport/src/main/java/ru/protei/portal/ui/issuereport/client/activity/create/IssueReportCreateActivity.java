package ru.protei.portal.ui.issuereport.client.activity.create;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.ui.common.client.activity.filter.AbstractIssueFilterModel;
import ru.protei.portal.ui.common.client.activity.issuefilter.AbstractIssueFilterParamView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.activity.projectfilter.AbstractProjectFilterActivity;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ReportControllerAsync;
import ru.protei.portal.ui.common.client.view.projectfilter.ProjectFilterView;
import ru.protei.portal.ui.common.client.widget.issuefilter.IssueFilterWidget;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.Arrays;
import java.util.List;

import static ru.protei.portal.core.model.helper.StringUtils.isBlank;
import static ru.protei.portal.core.model.helper.StringUtils.nullIfEmpty;
import static ru.protei.portal.ui.common.client.util.IssueFilterUtils.searchCaseNumber;

public abstract class IssueReportCreateActivity implements Activity,
        AbstractIssueReportCreateActivity, AbstractIssueFilterModel, AbstractProjectFilterActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        issueFilterWidget.addAdditionalFilterValidate(
                caseFilter -> validateQuery(caseFilter.getType(), caseFilter.getParams()));
        issueFilterWidget.getIssueFilterParams().setModel(this);
        issueFilterWidget.clearFooterStyle();
        projectFilterView.clearFooterStyle();
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        view.fillReportTypes(makeReportTypeList());
        view.fillReportScheduledTypes(Arrays.asList(En_ReportScheduledType.values()));

        projectFilterView.resetFilter();
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(IssueReportEvents.Create event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.ISSUE_REPORT)) {
            fireEvent(new ForbiddenEvents.Show());
            return;
        }

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());

        isSaving = false;
        resetView();

        if(!policyService.hasSystemScopeForPrivilege(En_Privilege.COMPANY_VIEW)){
            issueFilterWidget.getIssueFilterParams().presetCompany(policyService.getProfile().getCompany());
            issueFilterWidget.getIssueFilterParams().presetManagerCompany(policyService.getProfile().getCompany());
        }
    }

    @Override
    public void onSaveClicked() {

        En_ReportType reportType = view.reportType().getValue();
        En_ReportScheduledType scheduledType = view.reportScheduledType().getValue();

        CaseQuery query;
        if (reportType == En_ReportType.PROJECT) {
            query = getProjectQuery().toCaseQuery(policyService.getProfile().getId());
        } else {
            query = issueFilterWidget.getFilterFieldsByFilterType();
            query.setCheckImportanceHistory(view.checkImportanceHistory().getValue());
        }

        if (!validateQuery(reportType, query)) {
            return;
        }
        Report report = new Report();
        report.setReportType(reportType);
        report.setScheduledType(scheduledType);
        report.setName(view.name().getValue());
        report.setLocale(LocaleInfo.getCurrentLocale().getLocaleName());
        report.setWithDescription(view.withDescription().getValue());
        report.setCaseQuery(query);

        if (isSaving) {
            return;
        }
        isSaving = true;

        reportController.createReport(report, new FluentCallback<Long>()
                .withError(t -> isSaving = false)
                .withSuccess(result -> {
                    isSaving = false;
                    fireEvent(new Back());
                    fireEvent(new NotifyEvents.Show(lang.reportRequested(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new IssueReportEvents.Show());
                })
        );
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }

    @Override
    public void onReportTypeChanged() {
        En_ReportType reportType = view.reportType().getValue();
        if (reportType == En_ReportType.PROJECT) {
            projectFilterView.resetFilter();
            view.reportScheduledType().setValue(En_ReportScheduledType.NONE);
            view.getIssueFilterContainer().clear();
            view.getIssueFilterContainer().add(projectFilterView.asWidget());
            view.scheduledTypeContainerVisibility().setVisible(false);
            view.checkImportanceHistoryContainerVisibility().setVisible(false);
            view.withDescriptionContainerVisibility().setVisible(false);
            view.checkImportanceHistory().setValue(false);
            view.withDescription().setValue(false);
        } else {
            view.reportScheduledType().setValue(En_ReportScheduledType.NONE);
            view.checkImportanceHistory().setValue(false);
            view.withDescription().setValue(false);
            view.scheduledTypeContainerVisibility().setVisible(isScheduledTypeContainerVisible(reportType));
            view.checkImportanceHistoryContainerVisibility().setVisible(reportType == En_ReportType.CASE_OBJECTS);
            view.withDescriptionContainerVisibility().setVisible(reportType == En_ReportType.CASE_OBJECTS);
            issueFilterWidget.updateFilterType(En_CaseFilterType.valueOf(reportType.name()));
            applyIssueFilterVisibilityByPrivileges();
            view.getIssueFilterContainer().clear();
            view.getIssueFilterContainer().add(issueFilterWidget.asWidget());
        }
    }

    @Override
    public void onProjectFilterChanged()  {
        ; // ничего не делаем, мы используем фильтр при создании отчета
    }

    @Override
    public void onUserFilterChanged() {
        // ничего не делаем, мы используем фильтр при создании отчета
    }

    private boolean validateQuery(En_CaseFilterType filterType, CaseQuery query) {
        return validateQuery(En_ReportType.valueOf(filterType.name()), query);
    }

    private boolean validateQuery(En_ReportType reportType, CaseQuery query) {
        if (reportType == null || query == null) {
            return false;
        }

        switch (reportType) {
            case CASE_RESOLUTION_TIME:
                if (query.getCreatedFrom() == null || query.getCreatedTo() == null)  {
                    fireEvent(new NotifyEvents.Show(lang.reportMissingPeriod(), NotifyEvents.NotifyType.ERROR));
                    return false;
                }
                if (query.getStateIds() == null)  {
                    fireEvent(new NotifyEvents.Show( lang.reportMissingState(), NotifyEvents.NotifyType.ERROR));
                    return false;
                }
                break;
        }
        return true;
    }

    private void applyIssueFilterVisibilityByPrivileges() {
        AbstractIssueFilterParamView issueFilterParams = issueFilterWidget.getIssueFilterParams();
        if (issueFilterParams.productsVisibility().isVisible()) {
            issueFilterParams.productsVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_PRODUCT_VIEW));
        }
        if (issueFilterParams.searchPrivateVisibility().isVisible()) {
            issueFilterParams.searchPrivateVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_PRIVACY_VIEW));
        }
    }

    private List<En_ReportType> makeReportTypeList() {
        if (policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_REPORT)) {
            return Arrays.asList(En_ReportType.values());
        }
        return Arrays.asList(En_ReportType.CASE_OBJECTS);
    }

    private ProjectQuery getProjectQuery() {
        ProjectQuery query = new ProjectQuery();

        String searchString = projectFilterView.searchPattern().getValue();
        query.setCaseIds(searchCaseNumber(searchString, false));
        if (query.getCaseIds() == null) {
            query.setSearchString(isBlank(searchString) ? null : searchString);
        }

        query.setStates(projectFilterView.states().getValue());
        query.setRegions(projectFilterView.regions().getValue());
        query.setHeadManagers(projectFilterView.headManagers().getValue());
        query.setCaseMembers(projectFilterView.caseMembers().getValue());
        query.setDirections(projectFilterView.direction().getValue());
        query.setSortField(projectFilterView.sortField().getValue());
        query.setSortDir(projectFilterView.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC);
        query.setOnlyMineProjects(projectFilterView.onlyMineProjects().getValue());
        return  query;
    }

    private void resetView() {
        view.name().setValue(null);
        view.reportType().setValue(En_ReportType.CASE_OBJECTS, true);
        view.reportScheduledType().setValue(En_ReportScheduledType.NONE);
        view.checkImportanceHistory().setValue(false);
        view.withDescription().setValue(false);
    }

    private boolean isScheduledTypeContainerVisible(En_ReportType reportType) {
        if (En_ReportType.CASE_OBJECTS.equals(reportType)) {
            return true;
        }

        if (En_ReportType.CASE_TIME_ELAPSED.equals(reportType)) {
            return true;
        }

        return false;
    }

    @Inject
    Lang lang;
    @Inject
    AbstractIssueReportCreateView view;
    @Inject
    ReportControllerAsync reportController;
    @Inject
    PolicyService policyService;

    @Inject
    IssueFilterWidget issueFilterWidget;
    @Inject
    ProjectFilterView projectFilterView;

    private boolean isSaving;
    private AppEvents.InitDetails initDetails;
}
