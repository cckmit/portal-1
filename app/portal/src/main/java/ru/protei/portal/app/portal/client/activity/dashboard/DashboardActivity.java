package ru.protei.portal.app.portal.client.activity.dashboard;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.protei.portal.app.portal.client.activity.dashboardblocks.table.AbstractDashboardIssueTableActivity;
import ru.protei.portal.app.portal.client.activity.dashboardblocks.table.AbstractDashboardIssueTableView;
import ru.protei.portal.app.portal.client.activity.dashboardblocks.table.AbstractDashboardProjectTableActivity;
import ru.protei.portal.app.portal.client.activity.dashboardblocks.table.AbstractDashboardProjectTableView;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.dto.CaseFilterDto;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.ent.UserDashboard;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.DragAndDropElementsHandler;
import ru.protei.portal.ui.common.client.common.UiConstants;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.En_ResultStatusLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IssueControllerAsync;
import ru.protei.portal.ui.common.client.service.RegionControllerAsync;
import ru.protei.portal.ui.common.client.service.UserLoginControllerAsync;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.common.shared.model.RequestCallback;
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
    public void onProjectCreateClicked(ActionBarEvents.Clicked event) {
        if (!UiConstants.ActionBarIdentity.DASHBOARD_CREATE_PROJECT.equals(event.identity)) {
            return;
        }
        if (!policyService.hasPrivilegeFor(En_Privilege.PROJECT_CREATE)) {
            return;
        }
        fireEvent(new ProjectEvents.Edit());
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
        if (policyService.hasPrivilegeFor(En_Privilege.PROJECT_CREATE)) {
            fireEvent(new ActionBarEvents.Add(lang.projectCreate(), null, UiConstants.ActionBarIdentity.DASHBOARD_CREATE_PROJECT));
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
            createDashboardElement(dashboard, i);
        }
    }

    private void createDashboardElement(UserDashboard dashboard, int order ) {
        String name = dashboard.getName();
        if (dashboard.getCaseFilterDto() != null) {
            CaseQuery caseQuery = dashboard.getCaseFilterDto().getQuery();
            AbstractDashboardIssueTableView table = createIssueTable(dashboard, order, name, dashboard.getCaseFilterDto());
            dragAndDropElementsHandler.addDraggableElement(dashboard.getId(), table);
            view.addTableToContainer(table.asWidget());
            loadIssueTable(table, new CaseQuery(caseQuery));
        }
        if (dashboard.getProjectFilterDto() != null) {
            ProjectQuery projectQuery = dashboard.getProjectFilterDto().getQuery();
            AbstractDashboardProjectTableView table = createProjectTable(dashboard, order, name, dashboard.getProjectFilterDto());
            dragAndDropElementsHandler.addDraggableElement(dashboard.getId(), table);
            view.addTableToContainer(table.asWidget());
            loadProjectTable(table, projectQuery);
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

    private AbstractDashboardIssueTableView createIssueTable(UserDashboard dashboard, int order, String name, CaseFilterDto<CaseQuery> caseFilterDto) {
        AbstractDashboardIssueTableView table = issueTableProvider.get();
        table.setEnsureDebugId(DebugIds.DASHBOARD.TABLE + order);
        table.setName(name);
        table.setCollapsed(dashboard.getCollapsed() == null ? false : dashboard.getCollapsed());
        table.setChangeSelectionIfSelectedPredicate(caseShortView -> view.isQuickviewShow());
        table.setActivity(new AbstractDashboardIssueTableActivity() {
            @Override
            public void onItemClicked(CaseShortView value) {
                if (value != null) {
                    showIssuePreview(value.getCaseNumber());
                }
            }
            @Override
            public void onOpenClicked() {
                fireEvent(new IssueEvents.Show(caseFilterDto, false));
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
                loadIssueTable(table, caseFilterDto.getQuery());
            }
        });
        return table;
    }

    private void loadIssueTable(AbstractDashboardIssueTableView table, CaseQuery query) {
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

    private AbstractDashboardProjectTableView createProjectTable(UserDashboard dashboard, int order, String name, CaseFilterDto<ProjectQuery> projectFilterDto) {
        AbstractDashboardProjectTableView table = projectTableProvider.get();
        table.setEnsureDebugId(DebugIds.DASHBOARD.TABLE + order);
        table.setName(name);
        table.setCollapsed(dashboard.getCollapsed() == null ? false : dashboard.getCollapsed());
        table.setChangeSelectionIfSelectedPredicate(project -> view.isQuickviewShow());
        table.setActivity(new AbstractDashboardProjectTableActivity() {
            @Override
            public void onItemClicked(Project project) {
                if (project != null) {
                    showProjectPreview(project.getId());
                }
            }

            @Override
            public void onOpenClicked() {
                fireEvent(new ProjectEvents.Show(projectFilterDto));
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
            public void onCollapseClicked(boolean isCollapsed) {
                dashboard.setCollapsed(isCollapsed);
                userLoginController.saveUserDashboard(dashboard, new FluentCallback<Long>());
            }

            @Override
            public void onReloadClicked() {
                loadProjectTable(table, projectFilterDto.getQuery());
            }
        });
        return table;
    }

    private void loadProjectTable(AbstractDashboardProjectTableView table, ProjectQuery query) {
        table.showLoader(true);
        table.clearRecords();
        table.hideTableOverflow();
        query.setOffset(0);
        query.setLimit(TABLE_LIMIT);
        projectController.getProjects(query, new RequestCallback<SearchResult<Project>>() {
            @Override
            public void onError( Throwable throwable ) {
                fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                table.showLoader(false);
                table.setTotalRecords(0);
                table.hideTableOverflow();
            }

            @Override
            public void onSuccess( SearchResult<Project> sr ) {
                table.showLoader(false);
                table.setTotalRecords(sr.getTotalCount());
                table.putRecords(sr.getResults());
                if (sr.getTotalCount() > TABLE_LIMIT) {
                    table.showTableOverflow(TABLE_LIMIT);
                }
            }
        } );
    }

    private void removeTable(UserDashboard dashboard) {
        if (dashboard == null || dashboard.getId() == null) {
            return;
        }

        fireEvent(new ConfirmDialogEvents.Show(lang.dashboardTableConfirmRemove(), removeAction(dashboard.getId())));
    }

    private Runnable removeAction(Long dashboardId) {
        return () -> userLoginController.removeUserDashboard(dashboardId, new FluentCallback<Long>()
                .withSuccess(result -> {
                    fireEvent(new NotifyEvents.Show(lang.dashboardTableRemoved(), NotifyEvents.NotifyType.SUCCESS));
                    loadDashboard();
                }));
    }

    private void showIssuePreview(Long caseNumber) {
        view.showQuickview(false);
        fireEvent(new IssueEvents.ShowPreview(view.quickview(), caseNumber));
        view.showQuickview(true);
    }

    private void showProjectPreview(Long projectNumber) {
        view.showQuickview(false);
        fireEvent(new ProjectEvents.ShowPreview(view.quickview(), projectNumber));
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
    RegionControllerAsync projectController;
    @Inject
    AbstractDashboardView view;
    @Inject
    Provider<AbstractDashboardIssueTableView> issueTableProvider;
    @Inject
    Provider<AbstractDashboardProjectTableView> projectTableProvider;
    @Inject
    PolicyService policyService;
    @Inject
    private DragAndDropElementsHandler<Long> dragAndDropElementsHandler;

    private AppEvents.InitDetails initDetails;

    private final static int TABLE_LIMIT = 50;
}
