package ru.protei.portal.ui.plan.client.activity.edit.tables;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.UIObject;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.Plan;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.client.events.ConfirmDialogEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.PlanEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.PlanControllerAsync;
import ru.protei.portal.ui.common.client.widget.popupselector.RemovablePopupSingleSelector;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class PlannedIssuesTableActivity implements AbstractPlannedIssuesTableActivity, Activity {
    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onShow(PlanEvents.ShowPlannedIssueTable event) {
        HasWidgets container = event.parent;
        container.clear();
        container.add(view.asWidget());
        planId = event.planId;
        plans = event.planList;
        view.moveColumnVisibility(!isNew());
        scrollTo = event.scrollTo;

        if (isNew()){
            issues = new ArrayList<>();
            loadTable(issues);
        } else {
            loadTable(event.planId);
        }
    }

    @Event
    public void onAddIssue(PlanEvents.AddIssueToPlan event) {
        if (isNew()){
            if (issues.contains(event.issue)){
                fireEvent(new NotifyEvents.Show(lang.errIssueAlreadyExistInPlan(), NotifyEvents.NotifyType.ERROR));
            } else {
                issues.add(event.issue);
                loadTable(issues);
                fireEvent(new PlanEvents.UpdateIssues(issues));
            }
        } else {
            planService.addIssueToPlan(planId, event.issue.getId(), new FluentCallback<Plan>()
                    .withError(throwable -> {
                        if (throwable instanceof RequestFailedException && En_ResultStatus.ALREADY_EXIST.equals(((RequestFailedException) throwable).status)) {
                            fireEvent(new NotifyEvents.Show(lang.errIssueAlreadyExistInPlan(), NotifyEvents.NotifyType.ERROR));
                        } else {
                            defaultErrorHandler.accept(throwable);
                        }
                    })
                    .withSuccess(plan -> {
                        fireEvent(new NotifyEvents.Show(lang.planIssueAdded(), NotifyEvents.NotifyType.SUCCESS));
                        issues = plan.getIssueList();
                        loadTable(plan.getIssueList());
                    }));
        }
    }

    @Event
    public void removeSelection(PlanEvents.RemovePlannedSelection event) {
        view.getIssuesColumnProvider().removeSelection();

    }

    @Override
    public void onRemoveClicked(CaseShortView value) {
        if (isNew()){
            issues.remove(value);
            loadTable(issues);
            fireEvent(new PlanEvents.UpdateIssues(issues));
        }
        else {
            fireEvent(new ConfirmDialogEvents.Show(lang.planIssueConfirmRemove(), removeAction(value)));
        }
    }

    @Override
    public void onSwapItems(CaseShortView src, CaseShortView dst) {
        if (src.getId().equals(dst.getId())){
            return;
        }

        swapIssues(src, dst);

        if (isNew()){
            loadTable(issues);
            fireEvent(new PlanEvents.UpdateIssues(issues));
        } else {
            Plan plan = new Plan();
            plan.setIssueList(issues);
            plan.setId(planId);
            planService.changeIssuesOrder(plan, new FluentCallback<Boolean>()
                    .withError((throwable, defaultErrorHandler, status) -> {
                        defaultErrorHandler.accept(throwable);
                        loadTable(planId);
                    })
                    .withSuccess(flag -> {
                        loadTable(planId);
                    }));
        }
    }


    @Override
    public void onItemClicked(CaseShortView value) {
        if (planId != null) {
            fireEvent(new PlanEvents.PersistScroll());
            fireEvent(new PlanEvents.RemoveUnplannedSelection());
            fireEvent(new IssueEvents.Edit(value.getCaseNumber()));
        }
    }

    @Override
    public void onItemActionAssign(CaseShortView value, UIObject relative) {
        showPlanSingleSelector(relative.getElement(), plan -> {
            if (plan == null || isNew()) {
                return;
            }

            planService.moveIssueToAnotherPlan(planId, value.getId(), plan.getId(), new FluentCallback<Boolean>()
                    .withError((throwable, defaultErrorHandler, status) -> {
                        if (En_ResultStatus.ALREADY_EXIST.equals(status)) {
                            fireEvent(new NotifyEvents.Show(lang.errIssueAlreadyExistInPlan(), NotifyEvents.NotifyType.ERROR));
                        } else {
                            defaultErrorHandler.accept(throwable);
                        }
                        loadTable(planId);
                    })
                    .withSuccess(flag -> {
                        loadTable(planId);
                        fireEvent(new NotifyEvents.Show(lang.planIssueMoved(), NotifyEvents.NotifyType.SUCCESS));
                    }));
        });
    }


    private void loadTable(List<CaseShortView> issuesList){
        view.clearRecords();
        view.putRecords(issuesList);
    }

    private void loadTable(Long planId) {
        view.clearRecords();
        planService.getPlanWithIssues(planId, new FluentCallback<Plan>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess(plan -> {
                    view.putRecords(plan.getIssueList());
                    this.issues = plan.getIssueList();
                    restoreScroll();
                }));
    }

    private Runnable removeAction(CaseShortView value) {
        return () -> planService.removeIssueFromPlan(planId, value.getId(), new FluentCallback<Long>()
                .withError(throwable -> {
                    defaultErrorHandler.accept(throwable);
                    loadTable(planId);
                })
                .withSuccess(issueId -> {
                    loadTable(planId);
                    fireEvent(new NotifyEvents.Show(lang.planIssueRemoved(), NotifyEvents.NotifyType.SUCCESS));
                }));
    }

    private void showPlanSingleSelector(Element relative, Consumer<Plan> onChanged) {
        RemovablePopupSingleSelector<Plan> selector = new RemovablePopupSingleSelector<>();
        selector.setModel(index -> index >= plans.size() ? null : plans.get(index));
        selector.setItemRenderer(Plan::getName);
        selector.setFilter(value -> !value.getId().equals(planId));
        selector.setEmptyListText(lang.emptySelectorList());
        selector.setEmptySearchText(lang.searchNoMatchesFound());
        selector.setRelative(relative);
        selector.addValueChangeHandler(event -> {
            onChanged.accept(selector.getValue());
        });
        selector.clearPopup();
        selector.fill();
        selector.showPopup();
    }

    private void swapIssues( CaseShortView src, CaseShortView dst ) {
        int dstIndex = issues.indexOf( dst );
        issues.remove( src );
        issues.add( dstIndex, src );
    }

    private boolean isNew (){
        return planId == null;
    }

    private void restoreScroll() {
        Window.scrollTo(0, scrollTo);
    }

    @Inject
    Lang lang;
    @Inject
    AbstractPlannedIssuesTableView view;
    @Inject
    PlanControllerAsync planService;
    @Inject
    DefaultErrorHandler defaultErrorHandler;

    private Integer scrollTo = 0;

    private List<Plan> plans = new ArrayList<>();
    private Long planId;
    private List<CaseShortView> issues;
}
