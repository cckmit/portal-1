package ru.protei.portal.ui.delivery.client.activity.delivery.module.state;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.service.CaseStateControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.ArrayList;
import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.stream;

/**
 * Модель статусов модулей
 */
public abstract class ModuleStateModel implements Activity {

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
                caseStateController.getCaseStatesOmitPrivileges(En_CaseType.MODULE, new FluentCallback<List<CaseState>>()
                .withSuccess(this::notifySubscribers));
    }

    private void notifySubscribers(List<CaseState> caseStates) {
        stream(subscribers).forEach(subscriber -> subscriber.onStatesLoaded(caseStates));
    }

    @Inject
    CaseStateControllerAsync caseStateController;

    public void subscribeStates(ModuleStateSubscriber subscriber){
        subscribers.add(subscriber);
    }
    private List<ModuleStateSubscriber> subscribers = new ArrayList<>();
}
