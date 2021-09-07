package ru.protei.portal.ui.report.client.activity.edit;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.dto.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.*;
import ru.protei.portal.core.model.struct.DateRange;
import ru.protei.portal.core.model.util.CaseStateUtil;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.activity.contractfilter.AbstractContractFilterView;
import ru.protei.portal.ui.common.client.activity.filter.AbstractIssueFilterModel;
import ru.protei.portal.ui.common.client.activity.issuefilter.AbstractIssueFilterParamView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.activity.ytwork.AbstractYoutrackWorkFilterActivity;
import ru.protei.portal.ui.common.client.activity.ytwork.AbstractYoutrackWorkFilterView;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CaseFilterControllerAsync;
import ru.protei.portal.ui.common.client.service.ContractControllerAsync;
import ru.protei.portal.ui.common.client.service.ReportControllerAsync;
import ru.protei.portal.ui.common.client.widget.issuefilter.IssueFilterWidget;
import ru.protei.portal.ui.common.client.widget.project.filter.ProjectFilterWidget;
import ru.protei.portal.ui.common.client.widget.project.filter.ProjectFilterWidgetModel;
import ru.protei.portal.ui.common.client.widget.selector.company.CompanyModel;
import ru.protei.portal.ui.common.client.widget.selector.company.CustomerCompanyModel;
import ru.protei.portal.ui.common.client.widget.selector.company.SubcontractorCompanyModel;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.Profile;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType.fromDateRange;
import static ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType.toDateRange;
import static ru.protei.portal.ui.report.client.util.AccessUtil.availableReportTypes;
import static ru.protei.portal.ui.report.client.util.AccessUtil.canEdit;

