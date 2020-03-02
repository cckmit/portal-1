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
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.lang.En_ResultStatusLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.popup.BasePopupView;
import ru.protei.portal.ui.common.client.service.IssueControllerAsync;
import ru.protei.portal.ui.common.client.service.UserCaseAssignmentControllerAsync;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.issueassignment.client.activity.desk.rowadd.AbstractDeskRowAddView;
import ru.protei.portal.ui.issueassignment.client.activity.desk.rowissue.AbstractDeskRowIssueView;
import ru.protei.portal.ui.issueassignment.client.activity.desk.rowperson.AbstractDeskRowPersonView;
import ru.protei.portal.ui.issueassignment.client.activity.desk.rowstate.AbstractDeskRowStateView;
import ru.protei.portal.ui.issueassignment.client.widget.popupselector.PopupSingleSelector;
import ru.protei.portal.ui.issueassignment.client.widget.popupselector.person.DeskPersonMultiPopup;
import ru.protei.portal.ui.issueassignment.client.widget.popupselector.state.DeskStateMultiPopup;

import java.util.*;
import java.util.function.Consumer;
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

    @Event
    public void onReload(IssueAssignmentEvents.ReloadDesk event) {
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
                    fireEvent(new IssueAssignmentEvents.DeskPeopleChanged(new ArrayList<>(people)));
                });
    }

    private void showPersonMultiSelector(UIObject relative, List<UserCaseAssignment> rows, UserCaseAssignment row) {
        List<PersonShortView> people = row.getPersonShortViews();
        List<PersonShortView> existingPeople = fetchAllPeople(rows);
        existingPeople.removeAll(people);
        DeskPersonMultiPopup personPopup = personPopupProvider.get();
        personPopup.setValue(new HashSet<>(people));
        personPopup.show(relative, existingPeople, value -> {
            if (CollectionUtils.isEmpty(value)) {
                return;
            }
            List<PersonShortView> newPeople = new ArrayList<>(value);
            if (CollectionUtils.diffCollection(people, newPeople).hasDifferences()) {
                row.setPersonShortViews(newPeople);
                saveTableEntity(row);
            }
        });
    }

    private void showStateMultiSelector(UIObject relative, List<UserCaseAssignment> columns, UserCaseAssignment column) {
        List<En_CaseState> states = column.getStates();
        List<En_CaseState> existingStates = fetchAllStates(columns);
        existingStates.removeAll(states);
        DeskStateMultiPopup statePopup = statePopupProvider.get();
        statePopup.setValue(new HashSet<>(states));
        statePopup.show(relative, existingStates, value -> {
            if (CollectionUtils.isEmpty(value)) {
                return;
            }
            List<En_CaseState> newStates = new ArrayList<>(value);
            if (CollectionUtils.diffCollection(states, newStates).hasDifferences()) {
                column.setStates(newStates);
                saveTableEntity(column);
            }
        });
    }

    private void showPersonSingleSelector(UIObject relative, Consumer<PersonShortView> onChanged) {
        PopupSingleSelector<PersonShortView> popup = new PopupSingleSelector<PersonShortView>() {};
        popup.setModel(index -> index >= people.size() ? null : people.get(index));
        popup.setItemRenderer(PersonShortView::getName);
        popup.setEmptyListText(lang.emptySelectorList());
        popup.setEmptySearchText(lang.searchNoMatchesFound());
        popup.setRelative(relative);
        popup.addValueChangeHandler(event -> {
            onChanged.accept(popup.getValue());
            popup.getPopup().hide();
        });
        popup.getPopup().getChildContainer().clear();
        popup.fill();
        popup.getPopup().showNear(relative, BasePopupView.Position.BY_RIGHT_SIDE, null);
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

        people = fetchAllPeople(rows);

        view.tableContainer().clear();
        view.tableContainer().add(buildTable(columns, rows, issues));
        showView();
    }

    private Widget buildTable(List<UserCaseAssignment> columns, List<UserCaseAssignment> rows, List<CaseShortView> issues) {

        HTMLPanel table = new HTMLPanel("table", "");
        HTMLPanel thead = new HTMLPanel("thead", "");
        HTMLPanel tbody = new HTMLPanel("tbody", "");

        int columnsSize = columns.isEmpty()
                ? 1
                : columns.size() + 1;
        Map<UserCaseAssignment, Boolean> expandedState = new HashMap<>();

        thead.add(buildHeaderRow(columns, issues));
        for (UserCaseAssignment row : rows) {
            expandedState.put(row, true);
            List<Long> people = row.getPersons();
            List<CaseShortView> rowIssues = CollectionUtils.stream(issues)
                    .filter(issue -> people.contains(issue.getManagerId()))
                    .collect(Collectors.toList());
            Widget issuesRow = buildIssuesRow(columns, rowIssues);
            Widget personsRow = buildPersonRow(rows, row, columnsSize, rowIssues.size(), () -> {
                boolean isExpanded = !expandedState.getOrDefault(row, true);
                issuesRow.setVisible(isExpanded);
                expandedState.put(row, isExpanded);
                return isExpanded;
            });
            tbody.add(personsRow);
            tbody.add(issuesRow);
        }

        HTMLPanel tr = new HTMLPanel("tr", "");
        tr.addStyleName("table-desk-row-new-row");
        tr.add(buildPersonAddCell(rows, columnsSize));
        tbody.add(tr);

        table.add(thead);
        table.add(tbody);
        table.addStyleName("table-desk");
        table.getElement().getStyle().setPropertyPx("minWidth", (columnsSize - 1) * COLUMN_MIN_WIDTH_PX);
        return table;
    }

    private Widget buildHeaderRow(List<UserCaseAssignment> columns, List<CaseShortView> issues) {
        HTMLPanel tr = new HTMLPanel("tr", "");
        tr.addStyleName("table-desk-row-header");
        for (UserCaseAssignment column : columns) {
            List<En_CaseState> states = column.getStates();
            List<CaseShortView> columnIssues =  CollectionUtils.stream(issues)
                    .filter(issue -> states.contains(En_CaseState.getById(issue.getStateId())))
                    .collect(Collectors.toList());
            tr.add(buildHeaderStateCell(columns, column, columnIssues.size()));
        }
        if (columns.isEmpty()) {
            tr.add(new HTMLPanel("th", ""));
        }
        tr.add(buildHeaderAddCell(columns, tr));
        return tr;
    }

    private Widget buildHeaderStateCell(List<UserCaseAssignment> columns, UserCaseAssignment column, int issuesCount) {
        AbstractDeskRowStateView rowStateView = rowStateViewProvider.get();
        rowStateView.setStates(column.getStates(), issuesCount);
        rowStateView.setHandler(new AbstractDeskRowStateView.Handler() {
            @Override
            public void onEdit() {
                showStateMultiSelector(rowStateView.asWidget(), columns, column);
            }
            @Override
            public void onRemove() {
                removeTableEntity(column);
            }
        });
        HTMLPanel th = new HTMLPanel("th", "");
        th.add(rowStateView.asWidget());
        return th;
    }

    private Widget buildHeaderAddCell(List<UserCaseAssignment> columns, UIObject relative) {
        AbstractDeskRowAddView rowAddView = rowAddViewProvider.get();
        rowAddView.setHandler(() -> {
            UserCaseAssignment column = new UserCaseAssignment();
            column.setTableEntity(En_TableEntity.COLUMN);
            column.setStates(new ArrayList<>());
            showStateMultiSelector(relative, columns, column);
        });
        HTMLPanel th = new HTMLPanel("th", "");
        th.addStyleName("icon-cell");
        th.add(rowAddView.asWidget());
        return th;
    }

    private Widget buildPersonRow(List<UserCaseAssignment> rows, UserCaseAssignment row, int columnsCount, int issuesCount, Supplier<Boolean> onToggle) {
        HTMLPanel tr = new HTMLPanel("tr", "");
        tr.addStyleName("table-desk-row-person");
        tr.add(buildPersonCell(rows, row, columnsCount, issuesCount, onToggle));
        return tr;
    }

    private Widget buildPersonCell(List<UserCaseAssignment> rows, UserCaseAssignment row, int columnsCount, int issuesCount, Supplier<Boolean> onToggle) {
        AbstractDeskRowPersonView rowPersonView = rowPersonViewProvider.get();
        rowPersonView.setPeople(row.getPersonShortViews(), issuesCount);
        rowPersonView.setHandler(new AbstractDeskRowPersonView.Handler() {
            @Override
            public void onEdit() {
                showPersonMultiSelector(rowPersonView.asWidget(), rows, row);
            }
            @Override
            public void onRemove() {
                removeTableEntity(row);
            }
            @Override
            public void onToggleIssuesVisibility() {
                boolean isExpanded = onToggle.get();
                rowPersonView.setIconExpanded(isExpanded);
            }
        });
        HTMLPanel td = new HTMLPanel("td", "");
        td.getElement().setAttribute("colspan", String.valueOf(columnsCount));
        td.add(rowPersonView.asWidget());
        return td;
    }

    private Widget buildPersonAddCell(List<UserCaseAssignment> rows, int columnsCount) {
        AbstractDeskRowAddView rowAddView = rowAddViewProvider.get();
        rowAddView.setHandler(() -> {
            UserCaseAssignment row = new UserCaseAssignment();
            row.setTableEntity(En_TableEntity.ROW);
            row.setPersonShortViews(new ArrayList<>());
            showPersonMultiSelector(rowAddView.asWidget(), rows, row);
        });
        HTMLPanel td = new HTMLPanel("td", "");
        td.getElement().setAttribute("colspan", String.valueOf(columnsCount));
        td.add(rowAddView.asWidget());
        return td;
    }

    private Widget buildIssuesRow(List<UserCaseAssignment> columns, List<CaseShortView> rowIssues) {
        HTMLPanel tr = new HTMLPanel("tr", "");
        tr.addStyleName("table-desk-row-issues");
        for (UserCaseAssignment column : columns) {
            List<En_CaseState> states = column.getStates();
            List<CaseShortView> cellIssues =  CollectionUtils.stream(rowIssues)
                    .filter(issue -> states.contains(En_CaseState.getById(issue.getStateId())))
                    .collect(Collectors.toList());
            tr.add(buildIssuesCell(cellIssues));
        }
        tr.add(new HTMLPanel("td", ""));
        return tr;
    }

    private Widget buildIssuesCell(List<CaseShortView> cellIssues) {
        AbstractDeskRowIssueView rowIssueView = rowIssueViewProvider.get();
        rowIssueView.setIssues(cellIssues);
        rowIssueView.setHandler(new AbstractDeskRowIssueView.Handler() {
            @Override
            public void onOpenIssue(CaseShortView issue) {
                fireEvent(new IssueEvents.Edit(issue.getCaseNumber()));
            }
            @Override
            public void onOpenOptions(UIObject relative, CaseShortView issue) {
                showPersonSingleSelector(relative, person -> {
                    if (person == null) {
                        return;
                    }
                    if (Objects.equals(person.getName(), issue.getManagerName())) {
                        return;
                    }
                    if (Objects.equals(person.getName(), issue.getManagerShortName())) {
                        return;
                    }
                    issueController.updateManagerOfIssue(issue.getId(), person.getId(), new FluentCallback<Void>()
                            .withSuccess(v -> {
                                fireEvent(new IssueAssignmentEvents.ReloadDesk());
                                fireEvent(new IssueAssignmentEvents.ReloadTable());
                            }));
                });
            }
        });
        HTMLPanel td = new HTMLPanel("td", "");
        td.add(rowIssueView.asWidget());
        return td;
    }

    private List<PersonShortView> fetchAllPeople(List<UserCaseAssignment> assignments) {
        return assignments.stream()
            .map(UserCaseAssignment::getPersonShortViews)
            .flatMap(Collection::stream) // flatten
            .distinct()
            .collect(Collectors.toList());
    }

    private List<En_CaseState> fetchAllStates(List<UserCaseAssignment> assignments) {
        return assignments.stream()
                .map(UserCaseAssignment::getStates)
                .flatMap(Collection::stream) // flatten
                .distinct()
                .collect(Collectors.toList());
    }

    @Inject
    Lang lang;
    @Inject
    En_ResultStatusLang resultStatusLang;
    @Inject
    UserCaseAssignmentControllerAsync userCaseAssignmentController;
    @Inject
    IssueControllerAsync issueController;
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

    private List<PersonShortView> people = new ArrayList<>();

    private static final int COLUMN_MIN_WIDTH_PX = 200;
}
