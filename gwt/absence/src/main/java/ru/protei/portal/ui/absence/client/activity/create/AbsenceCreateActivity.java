package ru.protei.portal.ui.absence.client.activity.create;

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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class AbsenceCreateActivity extends AbsenceCommonActivity {

    @Inject
    public void onInit() {
        super.onInit();
        view.getDateContainer().add(createView.asWidget());
        createView.setActivity(this);
    }

    @Event
    public void onShow(AbsenceEvents.Create event) {
        if (!hasAccessCreate()) {
            return;
        }
        dialogView.setHeader(lang.absenceCreation());
        onShow();
    }

    @Override
    public void onDateRangeChanged() {
        List<DateInterval> value = createView.dateRange().getValue();
        if (isDateRangeValid(value)) {
            createView.setDateRangeValid(isDateRangesIntersectValid(value));
        } else {
            createView.setDateRangeValid(true);
        }
    }

    protected void performFillView() {
        fillView(new PersonAbsence());

        createView.dateRange().setValue(new ArrayList<>());
        createView.setDateRangeValid(true);
        dialogView.saveButtonVisibility().setVisible(true);
    }

    protected boolean additionalValidate() {
        if (!isDateRangeValid(createView.dateRange().getValue())) {
            fireEvent(new NotifyEvents.Show(lang.absenceValidationDateRanges(), NotifyEvents.NotifyType.ERROR));
            return false;
        }
        if (!isDateRangesIntersectValid(createView.dateRange().getValue())) {
            fireEvent(new NotifyEvents.Show(lang.absenceValidationDateRangesIntersection(), NotifyEvents.NotifyType.ERROR));
            return false;
        }
        return true;
    }

    @Override
    protected void performSave(Consumer<Throwable> onError, Runnable onSuccess) {
        List<DateInterval> value = createView.dateRange().getValue();
        List<PersonAbsence> collect = value.stream().map(date -> {
            PersonAbsence personAbsence = fillDTO();
            personAbsence.setFromTime(date.from);
            personAbsence.setTillTime(date.to);
            return personAbsence;
        }).collect(Collectors.toList());

        absenceController.saveAbsences(collect, new FluentCallback<List<Long>>()
                .withError(throwable -> {
                    defaultErrorHandler.accept(throwable);
                    onError.accept(throwable);
                })
                .withSuccess(absence -> {
                    fireEvent(new NotifyEvents.Show(lang.absenceCreated(collect.size()), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new EmployeeEvents.Update(collect.get(0).getPersonId()));
                    onSuccess.run();
                }));
    }

    private boolean hasAccessCreate() {
        return policyService.hasPrivilegeFor(En_Privilege.ABSENCE_CREATE);
    }

    private PersonAbsence fillDTO() {
        return fillCommonDTO();
    }

    private boolean isDateRangesIntersectValid(List<DateInterval> dateIntervals) {
        List<DateInterval> temp = new ArrayList<>(dateIntervals);
        temp.sort(Comparator.nullsFirst(Comparator.comparing(date -> date.from)));
        boolean result = true;
        for (int i = 0; i < temp.size()-1; i++) {
            if (temp.get(i).to.after(temp.get(i + 1).from)) {
                result = false;
                break;
            }
        }
        return result;
    }

    private boolean isDateRangeValid(List<DateInterval> dateIntervals) {
        for (DateInterval dateInterval : dateIntervals) {
            if (!(dateInterval != null &&
                    dateInterval.from != null &&
                    dateInterval.to != null &&
                    dateInterval.from.before(dateInterval.to))) {
                return false;
            }
        }
        return true;
    }

    @Inject
    AbstractAbsenceCreateView createView;
}
