package ru.protei.portal.ui.issuereport.client.activity.create;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.IssueReportEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IssueFilterControllerAsync;
import ru.protei.portal.ui.common.client.service.ReportControllerAsync;
import ru.protei.portal.ui.common.client.util.IssueFilterUtils;
import ru.protei.portal.ui.common.client.activity.issuefilter.AbstractIssueFilterParamActivity;
import ru.protei.portal.ui.common.client.view.report.timeresolution.AbstractResolutionTimeReportView;
import ru.protei.portal.ui.common.client.view.report.caseobjects.AbstractCaseObjectsReportView;
import ru.protei.portal.ui.common.client.view.report.timeelapsed.AbstractTimeElapsedReportView;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.StringUtils.isBlank;
import static ru.protei.portal.ui.common.client.util.IssueFilterUtils.*;

public abstract class IssueReportCreateActivity implements Activity,
        AbstractIssueReportCreateActivity, AbstractDialogDetailsActivity, AbstractIssueFilterParamActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        caseObjectReportView.setActivity(this);
        caseResolutionTimeReportView.setActivity( this );
        timeElapsedReportView.setActivity( this );
        dialogView.setActivity(this);
        dialogView.setHeader(lang.issueReportNew());
        dialogView.getBodyContainer().add(view.asWidget());
        caseObjectReportView.setInitiatorCompaniesSupplier(() -> caseObjectReportView.companies().getValue());
    }

    @Event
    public void onShow(IssueReportEvents.Create event) {
        isSaving = false;
        view.resetFilter();
        resetFilters();
        applyFilterViewPrivileges();
        dialogView.showPopup();
    }

    @Override
    public void onSaveClicked() {

        En_ReportType reportType = view.reportType().getValue();
        Report report = new Report();
        report.setReportType(reportType);
        report.setName(view.name().getValue());
        report.setLocale(LocaleInfo.getCurrentLocale().getLocaleName());
        CaseQuery query = makeQuery(reportType);
        if (query == null) return;
        report.setCaseQuery( query );

        if (isSaving) {
            return;
        }
        isSaving = true;

        reportController.createReport(report, new FluentCallback<Long>()
                .withResult(() -> isSaving = false)
                .withSuccess(result -> {
                    dialogView.hidePopup();
                    fireEvent(new NotifyEvents.Show(lang.reportRequested(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new IssueReportEvents.Show());
                })
        );
    }

    @Override
    public void onCancelClicked() {
        dialogView.hidePopup();
    }

    @Override
    public void onReportTypeSelected() {
        if (view.reportType().getValue() == null) {
            return;
        }

        view.getReportContainer().clear();
        switch (view.reportType().getValue()) {
            case CASE_OBJECTS: {
                view.getReportContainer().add( caseObjectReportView.asWidget());
                break;
            }
            case CASE_TIME_ELAPSED: {
                view.getReportContainer().add(timeElapsedReportView.asWidget());
                break;
            }
            case CASE_RESOLUTION_TIME:
                view.getReportContainer().add( caseResolutionTimeReportView.asWidget());
                break;
        }
    }

    @Override
    public void onFilterChanged() {
        caseObjectReportView.toggleMsgSearchThreshold();
    }

    @Override
    public void onCompaniesFilterChanged() {
        onFilterChanged();
        caseObjectReportView.updateInitiators();
    }

    @Override
    public void onUserFilterChanged() {

        CaseFilterShortView filter = caseObjectReportView.userFilter().getValue();
        if (filter == null || filter.getId() == null) {
            caseObjectReportView.resetFilter();
            caseObjectReportView.toggleMsgSearchThreshold();
            return;
        }

        filterController.getIssueFilter(filter.getId(), new FluentCallback<CaseFilter>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errNotFound(), NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess( caseFilter ->{
                    caseObjectReportView.fillFilterFields(caseFilter.getParams());
                    caseObjectReportView.toggleMsgSearchThreshold();
                })
        );
    }

    private void resetFilters() {
        caseObjectReportView.resetFilter();
        caseObjectReportView.commentAuthorsVisibility().setVisible(false);
        timeElapsedReportView.resetFilter();
        caseResolutionTimeReportView.resetFilter();
        caseResolutionTimeReportView.states().setValue( activeStates );
    }

    private CaseQuery makeQuery( En_ReportType reportType ) {
        if (En_ReportType.CASE_OBJECTS.equals( reportType )) {
            return makeCaseObjectsQuery( caseObjectReportView, true);
        }

        if (En_ReportType.CASE_RESOLUTION_TIME.equals( reportType )) {
            return makeTimeResolutionQuery();
        }

        return makeTimeElapsedQuery();
    }

    private CaseQuery makeCaseObjectsQuery( AbstractCaseObjectsReportView filterWidgetView, boolean isFillSearchString) {
        CaseQuery query = new CaseQuery();
        query.setType(En_CaseType.CRM_SUPPORT);
        if (isFillSearchString) {
            String searchString = filterWidgetView.searchPattern().getValue();
            query.setCaseNumbers( searchCaseNumber( searchString, filterWidgetView.searchByComments().getValue() ) );
            if (query.getCaseNumbers() == null) {
                query.setSearchStringAtComments(filterWidgetView.searchByComments().getValue());
                query.setSearchString( isBlank( searchString ) ? null : searchString );
            }
        }
        query.setViewPrivate(filterWidgetView.searchPrivate().getValue());
        query.setSortField(filterWidgetView.sortField().getValue());
        query.setSortDir(filterWidgetView.sortDir().getValue() ? En_SortDir.ASC : En_SortDir.DESC);
        query.setCompanyIds(getCompaniesIdList(filterWidgetView.companies().getValue()));
        query.setProductIds(getProductsIdList(filterWidgetView.products().getValue()));
        query.setManagerIds(getManagersIdList(filterWidgetView.managers().getValue()));
        query.setInitiatorIds(getManagersIdList(filterWidgetView.initiators().getValue()));
        query.setImportanceIds(getImportancesIdList(filterWidgetView.importances().getValue()));
        query.setStates(getStateList(filterWidgetView.states().getValue()));
        query.setCommentAuthorIds(getManagersIdList(filterWidgetView.commentAuthors().getValue()));
        query.setCaseTagsIds(getIds(filterWidgetView.tags().getValue()));

        query = IssueFilterUtils.fillCreatedInterval(query, filterWidgetView.dateCreatedRange().getValue() );
        query = IssueFilterUtils.fillModifiedInterval( query, filterWidgetView.dateModifiedRange().getValue() );

        return query;
    }

    private CaseQuery makeTimeElapsedQuery() {
        CaseQuery query = new CaseQuery();
        query.setType( En_CaseType.CRM_SUPPORT );

        query.setCompanyIds( getCompaniesIdList( timeElapsedReportView.companies().getValue() ) );
        query.setProductIds( getProductsIdList( timeElapsedReportView.products().getValue() ) );
        query.setCommentAuthorIds( getManagersIdList( timeElapsedReportView.commentAuthors().getValue() ) );
        query = IssueFilterUtils.fillCreatedInterval( query, timeElapsedReportView.dateRange().getValue() );

        return query;
    }

    private CaseQuery makeTimeResolutionQuery() {
        CaseQuery query = new CaseQuery();

        query.setCompanyIds( caseResolutionTimeReportView.companies().getValue().stream().map(EntityOption::getId).collect(Collectors.toList()) );
        query.setProductIds( caseResolutionTimeReportView.products().getValue().stream().map(ProductShortView::getId).collect(Collectors.toList()) );
        query.setManagerIds( getManagersIdList(caseResolutionTimeReportView.managers().getValue()) );
        query.setImportanceIds( getImportancesIdList(caseResolutionTimeReportView.importances().getValue()) );

        query.setStates( IssueFilterUtils.getStateList( caseResolutionTimeReportView.states().getValue() ) );
        DateInterval interval = caseResolutionTimeReportView.dateRange().getValue();
        if (interval == null) {
            fireEvent( new NotifyEvents.Show( lang.reportMissingPeriod(), NotifyEvents.NotifyType.ERROR ) );
            return null;
        }
        query.setCreatedFrom( interval.from );
        query.setCreatedTo( interval.to );
        return query;
    }

    private void applyFilterViewPrivileges() {
        caseObjectReportView.companiesVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_COMPANY_VIEW));
        caseObjectReportView.productsVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_PRODUCT_VIEW));
        caseObjectReportView.managersVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_MANAGER_VIEW));
        caseObjectReportView.searchPrivateVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_PRIVACY_VIEW));
        caseObjectReportView.tagsVisibility().setVisible(policyService.hasGrantAccessFor(En_Privilege.ISSUE_VIEW));

        timeElapsedReportView.companiesVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_COMPANY_VIEW));
        timeElapsedReportView.productsVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_PRODUCT_VIEW));
    }

    @Inject
    Lang lang;
    @Inject
    AbstractIssueReportCreateView view;
    @Inject
    AbstractDialogDetailsView dialogView;
    @Inject
    ReportControllerAsync reportController;
    @Inject
    IssueFilterControllerAsync filterController;
    @Inject
    PolicyService policyService;
    @Inject
    AbstractResolutionTimeReportView caseResolutionTimeReportView;
    @Inject
    AbstractTimeElapsedReportView timeElapsedReportView;

    @Inject
    AbstractCaseObjectsReportView caseObjectReportView;

    private boolean isSaving;
    private Set<En_CaseState> activeStates = new HashSet<>( Arrays.asList( En_CaseState.CREATED, En_CaseState.OPENED,
            En_CaseState.ACTIVE, En_CaseState.TEST_LOCAL, En_CaseState.WORKAROUND ) );
}
