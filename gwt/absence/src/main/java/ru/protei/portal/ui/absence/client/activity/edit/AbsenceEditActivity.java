package ru.protei.portal.ui.absence.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.absence.client.util.AccessUtil;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.AbsenceEvents;
import ru.protei.portal.ui.common.client.events.EmployeeEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AbsenceControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.function.Consumer;

import static com.google.gwt.user.datepicker.client.CalendarUtil.copyDate;

public abstract class AbsenceEditActivity implements AbstractAbsenceEditActivity, AbstractDialogDetailsActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        dialogView.setActivity(this);
        dialogView.removeButtonVisibility().setVisible(false);
        dialogView.setSaveOnEnterClick(false);
        dialogView.getBodyContainer().add(view.asWidget());
    }

    @Event
    public void onShow(AbsenceEvents.Edit event) {
        if (!hasAccessCreate() && !hasAccessEdit()) {
            return;
        }

        hideForm();
        dialogView.setHeader(event.id == null ? lang.absenceCreation() : lang.absenceEditing());
        dialogView.showPopup();

        if (event.id == null) {
            showForm(new PersonAbsence());
        } else {
            loadAbsence(event.id, this::showForm);
        }
    }

    @Override
    public void onRemoveClicked() {}

    @Override
    public void onSaveClicked() {

        if (!validateView()) {
            return;
        }
        saveAbsence();
    }

    @Override
    public void onCancelClicked() {
        absence = null;
        dialogView.hidePopup();
    }

    @Override
    public void onDateRangeChanged() {
        view.setDateRangeValid(isDateRangeValid(view.dateRange().getValue()));
    }

    private boolean hasAccessCreate() {
        return isNew() && policyService.hasPrivilegeFor(En_Privilege.ABSENCE_CREATE);
    }

    private boolean hasAccessEdit() {
        return !isNew() && policyService.hasPrivilegeFor(En_Privilege.ABSENCE_EDIT);
    }

    private void hideForm() {
        view.contentVisibility().setVisible(false);
    }

    private void showForm(PersonAbsence absence) {
        view.contentVisibility().setVisible(true);
        if (!view.asWidget().isAttached()) {
            return;
        }
        fillView(absence);
    }
    private void showLoading() {
        view.loadingVisibility().setVisible(true);
        dialogView.removeButtonVisibility().setVisible(false);
        dialogView.saveButtonVisibility().setVisible(false);
    }

    private void hideLoading() {
        view.loadingVisibility().setVisible(false);
    }

    private void fillView(PersonAbsence absence) {
        this.absence = absence;

        boolean isAllowedCreate = hasAccessCreate();
        boolean isAllowedModify = isAllowedCreate || AccessUtil.isAllowedEdit(policyService, absence);

        PersonShortView currentPerson = new PersonShortView(policyService.getProfile().getFullName(), policyService.getProfile().getId());
        view.employee().setValue(absence.getPerson() == null ? currentPerson : absence.getPerson());
        view.dateRange().setValue(new DateInterval(copyDate(absence.getFromTime()), copyDate(absence.getTillTime())));
        view.setDateRangeValid(isDateRangeValid(view.dateRange().getValue()));
        view.reason().setValue(absence.getReason());
        view.comment().setValue(absence.getUserComment());

        view.employeeEnabled().setEnabled(isAllowedCreate);
        view.dateRangeEnabled().setEnabled(isAllowedModify);
        view.reasonEnabled().setEnabled(isAllowedCreate);
        view.commentEnabled().setEnabled(isAllowedModify);

        dialogView.saveButtonVisibility().setVisible(isAllowedModify);
    }

    private boolean validateView() {
        if (!view.employeeValidator().isValid()) {
            fireEvent(new NotifyEvents.Show(lang.absenceValidationEmployee(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        if (!isDateRangeValid(view.dateRange().getValue())) {
            fireEvent(new NotifyEvents.Show(lang.absenceValidationDateRange(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        if (!view.reasonValidator().isValid()) {
            fireEvent(new NotifyEvents.Show(lang.absenceValidationReason(), NotifyEvents.NotifyType.ERROR));
            return false;
        }
        return true;
    }

    private PersonAbsence fillDTO() {
        absence.setPersonId(view.employee().getValue().getId());
        DateInterval dateInterval = view.dateRange().getValue();
        absence.setFromTime(dateInterval.from);
        absence.setTillTime(dateInterval.to);
        absence.setReason(view.reason().getValue());
        absence.setUserComment(view.comment().getValue());
        return absence;
    }

    private void loadAbsence(Long absenceId, Consumer<PersonAbsence> onSuccess) {
        showLoading();
        absenceController.getAbsence(absenceId, new FluentCallback<PersonAbsence>()
                .withError(throwable -> {
                    hideLoading();
                    hideForm();
                    defaultErrorHandler.accept(throwable);
                })
                .withSuccess(absence -> {
                    hideLoading();
                    onSuccess.accept(absence);
                }));
    }

    private void saveAbsence() {
        enableButtons(false);
        absenceController.saveAbsence(fillDTO(), new FluentCallback<Long>()
                .withSuccess(result -> {
                    enableButtons(true);
                    fireEvent(new NotifyEvents.Show(isNew() ? lang.absenceCreated() : lang.absenceUpdated(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new EmployeeEvents.Show());
                    onCancelClicked();
                }));
    }

    private void enableButtons(boolean isEnable) {
        dialogView.removeButtonEnabled().setEnabled(isEnable);
        dialogView.saveButtonEnabled().setEnabled(isEnable);
    }

    private boolean isNew() {
        return absence == null || absence.getId() == null;
    }

    private boolean isDateRangeValid(DateInterval dateInterval) {
        return dateInterval != null &&
                dateInterval.from != null &&
                dateInterval.to != null &&
                dateInterval.from.before(dateInterval.to);
    }

    @Inject
    Lang lang;
    @Inject
    AbstractAbsenceEditView view;
    @Inject
    AbstractDialogDetailsView dialogView;
    @Inject
    AbsenceControllerAsync absenceController;
    @Inject
    PolicyService policyService;
    @Inject
    DefaultErrorHandler defaultErrorHandler;

    private PersonAbsence absence;
}
