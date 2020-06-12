package ru.protei.portal.ui.plan.client.activity.edit;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.context.client.events.Back;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.Plan;
import ru.protei.portal.core.model.query.PlanQuery;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.PlanControllerAsync;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

public abstract class PlanEditActivity implements AbstractPlanEditActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(PlanEvents.Edit event) {
        initDetails.parent.clear();
        Window.scrollTo(0, 0);

        if (!hasPrivileges(event.planId)) {
            fireEvent(new ForbiddenEvents.Show(initDetails.parent));
            return;
        }

        fireEvent(new ActionBarEvents.Clear());
        initDetails.parent.add(view.asWidget());

        fillIssuesTables(event.planId);

        if (event.planId == null) {
            Plan plan = new Plan();
            fillView(plan);
        }
        else {
            planService.getPlanWithIssues(event.planId, new FluentCallback<Plan>()
                    .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errGetObject(), NotifyEvents.NotifyType.ERROR)))
                    .withSuccess(result ->  fillView(result)));
        }
    }

    private void fillIssuesTables(Long planId) {
        PlanQuery query = new PlanQuery();
        query.setCreatorId(policyService.getProfile().getId());

        planService.listPlans(query, new FluentCallback<List<Plan>>()
                .withSuccess(planList -> {
                    fireEvent(new PlanEvents.ShowUnassignedIssueTable(view.unassignedTableContainer(), planId));
                    fireEvent(new PlanEvents.ShowAssignedIssueTable(view.assignedTableContainer(), planList, planId));
                }));
    }

    @Event
    public void onUpdateParams (PlanEvents.UpdateParams event){
        fillView(plan);
    }

    @Event
    public void onUpdateIssues (PlanEvents.UpdateIssues event){
        plan.setIssueList(event.issues);
    }

    @Override
    public void onSaveClicked() {
        if (!isValid()) {
            fireEvent(new NotifyEvents.Show(lang.errFieldsRequired(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        fillPlan(plan);

        planService.createPlan(plan, new FluentCallback<Long>()
                .withError(throwable -> {
                    if (throwable instanceof RequestFailedException && En_ResultStatus.ALREADY_EXIST.equals(((RequestFailedException) throwable).status)) {
                        fireEvent(new NotifyEvents.Show(lang.errPlanAlreadyExisted(), NotifyEvents.NotifyType.ERROR));
                    } else {
                        defaultErrorHandler.accept(throwable);
                    }
                })
                .withSuccess(result -> {
                    fireEvent(new NotifyEvents.Show(lang.planSaved(), NotifyEvents.NotifyType.SUCCESS));
                })
        );
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new Back());
    }

    @Override
    public void onEditClicked() {
        fireEvent(new PlanEvents.EditParams(plan));
    }

    private void fillView(Plan plan) {
        this.plan = plan;
        view.name().setValue(plan.getName());
        view.planPeriod().setValue(new DateInterval(plan.getStartDate(), plan.getFinishDate()));
    }


    private void fillPlan(Plan plan) {
        plan.setName(view.name().getValue());
        plan.setStartDate(view.planPeriod().getValue().from);
        plan.setFinishDate(view.planPeriod().getValue().to);
    }

    private boolean isValid() {
        return view.nameValidator().isValid() && view.planPeriod().getValue().from != null && view.planPeriod().getValue().to != null;
    }

    private boolean hasPrivileges(Long planId) {
        if (planId == null && policyService.hasPrivilegeFor(En_Privilege.PLAN_CREATE)) {
            return true;
        }

        if (planId != null && policyService.hasPrivilegeFor(En_Privilege.PLAN_EDIT)) {
            return true;
        }

        return false;
    }

    @Inject
    Lang lang;
    @Inject
    AbstractPlanEditView view;
    @Inject
    PlanControllerAsync planService;
    @Inject
    PolicyService policyService;
    @Inject
    DefaultErrorHandler defaultErrorHandler;

    private Plan plan;
    private AppEvents.InitDetails initDetails;
}
