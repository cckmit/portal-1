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
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IssueFilterControllerAsync;
import ru.protei.portal.ui.common.client.service.ReportControllerAsync;
import ru.protei.portal.ui.common.client.util.IssueFilterUtils;
import ru.protei.portal.ui.common.client.view.report.timeresolution.AbstractResolutionTimeReportView;
import ru.protei.portal.ui.common.client.view.report.timeelapsed.AbstractTimeElapsedReportView;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.issuereport.client.widget.issuefilter.model.AbstractIssueFilterActivity;

import java.util.*;
import java.util.List;
import java.util.function.Consumer;

import static ru.protei.portal.ui.common.client.util.IssueFilterUtils.*;

public abstract class IssueReportCreateActivity implements Activity,
        AbstractIssueReportCreateActivity, AbstractDialogDetailsActivity, AbstractIssueFilterActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        view.getIssueFilter().setActivity(this);
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
        applyFilterViewPrivileges();
        dialogView.showPopup();
    }

    @Event
    public void onConfirmRemove(ConfirmDialogEvents.Confirm event) {

        if (!event.identity.equals(getClass().getName()) || filterIdToRemove == null) {
            return;
        }

        filterService.removeIssueFilter(filterIdToRemove, new FluentCallback<Boolean>()
                .withError(throwable -> {
                    filterIdToRemove = null;
                    fireEvent(new NotifyEvents.Show(lang.errNotRemoved(), NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess(aBoolean -> {
                    filterIdToRemove = null;
                    fireEvent(new NotifyEvents.Show(lang.issueFilterRemoveSuccessed(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new IssueEvents.ChangeUserFilterModel());
                    view.getIssueFilter().resetFilter();
                }));
    }

    @Event
    public void onCancelRemove( ConfirmDialogEvents.Cancel event ) {
        if (!event.identity.equals(getClass().getName())) {
            return;
        }
        filterIdToRemove = null;
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
    public void onUserFilterChanged(Long id, Consumer<CaseFilter> consumer) {

        filterService.getIssueFilter(id, new FluentCallback<CaseFilter>()
                .withErrorMessage(lang.errNotFound())
                .withSuccess(caseFilter ->
                    consumer.accept(caseFilter)));
    }

    @Override
    public void onSaveFilterClicked(CaseFilter caseFilter, Consumer<CaseFilterShortView> consumer) {

        filterService.saveIssueFilter(caseFilter, new FluentCallback<CaseFilter>()
                .withErrorMessage(lang.errSaveIssueFilter())
                .withSuccess(filter -> {
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new IssueEvents.ChangeUserFilterModel());
                    consumer.accept(filter.toShortView());
                }));
    }

    @Override
    public void onRemoveFilterClicked(Long id) {
        filterIdToRemove = id;
        fireEvent(new ConfirmDialogEvents.Show(getClass().getName(), lang.issueFilterRemoveConfirmMessage()));
    }

/*
    @Override
    public void onCompaniesFilterChanged() {
        onFilterChanged();
        updateInitiatorSelector();
    }
*/

/*
    @Override
    public void onFilterChanged() {
        issueFilterWidgetView.toggleMsgSearchThreshold();
    }
*/

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
        view.getIssueFilter().companiesVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_COMPANY_VIEW));
        view.getIssueFilter().productsVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_PRODUCT_VIEW));
        view.getIssueFilter().managersVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_MANAGER_VIEW));
        view.getIssueFilter().searchPrivateVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_PRIVACY_VIEW));

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
    PolicyService policyService;
    @Inject
    AbstractResolutionTimeReportView caseResolutionTimeReportView;
    @Inject
    AbstractTimeElapsedReportView timeElapsedReportView;

    @Inject
    IssueFilterControllerAsync filterService;

    private Long filterIdToRemove;
    private boolean isSaving;
    private Set<En_CaseState> activeStates = new HashSet<>( Arrays.asList( En_CaseState.CREATED, En_CaseState.OPENED,
            En_CaseState.ACTIVE, En_CaseState.TEST_LOCAL, En_CaseState.WORKAROUND ) );
}
