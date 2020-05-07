package ru.protei.portal.ui.absence.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AbsenceControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

public abstract class AbsenceEditActivity implements AbstractAbsenceEditActivity, AbstractDialogDetailsActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
        dialogView.setActivity(this);
        dialogView.getBodyContainer().add(view.asWidget());
    }

    @Override
    public void onRemoveClicked() {

    }

    @Override
    public void onSaveClicked() {

        if (!hasPrivileges() || !validateView()) {
            return;
        }

        absenceController.saveAbsence(fillDTO(), new FluentCallback<Long>()
                .withSuccess(result -> {
                    onCancelClicked();
                    fireEvent(new NotifyEvents.Show(lang.absenceUpdated(), NotifyEvents.NotifyType.SUCCESS));
                }));
    }

    @Override
    public void onCancelClicked() {
        absence = null;
        dialogView.hidePopup();
    }

    private PersonAbsence fillDTO() {
        absence.setPersonId(view.employee().getValue().getId());
        DateInterval dateInterval = view.dateRange().getValue();
        absence.setFromTime(dateInterval.from);
        absence.setTillTime(dateInterval.to);
        absence.setReason(view.reason().getValue());
        return absence;
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

    private boolean hasPrivileges() {
        if (isNew() && policyService.hasPrivilegeFor(En_Privilege.ABSENCE_CREATE)) {
            return true;
        }

        if (!isNew() && policyService.hasPrivilegeFor(En_Privilege.ABSENCE_EDIT)) {
            return true;
        }

        return false;
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

    private PersonAbsence absence;
}
