package ru.protei.portal.ui.employeeregistration.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.ent.EmployeeRegistrationShortView;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.events.EmployeeRegistrationEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.EmployeeRegistrationControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.function.Consumer;

public abstract class EmployeeRegistrationEditActivity implements AbstractEmployeeRegistrationEditActivity, AbstractDialogDetailsActivity, Activity {

    @Inject
    public void onInit() {
        view.setActivity(this);
        dialogDetailsView.setActivity(this);
        dialogDetailsView.getBodyContainer().add(view.asWidget());
    }

    @Event
    public void onShow(EmployeeRegistrationEvents.Edit event) {
        requestEmployeeRegistrationShortView(event.employeeRegistrationId, this::fillView);
    }

    private void requestEmployeeRegistrationShortView(Long id, Consumer<EmployeeRegistrationShortView> resultConsumer) {
        employeeRegistrationService.getEmployeeRegistrationShortView(id, new FluentCallback<EmployeeRegistrationShortView>()
                .withSuccess(result -> {
                    employeeRegistrationShortView = result;
                    resultConsumer.accept(result);
                })
        );
    }

    private void fillView(EmployeeRegistrationShortView employeeRegistration) {
        view.employmentDate().setValue(employeeRegistration.getEmploymentDate());
        view.curators().setValue(employeeRegistration.getCurators());

        dialogDetailsView.removeButtonVisibility().setVisible(false);
        dialogDetailsView.setHeader(lang.employeeRegistrationEditHeader());

        dialogDetailsView.showPopup();
    }

    @Override
    public void onSaveClicked() {
        if (view.employmentDate().getValue() == null) {
            return;
        }

        employeeRegistrationShortView.setEmploymentDate(view.employmentDate().getValue());
        employeeRegistrationShortView.setCurators(view.curators().getValue());

        employeeRegistrationService.updateEmployeeRegistration(employeeRegistrationShortView, new FluentCallback<Long>()
                .withError(throwable -> {
                    dialogDetailsView.hidePopup();
                    defaultErrorHandler.accept(throwable);
                })
                .withSuccess(employeeRegistrationId -> {
                    dialogDetailsView.hidePopup();
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                })
        );
    }

    @Override
    public void onCancelClicked() {
        dialogDetailsView.hidePopup();
    }

    @Inject
    AbstractEmployeeRegistrationEditView view;

    @Inject
    AbstractDialogDetailsView dialogDetailsView;

    @Inject
    EmployeeRegistrationControllerAsync employeeRegistrationService;

    @Inject
    Lang lang;

    @Inject
    static DefaultErrorHandler defaultErrorHandler;

    private EmployeeRegistrationShortView employeeRegistrationShortView;
}
