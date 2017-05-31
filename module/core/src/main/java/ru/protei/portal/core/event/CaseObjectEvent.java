package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.service.CaseService;
import sun.plugin.PluginURLJarFileCallBack;

import java.util.Date;

/**
 * Created by michael on 04.05.17.
 */
public class CaseObjectEvent extends ApplicationEvent {

    private CaseObject newState;
    private CaseObject oldState;

    public CaseObjectEvent(CaseService source, CaseObject newState) {
        this (source, newState, null);
    }

    public CaseObjectEvent(CaseService source, CaseObject newState, CaseObject oldState) {
        super(source);
        this.newState = newState;
        this.oldState = oldState;
    }

    public boolean isCreateEvent () {
        return this.oldState == null;
    }

    public boolean isUpdateEvent () {
        return this.oldState != null;
    }

    public boolean isCaseStateChanged () {
        return isUpdateEvent() && newState.getState() != oldState.getState();
    }

    public boolean isCaseImportanceChanged () {
        return isUpdateEvent() && !newState.getImpLevel().equals(oldState.getImpLevel());
    }

    public boolean isManagerChanged () {
        return isUpdateEvent() && !HelperFunc.equals(newState.getManagerId(),oldState.getManagerId());
    }

    public boolean isProductChanged() {
        return isUpdateEvent() && !HelperFunc.equals( newState.getProductId(), oldState.getProductId() );
    }

    public boolean isInitiatorChanged() {
        return isUpdateEvent() && !HelperFunc.equals( newState.getInitiatorId(), oldState.getInitiatorId() );
    }

    public boolean isInitiatorCompanyChanged() {
        return isUpdateEvent() && !HelperFunc.equals( newState.getInitiatorCompanyId(), oldState.getInitiatorCompanyId() );
    }

    public boolean isInfoChanged() {
        return isUpdateEvent() && !HelperFunc.equals( newState.getInfo(), oldState.getInfo() );
    }

    public Date getEventDate () {
        return new Date(getTimestamp());
    }

    public CaseObject getCaseObject () {
        return newState != null ? newState : oldState;
    }

    public CaseObject getNewState() {
        return newState;
    }

    public CaseObject getOldState() {
        return oldState;
    }

    public CaseService getCaseService () {
        return (CaseService) getSource();
    }
}
