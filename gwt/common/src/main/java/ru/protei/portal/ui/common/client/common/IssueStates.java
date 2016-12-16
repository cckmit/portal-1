package ru.protei.portal.ui.common.client.common;

import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.ui.common.client.events.AuthEvents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Статусы обращений
 */
public abstract class IssueStates implements Activity{

    @Event
    public void onInit( AuthEvents.Init event ) {

        states = new ArrayList<>(7);
        states.add(En_CaseState.CREATED);
        states.add(En_CaseState.OPENED);
        states.add(En_CaseState.ACTIVE);
        states.add(En_CaseState.TEST_LOCAL);
        states.add(En_CaseState.DONE);
        states.add(En_CaseState.TEST_CUST);
        states.add(En_CaseState.VERIFIED);

        activeStates = new ArrayList<>(4);
        activeStates.add(En_CaseState.CREATED);
        activeStates.add(En_CaseState.OPENED);
        activeStates.add(En_CaseState.ACTIVE);
        activeStates.add(En_CaseState.TEST_LOCAL);

        inactiveStates = new ArrayList<>(3);
        inactiveStates.add(En_CaseState.DONE);
        inactiveStates.add(En_CaseState.TEST_CUST);
        inactiveStates.add(En_CaseState.VERIFIED);

//        issueService.getStateList(new RequestCallback<List<En_CaseState>>() {
//            @Override
//            public void onError(Throwable throwable) {
//                Window.alert("issue states error");
//            }
//
//            @Override
//            public void onSuccess(List<En_CaseState> en_caseStates) {
//                activeStates = new ArrayList<>();
//                inactiveStates = new ArrayList<>();
//
//                int doneStateId = En_CaseState.DONE.getId();
//                for(En_CaseState state: en_caseStates){
//                    if(state.getId() < doneStateId)
//                        activeStates.add(state);
//                    else
//                        inactiveStates.add(state);
//                }
//
//                states = Collections.unmodifiableList(en_caseStates);
//                activeStates = Collections.unmodifiableList(activeStates);
//                inactiveStates = Collections.unmodifiableList(inactiveStates);
//            }
//        });

        states = Collections.unmodifiableList(states);
        activeStates = Collections.unmodifiableList(activeStates);
        inactiveStates = Collections.unmodifiableList(inactiveStates);
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


//    @Inject
//    IssueServiceAsync issueService;

    private List<En_CaseState> states;
    private List<En_CaseState> activeStates;
    private List<En_CaseState> inactiveStates;

}