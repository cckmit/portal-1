package ru.protei.portal.ui.issuereport.client.activity.create;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.IssueReportEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IssueFilterControllerAsync;
import ru.protei.portal.ui.common.client.service.ReportControllerAsync;
import ru.protei.portal.ui.common.client.util.IssueFilterUtils;
import ru.protei.portal.ui.common.client.activity.issuefilter.AbstractIssueFilterParamActivity;
import ru.protei.portal.ui.common.client.view.report.timeresolution.AbstractResolutionTimeReportView;
import ru.protei.portal.ui.common.client.view.report.timeelapsed.AbstractTimeElapsedReportView;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.*;
import java.util.List;

import static ru.protei.portal.ui.common.client.util.IssueFilterUtils.*;

public abstract class IssueReportCreateActivity implements Activity,
        AbstractIssueReportCreateActivity, AbstractDialogDetailsActivity, AbstractIssueFilterParamActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        caseResolutionTimeReportView.setActivity( this );
        timeElapsedReportView.setActivity( this );
        dialogView.setActivity(this);
        dialogView.setHeader(lang.issueReportNew());
        dialogView.getBodyContainer().add(view.asWidget());
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        view.fillReportTypes(makeReportTypeList());
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
        CaseQuery query = view.getIssueFilter().getValue();
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

        switch (view.reportType().getValue()) {
            case CASE_OBJECTS: {
//                view.getReportContainer().add(issueFilterWidgetView.asWidget());
//                view.updateFilterType(En_CaseFilterType.CASE_OBJECTS);
                break;
            }
            case CASE_TIME_ELAPSED: {
//                view.getReportContainer().add(timeElapsedReportView.asWidget());
//                view.updateFilterType(En_CaseFilterType.CASE_TIME_ELAPSED);
                break;
            }
            case CASE_RESOLUTION_TIME:
//                view.getReportContainer().add(caseResolutionTimeReportView.asWidget());
//                view.updateFilterType(En_CaseFilterType.CASE_RESOLUTION_TIME);
                break;
        }
        applyFilterViewPrivileges();
    }

    @Override
    public void onFilterChanged() {
        //issueFilterWidgetView.toggleMsgSearchThreshold();
    }

    @Override
    public void onCompaniesFilterChanged() {
        onFilterChanged();
        //issueFilterWidgetView.updateInitiators();
    }

    @Override
    public void onUserFilterChanged() {

/*
        CaseFilterShortView filter = issueFilterWidgetView.userFilter().getValue();
        if (filter == null || filter.getId() == null) {
            issueFilterWidgetView.resetFilter();
            issueFilterWidgetView.toggleMsgSearchThreshold();
            return;
        }

        filterController.getIssueFilter(filter.getId(), new FluentCallback<CaseFilter>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errNotFound(), NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess( caseFilter ->{
                    issueFilterWidgetView.fillFilterFields(caseFilter.getParams());
                    issueFilterWidgetView.toggleMsgSearchThreshold();
                })
        );
*/
    }

    private void resetFilters() {
/*
        issueFilterWidgetView.resetFilter();
        issueFilterWidgetView.commentAuthorsVisibility().setVisible(false);
*/
        timeElapsedReportView.resetFilter();
        caseResolutionTimeReportView.resetFilter();
        caseResolutionTimeReportView.states().setValue( activeStates );
    }

    private CaseQuery makeQuery( En_ReportType reportType ) {
/*
        if (En_ReportType.CASE_OBJECTS.equals( reportType )) {
            return makeCaseObjectsQuery( issueFilterWidgetView, true);
        }
*/

        if (En_ReportType.CASE_RESOLUTION_TIME.equals( reportType )) {
            CaseQuery query = makeTimeResolutionQuery();
            return validateTimeResolutionQuery(query) ? query : null;
        }

        return makeTimeElapsedQuery();
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

        query.setCompanyIds( getCompaniesIdList( caseResolutionTimeReportView.companies().getValue() ) );
        query.setProductIds( getProductsIdList( caseResolutionTimeReportView.products().getValue() ) );
        query.setManagerIds( getManagersIdList( caseResolutionTimeReportView.managers().getValue()) );
        query.setCaseTagsIds( getIds( caseResolutionTimeReportView.tags().getValue()) );
        query.setImportanceIds( getImportancesIdList( caseResolutionTimeReportView.importances().getValue()) );

        query.setStates( IssueFilterUtils.getStateList( caseResolutionTimeReportView.states().getValue() ) );

        DateInterval interval = caseResolutionTimeReportView.dateRange().getValue();
        query.setCreatedFrom( interval.from );
        query.setCreatedTo( interval.to );

        return query;
    }
    private boolean validateTimeResolutionQuery(CaseQuery query){
        if (query.getCreatedFrom() == null || query.getCreatedTo() == null)  {
            fireEvent( new NotifyEvents.Show( lang.reportMissingPeriod(), NotifyEvents.NotifyType.ERROR ) );
            return false;
        }
        if (query.getStateIds() == null)  {
            fireEvent( new NotifyEvents.Show( lang.reportMissingState(), NotifyEvents.NotifyType.ERROR ) );
            return false;
        }
        return true;
    }

    private void applyFilterViewPrivileges() {
        view.companiesVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_COMPANY_VIEW));
        view.productsVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_PRODUCT_VIEW));
        view.managersVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_MANAGER_VIEW));
        view.searchPrivateVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_PRIVACY_VIEW));

        timeElapsedReportView.companiesVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_COMPANY_VIEW));
        timeElapsedReportView.productsVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_PRODUCT_VIEW));
    }

    private List<En_ReportType> makeReportTypeList() {
        if (policyService.hasGrantAccessFor(En_Privilege.ISSUE_REPORT)) {
            return Arrays.asList(En_ReportType.values());
        }
        return Arrays.asList(En_ReportType.CASE_OBJECTS);
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

    private boolean isSaving;
    private Set<En_CaseState> activeStates = new HashSet<>( Arrays.asList( En_CaseState.CREATED, En_CaseState.OPENED,
            En_CaseState.ACTIVE, En_CaseState.TEST_LOCAL, En_CaseState.WORKAROUND ) );
}
