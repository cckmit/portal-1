package ru.protei.portal.ui.plan.client.activity.edit.tables;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.dto.CaseFilterDto;
import ru.protei.portal.core.model.ent.Plan;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.model.view.FilterShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.PlanEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CaseFilterControllerAsync;
import ru.protei.portal.ui.common.client.service.IssueControllerAsync;
import ru.protei.portal.ui.common.client.service.PlanControllerAsync;
import ru.protei.portal.ui.common.client.util.CaseStateUtils;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.ArrayList;
import java.util.Collections;

public abstract class UnplannedIssuesTableActivity implements AbstractUnplannedIssuesTableActivity, Activity {
    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onShow(PlanEvents.ShowUnplannedIssueTable event) {
        HasWidgets container = event.parent;
        container.clear();
        container.add(view.asWidget());
        planId = event.planId;
        view.setIssueDefaultCursor(planId == null);
        scrollTo = event.scrollTo;
        initFilter();
    }

    @Event
    public void onAddIssue(PlanEvents.AddIssueToUnplannedTable event) {
        if (StringUtils.isEmpty(view.issueNumber().getValue())) {
            reloadTable();
            return;
        }

        onIssueNumberChanged();
    }

    @Event
    public void onRemoveIssue(PlanEvents.RemoveIssueFromUnplannedTable event) {
        if (StringUtils.isEmpty(view.issueNumber().getValue())) {
            view.removeIssue(event.issue);
            return;
        }

        view.removeIssue(event.issue);
        view.issueNumber().setValue("");
        reloadTable();
    }

    @Override
    public void onItemClicked(CaseShortView value) {
        if (planId != null) {
            fireEvent(new PlanEvents.PersistScroll());
            fireEvent(new IssueEvents.Edit(value.getCaseNumber()));
        }
    }

    @Override
    public void onItemActionAssign(CaseShortView value) {
        fireEvent(new PlanEvents.AddIssueToPlan(value));
    }

    @Override
    public void onFilterChanged(FilterShortView filter) {
        if(StringUtils.isNotEmpty(view.issueNumber().getValue())){
            return;
        }
        if (filter == null) {
            saveTableFilterId(null);
            CaseQuery query = makeDefaultQuery();
            loadTable(query);
            return;
        }
        Long filterId = filter.getId();
        saveTableFilterId(filterId);
        issueFilterController.getCaseFilter(filterId, new FluentCallback<CaseFilterDto<CaseQuery>>()
                .withError(throwable -> {
                    defaultErrorHandler.accept(throwable);
                    view.filter().setValue(null, true);
                })
                .withSuccess(caseFilterDto -> {
                    CaseQuery query = caseFilterDto.getQuery();
                    loadTable(query);
                }));
    }

    @Override
    public void onIssueNumberChanged() {
        if(StringUtils.isEmpty(view.issueNumber().getValue())){
            onFilterChanged(view.filter().getValue());
        } else {
            CaseQuery q = new CaseQuery();
            q.setCaseNo(Long.parseLong(view.issueNumber().getValue()));
            loadTable(q);
        }
    }

    private void loadTable(CaseQuery query) {
        view.clearRecords();
        CaseQuery q = new CaseQuery(query);
        q.setOffset(0);
        q.setLimit(TABLE_LIMIT);
        issueController.getIssues(q, new FluentCallback<SearchResult<CaseShortView>>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                    view.setTotalRecords(0);
                })
                .withSuccess(sr -> {
                    loadUnplannedIssues(sr);
                }));
    }

    private CaseQuery makeDefaultQuery() {
        CaseQuery query = new CaseQuery(En_CaseType.CRM_SUPPORT, null, En_SortField.last_update, En_SortDir.DESC);
        query.setStateIds(CaseStateUtils.getActiveStateIds());
        if (policyService.getProfile() != null) {
            query.setManagerCompanyIds(new ArrayList<>(Collections.singletonList(policyService.getUserCompany().getId())));
        }
        query.setManagerIds(Collections.singletonList(CrmConstants.Employee.UNDEFINED));
        return query;
    }

    private void initFilter() {
        FilterShortView filter = null;
        Long filterId = getTableFilterId();
        if (filterId != null) {
            filter = new FilterShortView(filterId, null);
        }
        view.filter().setValue(filter, true);
        view.updateFilterSelector();
        view.setLimitLabel(String.valueOf(TABLE_LIMIT));
        view.issueNumber().setValue("");
        onFilterChanged(view.filter().getValue());
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

    private void restoreScroll() {
        Window.scrollTo(0, scrollTo);
    }

    private void loadUnplannedIssues(SearchResult<CaseShortView> sr) {
        if (planId == null) {
            view.setTotalRecords(sr.getTotalCount());
            view.putRecords(sr.getResults());
            restoreScroll();
            return;
        }

        planService.getPlanWithIssues(planId, new FluentCallback<Plan>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess(plan -> {
                    sr.getResults().removeAll(plan.getIssueList());
                    view.setTotalRecords(sr.getResults().size());
                    view.putRecords(sr.getResults());
                    restoreScroll();
                }));
    }

    public void reloadTable() {
        onFilterChanged(view.filter().getValue());
    }

    @Inject
    Lang lang;
    @Inject
    AbstractUnplannedIssuesTableView view;
    @Inject
    IssueControllerAsync issueController;
    @Inject
    CaseFilterControllerAsync issueFilterController;
    @Inject
    DefaultErrorHandler defaultErrorHandler;
    @Inject
    LocalStorageService localStorageService;
    @Inject
    PolicyService policyService;
    @Inject
    PlanControllerAsync planService;

    private Integer scrollTo = 0;

    private Long planId;
    private final static int TABLE_LIMIT = 100;
    private final static String TABLE_FILTER_ID_KEY = "plan_unplanned_issue_table_filter_id";
}
