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
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class AbsenceCreateActivity extends AbsenceCommonActivity {

    @Inject
    public void onInit() {
        super.onInit();
        view.getDateContainer().add(createView);
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
//        createView.setDateRangeValid(isDateRangeValid(createView.dateRange().getValue()));
    }

    private boolean hasAccessCreate() {
        return policyService.hasPrivilegeFor(En_Privilege.ABSENCE_CREATE);
    }

    protected void fillView() {
        fillView(new PersonAbsence());

        createView.dateRange().setValue(new ArrayList<>());
//        createView.setDateRangeValid(isDateRangeValid(view.dateRange().getValue()));
        dialogView.saveButtonVisibility().setVisible(true);
    }

    protected boolean validate() {
//        if (!isDateRangeValid(view.dateRange().getValue())) {
//            fireEvent(new NotifyEvents.Show(lang.absenceValidationDateRange(), NotifyEvents.NotifyType.ERROR));
//            return false;
//        }
        return true;
    }

    @Override
    protected void save(Consumer<Long> success) {
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
                })
                .withSuccess(absence -> {
                    fireEvent(new NotifyEvents.Show(lang.absenceCreated(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new EmployeeEvents.Update(collect.get(0).getPersonId()));
                    success.accept(0L);
                }));
    }

    private PersonAbsence fillDTO() {
        return fillCommonDTO();
    }

    @Inject
    AbstractAbsenceCreateView createView;
}
