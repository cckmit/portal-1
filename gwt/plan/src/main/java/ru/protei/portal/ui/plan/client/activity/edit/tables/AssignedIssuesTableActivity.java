package ru.protei.portal.ui.plan.client.activity.edit.tables;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.UIObject;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.Plan;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.events.ConfirmDialogEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.PlanEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.popup.BasePopupView;
import ru.protei.portal.ui.common.client.service.IssueFilterControllerAsync;
import ru.protei.portal.ui.common.client.service.PlanControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.plan.client.popupselector.PopupSingleSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class AssignedIssuesTableActivity implements AbstractAssignedIssuesTableActivity, Activity {
    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onShow(PlanEvents.ShowAssignedIssueTable event) {
        HasWidgets container = event.parent;
        container.clear();
        container.add(view.asWidget());
        plans = event.planList;
        loadTable(event.planId);
        planId = event.planId;
    }

    @Event
    public void onUpdate(PlanEvents.UpdateAssignedIssueTable event) {
        loadTable(event.issuesList);
    }

    @Override
    public void onRemoveClicked(CaseShortView value) {
        fireEvent(new ConfirmDialogEvents.Show(lang.planIssueConfirmRemove(), removeAction(value)));
    }

    private Runnable removeAction(CaseShortView value) {
        return () -> {
            view.showLoader(true);
            planService.removeIssueFromPlan(planId, value.getId(), new FluentCallback<Boolean>()
                    .withError(throwable -> {
                        view.showLoader(false);
                        defaultErrorHandler.accept(throwable);
                        loadTable(planId);
                    })
                    .withSuccess(flag -> {
                        view.showLoader(false);
                        loadTable(planId);
                        fireEvent(new NotifyEvents.Show(lang.planIssueRemoved(), NotifyEvents.NotifyType.SUCCESS));
                    }));
        };
    }


    @Override
    public void onItemClicked(CaseShortView value) {
        fireEvent(new IssueEvents.Edit(value.getCaseNumber()));
    }

    @Override
    public void onItemActionAssign(CaseShortView value, UIObject relative) {
        showPlanSingleSelector(relative, plan -> {
            if (plan == null) {
                return;
            }

            view.showLoader(true);
            planService.moveIssueToAnotherPlan(planId, value.getId(), plan.getId(), new FluentCallback<Boolean>()
                    .withError(throwable -> {
                        view.showLoader(false);
                        defaultErrorHandler.accept(throwable);
                        loadTable(planId);
                    })
                    .withSuccess(flag -> {
                        view.showLoader(false);
                        loadTable(planId);
                        fireEvent(new NotifyEvents.Show(lang.planIssueMoved(), NotifyEvents.NotifyType.SUCCESS));
                    }));
        });
    }


    private void loadTable(List<CaseShortView> issuesList){
        view.clearRecords();
        view.setTotalRecords(issuesList.size());
        view.putRecords(issuesList);
    }

    private void loadTable(Long planId) {
        view.showLoader(true);
        view.clearRecords();
        planService.getPlanWithIssues(planId, new FluentCallback<Plan>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                    view.showLoader(false);
                    view.setTotalRecords(0);
                })
                .withSuccess(plan -> {
                    view.showLoader(false);
                    view.setTotalRecords(plan.getIssueList().size());
                    view.putRecords(plan.getIssueList());
                }));
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
        popup.getPopup().showNear(relative, BasePopupView.Position.BY_RIGHT_SIDE, null);
    }

    @Inject
    Lang lang;
    @Inject
    AbstractAssignedIssuesTableView view;
    @Inject
    PlanControllerAsync planService;
    @Inject
    IssueFilterControllerAsync issueFilterController;
    @Inject
    DefaultErrorHandler defaultErrorHandler;
    @Inject
    LocalStorageService localStorageService;
    @Inject
    PolicyService policyService;

    private List<Plan> plans = new ArrayList<>();
    private Long planId;

    private final static int TABLE_LIMIT = 100;
    private final static String TABLE_FILTER_ID_KEY = "plan_unassigned_issue_table_filter_id";
}
