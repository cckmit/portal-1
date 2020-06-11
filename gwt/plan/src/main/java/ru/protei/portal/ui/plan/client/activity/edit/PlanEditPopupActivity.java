package ru.protei.portal.ui.plan.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.CompanyDepartment;
import ru.protei.portal.core.model.ent.Plan;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CompanyQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.events.CompanyDepartmentEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.events.PlanEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.PlanControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

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
                .withSuccess(result -> {
                    fireEvent(new PlanEvents.Update(plan));
                    fireEvent(new NotifyEvents.Show(lang.planSaved(), NotifyEvents.NotifyType.SUCCESS));
                    dialogView.hidePopup();
                }));

    }

    private void fillPlan(Plan plan) {
        plan.setName(view.name().getValue());
        plan.setStartDate(view.planPeriod().getValue().from);
        plan.setFinishDate(view.planPeriod().getValue().to);
    }

    @Override
    public void onCancelClicked() {
        dialogView.hidePopup();
    }

    private boolean isValid() {
        return view.nameValidator().isValid() && view.planPeriod().getValue().from != null && view.planPeriod().getValue().to != null;
    }

    private boolean isNew( CompanyDepartment companyDepartment ) {
        return companyDepartment!=null && companyDepartment.getId() == null;
    }

    @Inject
    Lang lang;
    @Inject
    AbstractPlanEditPopupView view;
    @Inject
    AbstractDialogDetailsView dialogView;
    @Inject
    private PlanControllerAsync planService;

    private Plan plan;
}
