package ru.protei.portal.ui.plan.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.Plan;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.PlanEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.PlanControllerAsync;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

public abstract class PlanEditPopupActivity implements AbstractPlanEditPopupActivity, Activity, AbstractDialogDetailsActivity {
    @PostConstruct
    public void onInit() {
        dialogView.setActivity(this);
        dialogView.getBodyContainer().add(view.asWidget());
    }

    @Event
    public void onShow(PlanEvents.EditParams event) {
        if ( event.plan == null ) {
            fireEvent(new NotifyEvents.Show(lang.error(), NotifyEvents.NotifyType.ERROR));
            return;
        }

        this.plan = event.plan;
        view.name().setValue(plan.getName());
        view.planPeriod().setValue(new DateInterval(plan.getStartDate(), plan.getFinishDate()));

        dialogView.removeButtonVisibility().setVisible(false);
        dialogView.saveButtonVisibility().setVisible(true);
        dialogView.setHeader(lang.planEditPopupHeader());
        dialogView.showPopup();
    }

    @Override
    public void onRemoveClicked() {}

    @Override
    public void onSaveClicked() {
        if (!isValid()) {
            return;
        }

        fillPlan(plan);

        planService.editPlanParams(plan, new FluentCallback<Boolean>()
                .withError(throwable -> {
                    if (throwable instanceof RequestFailedException && En_ResultStatus.ALREADY_EXIST.equals(((RequestFailedException) throwable).status)) {
                        fireEvent(new NotifyEvents.Show(lang.errPlanAlreadyExisted(), NotifyEvents.NotifyType.ERROR));
                    } else {
                        defaultErrorHandler.accept(throwable);
                    }
                })
                .withSuccess(result -> {
                    fireEvent(new PlanEvents.UpdateParams(plan));
                    fireEvent(new NotifyEvents.Show(lang.planSaved(), NotifyEvents.NotifyType.SUCCESS));
                    dialogView.hidePopup();
                }));

    }

    @Override
    public void onCancelClicked() {
        dialogView.hidePopup();
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

    private void fillPlan(Plan plan) {
        plan.setName(clearName(view.name().getValue()));
        plan.setStartDate(view.planPeriod().getValue().from);
        plan.setFinishDate(view.planPeriod().getValue().to);
    }

    private String clearName(String name) {
        return name.trim().replaceAll("[\\s]{2,}", " ");
    }

    @Inject
    Lang lang;
    @Inject
    AbstractPlanEditPopupView view;
    @Inject
    AbstractDialogDetailsView dialogView;
    @Inject
    private PlanControllerAsync planService;
    @Inject
    DefaultErrorHandler defaultErrorHandler;

    private Plan plan;
}
