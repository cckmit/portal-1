package ru.protei.portal.ui.absencereport.client.activity.create;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.*;

public abstract class AbsenceReportCreateActivity implements AbstractAbsenceReportCreateActivity, Activity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        resetView();
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(AbsenceReportEvents.Show event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.ABSENCE_REPORT)) {
            fireEvent(new ForbiddenEvents.Show());
            return;
        }

        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());
    }

    @Override
    public void onResetClicked() {
        resetView();
    }

    private void resetView() {
        view.dateRange().setValue(null);
        view.markDateRangeError();
        view.employees().setValue(null);
        view.reasons().setValue(null);
        view.sortField().setValue(En_SortField.absence_person);
        view.sortDir().setValue(true);
    }

    @Inject
    AbstractAbsenceReportCreateView view;

    @Inject
    PolicyService policyService;

    private AppEvents.InitDetails initDetails;
}
