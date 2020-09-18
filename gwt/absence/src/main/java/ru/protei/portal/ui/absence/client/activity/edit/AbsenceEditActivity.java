package ru.protei.portal.ui.absence.client.activity.edit;

import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.ui.absence.client.activity.common.AbsenceCommonActivity;
import ru.protei.portal.ui.common.client.events.AbsenceEvents;
import ru.protei.portal.ui.common.client.events.EmployeeEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.function.Consumer;

import static com.google.gwt.user.datepicker.client.CalendarUtil.copyDate;

public abstract class AbsenceEditActivity extends AbsenceCommonActivity {

    @Inject
    public void onInit() {
        super.onInit();
        view.getDateContainer().add(editView.asWidget());
        editView.setActivity(this);

        view.employeeEnabled().setEnabled(false);
        view.reasonEnabled().setEnabled(false);
    }

    @Event
    public void onShow(AbsenceEvents.Edit event) {
        if (!hasAccessEdit()) {
            return;
        }
        dialogView.setHeader(lang.absenceEditing());
        this.event = event;
        onShow();
    }

    @Override
    public void onDateRangeChanged() {
        editView.setDateRangeValid(isDateRangeValid(editView.dateRange().getValue()));
    }

    protected void performFillView() {
        showLoading();
        absenceController.getAbsence(event.id, new FluentCallback<PersonAbsence>()
                .withError(throwable -> {
                    defaultErrorHandler.accept(throwable);
                    hideLoading();
                })
                .withSuccess(absence -> {
                    hideLoading();
                    fillView(absence);
                    editView.dateRange().setValue(new DateInterval(copyDate(absence.getFromTime()), copyDate(absence.getTillTime())));
                    editView.setDateRangeValid(isDateRangeValid(editView.dateRange().getValue()));
                }));
    }


    protected boolean additionalValidate() {
        if (!isDateRangeValid(editView.dateRange().getValue())) {
            fireEvent(new NotifyEvents.Show(lang.absenceValidationDateRange(), NotifyEvents.NotifyType.ERROR));
            return false;
        }
        return true;
    }

    @Override
    protected void performSave(Consumer<Throwable> onError, Runnable onSuccess) {
        PersonAbsence personAbsence = fillDTO();
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

    private PersonAbsence fillDTO() {
        PersonAbsence absence = fillCommonDTO();

        absence.setId(event.id);

        DateInterval dateInterval = editView.dateRange().getValue();
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
    AbstractAbsenceEditView editView;

    private AbsenceEvents.Edit event;
}
