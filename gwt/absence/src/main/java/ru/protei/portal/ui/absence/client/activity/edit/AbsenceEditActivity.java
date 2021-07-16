package ru.protei.portal.ui.absence.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_AbsenceReason;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dto.ScheduleItem;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsActivity;
import ru.protei.portal.ui.common.client.activity.dialogdetails.AbstractDialogDetailsView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.AbsenceEvents;
import ru.protei.portal.ui.common.client.events.EmployeeEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AbsenceControllerAsync;
import ru.protei.portal.ui.common.client.util.DateUtils;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.function.Consumer;

import static com.google.gwt.user.datepicker.client.CalendarUtil.copyDate;

public abstract class AbsenceEditActivity
        implements Activity, AbstractAbsenceEditActivity, AbstractDialogDetailsActivity {

    @Inject
    public void onInit() {
        dialogView.setActivity(this);
        dialogView.removeButtonVisibility().setVisible(false);
        dialogView.saveButtonVisibility().setVisible(true);
        dialogView.setSaveOnEnterClick(false);
        dialogView.getBodyContainer().add(view.asWidget());
        dialogView.addBodyStyleName("no-padding");

        view.setActivity(this);
    }

    @Event
    public void onShow(AbsenceEvents.Create event) {
        if (!hasAccessCreate()) {
            return;
        }
        dialogView.setHeader(lang.absenceCreation());

        hideForm();
        dialogView.showPopup();

        view.contentVisibility().setVisible(true);

        value = new PersonAbsence();
        value.setPersonId(policyService.getProfile().getId());
        value.setPersonDisplayName(policyService.getProfile().getFullName());
        value.setFromTime(DateUtils.setBeginOfDay(new Date()));
        value.setTillTime(DateUtils.setEndOfDay(new Date()));

        fillView();
        view.setDateRangeValid(true);
        view.reasonValidator().setValid(true);
    }

    @Event
    public void onShow(AbsenceEvents.Edit event) {
        if (!hasAccessEdit()) {
            return;
        }
        dialogView.setHeader(lang.absenceEditing());
        this.event = event;

        hideForm();
        dialogView.showPopup();

        view.contentVisibility().setVisible(true);

        performFillView();

        view.employeeEnabled().setEnabled(false);
        view.reasonEnabled().setEnabled(false);
    }

    @Override
    public void onReasonChanged() {
        view.enableScheduleEnabled().setEnabled(isEnabledSchedule());

        if (!view.reason().getValue().equals(En_AbsenceReason.NIGHT_WORK)) {
            return;
        }

        DateInterval interval = view.dateRange().getValue();
        if (interval == null) {
            return;
        }

        interval.to.setHours(13);
        interval.to.setMinutes(0);
        interval.to.setSeconds(0);

        view.dateRange().setValue(interval);
    }

    @Override
    public void onDateRangeChanged() {
        view.setDateRangeValid(isDateRangeValid(view.dateRange().getValue()));
    }

    @Override
    public void onEnableScheduleChanged() {
        Boolean isEnabled = view.enableSchedule().getValue();
        view.scheduleVisibility().setVisible(isEnabled);
        view.scheduleCreateVisibility().setVisible(false);
    }

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

    private boolean isEnabledSchedule() {
        return Objects.equals(view.reason().getValue(), En_AbsenceReason.REMOTE_WORK)
                || Objects.equals(view.reason().getValue(), En_AbsenceReason.STUDY);
    }

    private void fillView() {
        view.employee().setValue(value.getPerson());
        view.reason().setValue(value.getReason());
        view.comment().setValue(value.getUserComment());
        view.dateRange().setValue(new DateInterval(copyDate(value.getFromTime()), copyDate(value.getTillTime())));

        boolean isScheduleDefined = CollectionUtils.isNotEmpty(value.getScheduleItems());
        view.enableSchedule().setValue(isScheduleDefined, true);
        view.enableScheduleEnabled().setEnabled(isEnabledSchedule());
        view.scheduleItems().setValue(value.getScheduleItems());
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

        if (!isDateRangeValid(view.dateRange().getValue())) {
            fireEvent(new NotifyEvents.Show(lang.absenceValidationDateRange(), NotifyEvents.NotifyType.ERROR));
            return false;
        }

//        if (isEnabledSchedule())
        return true;
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
    
    private void performFillView() {
        showLoading();
        absenceController.getAbsence(event.id, new FluentCallback<PersonAbsence>()
                .withError(throwable -> {
                    defaultErrorHandler.accept(throwable);
                    hideLoading();
                })
                .withSuccess(absence -> {
                    hideLoading();
                    value = absence;
                    fillView();
                }));
    }

    private void performSave(Consumer<Throwable> onError, Runnable onSuccess) {
        PersonAbsence personAbsence = fillDto();
        absenceController.saveAbsence(personAbsence, new FluentCallback<Long>()
                .withError(throwable -> {
                    defaultErrorHandler.accept(throwable);
                    onError.accept(throwable);
                })
                .withSuccess(result -> {
                    fireEvent(new NotifyEvents.Show(lang.absenceUpdated(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new EmployeeEvents.Update(personAbsence.getPersonId()));
                    onSuccess.run();
                }));
    }

    private boolean hasAccessEdit() {
        return policyService.hasPrivilegeFor(En_Privilege.ABSENCE_EDIT);
    }

    private boolean hasAccessCreate() {
        return policyService.hasPrivilegeFor(En_Privilege.ABSENCE_CREATE);
    }

    void showLoading() {
        view.loadingVisibility().setVisible(true);
        view.contentVisibility().setVisible(false);
        dialogView.cancelButtonVisibility().setVisible(false);
        dialogView.saveButtonVisibility().setVisible(false);
    }

    void hideLoading() {
        view.loadingVisibility().setVisible(false);
        view.contentVisibility().setVisible(true);
        dialogView.cancelButtonVisibility().setVisible(true);
        dialogView.saveButtonVisibility().setVisible(true);
    }

    private void hideForm() {
        view.contentVisibility().setVisible(false);
    }

    private PersonAbsence fillDto() {
        PersonAbsence absence = new PersonAbsence();
        absence.setPersonId(view.employee().getValue().getId());
        absence.setReason(view.reason().getValue());
        absence.setUserComment(view.comment().getValue());
        absence.setId(event.id);

        DateInterval dateInterval = view.dateRange().getValue();
        absence.setFromTime(dateInterval.from);
        absence.setTillTime(dateInterval.to);

        return absence;
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
    PolicyService policyService;
    @Inject
    AbsenceControllerAsync absenceController;
    @Inject
    DefaultErrorHandler defaultErrorHandler;

    private PersonAbsence value;
    private AbsenceEvents.Edit event;
}
