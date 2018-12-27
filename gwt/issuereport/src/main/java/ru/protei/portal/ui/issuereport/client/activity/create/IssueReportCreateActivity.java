package ru.protei.portal.ui.issuereport.client.activity.create;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ReportType;
import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.view.CaseFilterShortView;
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
import ru.protei.portal.ui.common.shared.model.FluentCallback;

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
        report.setCaseQuery(IssueFilterUtils.makeCaseQuery(filterWidgetView, true));

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
                filterWidgetView.commentAuthorsVisibility().setVisible(false);
                filterWidgetView.searchByCommentsVisibility().setVisible(true);
                break;
            }
            case CASE_TIME_ELAPSED: {
                filterWidgetView.commentAuthorsVisibility().setVisible(true);
                filterWidgetView.searchByCommentsVisibility().setVisible(false);
                break;
            }
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

    private AbstractIssueFilterWidgetView filterWidgetView;
    private boolean isSaving;
}
