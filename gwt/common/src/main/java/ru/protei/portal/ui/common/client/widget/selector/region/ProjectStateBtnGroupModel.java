package ru.protei.portal.ui.common.client.widget.selector.region;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.NotifyEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.selector.model.BaseSelectorModel;
import ru.protei.portal.ui.common.client.service.CaseStateControllerAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.SelectorWithModel;
import ru.protei.portal.ui.common.shared.model.FluentCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Модель состояния проекта для группы кнопок
 */
public abstract class ProjectStateBtnGroupModel extends BaseSelectorModel<CaseState> implements Activity {

    @Event
    public void onAuthSuccess(AuthEvents.Success event) {
        refreshOptions();
    }

    public void subscribe(SelectorWithModel<CaseState> selector) {
        subscribers.add(selector);
        selector.fillOptions(caseStates);
    }

    private void notifySubscribers(List<CaseState> caseStates) {
        subscribers.forEach(subscriber -> {
            subscriber.fillOptions(caseStates);
            subscriber.refreshValue();
        });
    }

    private void refreshOptions() {
        caseStateController.getCaseStatesOmitPrivileges(En_CaseType.PROJECT, new FluentCallback<List<CaseState>>()
                .withError(throwable -> fireEvent(new NotifyEvents.Show(lang.errGetList(), NotifyEvents.NotifyType.ERROR)))
                .withSuccess(this::notifySubscribers));
    }

    @Inject
    Lang lang;
    @Inject
    CaseStateControllerAsync caseStateController;

    private final List<SelectorWithModel<CaseState>> subscribers = new ArrayList<>();
    private List<CaseState> caseStates = new ArrayList<>();
}
