package ru.protei.portal.ui.absence.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.AbsenceEvents;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AbsenceControllerAsync;
import ru.protei.portal.ui.common.client.service.EmployeeControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.Objects;
import java.util.function.Consumer;

import static com.google.gwt.user.datepicker.client.CalendarUtil.copyDate;

public abstract class AbsenceEditActivity implements AbstractAbsenceEditActivity, AbstractDialogDetailsActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        dialogView.setActivity(this);
        dialogView.getBodyContainer().add(view.asWidget());
    }

    @Event
    public void onShow(AbsenceEvents.Edit event) {
        if (!hasCreateAccess() && !hasEditAccess()) {
            return;
        }

        hideForm();
        dialogView.setHeader(isNew() ? lang.absenceCreation() : lang.absenceEditing());
        dialogView.showPopup();

        if (event.id == null) {
            showForm(new PersonAbsence());
        }
        loadAbsence(event.id, this::showForm);
    }

    @Override
    public void onRemoveClicked() {

    }

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

    private boolean hasCreateAccess() {
        return isNew() && policyService.hasPrivilegeFor(En_Privilege.ABSENCE_CREATE);
    }

    private boolean hasEditAccess() {
        return !isNew() && (policyService.hasPrivilegeFor(En_Privilege.ABSENCE_VIEW) || policyService.hasPrivilegeFor(En_Privilege.ABSENCE_EDIT));
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
        //view.loadingVisibility().setVisible(true);
        dialogView.removeButtonVisibility().setVisible(false);
        dialogView.saveButtonVisibility().setVisible(false);
    }

    private void hideLoading() {
        //view.loadingVisibility().setVisible(false);
    }

    private void fillView(PersonAbsence absence) {
        this.absence = absence;

        boolean isAllowedCreate = hasCreateAccess();
        boolean isAllowedEdit = hasAccess(En_Privilege.ABSENCE_EDIT);
        boolean isAllowedRemove = hasAccess(En_Privilege.ABSENCE_REMOVE);
        boolean isAllowedModify = isAllowedCreate || isAllowedEdit;

        PersonShortView currentPerson = new PersonShortView(policyService.getProfile().getFullName(), policyService.getProfile().getId());
        view.employee().setValue(absence.getPerson() == null ? currentPerson : absence.getPerson().toFullNameShortView());
        view.dateRange().setValue(new DateInterval(copyDate(absence.getFromTime()), copyDate(absence.getTillTime())));
        view.reason().setValue(absence.getReason());
        view.comment().setValue(absence.getUserComment());

        view.employeeEnabled().setEnabled(isAllowedCreate);
        view.dateRangeEnabled().setEnabled(isAllowedModify);
        view.reasonEnabled().setEnabled(isAllowedCreate);
        view.commentEnabled().setEnabled(isAllowedModify);

        dialogView.saveButtonVisibility().setVisible(isAllowedModify);
        dialogView.removeButtonVisibility().setVisible(isAllowedRemove);
    }

    private boolean hasAccess(En_Privilege privilege) {
        Long currentPersonId = policyService.getProfile().getId();
        boolean isCreator = Objects.equals(absence.getCreatorId(), currentPersonId);
        boolean isAbsent = Objects.equals(absence.getPersonId(), currentPersonId);
        boolean isAdmin = policyService.hasSystemScopeForPrivilege(privilege);
        boolean isPrivileged = policyService.hasPrivilegeFor(privilege);
        boolean isUserWithAccess = isPrivileged && (isCreator || isAbsent);
        return isAdmin || isUserWithAccess;
    }

    private boolean validateView() {
        if (view.employee().getValue() == null) {
            fireEvent(new NotifyEvents.Show(lang.absenceValidationEmployee(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        if (view.dateRange().getValue() == null) {
            fireEvent(new NotifyEvents.Show(lang.absenceValidationDateRange(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        if (view.reason().getValue() == null) {
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
                    onCancelClicked();
                    fireEvent(new NotifyEvents.Show(lang.absenceUpdated(), NotifyEvents.NotifyType.SUCCESS));
                }));
    }

    private void enableButtons(boolean isEnable) {
        dialogView.removeButtonEnabled().setEnabled(isEnable);
        dialogView.saveButtonEnabled().setEnabled(isEnable);
    }

    private boolean isNew() {
        return absence.getId() == null;
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
