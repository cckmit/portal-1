package ru.protei.portal.ui.issuereport.client.activity.create;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CaseFilterType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ReportType;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.activity.filter.AbstractIssueFilterModel;
import ru.protei.portal.ui.common.client.activity.filter.AbstractIssueFilterView;
import ru.protei.portal.ui.common.client.activity.issuefilter.AbstractIssueFilterWidgetView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.ReportControllerAsync;
import ru.protei.portal.ui.common.client.util.IssueFilterUtils;
import ru.protei.portal.ui.common.client.widget.selector.person.InitiatorModel;
import ru.protei.portal.ui.common.client.widget.selector.person.PersonModel;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public abstract class IssueReportCreateActivity implements Activity,
        AbstractIssueReportCreateActivity, AbstractIssueFilterModel {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);

        filterParamView.setModel(this);
        filterParamView.setInitiatorModel(initiatorModel);
        filterParamView.setCreatorModel(personModel);
        filterParamView.setInitiatorCompaniesSupplier(() -> new HashSet<>( filterParamView.companies().getValue()));

        filterView.setIssueFilterParam(filterParamView);

        view.getIssueFilterContainer().add(filterView.asWidget());
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        view.fillReportTypes(makeReportTypeList());
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
        view.reset();

        if(!policyService.hasSystemScopeForPrivilege(En_Privilege.COMPANY_VIEW)){
            HashSet<EntityOption> companyIds = new HashSet<>();
            companyIds.add(IssueFilterUtils.toEntityOption(policyService.getProfile().getCompany()));
            filterParamView.companies().setValue(companyIds);
            filterParamView.updateInitiators();
        }
    }

    private void applyFilterViewPrivileges() {
        filterParamView.productsVisibility().setVisible( policyService.hasPrivilegeFor( En_Privilege.ISSUE_FILTER_PRODUCT_VIEW ) );
        filterParamView.managersVisibility().setVisible( policyService.hasPrivilegeFor( En_Privilege.ISSUE_FILTER_MANAGER_VIEW ) );
        filterParamView.searchPrivateVisibility().setVisible( policyService.hasPrivilegeFor( En_Privilege.ISSUE_PRIVACY_VIEW ) );
    }

    @Override
    public void onSaveClicked() {

        En_ReportType reportType = view.reportType().getValue();
        CaseQuery query = filterParamView.getFilterFields();

        if (!validateQuery(reportType, query)) {
            return;
        }

        Report report = new Report();
        report.setReportType(reportType);
        report.setName(view.name().getValue());
        report.setLocale(LocaleInfo.getCurrentLocale().getLocaleName());
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
    public void onReportTypeChanged(En_CaseFilterType filterType) {
        filterView.updateFilterType(filterType);
        applyIssueFilterVisibilityByPrivileges();
    }

    @Override
    public void onUserFilterChanged() {
        ; // ничего не делаем, мы используем фильтр при создании отчета
    }

/*    private boolean validateQuery( En_CaseFilterType filterType, CaseQuery query) {
        return validateQuery(En_ReportType.valueOf(filterType.name()), query);
    }*/

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
//        if (view.getIssueFilter().productsVisibility().isVisible()) {
//            view.getIssueFilter().productsVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_PRODUCT_VIEW));
//        }
//        if (view.getIssueFilter().managersVisibility().isVisible()) {
//            view.getIssueFilter().managersVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_FILTER_MANAGER_VIEW));
//        }
//        if (view.getIssueFilter().searchPrivateVisibility().isVisible()) {
//            view.getIssueFilter().searchPrivateVisibility().setVisible(policyService.hasPrivilegeFor(En_Privilege.ISSUE_PRIVACY_VIEW));
//        }
    }

    private List<En_ReportType> makeReportTypeList() {
        if (policyService.hasSystemScopeForPrivilege(En_Privilege.ISSUE_REPORT)) {
            return Arrays.asList(En_ReportType.values());
        }
        return Arrays.asList(En_ReportType.CASE_OBJECTS);
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
    PersonModel personModel;
    @Inject
    InitiatorModel initiatorModel;

    @Inject
    AbstractIssueFilterView filterView;
    @Inject
    AbstractIssueFilterWidgetView filterParamView;

    private boolean isSaving;
    private AppEvents.InitDetails initDetails;
}
