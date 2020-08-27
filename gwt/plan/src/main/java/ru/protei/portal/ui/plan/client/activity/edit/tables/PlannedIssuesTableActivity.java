package ru.protei.portal.ui.plan.client.activity.edit.tables;

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
import ru.protei.portal.ui.common.client.popup.BasePopupView;
import ru.protei.portal.ui.common.client.service.PlanControllerAsync;
import ru.protei.portal.ui.common.client.widget.composite.popper.PopperComposite;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.plan.client.popupselector.PopupSingleSelector;

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
                    .withError(throwable -> {
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
            fireEvent(new IssueEvents.Edit(value.getCaseNumber()));
        }
    }

    @Override
    public void onItemActionAssign(CaseShortView value, UIObject relative) {
        showPlanSingleSelector(relative, plan -> {
            if (plan == null || isNew()) {
                return;
            }

            planService.moveIssueToAnotherPlan(planId, value.getId(), plan.getId(), new FluentCallback<Boolean>()
                    .withError(throwable -> {
                        if (throwable instanceof RequestFailedException && En_ResultStatus.ALREADY_EXIST.equals(((RequestFailedException) throwable).status)) {
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
                }));
    }

    private Runnable removeAction(CaseShortView value) {
        return () -> {
            planService.removeIssueFromPlan(planId, value.getId(), new FluentCallback<Boolean>()
                    .withError(throwable -> {
                        defaultErrorHandler.accept(throwable);
                        loadTable(planId);
                    })
                    .withSuccess(flag -> {
                        loadTable(planId);
                        fireEvent(new NotifyEvents.Show(lang.planIssueRemoved(), NotifyEvents.NotifyType.SUCCESS));
                    }));
        };
    }

    private void showPlanSingleSelector(UIObject relative, Consumer<Plan> onChanged) {
        PopupSingleSelector<Plan> popup = new PopupSingleSelector<Plan>() {};
        popup.setModel(index -> index >= plans.size() ? null : plans.get(index));
        popup.setItemRenderer(plan -> plan.getName());
        popup.setFilter(value -> !value.getId().equals(planId));
        popup.setEmptyListText(lang.emptySelectorList());
        popup.setEmptySearchText(lang.searchNoMatchesFound());
        popup.setRelative(relative);
        popup.addValueChangeHandler(event -> {
            onChanged.accept(popup.getValue());
            popup.getPopup().hide();
        });
        popup.getPopup().getChildContainer().clear();
        popup.fill();
        popup.getPopup().showNear(relative, PopperComposite.Placement.RIGHT);
    }

    private void swapIssues( CaseShortView src, CaseShortView dst ) {
        int dstIndex = issues.indexOf( dst );
        issues.remove( src );
        issues.add( dstIndex, src );
    }

    private boolean isNew (){
        return planId == null;
    }

    @Inject
    Lang lang;
    @Inject
    AbstractPlannedIssuesTableView view;
    @Inject
    PlanControllerAsync planService;
    @Inject
    DefaultErrorHandler defaultErrorHandler;

    private List<Plan> plans = new ArrayList<>();
    private Long planId;
    private List<CaseShortView> issues;
}
