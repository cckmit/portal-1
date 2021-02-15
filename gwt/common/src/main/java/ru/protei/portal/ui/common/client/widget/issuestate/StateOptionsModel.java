package ru.protei.portal.ui.common.client.widget.issuestate;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.CaseStateControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.LifecycleSelectorModel;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.List;

/**
 * Модель статусов
 */
public abstract class StateOptionsModel extends LifecycleSelectorModel<CaseState> {

    @Event
    public void onInit(AuthEvents.Success event) {
        clear();
    }

    @Event
    public void onStateListChanged(IssueEvents.ChangeStateModel event) {
        refreshOptions();
    }

    @Override
    public void refreshOptions() {
        caseStateController.getCaseStatesOmitPrivileges(En_CaseType.CRM_SUPPORT, new FluentCallback<List<CaseState>>()
                .withError(throwable -> {
                    fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR));
                })
                .withSuccess(this::notifySubscribers));
    }

    @Inject
    Lang lang;
    @Inject
    CaseStateControllerAsync caseStateController;
}
