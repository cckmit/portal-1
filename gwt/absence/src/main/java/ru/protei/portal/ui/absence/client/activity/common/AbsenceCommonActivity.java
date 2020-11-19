package ru.protei.portal.ui.absence.client.activity.common;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.ui.absence.client.view.common.AbsenceCommonView;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.AbsenceEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AbsenceControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;

import java.util.function.Consumer;

public abstract class AbsenceCommonActivity implements AbstractAbsenceCommonActivity, AbstractDialogDetailsActivity, Activity {

    @Override
    public void onSaveClicked() {
        if (!validateView()) {
            return;
        }
        save();
    }

    @Override
    public void onCancelClicked() {
        dialogView.hidePopup();
    }

    protected void onInit() {
        dialogView.setActivity(this);
        dialogView.removeButtonVisibility().setVisible(false);
        dialogView.setSaveOnEnterClick(false);
        dialogView.getBodyContainer().add(view.asWidget());
    }

    protected void onShow() {
        hideForm();
        dialogView.showPopup();
        showForm();
    }

    protected void showLoading() {
        view.loadingVisibility().setVisible(true);
        view.contentVisibility().setVisible(false);
        dialogView.cancelButtonVisibility().setVisible(false);
        dialogView.saveButtonVisibility().setVisible(false);
    }

    protected void hideLoading() {
        view.loadingVisibility().setVisible(false);
        view.contentVisibility().setVisible(true);
        dialogView.cancelButtonVisibility().setVisible(true);
        dialogView.saveButtonVisibility().setVisible(true);
    }

    protected void fillView(PersonAbsence absence) {
        PersonShortView currentPerson = new PersonShortView(policyService.getProfile().getId());
        currentPerson.setDisplayName(policyService.getProfile().getFullName());
        view.employee().setValue(absence.getPerson() == null ? currentPerson : absence.getPerson());
        view.reason().setValue(absence.getReason());
        view.comment().setValue(absence.getUserComment());
    }

    protected PersonAbsence fillCommonDTO() {
        PersonAbsence absence = new PersonAbsence();
        absence.setPersonId(view.employee().getValue().getId());
        absence.setReason(view.reason().getValue());
        absence.setUserComment(view.comment().getValue());
        return absence;
    }

    protected abstract void performSave(Consumer<Throwable> onError, Runnable onSuccess);
    protected abstract void performFillView();
    protected abstract boolean additionalValidate();

    private void hideForm() {
        view.contentVisibility().setVisible(false);
    }

    private void showForm() {
        view.contentVisibility().setVisible(true);
        if (!view.asWidget().isAttached()) {
            return;
        }
        performFillView();
    }

    private boolean validateView() {
        if (!view.employeeValidator().isValid()) {
            fireEvent(new NotifyEvents.Show(lang.absenceValidationEmployee(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        if (!view.reasonValidator().isValid()) {
            fireEvent(new NotifyEvents.Show(lang.absenceValidationReason(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

        return additionalValidate();
    }

    private void save() {
        enableButtons(false);
        performSave(
            (throwable) -> {
                enableButtons(true);
            },
            () -> {
                enableButtons(true);
                fireEvent(new AbsenceEvents.Update());
                onCancelClicked();
            }
        );
    }

    private void enableButtons(boolean isEnable) {
        dialogView.cancelButtonEnabled().setEnabled(isEnable);
        dialogView.saveButtonProcessable().setProcessing(!isEnable);
    }

    @Inject
    protected Lang lang;
    @Inject
    protected AbsenceCommonView view;
    @Inject
    protected AbstractDialogDetailsView dialogView;
    @Inject
    protected AbsenceControllerAsync absenceController;
    @Inject
    protected PolicyService policyService;
    @Inject
    protected DefaultErrorHandler defaultErrorHandler;
}
