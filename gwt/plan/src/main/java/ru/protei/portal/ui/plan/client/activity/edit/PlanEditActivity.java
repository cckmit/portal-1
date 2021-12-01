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
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.ErrorPageEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.PlanEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.PlanControllerAsync;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;
import java.util.Objects;

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

        if (!hasPrivileges(event.planId)) {
            fireEvent(new ErrorPageEvents.ShowForbidden(initDetails.parent));
            return;
        }

        initDetails.parent.add(view.asWidget());
        planId = event.planId;

        fillIssuesTables(event.planId);

        if (isNew()) {
            Plan plan = new Plan();
            fillView(plan);
        }
        else {
            planService.getPlanWithIssues(event.planId, new FluentCallback<Plan>()
                    .withError(throwable -> {
                        fireEvent(new NotifyEvents.Show(lang.errGetObject(), NotifyEvents.NotifyType.ERROR));
                        fireEvent(new Back());
                    })
                    .withSuccess(result ->  {
                        if (!Objects.equals(result.getCreatorId(), policyService.getProfile().getId())){
                            fireEvent(new ErrorPageEvents.ShowForbidden(initDetails.parent));
                            return;
                        }
                        fillView(result);
                    }));
        }
    }

    @Event
    public void onUpdateParams (PlanEvents.UpdateParams event){
        fillView(plan);
    }

    @Event
    public void onUpdateIssues (PlanEvents.UpdateIssues event){
        plan.setIssueList(event.issues);
    }

    @Event
    public void persistScroll( PlanEvents.PersistScroll event) {
            scrollTo = Window.getScrollTop();
    }

    @Override
    public void onSaveClicked() {
        if (!isValid()) {
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
                    fireEvent(new PlanEvents.ChangeModel());
                    fireEvent(new Back());
                })
        );
    }

    @Override
    public void onCancelClicked() {
        fireEvent(new PlanEvents.ShowPlans(true));
    }

    @Override
    public void onBackClicked() {
        fireEvent(new PlanEvents.ShowPlans(true));
    }

    @Override
    public void onEditClicked() {
        fireEvent(new PlanEvents.EditParams(plan));
    }

    private void fillView(Plan plan) {
        this.plan = plan;

        view.editButtonVisibility().setVisible(!isNew());
        view.saveButtonVisibility().setVisible(isNew());
        view.cancelButtonVisibility().setVisible(isNew());
        view.backButtonVisibility().setVisible(!isNew());
        view.nameEnabled().setEnabled(isNew());
        view.periodEnabled().setEnabled(isNew());

        view.name().setValue(plan.getName());
        view.planPeriod().setValue(new DateInterval(plan.getStartDate(), plan.getFinishDate()));

        if (isNew()) {
            view.setPlanPeriodValid(false);
            view.setHeader( lang.planHeaderNew() );
            view.setCreatedBy("");
        } else {
            view.setHeader(lang.planHeader(plan.getId().toString()));
            view.setCreatedBy(lang.createBy(plan.getCreatorShortName(), DateFormatter.formatDateTime(plan.getCreated())));
        }
    }


    private void fillPlan(Plan plan) {
        plan.setName(normalizeSpaces(view.name().getValue()));
        plan.setStartDate(view.planPeriod().getValue().from);
        plan.setFinishDate(view.planPeriod().getValue().to);
    }

    private boolean isValid() {
        if (!view.nameValidator().isValid()){
            fireEvent(new NotifyEvents.Show(lang.errFieldsRequired(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        if(view.planPeriod().getValue().to == null || view.planPeriod().getValue().from == null){
            fireEvent(new NotifyEvents.Show(lang.errFieldsRequired(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        if (view.planPeriod().getValue().from.after(view.planPeriod().getValue().to)){
            fireEvent(new NotifyEvents.Show(lang.errIncorrectParams(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        return true;
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

    private void fillIssuesTables(Long planId) {
        PlanQuery query = new PlanQuery();
        query.setCreatorId(policyService.getProfile().getId());

        planService.listPlans(query, new FluentCallback<List<Plan>>()
                .withSuccess(planList -> {
                    fireEvent(new PlanEvents.ShowUnplannedIssueTable(view.unplannedTableContainer(), planId, scrollTo));
                    fireEvent(new PlanEvents.ShowPlannedIssueTable(view.plannedTableContainer(), planList, planId, scrollTo));
                    resetScrollTo();
                }));
    }

    private String normalizeSpaces(String text) {
        return text.trim().replaceAll("[\\s]{2,}", " ");
    }

    private boolean isNew(){
        return planId == null;
    }

    private void resetScrollTo() {
        scrollTo = 0;
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

    private Integer scrollTo;

    private Plan plan;
    private AppEvents.InitDetails initDetails;
    private Long planId;
}