public abstract class ReportEditActivity implements Activity,
        AbstractReportCreateEditActivity, AbstractIssueFilterModel {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        issueFilterWidget.getIssueFilterParams().setModel(this);
        issueFilterWidget.clearFooterStyle();

        projectFilterWidget.onInit(projectFilterModel);
        projectFilterWidget.clearFooterStyles();

        contractFilterView.clearFooterStyle();
        ytWorkFilterView.clearFooterStyle();
        view.fillReportScheduledTypes(asList(En_ReportScheduledType.values()));

        ytWorkFilterView.setActivity(ytWorkFilterActivity);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        issueFilterWidget.resetFilter(null);
        projectFilterWidget.resetFilter();
        contractFilterView.resetFilter();
        ytWorkFilterView.resetFilter();
        updateCompanyModels(event.profile);
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(ReportEvents.Create event) {
        if (!canEdit(policyService)) {
            fireEvent(new ErrorPageEvents.ShowForbidden(initDetails.parent));
            return;
        }
        reportDto = null;

        showView();
        resetView();
        fillSelectors();
        presetCompanyAtFilter();
        isSaving = false;
    }

    @Event(Type.FILL_CONTENT)
    public void onEdit(ReportEvents.Edit event) {
        if (!canEdit(policyService)) {
            fireEvent(new ErrorPageEvents.ShowForbidden(initDetails.parent));
            return;
        }

        if (event.reportDto != null) {
            fillEditView(event.reportDto);
        } else {
            reportController.getReport(event.reportId, new FluentCallback<ReportDto>()
                    .withError(defaultErrorHandler)
                    .withSuccess(this::fillEditView));
        }
    }

    private void fillEditView(ReportDto reportDto) {
        this.reportDto = reportDto;
        showView();
        fillView(this.reportDto);
        presetCompanyAtFilter();
        isSaving = false;
    }

    private void fillSelectors() {
        view.fillReportTypes(availableReportTypes(policyService));
    }

    private void showView() {
        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());
    }

    private void resetView() {
        view.name().setValue(null);
        view.reportType().setValue(availableReportTypes(policyService).get(0), true);
        view.reportTypeEnable().setEnabled(true);
        view.reportScheduledType().setValue(En_ReportScheduledType.NONE);
        view.additionalParams().setValue(null);
    }

    private void presetCompanyAtFilter() {
        if(!policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_VIEW)){
            if (policyService.isSubcontractorCompany()) {
                issueFilterWidget.getIssueFilterParams().presetManagerCompany(policyService.getProfile().getCompany());
            } else {
                issueFilterWidget.getIssueFilterParams().presetCompany(policyService.getProfile().getCompany());
            }
        }
    }

    @Override
    public void onSaveClicked() {
        Report report = makeReport(reportDto == null ? new Report() : reportDto.getReport());
        ReportDto reportDto = makeReportDto(report);

        if (reportDto == null || isSaving) {
            return;
        }
        isSaving = true;

        reportController.saveReport(reportDto, new FluentCallback<Long>()
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

    private void fillView(ReportDto reportDto) {
        Report report = reportDto.getReport();

        view.reportTypeEnable().setEnabled(false);
        view.reportType().setValue(report.getReportType());
        onReportTypeChanged();

        view.reportScheduledType().setValue(report.getScheduledType());
        view.name().setValue(report.getName());

        Set<En_ReportAdditionalParamType> additionalParams = new HashSet<>();
        if (report.isWithDescription()) {
            additionalParams.add(En_ReportAdditionalParamType.DESCRIPTION);
        }
        if (report.isWithTags()) {
            additionalParams.add(En_ReportAdditionalParamType.TAGS);
        }
        if (report.isWithLinkedIssues()) {
            additionalParams.add(En_ReportAdditionalParamType.LINKED_ISSUES);
        }
        if (report.isHumanReadable()) {
            additionalParams.add(En_ReportAdditionalParamType.HUMAN_READABLE);
        }
        if (report.isWithDeadlineAndWorkTrigger()) {
            additionalParams.add(En_ReportAdditionalParamType.DEADLINE_AND_WORK_TRIGGER);
        }

        BaseQuery query = reportDto.getQuery();
        switch (report.getReportType()) {
            case CASE_OBJECTS:
            case CASE_TIME_ELAPSED:
            case CASE_RESOLUTION_TIME:
            case NIGHT_WORK: {
                CaseQuery caseQuery = (CaseQuery)query;
                if (caseQuery.isCheckImportanceHistory()) {
                    additionalParams.add(En_ReportAdditionalParamType.IMPORTANCE_HISTORY);
                }
                fillFilter(caseQuery);
                break;
            }
            case PROJECT: {
                fillFilter((ProjectQuery)query);
                break;
            }
            case CONTRACT: {
                fillFilter((ContractQuery)query);
                break;
            }
            case YT_WORK:
                fillFilter((YtWorkQuery)query);
                break;
        }
        view.additionalParams().setValue(additionalParams);
    }

    private void fillFilter(CaseQuery query) {
        filterController.getSelectorsParams(query, new RequestCallback<SelectorsParams>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errNotFound(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(SelectorsParams selectorsParams) {
                issueFilterWidget.getIssueFilterParams().fillFilterFields(query, selectorsParams);
            }
        });
    }

    private void fillFilter(ProjectQuery query) {
        filterController.getSelectorsParams(query, new RequestCallback<SelectorsParams>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errNotFound(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(SelectorsParams selectorsParams) {
                projectFilterWidget.getFilterParamView().fillFilterFields(query, selectorsParams);
            }
        });
    }

    private void fillFilter(ContractQuery query) {
        contractFilterView.searchString().setValue(query.getSearchString());
        contractFilterView.sortDir().setValue(query.getSortDir() ==  En_SortDir.ASC);
        contractFilterView.sortField().setValue(query.getSortField());
        contractFilterView.contractors().setValue(
                stream(query.getContractorIds()).map(id -> {
                    Contractor contractor = new Contractor();
                    contractor.setId(id);
                    return contractor;
                }).collect(Collectors.toSet()));
        contractFilterView.curators().setValue(
                stream(query.getManagerIds()).map(PersonShortView::new).collect(Collectors.toSet()));
        contractFilterView.organizations().setValue(
                stream(query.getOrganizationIds()).map(EntityOption::new).collect(Collectors.toSet()));
        contractFilterView.managers().setValue(
                stream(query.getManagerIds()).map(PersonShortView::new).collect(Collectors.toSet()));
        contractFilterView.types().setValue(query.getTypes() == null? null : new HashSet<>(query.getTypes()));

        if (query.getCaseTagsIds() == null) {
            contractFilterView.tags().setValue(null);
        } else {
            contractFilterView.tags().setValue(
                stream(query.getCaseTagsIds()).map(id -> id.equals(CrmConstants.CaseTag.NOT_SPECIFIED) ? null : new CaseTag(id)).collect(Collectors.toSet())
            );
        }
        contractFilterView.states().setValue(query.getStates() == null ? null : new HashSet<>(query.getStates()));
        contractFilterView.direction().setValue(query.getDirectionId() == null ? null : new ProductDirectionInfo(query.getDirectionId(), ""));
        contractFilterView.kind().setValue(query.getKind());
        contractFilterView.dateSigningRange().setValue(fromDateRange(query.getDateSigningRange()));
        contractFilterView.dateValidRange().setValue(fromDateRange(query.getDateValidRange()));

        contractController.getSelectorsParams(query, new RequestCallback<SelectorsParams>() {
            @Override
            public void onError(Throwable throwable) {
                fireEvent(new NotifyEvents.Show(lang.errNotFound(), NotifyEvents.NotifyType.ERROR));
            }

            @Override
            public void onSuccess(SelectorsParams selectorsParams) {
                Set<Contractor> contractors = collectContractors(selectorsParams.getContractors(), query.getContractorIds());
                contractFilterView.contractors().setValue(contractors);
                Set<PersonShortView> curators = collectPersons(selectorsParams.getPersonShortViews(), query.getCuratorIds());
                contractFilterView.curators().setValue(curators);
                Set<EntityOption> organisations = collectCompanies(selectorsParams.getCompanyEntityOptions(), query.getOrganizationIds());
                contractFilterView.organizations().setValue(organisations);
                Set<PersonShortView> managers = collectPersons(selectorsParams.getPersonShortViews(), query.getManagerIds());
                contractFilterView.managers().setValue(managers);
                if (query.getDirectionId() != null) {
                    contractFilterView.direction().setValue(selectorsParams.getProductDirectionInfos().get(0));
                }
            }
        });
    }

    private void fillFilter(YtWorkQuery query) {
        ytWorkFilterView.date().setValue(fromDateRange(query.getDateRange()));
    }

    private Set<EntityOption> collectCompanies(Collection<EntityOption> companies, Collection<Long> companyIds) {
        return stream(companies)
                .filter(company ->
                        stream(companyIds).anyMatch(ids -> ids.equals(company.getId())))
                .collect(Collectors.toSet());
    }

    private Set<PersonShortView> collectPersons(List<PersonShortView> personShortViews, Collection<Long> personIds) {
        return stream(personShortViews)
                .filter(personShortView ->
                        stream(personIds).anyMatch(ids -> ids.equals(personShortView.getId())))
                .collect(Collectors.toSet());
    }

    private Set<Contractor> collectContractors(Collection<Contractor> contractors, Collection<Long> ids) {
        return stream(contractors)
                .filter(contractor ->
                        stream(ids).anyMatch(id -> id.equals(contractor.getId())))
                .collect(Collectors.toSet());
    }

    private Report makeReport(Report report) {
        report.setReportType(view.reportType().getValue());
        report.setScheduledType(view.reportScheduledType().getValue());
        report.setName(view.name().getValue());
        report.setLocale(LocaleInfo.getCurrentLocale().getLocaleName());
        report.setWithDescription(contains(view.additionalParams().getValue(), En_ReportAdditionalParamType.DESCRIPTION));
        report.setWithTags(contains(view.additionalParams().getValue(), En_ReportAdditionalParamType.TAGS));
        report.setWithLinkedIssues(contains(view.additionalParams().getValue(), En_ReportAdditionalParamType.LINKED_ISSUES));
        report.setHumanReadable(contains(view.additionalParams().getValue(), En_ReportAdditionalParamType.HUMAN_READABLE));
        report.setWithDeadlineAndWorkTrigger(contains(view.additionalParams().getValue(), En_ReportAdditionalParamType.DEADLINE_AND_WORK_TRIGGER));
        return report;
    }

    private ReportDto makeReportDto(Report report) {
        if (report == null || report.getReportType() == null) {
            return null;
        }
        switch (report.getReportType()) {
            case CASE_OBJECTS:
            case CASE_TIME_ELAPSED:
            case CASE_RESOLUTION_TIME:
            case NIGHT_WORK: {
                CaseQuery query = getIssueQuery();
                if (!validateCaseQuery(report.getReportType(), query)) {
                    return null;
                }
                return new ReportCaseQuery(report, query);
            }
            case PROJECT: {
                ProjectQuery query = getProjectQuery();
                if (!validateProjectQuery(query)) {
                    return null;
                }
                return new ReportProjectQuery(report, query);
            }
            case CONTRACT: {
                ContractQuery query = getContractQuery();
                return new ReportContractQuery(report, query);
            }
            case YT_WORK:
                YtWorkQuery query = getYtWorkQuery();
                return new ReportYtWorkQuery(report, query);
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
                projectFilterWidget.resetFilter();
                view.reportScheduledType().setValue(En_ReportScheduledType.NONE);
                view.getFilterContainer().clear();
                view.getFilterContainer().add(projectFilterWidget.asWidget());
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
            case CASE_RESOLUTION_TIME:
            case NIGHT_WORK: {
                view.reportScheduledType().setValue(En_ReportScheduledType.NONE);
                view.scheduledTypeContainerVisibility().setVisible(isScheduledEnabled(reportType));
                view.additionalParamsVisibility().setVisible(reportType == En_ReportType.CASE_OBJECTS);
                view.additionalParams().setValue(null);
                issueFilterWidget.updateFilterType(En_CaseFilterType.valueOf(reportType.name()));
                validateDateRanges(reportType);
                applyIssueFilterVisibilityByPrivileges();
                view.getFilterContainer().clear();
                view.getFilterContainer().add(issueFilterWidget.asWidget());
                break;
            }
            case YT_WORK:
                ytWorkFilterView.resetFilter();
                view.reportScheduledType().setValue(En_ReportScheduledType.NONE);
                view.getFilterContainer().clear();
                view.getFilterContainer().add(ytWorkFilterView.asWidget());
                view.scheduledTypeContainerVisibility().setVisible(false);
                view.additionalParamsVisibility().setVisible(false);
                view.additionalParams().setValue(null);
        }
    }

    private boolean isScheduledEnabled(En_ReportType reportType) {
        switch (reportType) {
            case CASE_OBJECTS:
            case CASE_TIME_ELAPSED:
            case NIGHT_WORK:
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onUserFilterChanged() {
        // почти ничего не делаем, только валидацию dateRange, мы используем фильтр при создании отчета
        validateDateRanges(view.reportType().getValue());
    }

    @Override
    public void onPlanPresent(boolean isPresent) {
        if (isPresent) {
            issueFilterWidget.getIssueFilterParams().sortField().setValue(En_SortField.by_plan);
            issueFilterWidget.getIssueFilterParams().sortDir().setValue(true);
            issueFilterWidget.getIssueFilterParams().resetRanges();
            fireEvent(new NotifyEvents.Show(lang.reportCaseObjectPlanInfo(), NotifyEvents.NotifyType.INFO));
        } else {
            issueFilterWidget.getIssueFilterParams().sortField().setValue(En_SortField.issue_number);
            issueFilterWidget.getIssueFilterParams().sortDir().setValue(false);
        }
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
            case CASE_TIME_ELAPSED:
            case NIGHT_WORK:
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
                if (!query.isAnySelectedParamPresent()) {
                    fireEvent(new NotifyEvents.Show(lang.reportCaseObjectIsAnySelectedParamNotPresentError(), NotifyEvents.NotifyType.ERROR));
                    return false;
                }
                if (!isLimitCaseQuery(query)) {
                    fireEvent(new NotifyEvents.Show(lang.reportCaseObjectAdditionalLimitError(), NotifyEvents.NotifyType.ERROR));
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
    
    private boolean isLimitCaseQuery(CaseQuery query) {
        return query.isUnLimitSelectedParamsPresent() ||
                isLimitByCustomerCompany(query) ||
                isLimitByState(query) ||
                isLimitByImportance(query);
    }

    private boolean isLimitByCustomerCompany(CaseQuery query) {
        if (isEmpty(query.getManagerCompanyIds())) {
            return false;
        }
        boolean selectNotOnlyHomeCompany = query.getManagerCompanyIds().size() > 1
                || query.getManagerCompanyIds().get(0) != CrmConstants.Company.HOME_COMPANY_ID;

        return selectNotOnlyHomeCompany;
    }

    private boolean isLimitByState(CaseQuery query) {
        if (isEmpty(query.getStateIds())) {
            return false;
        }
        boolean selectNotAllState = query.getStateIds().size() < issueFilterWidget.getIssueFilterParams().statesSize();
        boolean selectNotOnlyTerminate = !query.getStateIds().stream().allMatch(CaseStateUtil::isTerminalState);
        return selectNotAllState && selectNotOnlyTerminate;
    }

    private boolean isLimitByImportance(CaseQuery query) {
        if (isEmpty(query.getImportanceIds())) {
            return false;
        }
        boolean selectNotAllImportance = query.getImportanceIds().size() < issueFilterWidget.getIssueFilterParams().importanceSize();
        return selectNotAllImportance;
    }

    private boolean validateProjectQuery(ProjectQuery query) {
        if (query == null) {
            return false;
        }

        if (!query.isParamsPresent()) {
            fireEvent(new NotifyEvents.Show(lang.reportCaseObjectIsAnySelectedParamNotPresentError(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        boolean dateRangeValid = validateProjectCommentCreationRange(query.getCommentCreationRange(), false);
        if (!dateRangeValid) {
            fireEvent(new NotifyEvents.Show(lang.reportNotValidPeriod(), NotifyEvents.NotifyType.ERROR));
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

    private boolean validateProjectCommentCreationRange(DateRange dateRange, boolean isMandatory) {
        boolean typeValid = validateTypeRange(dateRange, isMandatory);
        boolean rangeValid = typeValid ? validateDateRange(dateRange) : true;

        validateProjectCommentCreation(typeValid, rangeValid);
        return typeValid && rangeValid;
    }

    private void validateCreatedRange(boolean isTypeValid, boolean isRangeValid) {
        issueFilterWidget.getIssueFilterParams().setCreatedRangeValid(isTypeValid, isRangeValid);
    }

    private void validateModifiedRange(boolean isTypeValid, boolean isRangeValid) {
        issueFilterWidget.getIssueFilterParams().setModifiedRangeValid(isTypeValid, isRangeValid);
    }

    private void validateProjectCommentCreation(boolean isTypeValid, boolean isRangeValid) {
        projectFilterWidget.getFilterParamView().setCommentCreationRangeValid(isTypeValid, isRangeValid);
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
        return projectFilterWidget.getFilterParamView().getQuery();
    }
    
    private ContractQuery getContractQuery() {
        ContractQuery query = new ContractQuery();
        query.setSearchString(contractFilterView.searchString().getValue());
        query.setSortDir(contractFilterView.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC);
        query.setSortField(contractFilterView.sortField().getValue());
        query.setContractorIds(collectIds(contractFilterView.contractors().getValue()));
        query.setCuratorIds(collectIds(contractFilterView.curators().getValue()));
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

    private YtWorkQuery getYtWorkQuery() {
        YtWorkQuery query = new YtWorkQuery();
        query.setDateRange(toDateRange(ytWorkFilterView.date().getValue()));
        return query;
    }

    private void updateCompanyModels(Profile profile) {
        Company userCompany = profile.getCompany();
        subcontractorCompanyModel.setCompanyId(userCompany.getId());
        subcontractorCompanyModel.setActive(false);
        customerCompanyModel.setSubcontractorId(userCompany.getId());
        customerCompanyModel.setActive(false);

        issueFilterWidget.setInitiatorCompaniesModel(isSubcontractorCompany(userCompany) ? customerCompanyModel : companyModel);
        issueFilterWidget.setManagerCompaniesModel(profile.hasSystemScopeForPrivilege(En_Privilege.ISSUE_VIEW) || isSubcontractorCompany(userCompany) ? companyModel : subcontractorCompanyModel);
    }

    private boolean isSubcontractorCompany(Company userCompany) {
        return userCompany.getCategory() == En_CompanyCategory.SUBCONTRACTOR;
    }

    @Inject
    Lang lang;
    @Inject
    AbstractReportEditView view;
    @Inject
    ReportControllerAsync reportController;
    @Inject
    PolicyService policyService;
    @Inject
    DefaultErrorHandler defaultErrorHandler;

    @Inject
    IssueFilterWidget issueFilterWidget;
    @Inject
    ProjectFilterWidget projectFilterWidget;
    @Inject
    ProjectFilterWidgetModel projectFilterModel;
    @Inject
    AbstractContractFilterView contractFilterView;
    @Inject
    AbstractYoutrackWorkFilterView ytWorkFilterView;
    @Inject
    AbstractYoutrackWorkFilterActivity ytWorkFilterActivity;

    @Inject
    CompanyModel companyModel;
    @Inject
    CustomerCompanyModel customerCompanyModel;
    @Inject
    SubcontractorCompanyModel subcontractorCompanyModel;
    @Inject
    CaseFilterControllerAsync filterController;
    @Inject
    ContractControllerAsync contractController;

    private boolean isSaving;
    private AppEvents.InitDetails initDetails;
    private ReportDto reportDto;

    static public int LITTLE_OVER_YEAR_DAYS = 370;
}
