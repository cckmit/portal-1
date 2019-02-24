package ru.protei.portal.ui.issuereport.client.activity.create;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ReportType;
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
import ru.protei.portal.ui.common.client.activity.issuefilter.AbstractIssueFilterWidgetView;
import ru.protei.portal.ui.common.client.widget.report.AbstractCaseCompletionTimeReportView;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static ru.protei.portal.core.model.dict.En_ReportType.CASE_RESOLUTION_TIME;

public abstract class IssueReportCreateActivity implements Activity,
        AbstractIssueReportCreateActivity, AbstractDialogDetailsActivity, AbstractIssueFilterParamActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        view.getIssueFilterWidget().setActivity(this);
        dialogView.setActivity(this);
        dialogView.setHeader(lang.issueReportNew());
        dialogView.getBodyContainer().add(view.asWidget());
        filterWidgetView = view.getIssueFilterWidget();
        filterWidgetView.setInitiatorCompaniesSupplier(() -> filterWidgetView.companies().getValue());
    }

    @Event
    public void onShow(IssueReportEvents.Create event) {
        isSaving = false;
        view.resetFilter();
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
        switch (view.reportType().getValue()) {
            case CASE_OBJECTS: {
                view.getReportContainer().clear();
                view.filterWidgetView().setVisible( true );
                filterWidgetView.commentAuthorsVisibility().setVisible(false);
                filterWidgetView.searchByCommentsVisibility().setVisible(true);
                break;
            }
            case CASE_TIME_ELAPSED: {
                view.getReportContainer().clear();
                view.filterWidgetView().setVisible( true );
                filterWidgetView.commentAuthorsVisibility().setVisible(true);
                filterWidgetView.searchByCommentsVisibility().setVisible(false);
                break;
            }
            case CASE_RESOLUTION_TIME:
                filterWidgetView.commentAuthorsVisibility().setVisible(false);
                filterWidgetView.searchByCommentsVisibility().setVisible(false);
                view.filterWidgetView().setVisible( false );
                view.getReportContainer().clear();
                caseCompletionTimeReportView.states().setValue( activeStates );
                caseCompletionTimeReportView.products().setValue( null );
                caseCompletionTimeReportView.dateRange().setValue( null );
                view.getReportContainer().add(caseCompletionTimeReportView.asWidget());
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

        CaseFilterShortView filter = filterWidgetView.userFilter().getValue();
        if (filter == null || filter.getId() == null) {
            view.resetFilter();
            filterWidgetView.toggleMsgSearchThreshold();
            return;
        }

        filterController.getIssueFilter(filter.getId(), new FluentCallback<CaseFilter>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errNotFound(), NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess(caseFilter -> {
                    filterWidgetView.fillFilterFields(caseFilter.getParams());
                    filterWidgetView.toggleMsgSearchThreshold();
                })
        );
    }

    private CaseQuery makeQuery( En_ReportType reportType ) {
        if (!CASE_RESOLUTION_TIME.equals( reportType )) {
            return IssueFilterUtils.makeCaseQuery(filterWidgetView, true);
        }

        CaseQuery query = new CaseQuery();
        ProductShortView product = caseCompletionTimeReportView.products().getValue();
        if (product == null || product.getId() == null) {
            fireEvent( new NotifyEvents.Show( lang.reportMissingProduct(), NotifyEvents.NotifyType.ERROR ) );
            return null;
        }
        query.setProductIds( Arrays.asList( product.getId() ) );
        query.setStates( IssueFilterUtils.getStateList( caseCompletionTimeReportView.states().getValue() ) );
        DateInterval interval = caseCompletionTimeReportView.dateRange().getValue();
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
    AbstractCaseCompletionTimeReportView caseCompletionTimeReportView;

    private AbstractIssueFilterWidgetView filterWidgetView;
    private boolean isSaving;
    private Set<En_CaseState> activeStates = new HashSet<>( Arrays.asList( En_CaseState.CREATED, En_CaseState.OPENED,
            En_CaseState.ACTIVE, En_CaseState.TEST_LOCAL, En_CaseState.WORKAROUND ) );
}
