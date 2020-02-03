package ru.protei.portal.app.portal.client.activity.dashboard;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.protei.portal.app.portal.client.activity.dashboardblocks.table.AbstractDashboardTableActivity;
import ru.protei.portal.app.portal.client.activity.dashboardblocks.table.AbstractDashboardTableView;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.ent.UserDashboard;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.En_ResultStatusLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IssueControllerAsync;
import ru.protei.portal.ui.common.client.service.UserLoginControllerAsync;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;
import java.util.Objects;

public abstract class DashboardActivity implements AbstractDashboardActivity, Activity{

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(DashboardEvents.Show event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.DASHBOARD_VIEW)) {
            fireEvent(new ForbiddenEvents.Show(initDetails.parent));
            return;
        }
        showView();
        showActionBarActions();
        loadDashboard();
    }

    @Event
    public void onChangeTableModel(DashboardEvents.ChangeTableModel event) {
        loadDashboard();
    }

    @Event
    public void onIssueCreateClicked(ActionBarEvents.Clicked event) {
        if (!UiConstants.ActionBarIdentity.DASHBOARD_CREATE_ISSUE.equals(event.identity)) {
            return;
        }
        if (!policyService.hasPrivilegeFor(En_Privilege.ISSUE_CREATE)) {
            return;
        }
        fireEvent(new IssueEvents.Create());
    }

    @Event
    public void onTableCreateClicked(ActionBarEvents.Clicked event) {
        if (!UiConstants.ActionBarIdentity.DASHBOARD_CREATE_TABLE.equals(event.identity)) {
            return;
        }
        fireEvent(new DashboardEvents.EditTable());
    }

    @Event
    public void onConfirmTableRemove(ConfirmDialogEvents.Confirm event) {
        if (!Objects.equals(event.identity, getClass().getName())) {
            return;
        }
        if (dashboardIdToRemove == null) {
            return;
        }
        userLoginController.removeUserDashboard(dashboardIdToRemove, new FluentCallback<Void>()
                .withSuccess(v -> {
                    fireEvent(new NotifyEvents.Show(lang.dashboardTableRemoved(), NotifyEvents.NotifyType.SUCCESS));
                    loadDashboard();
                }));
    }

    @Event
    public void onCancelTableRemove(ConfirmDialogEvents.Cancel event) {
        if (!Objects.equals(event.identity, getClass().getName())) {
            return;
        }
        dashboardIdToRemove = null;
    }

    private void showView() {
        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());
    }

    private void showActionBarActions() {
        fireEvent(new ActionBarEvents.Clear());
        fireEvent(new ActionBarEvents.Add(lang.dashboardAddTable(), null, UiConstants.ActionBarIdentity.DASHBOARD_CREATE_TABLE));
        if (policyService.hasPrivilegeFor(En_Privilege.ISSUE_CREATE)) {
            fireEvent(new ActionBarEvents.Add(lang.issueCreate(), null, UiConstants.ActionBarIdentity.DASHBOARD_CREATE_ISSUE));
        }
    }

    private void showLoader() {
        hideLoader();
        hideError();
        hideEmpty();
        view.container().clear();
        view.loadingViewVisibility().setVisible(true);
    }

    private void showError(String text) {
        hideLoader();
        hideError();
        hideEmpty();
        view.container().clear();
        view.failedViewVisibility().setVisible(true);
        view.setFailedViewText(text);
    }

    private void showEmpty() {
        hideLoader();
        hideError();
        hideEmpty();
        view.container().clear();
        view.emptyViewVisibility().setVisible(true);
    }

    private void hideLoader() {
        view.loadingViewVisibility().setVisible(false);
    }

    private void hideError() {
        view.failedViewVisibility().setVisible(false);
        view.setFailedViewText("");
    }

    private void hideEmpty() {
        view.emptyViewVisibility().setVisible(false);
    }

    private void loadDashboard() {
        showLoader();
        userLoginController.getUserDashboards(new FluentCallback<List<UserDashboard>>()
                .withError(throwable -> {
                    hideEmpty();
                    hideLoader();
                    if (throwable instanceof RequestFailedException) {
                        showError(resultStatusLang.getMessage(((RequestFailedException) throwable).status));
                    } else {
                        showError(resultStatusLang.getMessage(En_ResultStatus.INTERNAL_ERROR));
                    }
                })
                .withSuccess(dashboardList -> {
                    hideEmpty();
                    hideLoader();
                    hideError();
                    showDashboard(dashboardList);
                }));
    }

    private void showDashboard(List<UserDashboard> dashboardList) {
        if (CollectionUtils.isEmpty(dashboardList)) {
            showEmpty();
            return;
        }
        for (int i = 0; i < dashboardList.size(); i++) {
            UserDashboard dashboard = dashboardList.get(i);
            if (dashboard.getCaseFilter() == null || dashboard.getCaseFilter().getParams() == null) {
                continue;
            }
            CaseFilter filter = dashboard.getCaseFilter();
            String name = dashboard.getName();
            CaseQuery query = new CaseQuery(filter.getParams());
            AbstractDashboardTableView table = createIssueTable(dashboard, i, name, query);
            view.container().add(table.asWidget());
            loadTable(table, query);
        }
    }

    private AbstractDashboardTableView createIssueTable(UserDashboard dashboard, int order, String name, CaseQuery query) {
        AbstractDashboardTableView table = tableProvider.get();
        table.setEnsureDebugId(DebugIds.DASHBOARD.TABLE + order);
        table.setName(name);
        table.setActivity(new AbstractDashboardTableActivity() {
            @Override
            public void onItemClicked(CaseShortView value) {
                fireEvent(new IssueEvents.Edit(value.getCaseNumber()));
            }
            @Override
            public void onOpenClicked() {
                fireEvent(new IssueEvents.Show(new CaseQuery(query), true));
            }
            @Override
            public void onEditClicked() {
                fireEvent(new DashboardEvents.EditTable(dashboard));
            }
            @Override
            public void onRemoveClicked() {
                removeTable(dashboard);
            }
            @Override
            public void onReloadClicked() {
                loadTable(table, query);
            }
        });
        return table;
    }

    private void loadTable(AbstractDashboardTableView table, CaseQuery query) {
        table.showLoader(true);
        table.clearRecords();
        table.hideTableOverflow();
        CaseQuery q = new CaseQuery(query);
        q.setOffset(0);
        q.setLimit(TABLE_LIMIT);
        issueController.getIssues(q, new FluentCallback<SearchResult<CaseShortView>>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                    table.showLoader(false);
                    table.setTotalRecords(0);
                    table.hideTableOverflow();
                })
                .withSuccess(sr -> {
                    table.showLoader(false);
                    table.setTotalRecords(sr.getTotalCount());
                    table.putRecords(sr.getResults());
                    if (sr.getTotalCount() > TABLE_LIMIT) {
                        table.showTableOverflow(TABLE_LIMIT);
                    }
                }));
    }

    private void removeTable(UserDashboard dashboard) {
        if (dashboard == null || dashboard.getId() == null) {
            return;
        }
        dashboardIdToRemove = dashboard.getId();
        fireEvent(new ConfirmDialogEvents.Show(getClass().getName(), lang.dashboardTableConfirmRemove()));
    }

    @Inject
    Lang lang;
    @Inject
    En_ResultStatusLang resultStatusLang;
    @Inject
    UserLoginControllerAsync userLoginController;
    @Inject
    IssueControllerAsync issueController;
    @Inject
    AbstractDashboardView view;
    @Inject
    Provider<AbstractDashboardTableView> tableProvider;
    @Inject
    PolicyService policyService;

    private Long dashboardIdToRemove = null;
    private AppEvents.InitDetails initDetails;
    private final static int TABLE_LIMIT = 50;
}
