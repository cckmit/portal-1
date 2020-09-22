package ru.protei.portal.ui.issueassignment.client.activity.desk;

import com.google.gwt.dom.client.Element;
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
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.dict.En_TableEntity;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.ent.UserCaseAssignment;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.struct.UserCaseAssignmentTable;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.events.CaseTagEvents;
import ru.protei.portal.ui.common.client.events.IssueAssignmentEvents;
import ru.protei.portal.ui.common.client.lang.En_ResultStatusLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.IssueControllerAsync;
import ru.protei.portal.ui.common.client.service.UserCaseAssignmentControllerAsync;
import ru.protei.portal.ui.common.client.widget.popupselector.RemovablePopupSingleSelector;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.issueassignment.client.activity.desk.rowadd.AbstractDeskRowAddView;
import ru.protei.portal.ui.issueassignment.client.activity.desk.rowissue.AbstractDeskRowIssueView;
import ru.protei.portal.ui.issueassignment.client.activity.desk.rowperson.AbstractDeskRowPersonView;
import ru.protei.portal.ui.issueassignment.client.activity.desk.rowstate.AbstractDeskRowStateView;
import ru.protei.portal.ui.issueassignment.client.widget.popupselector.person.DeskPersonMultiPopup;
import ru.protei.portal.ui.issueassignment.client.widget.popupselector.state.DeskStateMultiPopup;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.helper.StringUtils.join;

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
        view.notificationVisibility().setVisible(false);
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
                    showNotification(userCaseAssignmentTable);
                    fireEvent(new IssueAssignmentEvents.DeskPeopleChanged(new ArrayList<>(people)));
                });
    }

    private void showPersonMultiSelector(Element relative, List<UserCaseAssignment> rows, UserCaseAssignment row) {
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

    private void showStateMultiSelector(Element relative, List<UserCaseAssignment> columns, UserCaseAssignment column) {
        List<EntityOption> states = column.getStateEntityOptions();
        List<EntityOption> existingStates = fetchAllStates(columns);
        existingStates.removeAll(states);
        DeskStateMultiPopup statePopup = statePopupProvider.get();
        statePopup.setValue(new HashSet<>(states));

        statePopup.show(relative, existingStates, value -> {
            if (CollectionUtils.isEmpty(value)) {
                return;
            }
            List<EntityOption> newStates = new ArrayList<>(value);
            if (CollectionUtils.diffCollection(states, newStates).hasDifferences()) {
                column.setStateEntityOptions(newStates);
                saveTableEntity(column);
            }
        });
    }

    private void showPersonSingleSelector(Element relative, Consumer<PersonShortView> onChanged) {
        RemovablePopupSingleSelector<PersonShortView> popup = new RemovablePopupSingleSelector<>();
        popup.setModel(index -> index >= people.size() ? null : people.get(index));
        popup.setItemRenderer(PersonShortView::getName);
        popup.setEmptyListText(lang.emptySelectorList());
        popup.setEmptySearchText(lang.searchNoMatchesFound());
        popup.setRelative(relative);
        popup.addValueChangeHandler(event -> {
            onChanged.accept(popup.getValue());
            popup.hidePopup();
        });
        popup.clearPopup();
        popup.fill();
        popup.showPopup();
    }

    private void showNotification(UserCaseAssignmentTable userCaseAssignmentTable) {
        long limit = userCaseAssignmentTable.getCaseShortViewsLimit();
        boolean isOverflow = userCaseAssignmentTable.isCaseShortViewsLimitOverflow();
        if (isOverflow) {
            view.notificationText().setValue(lang.issueAssignmentDeskOverflow(limit));
            view.notificationVisibility().setVisible(true);
        } else {
            view.notificationVisibility().setVisible(false);
        }
    }

    private void showDesk(UserCaseAssignmentTable userCaseAssignmentTable) {
        List<UserCaseAssignment> assignments = CollectionUtils.emptyIfNull(userCaseAssignmentTable.getUserCaseAssignments());
        List<UserCaseAssignment> columns = assignments.stream()
                .filter(c -> c.getTableEntity() == En_TableEntity.COLUMN)
                .filter(c -> isNotEmpty(c.getStates()))
                .collect(Collectors.toList());
        List<UserCaseAssignment> rows = assignments.stream()
                .filter(c -> c.getTableEntity() == En_TableEntity.ROW)
                .filter(c -> isNotEmpty(c.getPersonShortViews()))
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
                ? 2
                : columns.size() + 1;

        thead.add(buildHeaderRow(columns, issues));
        for (UserCaseAssignment row : rows) {
            List<Long> people = row.getPersons();
            List<CaseShortView> rowIssues = stream(issues)
                    .filter(issue -> people.contains(issue.getManagerId()))
                    .collect(Collectors.toList());
            boolean isRowExpanded = isPersonRowExpanded(row.getPersons());
            Widget issuesRow = buildIssuesRow(columns, rowIssues);
            Widget personsRow = buildPersonRow(rows, row, columnsSize, rowIssues.size(), isRowExpanded, () -> {
                boolean isRowWillBeExpanded = !isPersonRowExpanded(row.getPersons());
                issuesRow.setVisible(isRowWillBeExpanded);
                setPersonRowExpanded(row.getPersons(), isRowWillBeExpanded);
                return isRowWillBeExpanded;
            });
            issuesRow.setVisible(isRowExpanded);
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
            List<Long> states = column.getStates();
            List<CaseShortView> columnIssues =  stream(issues)
                    .filter(issue -> states.contains(issue.getStateId()))
                    .collect(Collectors.toList());
            tr.add(buildHeaderStateCell(columns, column, columnIssues.size()));
        }
        if (columns.isEmpty()) {
            tr.add(new HTMLPanel("th", ""));
        }
        tr.add(buildHeaderAddCell(columns, tr.getElement()));
        return tr;
    }

    private Widget buildHeaderStateCell(List<UserCaseAssignment> columns, UserCaseAssignment column, int issuesCount) {
        AbstractDeskRowStateView rowStateView = rowStateViewProvider.get();
        rowStateView.setStates(column.getStateEntityOptions(), issuesCount);
        rowStateView.setHandler(new AbstractDeskRowStateView.Handler() {
            @Override
            public void onEdit() {
                showStateMultiSelector(rowStateView.rootContainer().getElement(), columns, column);
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

    private Widget buildHeaderAddCell(List<UserCaseAssignment> columns, Element relative) {
        AbstractDeskRowAddView rowAddView = rowAddViewProvider.get();
        rowAddView.setButtonTitle(lang.issueAssignmentDeskAddColumn());
        rowAddView.setHandler(() -> {
            UserCaseAssignment column = new UserCaseAssignment();
            column.setTableEntity(En_TableEntity.COLUMN);
            column.setStates(new ArrayList<>());
            showStateMultiSelector(relative.getParentElement(), columns, column);
        });
        HTMLPanel th = new HTMLPanel("th", "");
        th.addStyleName("icon-cell");
        th.add(rowAddView.asWidget());
        return th;
    }

    private Widget buildPersonRow(List<UserCaseAssignment> rows, UserCaseAssignment row, int columnsCount, int issuesCount, boolean isRowExpanded, Supplier<Boolean> onToggle) {
        HTMLPanel tr = new HTMLPanel("tr", "");
        tr.addStyleName("table-desk-row-person");
        tr.add(buildPersonCell(rows, row, columnsCount, issuesCount, isRowExpanded, onToggle));
        return tr;
    }

    private Widget buildPersonCell(List<UserCaseAssignment> rows, UserCaseAssignment row, int columnsCount, int issuesCount, boolean isRowExpanded, Supplier<Boolean> onToggle) {
        AbstractDeskRowPersonView rowPersonView = rowPersonViewProvider.get();
        rowPersonView.setPeople(row.getPersonShortViews(), issuesCount);
        rowPersonView.setIconExpanded(isRowExpanded);
        rowPersonView.setHandler(new AbstractDeskRowPersonView.Handler() {
            @Override
            public void onEdit() {
                showPersonMultiSelector(rowPersonView.asWidget().getElement(), rows, row);
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
        rowAddView.setButtonTitle(lang.issueAssignmentDeskAddRow());
        rowAddView.setHandler(() -> {
            UserCaseAssignment row = new UserCaseAssignment();
            row.setTableEntity(En_TableEntity.ROW);
            row.setPersonShortViews(new ArrayList<>());
            showPersonMultiSelector(rowAddView.asWidget().getElement(), rows, row);
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
            List<Long> states = column.getStates();
            List<CaseShortView> cellIssues = stream(rowIssues)
                    .filter(issue -> states.contains(issue.getStateId()))
                    .collect(Collectors.toList());
            tr.add(buildIssuesCell(cellIssues));
        }
        tr.add(new HTMLPanel("td", ""));
        return tr;
    }

    private Widget buildIssuesCell(List<CaseShortView> cellIssues) {
        AbstractDeskRowIssueView rowIssueView = rowIssueViewProvider.get();
        rowIssueView.setHandler(new AbstractDeskRowIssueView.Handler() {
            @Override
            public void onOpenIssue(CaseShortView issue) {
                fireEvent(new IssueAssignmentEvents.ShowIssuePreview(issue.getCaseNumber()));
            }
            @Override
            public void onOpenOptions(UIObject relative, CaseShortView issue) {
                showPersonSingleSelector(relative.getElement(), person -> {
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
            @Override
            public void showTags(HasWidgets parent, List<CaseTag> caseTags) {
                fireEvent(new CaseTagEvents.ShowList(parent, caseTags, true, null));
            }
        });
        rowIssueView.setIssues(cellIssues);
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

    private List<EntityOption> fetchAllStates(List<UserCaseAssignment> assignments) {
        return assignments.stream()
                .map(UserCaseAssignment::getStateEntityOptions)
                .flatMap(Collection::stream) // flatten
                .distinct()
                .collect(Collectors.toList());
    }

    private boolean isPersonRowExpanded(List<Long> personIds) {
        return stream(getPersonRowCollapseState())
            .allMatch(entry -> diffCollection(entry, personIds).hasDifferences());
    }

    private void setPersonRowExpanded(List<Long> personIds, boolean isExpanded) {
        List<List<Long>> state = stream(getPersonRowCollapseState())
            .filter(entry -> diffCollection(entry, personIds).hasDifferences())
            .collect(Collectors.toList());
        if (!isExpanded) {
            state.add(personIds);
        }
        savePersonCollapseState(state);
    }

    private void savePersonCollapseState(List<List<Long>> state) {
        String value = stream(state)
            .map(entry -> join(entry, ","))
            .collect(Collectors.joining("#"));
        if (value.isEmpty()) {
            localStorageService.remove(DESK_PERSON_COLLAPSE_STATE_KEY);
        } else {
            localStorageService.set(DESK_PERSON_COLLAPSE_STATE_KEY, value);
        }
    }

    private List<List<Long>> getPersonRowCollapseState() {
        try {
            String value = localStorageService.getOrDefault(DESK_PERSON_COLLAPSE_STATE_KEY, "");
            return stream(Arrays.asList(value.split("#")))
                .filter(not(String::isEmpty))
                .map(entry -> stream(Arrays.asList(entry.split(",")))
                    .map(personId -> {
                        try {
                            return Long.parseLong(personId);
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList())
                )
                .collect(Collectors.toList());
        } catch (Exception e) {
            localStorageService.remove(DESK_PERSON_COLLAPSE_STATE_KEY);
            return new ArrayList<>();
        }
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
    LocalStorageService localStorageService;
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
    private final static String DESK_PERSON_COLLAPSE_STATE_KEY = "issue_assignment_desk_person_collapse_state";
}
