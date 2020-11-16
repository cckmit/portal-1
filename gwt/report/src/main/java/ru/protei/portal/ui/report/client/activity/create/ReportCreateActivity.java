package ru.protei.portal.ui.report.client.activity.create;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.dto.*;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.ContractQuery;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.struct.DateRange;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.ui.common.client.activity.contractfilter.AbstractContractFilterView;
import ru.protei.portal.ui.common.client.activity.filter.AbstractIssueFilterModel;
import ru.protei.portal.ui.common.client.activity.issuefilter.AbstractIssueFilterParamView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.activity.projectfilter.AbstractProjectFilterActivity;
import ru.protei.portal.ui.common.client.activity.projectfilter.AbstractProjectFilterView;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ReportControllerAsync;
import ru.protei.portal.ui.common.client.widget.issuefilter.IssueFilterWidget;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.helper.StringUtils.isBlank;
import static ru.protei.portal.ui.common.client.util.IssueFilterUtils.searchCaseNumber;
import static ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType.toDateRange;
import static ru.protei.portal.ui.report.client.util.AccessUtil.availableReportTypes;
import static ru.protei.portal.ui.report.client.util.AccessUtil.canEdit;

public abstract class ReportCreateActivity implements Activity,
        AbstractReportCreateActivity, AbstractIssueFilterModel, AbstractProjectFilterActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        issueFilterWidget.getIssueFilterParams().setModel(this);
        issueFilterWidget.clearFooterStyle();
        projectFilterView.clearFooterStyle();
        contractFilterView.clearFooterStyle();
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        issueFilterWidget.resetFilter(null);
        projectFilterView.resetFilter();
        contractFilterView.resetFilter();
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(ReportEvents.Create event) {
        if (!canEdit(policyService)) {
            fireEvent(new ErrorPageEvents.ShowForbidden(initDetails.parent));
            return;
        }
        showView();
        resetView();
        fillSelectors();
        presetCompanyAtFilter();
        isSaving = false;
    }

    private void fillSelectors() {
        view.fillReportTypes(availableReportTypes(policyService));
        view.fillReportScheduledTypes(asList(En_ReportScheduledType.values()));
    }

    private void showView() {
        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());
    }

    private void resetView() {
        view.name().setValue(null);
        view.reportType().setValue(availableReportTypes(policyService).get(0), true);
        view.reportScheduledType().setValue(En_ReportScheduledType.NONE);
        view.additionalParams().setValue(null);
    }

    private void presetCompanyAtFilter() {
        if (!policyService.hasSystemScopeForPrivilege(En_Privilege.COMPANY_VIEW)) {
            issueFilterWidget.getIssueFilterParams().presetCompany(policyService.getProfile().getCompany());
            issueFilterWidget.getIssueFilterParams().presetManagerCompany(policyService.getProfile().getCompany());
        }
    }

    @Override
    public void onSaveClicked() {
        Report report = makeReport();
        ReportDto reportDto = makeReportDto(report);

        if (reportDto == null || isSaving) {
            return;
        }
        isSaving = true;

        reportController.createReport(reportDto, new FluentCallback<Long>()
                .withError(throwable -> {
                    isSaving = false;
                    defaultErrorHandler.accept(throwable);
                })
                .withSuccess(result -> {
                    isSaving = false;
                    fireEvent(new NotifyEvents.Show(lang.reportRequested(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new ReportEvents.Show());
                })
        );
    }

    private Report makeReport() {
        Report report = new Report();
        report.setReportType(view.reportType().getValue());
        report.setScheduledType(view.reportScheduledType().getValue());
        report.setName(view.name().getValue());
        report.setLocale(LocaleInfo.getCurrentLocale().getLocaleName());
        report.setWithDescription(contains(view.additionalParams().getValue(), En_ReportAdditionalParamType.DESCRIPTION));
        report.setWithTags(contains(view.additionalParams().getValue(), En_ReportAdditionalParamType.TAGS));
        report.setWithLinkedIssues(contains(view.additionalParams().getValue(), En_ReportAdditionalParamType.LINKED_ISSUES));
        report.setHumanReadable(contains(view.additionalParams().getValue(), En_ReportAdditionalParamType.HUMAN_READABLE));
        return report;
    }

    private ReportDto makeReportDto(Report report) {
        if (report == null || report.getReportType() == null) {
            return null;
        }
        switch (report.getReportType()) {
            case CASE_OBJECTS:
            case CASE_TIME_ELAPSED:
            case CASE_RESOLUTION_TIME: {
                CaseQuery query = getIssueQuery();
                if (!validateCaseQuery(report.getReportType(), query)) {
                    return null;
                }
                return new ReportCaseQuery(report, query);
            }
            case PROJECT: {
                ProjectQuery query = getProjectQuery();
                return new ReportProjectQuery(report, query);
            }
            case CONTRACT: {
                ContractQuery query = getContractQuery();
                return new ReportContractQuery(report, query);
            }
        }
        throw new IllegalStateException("No switch branch matched for En_ReportType");
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }

    @Override
    public void onReportTypeChanged() {
        En_ReportType reportType = view.reportType().getValue();
        showFilterForReportType(reportType);
    }

    private void showFilterForReportType(En_ReportType reportType) {
        switch (reportType) {
            case PROJECT: {
                projectFilterView.resetFilter();
                view.reportScheduledType().setValue(En_ReportScheduledType.NONE);
                view.getFilterContainer().clear();
                view.getFilterContainer().add(projectFilterView.asWidget());
                view.scheduledTypeContainerVisibility().setVisible(false);
                view.additionalParamsVisibility().setVisible(false);
                view.additionalParams().setValue(null);
                break;
            }
            case CONTRACT: {
                contractFilterView.resetFilter();
                view.reportScheduledType().setValue(En_ReportScheduledType.NONE);
                view.getFilterContainer().clear();
                view.getFilterContainer().add(contractFilterView.asWidget());
                view.scheduledTypeContainerVisibility().setVisible(false);
                view.additionalParamsVisibility().setVisible(false);
                view.additionalParams().setValue(null);
                break;
            }
            case CASE_OBJECTS:
            case CASE_TIME_ELAPSED:
            case CASE_RESOLUTION_TIME: {
                view.reportScheduledType().setValue(En_ReportScheduledType.NONE);
                view.additionalParams().setValue(null);
                view.scheduledTypeContainerVisibility().setVisible(isScheduledEnabled(reportType));
                view.additionalParamsVisibility().setVisible(reportType == En_ReportType.CASE_OBJECTS);
                issueFilterWidget.updateFilterType(En_CaseFilterType.valueOf(reportType.name()));
                validateDateRanges(reportType);
                applyIssueFilterVisibilityByPrivileges();
                view.getFilterContainer().clear();
                view.getFilterContainer().add(issueFilterWidget.asWidget());
                break;
            }
        }
    }

    private boolean isScheduledEnabled(En_ReportType reportType) {
        switch (reportType) {
            case CASE_OBJECTS: return true;
            case CASE_TIME_ELAPSED: return true;
            case CASE_RESOLUTION_TIME: return false;
            case PROJECT: return false;
            case CONTRACT: return false;
        }
        return false;
    }

    @Override
    public void onProjectFilterChanged()  {
        ; // ничего не делаем, мы используем фильтр при создании отчета
    }

    @Override
    public void onUserFilterChanged() {
        // почти ничего не делаем, только валидацию dateRange, мы используем фильтр при создании отчета
        validateDateRanges(view.reportType().getValue());
    }

    // валидация виджетов выбора временных периодов в зависимости от типа отчета
    private void validateDateRanges(En_ReportType reportType) {
        boolean isTimeLimitMandatory = En_ReportType.isTimeLimitMandatory(reportType);

        issueFilterWidget.getIssueFilterParams().setCreatedRangeMandatory(isTimeLimitMandatory);
        validateCreatedRange(
                issueFilterWidget.getIssueFilterParams().isCreatedRangeTypeValid(),
                issueFilterWidget.getIssueFilterParams().isCreatedRangeValid());
        validateModifiedRange(
                true,
                issueFilterWidget.getIssueFilterParams().isModifiedRangeValid());
    }

    private boolean validateCaseQuery(En_ReportType reportType, CaseQuery query) {
        if (reportType == null || query == null) {
            return false;
        }

        boolean rangeTypeMandatory = En_ReportType.isTimeLimitMandatory(reportType);

        switch (reportType) {
            case CASE_RESOLUTION_TIME:
            case CASE_TIME_ELAPSED :
                boolean dateRangeValid = validateCreatedRange(query.getCreatedRange(), rangeTypeMandatory);

                if (!dateRangeValid) {
                    fireEvent(new NotifyEvents.Show(lang.reportMissingPeriod(), NotifyEvents.NotifyType.ERROR));
                    return false;
                }
                if (Objects.equals(reportType, En_ReportType.CASE_RESOLUTION_TIME)
                    && query.getStateIds() == null)  {
                    fireEvent(new NotifyEvents.Show( lang.reportMissingState(), NotifyEvents.NotifyType.ERROR));
                    return false;
                }
                break;
            case CASE_OBJECTS:
                if (query.getCreatedRange() == null && query.getModifiedRange() == null) {
                    fireEvent(new NotifyEvents.Show(lang.reportPeriodNotSelected(), NotifyEvents.NotifyType.ERROR));
                    return false;
                }
                boolean createdRangeValid = validateCreatedRange(query.getCreatedRange(), rangeTypeMandatory);
                boolean modifiedRangeValid = validateModifiedRange(query.getModifiedRange(), rangeTypeMandatory);

                if (!createdRangeValid || !modifiedRangeValid) {
                    fireEvent(new NotifyEvents.Show(lang.reportMissingPeriod(), NotifyEvents.NotifyType.ERROR));
                    return false;
                }
                break;
        }
        if (!isValidMaxPeriod(query.getCreatedRange()) || !isValidMaxPeriod(query.getModifiedRange())) {
            fireEvent(new NotifyEvents.Show(lang.reportPeriodMoreMaxError(), NotifyEvents.NotifyType.ERROR));
            return false;
        }
        return true;
    }

    private boolean isValidMaxPeriod(DateRange dateRange) {
        if (dateRange != null && dateRange.getIntervalType() == En_DateIntervalType.FIXED &&
                dateRange.getTo() != null && dateRange.getFrom() != null) {
            Date yearAgo = new Date(dateRange.getTo().getTime() - TimeUnit.DAYS.toMillis(LITTLE_OVER_YEAR_DAYS));
            return yearAgo.before(dateRange.getFrom());
        }
        return true;
    }

    private boolean validateTypeRange(DateRange dateRange, boolean isMandatory) {
        return !isMandatory || (dateRange != null && dateRange.getIntervalType() != null);
    }

    private boolean validateDateRange(DateRange dateRange) {
        if (dateRange == null
            || dateRange.getIntervalType() == null
            || !Objects.equals(dateRange.getIntervalType(), En_DateIntervalType.FIXED))
        return true;

        return dateRange.getFrom() != null
                && dateRange.getTo() != null
                && dateRange.getFrom().before(dateRange.getTo());
    }

    private boolean validateCreatedRange(DateRange dateRange, boolean isMandatory) {
        boolean typeValid = validateTypeRange(dateRange, isMandatory);
        boolean rangeValid = typeValid ? validateDateRange(dateRange) : true;

        validateCreatedRange(typeValid, rangeValid);
        return typeValid && rangeValid;
    }

    private boolean validateModifiedRange(DateRange dateRange, boolean isMandatory) {
        boolean typeValid = validateTypeRange(dateRange, isMandatory);
        boolean rangeValid = typeValid ? validateDateRange(dateRange) : true;

        validateModifiedRange(typeValid, rangeValid);
        return typeValid && rangeValid;
    }

    private void validateCreatedRange(boolean isTypeValid, boolean isRangeValid) {
        issueFilterWidget.getIssueFilterParams().setCreatedRangeValid(isTypeValid, isRangeValid);
    }

    private void validateModifiedRange(boolean isTypeValid, boolean isRangeValid) {
        issueFilterWidget.getIssueFilterParams().setModifiedRangeValid(isTypeValid, isRangeValid);
    }

    private void applyIssueFilterVisibilityByPrivileges() {
        AbstractIssueFilterParamView issueFilterParams = issueFilterWidget.getIssueFilterParams();
        if (issueFilterParams.productsVisibility().isVisible()) {
            issueFilterParams.productsVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_PRODUCT_VIEW));
        }
        if (issueFilterParams.searchPrivateVisibility().isVisible()) {
            issueFilterParams.searchPrivateVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_PRIVACY_VIEW));
        }
        if (issueFilterParams.creatorsVisibility().isVisible()) {
            issueFilterParams.creatorsVisibility().setVisible(policyService.personBelongsToHomeCompany());
        }
    }

    private CaseQuery getIssueQuery() {
        CaseQuery query = issueFilterWidget.getFilterFieldsByFilterType();
        query.setCheckImportanceHistory(contains(view.additionalParams().getValue(), En_ReportAdditionalParamType.IMPORTANCE_HISTORY));
        return query;
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
        if(projectFilterView.onlyMineProjects().getValue() != null && projectFilterView.onlyMineProjects().getValue()) {
            query.setMemberId(policyService.getProfile().getId());
        }
        query.setInitiatorCompanyIds(projectFilterView.initiatorCompanies().getValue().stream()
                .map(entityOption -> entityOption.getId()).collect(Collectors.toSet()));
        return query;
    }
    
    private ContractQuery getContractQuery() {
        ContractQuery query = new ContractQuery();
        query.setSearchString(contractFilterView.searchString().getValue());
        query.setSortDir(contractFilterView.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC);
        query.setSortField(contractFilterView.sortField().getValue());
        query.setContractorIds(collectIds(contractFilterView.contractors().getValue()));
        query.setOrganizationIds(collectIds(contractFilterView.organizations().getValue()));
        query.setManagerIds(collectIds(contractFilterView.managers().getValue()));
        query.setTypes(nullIfEmpty(listOfOrNull(contractFilterView.types().getValue())));
        query.setCaseTagsIds(nullIfEmpty(toList(contractFilterView.tags().getValue(), caseTag -> caseTag == null ? CrmConstants.CaseTag.NOT_SPECIFIED : caseTag.getId())));
        query.setStates(nullIfEmpty(listOfOrNull(contractFilterView.states().getValue())));
        ProductDirectionInfo value = contractFilterView.direction().getValue();
        query.setDirectionId(value == null ? null : value.id);
        query.setKind(contractFilterView.kind().getValue());
        query.setDateSigningRange(toDateRange(contractFilterView.dateSigningRange().getValue()));
        query.setDateValidRange(toDateRange(contractFilterView.dateValidRange().getValue()));
        return query;
    }

    @Inject
    Lang lang;
    @Inject
    AbstractReportCreateView view;
    @Inject
    ReportControllerAsync reportController;
    @Inject
    PolicyService policyService;
    @Inject
    DefaultErrorHandler defaultErrorHandler;

    @Inject
    IssueFilterWidget issueFilterWidget;
    @Inject
    AbstractProjectFilterView projectFilterView;
    @Inject
    AbstractContractFilterView contractFilterView;

    private boolean isSaving;
    private AppEvents.InitDetails initDetails;

    static public int LITTLE_OVER_YEAR_DAYS = 370;
}
