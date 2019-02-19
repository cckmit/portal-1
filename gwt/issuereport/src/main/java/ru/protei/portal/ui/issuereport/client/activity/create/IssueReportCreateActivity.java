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
import ru.protei.portal.ui.common.client.widget.report.timeresolution.AbstractCaseCompletionTimeReportView;
import ru.protei.portal.ui.common.client.widget.report.caseobjects.AbstractCaseObjectsReportView;
import ru.protei.portal.ui.common.client.widget.report.timeelapsed.AbstractTimeElapsedReportView;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static ru.protei.portal.core.model.helper.StringUtils.isBlank;
import static ru.protei.portal.ui.common.client.util.IssueFilterUtils.*;

public abstract class IssueReportCreateActivity implements Activity,
        AbstractIssueReportCreateActivity, AbstractDialogDetailsActivity, AbstractIssueFilterParamActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        filterWidgetView.setActivity(this);
        caseResolutionTimeReportView.setActivity( this );
        timeElapsedReportView.setActivity( this );
        dialogView.setActivity(this);
        dialogView.setHeader(lang.issueReportNew());
        dialogView.getBodyContainer().add(view.asWidget());
        filterWidgetView.setInitiatorCompaniesSupplier(() -> filterWidgetView.companies().getValue());
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
                view.getReportContainer().add(filterWidgetView.asWidget());
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
        filterWidgetView.toggleMsgSearchThreshold();
    }

    @Override
    public void onCompaniesFilterChanged() {
        onFilterChanged();
        filterWidgetView.updateInitiators();
    }

    @Override
    public void onUserFilterChanged() {

        CaseFilterShortView filter = view.userFilter().getValue();
        if (filter == null || filter.getId() == null) {
            resetFilters();
            filterWidgetView.toggleMsgSearchThreshold();
            return;
        }

        filterController.getIssueFilter(filter.getId(), new FluentCallback<CaseFilter>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errNotFound(), NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess( this::fillFilterFields )
        );
    }

    private void fillFilterFields( CaseFilter caseFilter ) {
        switch (view.reportType().getValue()) {
            case CASE_OBJECTS:
                filterWidgetView.fillFilterFields(caseFilter.getParams());
                filterWidgetView.toggleMsgSearchThreshold();
                break;
            case CASE_TIME_ELAPSED:
                timeElapsedReportView.fillFilterFields(caseFilter.getParams());
                timeElapsedReportView.toggleMsgSearchThreshold();
                break;
            case CASE_RESOLUTION_TIME:
                caseResolutionTimeReportView.fillFilterFields(caseFilter.getParams());
                break;
        }
    }

    private void resetFilters() {
        filterWidgetView.resetFilter();
        filterWidgetView.commentAuthorsVisibility().setVisible(false);
        timeElapsedReportView.resetFilter();
        caseResolutionTimeReportView.resetFilter();
        caseResolutionTimeReportView.states().setValue( activeStates );
    }

    private CaseQuery makeQuery( En_ReportType reportType ) {
        if (En_ReportType.CASE_OBJECTS.equals( reportType )) {
            return makeCaseObjectsQuery(filterWidgetView, true);
        }

        if (En_ReportType.CASE_RESOLUTION_TIME.equals( reportType )) {
            return makeTimeResolutionQuery();
        }

        return makeTimeElapsedQuery();
    }

    public static CaseQuery makeCaseObjectsQuery( AbstractCaseObjectsReportView filterWidgetView, boolean isFillSearchString) {
        CaseQuery query = new CaseQuery();
        query.setType(En_CaseType.CRM_SUPPORT);
        if (isFillSearchString) {
            String searchString = filterWidgetView.searchPattern().getValue();
            query.setCaseNumbers( searchCaseNumber( searchString, filterWidgetView.searchByComments().getValue() ) );
            if (query.getCaseNumbers() == null) {
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
        DateInterval interval = filterWidgetView.dateRange().getValue();
        if (interval != null) {
            query.setFrom(interval.from);
            query.setTo(interval.to);
        }
        return query;
    }

    private CaseQuery makeTimeElapsedQuery() {
        CaseQuery query = new CaseQuery();
        query.setType( En_CaseType.CRM_SUPPORT );

        String searchString = filterWidgetView.searchPattern().getValue();
        query.setCaseNumbers( searchCaseNumber( searchString, filterWidgetView.searchByComments().getValue() ) );
        if (query.getCaseNumbers() == null) {
            query.setSearchString( isBlank( searchString ) ? null : searchString );
        }

        query.setCompanyIds( getCompaniesIdList( timeElapsedReportView.companies().getValue() ) );
        query.setProductIds( getProductsIdList( timeElapsedReportView.products().getValue() ) );
        query.setManagerIds( getManagersIdList( timeElapsedReportView.managers().getValue() ) );
        query.setCommentAuthorIds( getManagersIdList( timeElapsedReportView.commentAuthors().getValue() ) );

        DateInterval interval = timeElapsedReportView.dateRange().getValue();
        if (interval != null) {
            query.setFrom( interval.from );
            query.setTo( interval.to );
        }
        return query;
    }

    private CaseQuery makeTimeResolutionQuery() {
        CaseQuery query = new CaseQuery();
        ProductShortView product = caseResolutionTimeReportView.products().getValue();
        if (product == null || product.getId() == null) {
            fireEvent( new NotifyEvents.Show( lang.reportMissingProduct(), NotifyEvents.NotifyType.ERROR ) );
            return null;
        }
        query.setProductIds( Arrays.asList( product.getId() ) );
        query.setStates( IssueFilterUtils.getStateList( caseResolutionTimeReportView.states().getValue() ) );
        DateInterval interval = caseResolutionTimeReportView.dateRange().getValue();
        if (interval == null) {
            fireEvent( new NotifyEvents.Show( lang.reportMissingPeriod(), NotifyEvents.NotifyType.ERROR ) );
            return null;
        }
        query.setFrom( interval.from );
        query.setTo( interval.to );
        return query;
    }

    private void applyFilterViewPrivileges() {
        filterWidgetView.companiesVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_COMPANY_VIEW));
        filterWidgetView.productsVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_PRODUCT_VIEW));
        filterWidgetView.managersVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_MANAGER_VIEW));
        filterWidgetView.searchPrivateVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_PRIVACY_VIEW));

        timeElapsedReportView.companiesVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_COMPANY_VIEW));
        timeElapsedReportView.productsVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_PRODUCT_VIEW));
        timeElapsedReportView.managersVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_MANAGER_VIEW));
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
    AbstractCaseCompletionTimeReportView caseResolutionTimeReportView;
    @Inject
    AbstractTimeElapsedReportView timeElapsedReportView;

    @Inject
    AbstractCaseObjectsReportView filterWidgetView;

    private boolean isSaving;
    private Set<En_CaseState> activeStates = new HashSet<>( Arrays.asList( En_CaseState.CREATED, En_CaseState.OPENED,
            En_CaseState.ACTIVE, En_CaseState.TEST_LOCAL, En_CaseState.WORKAROUND ) );
}
