package ru.protei.portal.ui.absence.client.activity.table;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.core.model.query.AbsenceQuery;
import ru.protei.portal.ui.common.client.events.AbsenceEvents;
import ru.protei.portal.ui.common.client.events.ConfirmDialogEvents;
import ru.protei.portal.ui.common.client.events.EmployeeEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AbsenceControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.Date;
import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.setOf;
import static ru.protei.portal.ui.common.client.util.DateUtils.setBeginOfDay;

public abstract class AbsenceTableActivity implements AbstractAbsenceTableActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onShow(AbsenceEvents.Show event) {

        view.clearRecords();

        event.parent.clear();
        event.parent.add(view.asWidget());

        if (event.employeeId != null) {
            AbsenceQuery query = makeQuery(event.employeeId);
            requestData(query);
        }
    }

    @Override
    public void onItemClicked(PersonAbsence value) {}

    @Override
    public void onCompleteAbsence(PersonAbsence value) {
        absenceController.completeAbsence(value.getId(), new FluentCallback<Boolean>()
                .withSuccess(result -> {
                    fireEvent(new NotifyEvents.Show(lang.absenceCompleteSuccessful(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new EmployeeEvents.Show());
                }));
    }

    @Override
    public void onEditClicked(PersonAbsence value) {
        fireEvent(new AbsenceEvents.Edit(value.getId()));
    }

    @Override
    public void onRemoveClicked(PersonAbsence value) {
        fireEvent(new ConfirmDialogEvents.Show(lang.absenceRemoveConfirmMessage(), removeAction(value)));
    }

    private AbsenceQuery makeQuery(Long employeeId) {
        AbsenceQuery query = new AbsenceQuery(setOf(employeeId));
        query.setFromTime(setBeginOfDay(new Date()));
        return query;
    }

    private void requestData(AbsenceQuery query) {
        absenceController.getAbsences(query, new FluentCallback<List<PersonAbsence>>()
                .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR)))
                .withSuccess(absences -> view.addRecords(absences)));
    }

    private Runnable removeAction(PersonAbsence value) {
        return () -> absenceController.removeAbsence(value.getId(), new FluentCallback<Boolean>()
                .withSuccess(result -> {
                    fireEvent(new NotifyEvents.Show(lang.absenceRemoveSuccessful(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new EmployeeEvents.Show());
                }));
    }

    @Inject
    AbstractAbsenceTableView view;

    @Inject
    AbsenceControllerAsync absenceController;

    @Inject
    Lang lang;

}
