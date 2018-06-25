package ru.protei.portal.ui.issue.client.widget.state;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.events.CaseStateEvents;
import ru.protei.portal.ui.common.client.events.IssueEvents;
import ru.protei.portal.ui.common.client.service.IssueServiceAsync;
import ru.protei.portal.ui.common.client.widget.selector.base.ModelSelector;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Модель статусов
 */
public abstract class StateModel implements Activity {

    @Event
    public void onInit( AuthEvents.Success event ) {
        refreshOptions();
    }

    @Event
    public void onStateListChanged(IssueEvents.ChangeStateModel event) {
        refreshOptions();
    }

    @Event
    public void onUpdateSelectorOptions(CaseStateEvents.UpdateSelectorOptions event) {
        for ( ModelSelector<En_CaseState > selector : subscribers ) {
            selector.fillOptions( list );
            selector.refreshValue();
        }
    }

    public void subscribe( ModelSelector<En_CaseState> selector ) {
        subscribers.add( selector );
        selector.fillOptions( list );
    }

    private void notifySubscribers() {
        for ( ModelSelector<En_CaseState > selector : subscribers ) {
            selector.fillOptions( list );
            selector.refreshValue();
        }
    }

    private void refreshOptions() {

        issueService.getStateList(
            new RequestCallback<List<En_CaseState>>() {
                @Override
                public void onError(Throwable throwable) {
                }

                @Override
                public void onSuccess(List<En_CaseState> caseStates) {
                    list.clear();
                    list.addAll( caseStates );

                    notifySubscribers();
                }
            }
        );
    }

    @Inject
    IssueServiceAsync issueService;

    private List< En_CaseState > list = new ArrayList<>();

    List<ModelSelector<En_CaseState>> subscribers = new ArrayList<>();
}
