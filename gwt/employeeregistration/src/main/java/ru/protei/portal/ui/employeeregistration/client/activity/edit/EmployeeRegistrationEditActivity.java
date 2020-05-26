package ru.protei.portal.ui.employeeregistration.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.ent.EmployeeRegistration;
import ru.protei.portal.core.model.ent.EmployeeRegistrationShortView;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.events.EmployeeRegistrationEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.EmployeeRegistrationControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.Objects;
import java.util.function.Consumer;

import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;

public abstract class EmployeeRegistrationEditActivity implements AbstractEmployeeRegistrationEditActivity, AbstractDialogDetailsActivity, Activity {

    @Inject
    public void onInit() {
        view.setActivity(this);
        dialogDetailsView.setActivity(this);
        dialogDetailsView.getBodyContainer().add(view.asWidget());
        dialogDetailsView.removeButtonVisibility().setVisible(false);
        dialogDetailsView.setHeader(lang.employeeRegistrationEditHeader());
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

    private void fillView(final EmployeeRegistrationShortView employeeRegistration) {
        view.employmentDate().setValue(employeeRegistration.getEmploymentDate());
        view.curators().setValue(employeeRegistration.getCurators());
        view.setCuratorsFilter(personShortView -> !personShortView.isFired() && !Objects.equals(personShortView.getId(), employeeRegistration.getHeadOfDepartmentId()));

        dialogDetailsView.showPopup();
    }

    @Override
    public void onSaveClicked() {
        employeeRegistrationShortView.setEmploymentDate(view.employmentDate().getValue());
        employeeRegistrationShortView.setCurators(view.curators().getValue());

        String validationError = getValidationError(employeeRegistrationShortView);

        if (validationError != null) {
            fireEvent(new NotifyEvents.Show(validationError, NotifyEvents.NotifyType.ERROR));
            return;
        }

        employeeRegistrationService.updateEmployeeRegistration(employeeRegistrationShortView, new FluentCallback<Long>()
                .withSuccess(employeeRegistrationId -> {
                    dialogDetailsView.hidePopup();
                    fireEvent(new NotifyEvents.Show(lang.msgObjectSaved(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new EmployeeRegistrationEvents.ChangeEmployeeRegistration(employeeRegistrationId));
                })
        );
    }

    private String getValidationError(EmployeeRegistrationShortView employeeRegistrationShortView) {
        if (employeeRegistrationShortView.getEmploymentDate() == null) {
            return lang.employeeRegistrationValidationEmploymentDate();
        }

        if (isEmpty(employeeRegistrationShortView.getCuratorIds())) {
            return lang.employeeRegistrationValidationCurators();
        }

        return null;
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

    private EmployeeRegistrationShortView employeeRegistrationShortView;
}
