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
import ru.protei.portal.ui.common.client.common.DragAndDropElementsHandler;
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

    @Inject
    public void onInit() {
        dragAndDropElementsHandler.addDropConsumer(this::swapElements);
    }

    @Event
    public void onInitDetails( AppEvents.InitDetails initDetails ) {
        this.initDetails = initDetails;
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(DashboardEvents.Show event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.DASHBOARD_VIEW)) {
            fireEvent(new ErrorPageEvents.ShowForbidden(initDetails.parent));
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

    private void showView() {
        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());
        view.showQuickview(false);
    }

    private void showActionBarActions() {
        fireEvent(new ActionBarEvents.Clear());
        if (policyService.hasPrivilegeFor(En_Privilege.ISSUE_CREATE)) {
            fireEvent(new ActionBarEvents.Add(lang.issueCreate(), null, UiConstants.ActionBarIdentity.DASHBOARD_CREATE_ISSUE));
        }
        fireEvent(new ActionBarEvents.Add(lang.dashboardAddTable(), null, UiConstants.ActionBarIdentity.DASHBOARD_CREATE_TABLE));
    }

    private void showLoader() {
        hideLoader();
        hideError();
        hideEmpty();
        view.clearContainers();
        view.loadingViewVisibility().setVisible(true);
    }

    private void showError(String text) {
        hideLoader();
        hideError();
        hideEmpty();
        view.clearContainers();
        view.failedViewVisibility().setVisible(true);
        view.setFailedViewText(text);
    }

    private void showEmpty() {
        hideLoader();
        hideError();
        hideEmpty();
        view.clearContainers();
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

        view.clearContainers();

        for (int i = 0; i < dashboardList.size(); i++) {
            UserDashboard dashboard = dashboardList.get(i);
            if (dashboard.getCaseFilter() == null || dashboard.getCaseFilter().getParams() == null) {
                continue;
            }
            CaseFilter filter = dashboard.getCaseFilter();
            String name = dashboard.getName();
            AbstractDashboardTableView table = createIssueTable(dashboard, i, name, filter);
            dragAndDropElementsHandler.addDraggableElement(dashboard.getId(), table);
            view.addTableToContainer(table.asWidget());
            loadTable(table, new CaseQuery(filter.getParams()));
        }
    }

    private void swapElements(Long src, Long dst) {
        if (Objects.equals(src, dst)) {
            return;
        }

        userLoginController.swapUserDashboards(src, dst, new FluentCallback<List<UserDashboard>>()
                .withSuccess(this::showDashboard)
        );
    }

    private AbstractDashboardTableView createIssueTable(UserDashboard dashboard, int order, String name, CaseFilter filter) {
        AbstractDashboardTableView table = tableProvider.get();
        table.setEnsureDebugId(DebugIds.DASHBOARD.TABLE + order);
        table.setName(name);
        table.setCollapsed(dashboard.getCollapsed() == null ? false : dashboard.getCollapsed());
        table.setChangeSelectionIfSelectedPredicate(caseShortView -> view.isQuickviewShow());
        table.setActivity(new AbstractDashboardTableActivity() {
            @Override
            public void onItemClicked(CaseShortView value) {
                if (value != null) {
                    showIssuePreview(value.getCaseNumber());
                }
            }
            @Override
            public void onOpenClicked() {
                fireEvent(new IssueEvents.Show(filter, false));
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
            public void onCollapseClicked(boolean isCollapsed){
                dashboard.setCollapsed(isCollapsed);
                userLoginController.saveUserDashboard(dashboard, new FluentCallback<Long>());
            }
            @Override
            public void onReloadClicked() {
                loadTable(table, filter.getParams());
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

        fireEvent(new ConfirmDialogEvents.Show(lang.dashboardTableConfirmRemove(), removeAction(dashboard.getId())));
    }

    private Runnable removeAction(Long dashboardId) {
        return () -> userLoginController.removeUserDashboard(dashboardId, new FluentCallback<Void>()
                .withSuccess(v -> {
                    fireEvent(new NotifyEvents.Show(lang.dashboardTableRemoved(), NotifyEvents.NotifyType.SUCCESS));
                    loadDashboard();
                }));
    }

    private void showIssuePreview(Long caseNumber) {
        view.showQuickview(false);
        fireEvent(new IssueEvents.ShowPreview(view.quickview(), caseNumber));
        view.showQuickview(true);
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
    @Inject
    private DragAndDropElementsHandler<Long> dragAndDropElementsHandler;

    private AppEvents.InitDetails initDetails;

    private final static int TABLE_LIMIT = 50;
}
