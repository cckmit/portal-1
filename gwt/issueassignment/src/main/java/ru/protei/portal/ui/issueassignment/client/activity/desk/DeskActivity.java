package ru.protei.portal.ui.issueassignment.client.activity.desk;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.dict.En_TableEntity;
import ru.protei.portal.core.model.ent.UserCaseAssignment;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.struct.UserCaseAssignmentTable;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.events.IssueAssignmentEvents;
import ru.protei.portal.ui.common.client.lang.En_ResultStatusLang;
import ru.protei.portal.ui.common.client.service.UserCaseAssignmentControllerAsync;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.issueassignment.client.activity.desk.rowadd.AbstractDeskRowAddView;
import ru.protei.portal.ui.issueassignment.client.activity.desk.rowissue.AbstractDeskRowIssueView;
import ru.protei.portal.ui.issueassignment.client.activity.desk.rowperson.AbstractDeskRowPersonView;
import ru.protei.portal.ui.issueassignment.client.activity.desk.rowstate.AbstractDeskRowStateView;
import ru.protei.portal.ui.issueassignment.client.widget.popupperson.DeskPersonMultiPopup;
import ru.protei.portal.ui.issueassignment.client.widget.popupstate.DeskStateMultiPopup;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class DeskActivity implements Activity, AbstractDeskActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onShow(IssueAssignmentEvents.ShowDesk event) {
        HasWidgets container = event.parent;
        container.clear();
        container.add(view.asWidget());
        loadDesk();
    }

    private void showView() {
        hideView();
        hideLoader();
        hideError();
        view.tableViewVisibility().setVisible(true);
    }

    private void showLoader() {
        hideView();
        hideLoader();
        hideError();
        view.loadingViewVisibility().setVisible(true);
    }

    private void showError(String text) {
        hideView();
        hideLoader();
        hideError();
        view.failedViewVisibility().setVisible(true);
        view.setFailedViewText(text);
    }

    private void hideView() {
        view.tableViewVisibility().setVisible(false);
    }

    private void hideLoader() {
        view.loadingViewVisibility().setVisible(false);
    }

    private void hideError() {
        view.failedViewVisibility().setVisible(false);
        view.setFailedViewText("");
    }

    private void loadDesk() {
        showLoader();
        userCaseAssignmentController.getCaseAssignmentTable(getLoadCallback());
    }

    private void removeTableEntity(UserCaseAssignment assignment) {
        showLoader();
        userCaseAssignmentController.removeTableEntity(assignment.getId(), getLoadCallback());
    }

    private void saveTableEntity(UserCaseAssignment assignment) {
        showLoader();
        userCaseAssignmentController.saveTableEntity(assignment, getLoadCallback());
    }

    private AsyncCallback<UserCaseAssignmentTable> getLoadCallback() {
        return new FluentCallback<UserCaseAssignmentTable>()
                .withError(throwable -> {
                    hideLoader();
                    hideView();
                    if (throwable instanceof RequestFailedException) {
                        showError(resultStatusLang.getMessage(((RequestFailedException) throwable).status));
                    } else {
                        showError(resultStatusLang.getMessage(En_ResultStatus.INTERNAL_ERROR));
                    }
                })
                .withSuccess(userCaseAssignmentTable -> {
                    hideLoader();
                    hideError();
                    showDesk(userCaseAssignmentTable);
                });
    }

    private void showPersonMultiSelector(UIObject relative, List<PersonShortView> people, UserCaseAssignment assignment) {
        DeskPersonMultiPopup personPopup = personPopupProvider.get();
        personPopup.setValue(new HashSet<>(people));
        personPopup.show(relative, value -> {
            if (CollectionUtils.isEmpty(value)) {
                return;
            }
            List<PersonShortView> newPeople = new ArrayList<>(value);
            if (CollectionUtils.diffCollection(people, newPeople).hasDifferences()) {
                assignment.setPersonShortViews(newPeople);
                saveTableEntity(assignment);
            }
        });
    }

    private void showStateMultiSelector(UIObject relative, List<En_CaseState> states, UserCaseAssignment assignment) {
        DeskStateMultiPopup statePopup = statePopupProvider.get();
        statePopup.setValue(new HashSet<>(states));
        statePopup.show(relative, value -> {
            if (CollectionUtils.isEmpty(value)) {
                return;
            }
            List<En_CaseState> newStates = new ArrayList<>(value);
            if (CollectionUtils.diffCollection(states, newStates).hasDifferences()) {
                assignment.setStates(newStates);
                saveTableEntity(assignment);
            }
        });
    }

    private void showDesk(UserCaseAssignmentTable userCaseAssignmentTable) {
        List<UserCaseAssignment> assignments = CollectionUtils.emptyIfNull(userCaseAssignmentTable.getUserCaseAssignments());
        List<UserCaseAssignment> columns = assignments.stream()
                .filter(c -> c.getTableEntity() == En_TableEntity.COLUMN)
                .filter(c -> CollectionUtils.isNotEmpty(c.getStates()))
                .collect(Collectors.toList());
        List<UserCaseAssignment> rows = assignments.stream()
                .filter(c -> c.getTableEntity() == En_TableEntity.ROW)
                .filter(c -> CollectionUtils.isNotEmpty(c.getPersonShortViews()))
                .collect(Collectors.toList());
        List<CaseShortView> issues = CollectionUtils.emptyIfNull(userCaseAssignmentTable.getCaseShortViews());

        view.tableContainer().clear();
        view.tableContainer().add(buildTable(columns, rows, issues));
        showView();
    }

    private Widget buildTable(List<UserCaseAssignment> columns, List<UserCaseAssignment> rows, List<CaseShortView> issues) {

        HTMLPanel table = new HTMLPanel("table", "");
        HTMLPanel thead = new HTMLPanel("thead", "");
        HTMLPanel tbody = new HTMLPanel("tbody", "");

        int columnsSize = columns.size() + 1;
        Map<UserCaseAssignment, Boolean> expandedState = new HashMap<>();

        thead.add(buildHeaderRow(columns, issues));
        for (UserCaseAssignment row : rows) {
            expandedState.put(row, true);
            List<Long> people = row.getPersons();
            List<CaseShortView> rowIssues = CollectionUtils.stream(issues)
                    .filter(issue -> people.contains(issue.getManagerId()))
                    .collect(Collectors.toList());
            Widget issuesRow = buildIssuesRow(columns, rowIssues);
            Widget personsRow = buildPersonRow(row, columnsSize, rowIssues.size(), () -> {
                boolean isExpanded = !expandedState.getOrDefault(row, true);
                issuesRow.setVisible(isExpanded);
                expandedState.put(row, isExpanded);
                return isExpanded;
            });
            tbody.add(personsRow);
            tbody.add(issuesRow);
        }

        HTMLPanel row = new HTMLPanel("tr", "");
        row.add(buildPersonAddCell(columnsSize));
        tbody.add(row);

        table.add(thead);
        table.add(tbody);
        table.addStyleName("table");
        return table;
    }

    private Widget buildHeaderRow(List<UserCaseAssignment> assignments, List<CaseShortView> issues) {
        HTMLPanel row = new HTMLPanel("tr", "");
        for (UserCaseAssignment assignment : assignments) {
            List<En_CaseState> states = assignment.getStates();
            List<CaseShortView> columnIssues =  CollectionUtils.stream(issues)
                    .filter(issue -> states.contains(En_CaseState.getById(issue.getStateId())))
                    .collect(Collectors.toList());
            row.add(buildHeaderStateCell(assignment, columnIssues.size()));
        }
        row.add(buildHeaderAddCell());
        return row;
    }

    private Widget buildHeaderStateCell(UserCaseAssignment assignment, int issuesCount) {
        AbstractDeskRowStateView rowStateView = rowStateViewProvider.get();
        rowStateView.setStates(assignment.getStates(), issuesCount);
        rowStateView.setHandler(new AbstractDeskRowStateView.Handler() {
            @Override
            public void onEdit() {
                showStateMultiSelector(rowStateView.asWidget(), assignment.getStates(), assignment);
            }
            @Override
            public void onRemove() {
                removeTableEntity(assignment);
            }
        });
        HTMLPanel cell = new HTMLPanel("th", "");
        cell.add(rowStateView.asWidget());
        return cell;
    }

    private Widget buildHeaderAddCell() {
        AbstractDeskRowAddView rowAddView = rowAddViewProvider.get();
        rowAddView.setHandler(() -> {
            UserCaseAssignment assignment = new UserCaseAssignment();
            assignment.setTableEntity(En_TableEntity.COLUMN);
            showStateMultiSelector(rowAddView.asWidget(), Collections.emptyList(), assignment);
        });
        HTMLPanel cell = new HTMLPanel("th", "");
        cell.add(rowAddView.asWidget());
        return cell;
    }

    private Widget buildPersonRow(UserCaseAssignment assignment, int columnsCount, int issuesCount, Supplier<Boolean> onToggle) {
        HTMLPanel row = new HTMLPanel("tr", "");
        row.add(buildPersonCell(assignment, columnsCount, issuesCount, onToggle));
        return row;
    }

    private Widget buildPersonCell(UserCaseAssignment assignment, int columnsCount, int issuesCount, Supplier<Boolean> onToggle) {
        AbstractDeskRowPersonView rowPersonView = rowPersonViewProvider.get();
        rowPersonView.setPeople(assignment.getPersonShortViews(), issuesCount);
        rowPersonView.setHandler(new AbstractDeskRowPersonView.Handler() {
            @Override
            public void onEdit() {
                showPersonMultiSelector(rowPersonView.asWidget(), assignment.getPersonShortViews(), assignment);
            }
            @Override
            public void onRemove() {
                removeTableEntity(assignment);
            }
            @Override
            public void onToggleIssuesVisibility() {
                boolean isExpanded = onToggle.get();
                rowPersonView.setIconExpanded(isExpanded);
            }
        });
        HTMLPanel cell = new HTMLPanel("td", "");
        cell.getElement().setAttribute("colspan", String.valueOf(columnsCount));
        cell.add(rowPersonView.asWidget());
        return cell;
    }

    private Widget buildPersonAddCell(int columnsCount) {
        AbstractDeskRowAddView rowAddView = rowAddViewProvider.get();
        rowAddView.setHandler(() -> {
            UserCaseAssignment assignment = new UserCaseAssignment();
            assignment.setTableEntity(En_TableEntity.ROW);
            showPersonMultiSelector(rowAddView.asWidget(), Collections.emptyList(), assignment);
        });
        HTMLPanel cell = new HTMLPanel("td", "");
        cell.getElement().setAttribute("colspan", String.valueOf(columnsCount));
        cell.add(rowAddView.asWidget());
        return cell;
    }

    private Widget buildIssuesRow(List<UserCaseAssignment> columns, List<CaseShortView> rowIssues) {
        HTMLPanel row = new HTMLPanel("tr", "");
        for (UserCaseAssignment column : columns) {
            List<En_CaseState> states = column.getStates();
            List<CaseShortView> cellIssues =  CollectionUtils.stream(rowIssues)
                    .filter(issue -> states.contains(En_CaseState.getById(issue.getStateId())))
                    .collect(Collectors.toList());
            row.add(buildIssuesCell(cellIssues));
        }
        row.add(new HTMLPanel("td", ""));
        return row;
    }

    private Widget buildIssuesCell(List<CaseShortView> cellIssues) {
        AbstractDeskRowIssueView rowIssueView = rowIssueViewProvider.get();
        rowIssueView.setIssues(cellIssues);
        rowIssueView.setHandler(new AbstractDeskRowIssueView.Handler() {

        });
        HTMLPanel cell = new HTMLPanel("td", "");
        cell.add(rowIssueView.asWidget());
        return cell;
    }

    @Inject
    En_ResultStatusLang resultStatusLang;
    @Inject
    UserCaseAssignmentControllerAsync userCaseAssignmentController;
    @Inject
    AbstractDeskView view;
    @Inject
    Provider<AbstractDeskRowStateView> rowStateViewProvider;
    @Inject
    Provider<AbstractDeskRowPersonView> rowPersonViewProvider;
    @Inject
    Provider<AbstractDeskRowIssueView> rowIssueViewProvider;
    @Inject
    Provider<AbstractDeskRowAddView> rowAddViewProvider;
    @Inject
    Provider<DeskPersonMultiPopup> personPopupProvider;
    @Inject
    Provider<DeskStateMultiPopup> statePopupProvider;
}
