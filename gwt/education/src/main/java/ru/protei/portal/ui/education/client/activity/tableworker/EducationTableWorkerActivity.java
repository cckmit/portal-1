package ru.protei.portal.ui.education.client.activity.tableworker;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.EducationEntry;
import ru.protei.portal.core.model.ent.EducationEntryAttendance;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.EducationEvents;
import ru.protei.portal.ui.common.client.events.ForbiddenEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.En_ResultStatusLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.EducationControllerAsync;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

public abstract class EducationTableWorkerActivity implements Activity, AbstractEducationTableWorkerActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onShow(EducationEvents.ShowWorkerTable event) {
        HasWidgets container = event.parent;
        boolean isWorker = policyService.hasPrivilegeFor(En_Privilege.EDUCATION_VIEW);
        boolean isWorkerCanRequest = isWorker && policyService.hasPrivilegeFor(En_Privilege.EDUCATION_EDIT);
        boolean isAdmin = policyService.hasPrivilegeFor(En_Privilege.EDUCATION_CREATE);
        boolean hasAccess = isWorker || isAdmin;
        if (!hasAccess) {
            fireEvent(new ForbiddenEvents.Show(container));
            return;
        }
        container.clear();
        container.add(view.asWidget());
        view.showRequestAttendanceAction(isWorker);
        view.showRequestEntryAction(isWorkerCanRequest);
        loadTable();
    }

    @Override
    public void requestEntry() {
        boolean isWorker = policyService.hasPrivilegeFor(En_Privilege.EDUCATION_VIEW);
        boolean isWorkerCanRequest = isWorker && policyService.hasPrivilegeFor(En_Privilege.EDUCATION_EDIT);
        if (!isWorkerCanRequest) {
            return;
        }
        fireEvent(new EducationEvents.EditEducationEntry());
    }

    @Override
    public void requestAttendance(EducationEntry entry) {
        boolean isWorker = policyService.hasPrivilegeFor(En_Privilege.EDUCATION_VIEW);
        if (!isWorker || entry == null) {
            return;
        }
        educationController.requestNewAttendance(entry.getId(), new FluentCallback<EducationEntryAttendance>()
                .withSuccess(attendance -> {
                    fireEvent(new NotifyEvents.Show(lang.educationEntryAttendanceRequested(), NotifyEvents.NotifyType.SUCCESS));
                    fireEvent(new EducationEvents.Show());
                }));
    }

    private void loadTable() {
        view.showLoader(true);
        view.clearRecords();
        educationController.getCurrentEntries(new FluentCallback<List<EducationEntry>>()
                .withError(throwable -> {
                    if (throwable instanceof RequestFailedException) {
                        fireEvent(new NotifyEvents.Show(resultStatusLang.getMessage(((RequestFailedException) throwable).status), NotifyEvents.NotifyType.ERROR));
                    } else {
                        fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                    }
                    view.showLoader(false);
                    view.setTotalRecords(0);
                })
                .withSuccess(educationEntryList -> {
                    view.showLoader(false);
                    view.setTotalRecords(educationEntryList.size());
                    view.putRecords(educationEntryList);
                }));
    }

    @Inject
    Lang lang;
    @Inject
    En_ResultStatusLang resultStatusLang;
    @Inject
    PolicyService policyService;
    @Inject
    EducationControllerAsync educationController;
    @Inject
    AbstractEducationTableWorkerView view;
}
