package ru.protei.portal.ui.issueassignment.client.activity.table;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.CaseFilter;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.CaseFilterShortView;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.events.IssueAssignmentEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.popup.BasePopupView;
import ru.protei.portal.ui.common.client.service.IssueControllerAsync;
import ru.protei.portal.ui.common.client.service.IssueFilterControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.issueassignment.client.widget.popupselector.PopupSingleSelector;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.*;
import java.util.function.Consumer;

public abstract class TableActivity implements Activity, AbstractTableActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onShow(IssueAssignmentEvents.ShowTable event) {
        HasWidgets container = event.parent;
        container.clear();
        container.add(view.asWidget());
        initFilter();
    }

    @Event
    public void onReload(IssueAssignmentEvents.ReloadTable event) {
        initFilter();
    }

    @Event
    public void onDeskPeopleChanged(IssueAssignmentEvents.DeskPeopleChanged event) {
        people = event.people;
    }

    private void initFilter() {
        CaseFilterShortView filter = null;
        Long filterId = getTableFilterId();
        if (filterId != null) {
            filter = new CaseFilterShortView(filterId, null);
        }
        view.filter().setValue(filter, true);
        view.updateFilterSelector();
    }

    @Override
    public void onItemClicked(CaseShortView value) {
        fireEvent(new IssueEvents.Edit(value.getCaseNumber()));
    }

    @Override
    public void onItemActionAssign(CaseShortView value, UIObject relative) {
        showPersonSingleSelector(relative, person -> {
            if (person == null) {
                return;
            }
            if (Objects.equals(person.getName(), value.getManagerName())) {
                return;
            }
            if (Objects.equals(person.getName(), value.getManagerShortName())) {
                return;
            }
            issueController.updateManagerOfIssue(value.getId(), person.getId(), new FluentCallback<Void>()
                    .withSuccess(v -> {
                        fireEvent(new IssueAssignmentEvents.ReloadTable());
                        fireEvent(new IssueAssignmentEvents.ReloadDesk());
                    }));
        });
    }

    @Override
    public void onFilterChanged(CaseFilterShortView filter) {
        if (filter == null) {
            saveTableFilterId(null);
            CaseQuery query = makeDefaultQuery();
            loadTable(query);
            return;
        }
        Long filterId = filter.getId();
        saveTableFilterId(filterId);
        view.showLoader(true);
        issueFilterController.getIssueFilter(filterId, new FluentCallback<CaseFilter>()
                .withError(throwable -> {
                    view.showLoader(false);
                    defaultErrorHandler.accept(throwable);
                    view.filter().setValue(null, true);
                })
                .withSuccess(caseFilter -> {
                    view.showLoader(false);
                    CaseQuery query = caseFilter.getParams();
                    loadTable(query);
                }));
    }

    private void loadTable(CaseQuery query) {
        view.showLoader(true);
        view.clearRecords();
        view.hideTableOverflow();
        CaseQuery q = new CaseQuery(query);
        q.setOffset(0);
        q.setLimit(TABLE_LIMIT);
        issueController.getIssues(q, new FluentCallback<SearchResult<CaseShortView>>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                    view.showLoader(false);
                    view.setTotalRecords(0);
                    view.hideTableOverflow();
                })
                .withSuccess(sr -> {
                    view.showLoader(false);
                    view.setTotalRecords(sr.getTotalCount());
                    view.putRecords(sr.getResults());
                    if (sr.getTotalCount() > TABLE_LIMIT) {
                        view.showTableOverflow(TABLE_LIMIT);
                    }
                }));
    }

    private CaseQuery makeDefaultQuery() {
        CaseQuery query = new CaseQuery(En_CaseType.CRM_SUPPORT, null, En_SortField.last_update, En_SortDir.DESC);
        query.setStates(Arrays.asList(En_CaseState.CREATED, En_CaseState.OPENED, En_CaseState.ACTIVE));
        if (policyService.getProfile() != null) {
            query.setManagerCompanyIds(new ArrayList<>(Collections.singletonList(policyService.getUserCompany().getId())));
        }
        query.setManagerIds(Collections.singletonList(CrmConstants.Employee.UNDEFINED));
        return query;
    }

    private void saveTableFilterId(Long filterId) {
        if (filterId == null) {
            localStorageService.remove(TABLE_FILTER_ID_KEY);
        } else {
            localStorageService.set(TABLE_FILTER_ID_KEY, String.valueOf(filterId));
        }
    }

    private Long getTableFilterId() {
        String value = localStorageService.getOrDefault(TABLE_FILTER_ID_KEY, null);
        if (value == null) {
            return null;
        }
        return Long.parseLong(value);
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

    @Inject
    Lang lang;
    @Inject
    AbstractTableView view;
    @Inject
    IssueControllerAsync issueController;
    @Inject
    IssueFilterControllerAsync issueFilterController;
    @Inject
    DefaultErrorHandler defaultErrorHandler;
    @Inject
    LocalStorageService localStorageService;
    @Inject
    PolicyService policyService;

    private List<PersonShortView> people = new ArrayList<>();

    private final static int TABLE_LIMIT = 100;
    private final static String TABLE_FILTER_ID_KEY = "issue_assignment_table_filter_id";
}
