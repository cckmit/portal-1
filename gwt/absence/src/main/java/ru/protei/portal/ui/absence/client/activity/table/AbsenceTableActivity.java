package ru.protei.portal.ui.absence.client.activity.table;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.core.model.query.AbsenceQuery;
import ru.protei.portal.ui.common.client.events.AbsenceEvents;
import ru.protei.portal.ui.common.client.events.ConfirmDialogEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AbsenceControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.setOf;

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
            requestData(new AbsenceQuery(setOf(event.employeeId)));
        }
    }

    @Override
    public void onItemClicked(PersonAbsence value) {}

    @Override
    public void onRemoveClicked(PersonAbsence value) {
/*
        if (value != null) {
            fireEvent(new ConfirmDialogEvents.Show(lang.absenceRemoveConfirmMessage(), removeAction(value.getId())));
        }
*/
    }

    private void requestData(AbsenceQuery query) {
        absenceController.getAbsences(query, new FluentCallback<List<PersonAbsence>>()
                .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR)))
                .withSuccess(absences -> view.addRecords(absences)));
    }

/*
    private Runnable removeAction(Long absenceId) {
        return () -> ;
    }
*/

    @Inject
    AbstractAbsenceTableView view;

    @Inject
    AbsenceControllerAsync absenceController;

    @Inject
    Lang lang;

}
