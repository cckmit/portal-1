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
import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.activity.filter.AbstractIssueFilterActivity;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IssueFilterControllerAsync;
import ru.protei.portal.ui.common.client.service.ReportControllerAsync;
import ru.protei.portal.ui.common.client.util.IssueFilterUtils;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.client.activity.filter.AbstractIssueFilterModel;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

public abstract class IssueReportCreateActivity implements Activity,
        AbstractIssueReportCreateActivity, AbstractIssueFilterActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
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
            view.getIssueFilterParams().companies().setValue(companyIds);
            view.getIssueFilterParams().updateInitiators();
        }
    }

    @Override
    public void onSaveClicked() {

        En_ReportType reportType = view.reportType().getValue();
        CaseQuery query = view.getIssueFilter().getValue();

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
    public void onReportTypeChanged() {
        applyIssueFilterVisibilityByPrivileges();
    }

    @Override
    public void onSaveFilterClicked() {
        isCreateFilterAction = false;
        showUserFilterName();
    }

    @Override
    public void onCreateFilterClicked() {
        isCreateFilterAction = true;
        showUserFilterName();
    }

    @Override
    public void onFilterRemoveClicked( Long id ) {
        fireEvent(new ConfirmDialogEvents.Show(lang.issueFilterRemoveConfirmMessage(), removeAction(id)));
    }

//    @Override
//    public void onUserFilterChanged() {
//    public void onUserFilterChanged(Long id, Consumer<CaseFilter> consumer) {
//
//        filterService.getIssueFilter(id, new FluentCallback<CaseFilter>()
//                .withErrorMessage(lang.errNotFound())
//                .withSuccess(caseFilter ->
//                    consumer.accept(caseFilter)));
//    }
//    }

    private boolean isCreateFilterAction = true;

    @Override
    public void onOkSavingFilterClicked() {
        if (view.getIssueFilter().filterName().getValue().isEmpty()){
            view.getIssueFilter().setFilterNameContainerErrorStyle( true );
            fireEvent( new NotifyEvents.Show( lang.errFilterNameRequired(), NotifyEvents.NotifyType.ERROR ) );
            return;
        }

        CaseFilter userFilter = fillUserFilter();
        if ( !isCreateFilterAction ){
            userFilter.setId( view.getIssueFilterParams().userFilter().getValue().getId() );
        }

        filterService.saveIssueFilter( userFilter, new RequestCallback< CaseFilter >() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent( new NotifyEvents.Show( lang.errSaveIssueFilter(), NotifyEvents.NotifyType.ERROR ) );
            }

            @Override
            public void onSuccess( CaseFilter filter ) {
                fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                fireEvent(new IssueEvents.ChangeUserFilterModel());

                view.getIssueFilter().editBtnVisibility().setVisible(true);
                view.getIssueFilter().removeFilterBtnVisibility().setVisible(true);

                view.getIssueFilter().getIssueFilterWidget().userFilter().setValue(filter.toShortView());

                showUserFilterControls();
            }
        } );
    }

    @Override
    public void onCancelSavingFilterClicked() {
        showUserFilterControls();
    }

//    @Override
//    public void onUserFilterChanged(Long id, Consumer<CaseFilter> consumer) {
//
//        filterService.getIssueFilter(id, new FluentCallback<CaseFilter>()
//                .withErrorMessage(lang.errNotFound())
//                .withSuccess(caseFilter ->
//                    consumer.accept(caseFilter)));
//    }
//
//    @Override
//    public void onSaveFilterClicked(CaseFilter caseFilter, Consumer<CaseFilterShortView> consumer) {
//
//        if (!validateQuery(caseFilter.getType(), caseFilter.getParams())) {
//            return;
//        }
//
//        filterService.saveIssueFilter(caseFilter, new FluentCallback<CaseFilter>()
//                .withError(throwable -> {
//                    defaultErrorHandler.accept(throwable);
//                    fireEvent(new NotifyEvents.Show(lang.errSaveIssueFilter(), NotifyEvents.NotifyType.ERROR));
//                })
//                .withSuccess(filter -> {
//                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
//                    fireEvent(new IssueEvents.ChangeUserFilterModel());
//                    consumer.accept(filter.toShortView());
//                }));
//    }
//
//    @Override
//    public void onRemoveFilterClicked(Long id) {
//        if (id != null) {
//            fireEvent(new ConfirmDialogEvents.Show(lang.issueFilterRemoveConfirmMessage(), removeAction(id)));
//        }
//    }

    private void showUserFilterName(){
        view.getIssueFilter().setUserFilterControlsVisibility(false);
        view.getIssueFilter().setUserFilterNameVisibility(true);
    }

    private void showUserFilterControls() {
        view.getIssueFilter().setUserFilterControlsVisibility(true);
        view.getIssueFilter().setUserFilterNameVisibility(false);
    }

    private CaseFilter fillUserFilter() {
        CaseFilter filter = new CaseFilter();
        filter.setName(view.getIssueFilter().filterName().getValue());
        filter.setType(En_CaseFilterType.CASE_OBJECTS);
        CaseQuery query = IssueFilterUtils.makeCaseQuery(view.getIssueFilterParams());
        filter.setParams(query);
        query.setSearchString(view.getIssueFilterParams().searchPattern().getValue());
        return filter;
    }


    private boolean validateQuery( En_CaseFilterType filterType, CaseQuery query) {
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

    private Runnable removeAction(Long filterId) {
        return () -> filterService.removeIssueFilter(filterId, new FluentCallback<Boolean>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errNotRemoved(), NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess(aBoolean -> {
                    fireEvent(new NotifyEvents.Show(lang.issueFilterRemoveSuccessed(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new IssueEvents.ChangeUserFilterModel());
                    view.getIssueFilter().resetFilter();
                }));
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
    IssueFilterControllerAsync filterService;
    @Inject
    DefaultErrorHandler defaultErrorHandler;

    private boolean isSaving;
    private AppEvents.InitDetails initDetails;
}
