package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.dict.En_CaseState;

/**
 * Created by michael on 16.05.17.
 */
public class CaseStateChange {
    En_CaseState oldState;
    En_CaseState newState;

    public CaseStateChange(En_CaseState oldState, En_CaseState newState) {
        this.oldState = oldState;
        this.newState = newState;
    }

    public En_CaseState getOldState() {
        return oldState;
    }

    public En_CaseState getNewState() {
        return newState;
    }

    @Override
    public int hashCode() {
        return this.oldState.hashCode() + this.newState.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        //
        if (obj instanceof CaseStateChange) {

            CaseStateChange cmp = (CaseStateChange) obj;
            return cmp.oldState == this.oldState && cmp.newState == this.newState;
        }

        return false;
    }
}
