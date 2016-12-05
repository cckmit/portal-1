package ru.protei.portal.ui.common.client.common;

import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.ui.common.client.events.AuthEvents;
import ru.protei.portal.ui.common.client.service.IssueServiceAsync;
import ru.protei.portal.ui.common.shared.model.RequestCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Статусы обращений
 */
public abstract class IssueStates implements Activity{

    @Event
    public void onInit( AuthEvents.Init event ) {
        issueService.getStateList(new RequestCallback<List<En_CaseState>>() {
            @Override
            public void onError(Throwable throwable) {
                Window.alert("issue states error");
            }

            @Override
            public void onSuccess(List<En_CaseState> en_caseStates) {
                activeStates = new ArrayList<>();
                inactiveStates = new ArrayList<>();

                int doneStateId = En_CaseState.DONE.getId();
                for(En_CaseState state: en_caseStates){
                    if(state.getId() >= doneStateId)
                        activeStates.add(state);
                    else
                        inactiveStates.add(state);
                }

                states = Collections.unmodifiableList(en_caseStates);
                activeStates = Collections.unmodifiableList(activeStates);
                inactiveStates = Collections.unmodifiableList(inactiveStates);

                //initialization.completeTask(Initialization.PreparatoryTask.ISSUE_STATES_LOADING);
            }
        });
    }

    public List<En_CaseState> getAllStates(){
        return states;
    }

    public List<En_CaseState> getActiveStates(){
        return activeStates;
    }

    public List<En_CaseState> getInactiveStates(){
        return inactiveStates;
    }


    @Inject
    IssueServiceAsync issueService;

    private List<En_CaseState> states;
    private List<En_CaseState> activeStates;
    private List<En_CaseState> inactiveStates;

}
