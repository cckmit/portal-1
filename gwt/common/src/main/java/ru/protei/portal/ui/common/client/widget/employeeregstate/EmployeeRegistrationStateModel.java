package ru.protei.portal.ui.common.client.widget.employeeregstate;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.CaseStateEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CaseStateControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.LifecycleSelectorModel;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

public abstract class EmployeeRegistrationStateModel extends LifecycleSelectorModel<CaseState> {

    @Event
    public void onInit(AuthEvents.Success event) {
        clear();
    }

    @Event
    public void onCaseStateChanged(CaseStateEvents.ChangeModel event) {
        refreshOptions();
    }

    @Override
    protected void refreshOptions() {
        caseStateController.getCaseStates(En_CaseType.EMPLOYEE_REGISTRATION, new FluentCallback<List<CaseState>>()
                .withError(throwable ->
                    fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR))
                )
                .withSuccess(caseStates -> {
                    notifySubscribers(caseStates);
                }
        ));
    }

    @Inject
    Lang lang;

    @Inject
    CaseStateControllerAsync caseStateController;
}
