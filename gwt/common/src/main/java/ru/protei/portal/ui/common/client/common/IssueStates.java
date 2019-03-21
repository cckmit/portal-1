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

        states = new ArrayList<>(11);
        states.add(En_CaseState.CREATED);
        states.add(En_CaseState.OPENED);
        states.add(En_CaseState.ACTIVE);
        states.add(En_CaseState.INFO_REQUEST);
        states.add(En_CaseState.WORKAROUND);
        states.add(En_CaseState.TEST_LOCAL);
        states.add(En_CaseState.CUST_PENDING);
        states.add(En_CaseState.DONE);
        states.add(En_CaseState.TEST_CUST);
        states.add(En_CaseState.VERIFIED);
        states.add(En_CaseState.CANCELED);

        activeStates = new ArrayList<>(8);
        activeStates.add(En_CaseState.CREATED);
        activeStates.add(En_CaseState.OPENED);
        activeStates.add(En_CaseState.ACTIVE);
        activeStates.add(En_CaseState.TEST_LOCAL);
        activeStates.add(En_CaseState.WORKAROUND);
        activeStates.add(En_CaseState.INFO_REQUEST);
        activeStates.add(En_CaseState.CUST_PENDING);
        activeStates.add(En_CaseState.TEST_CUST);

        inactiveStates = new ArrayList<>(3);
        inactiveStates.add(En_CaseState.DONE);
        inactiveStates.add(En_CaseState.VERIFIED);
        inactiveStates.add(En_CaseState.CANCELED);


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

    private List<En_CaseState> states;
    private List<En_CaseState> activeStates;
    private List<En_CaseState> inactiveStates;

}
