package ru.protei.portal.ui.absence.client.activity.summarytable;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.core.model.query.AbsenceQuery;
import ru.protei.portal.ui.common.client.events.*;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.AbsenceControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

public abstract class AbsenceSummaryTableActivity implements AbstractAbsenceSummaryTableActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onInit(AppEvents.InitDetails event) {
        this.initDetails = event;
    }

    @Event
    public void onShow(AbsenceEvents.ShowSummaryTable event) {
        view.clearRecords();

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());

        AbsenceQuery query = view.getFilterWidget().getFilterParamView().getQuery();
        requestData(query);
    }

    @Override
    public void onItemClicked(PersonAbsence value) {}

    @Override
    public void onCompleteAbsence(PersonAbsence value) {
        absenceController.completeAbsence(value, new FluentCallback<Boolean>()
                .withSuccess(result -> {
                    fireEvent(new NotifyEvents.Show(lang.absenceCompletedSuccessfully(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new EmployeeEvents.Update(value.getPersonId()));
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

    private void requestData(AbsenceQuery query) {
        absenceController.getAbsences(query, new FluentCallback<List<PersonAbsence>>()
                .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR)))
                .withSuccess(absences -> view.addRecords(absences)));
    }

    private Runnable removeAction(PersonAbsence value) {
        return () -> absenceController.removeAbsence(value, new FluentCallback<Boolean>()
                .withSuccess(result -> {
                    fireEvent(new NotifyEvents.Show(lang.absenceRemovedSuccessfully(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new EmployeeEvents.Update(value.getPersonId()));
                }));
    }

    @Inject
    AbstractAbsenceSummaryTableView view;

    @Inject
    AbsenceControllerAsync absenceController;

    @Inject
    Lang lang;

    private AppEvents.InitDetails initDetails;
}
