package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.service.CaseService;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Date;

public class CollectingCaseObjectEvent extends ApplicationEvent {

    private CaseObject lastState;
    private CaseObject initState;
    private Person person;
    private ServiceModule serviceModule;
    private final LocalTime timeCreated = LocalTime.now(ZoneOffset.UTC);
    private LocalTime lastUpdated = timeCreated;

    public CollectingCaseObjectEvent(CaseService caseService, CaseObject lastState, Person initiator ) {
        this (ServiceModule.GENERAL, caseService, lastState, null, initiator);
    }

    public CollectingCaseObjectEvent(CaseService caseService, CaseObject lastState, CaseObject initState, Person currentPerson ){
        this (ServiceModule.GENERAL, caseService, lastState, initState, currentPerson);
    }

    public CollectingCaseObjectEvent(ServiceModule module, CaseService caseService, CaseObject lastState, CaseObject initState, Person currentPerson ) {
        super( caseService );
        this.lastState = lastState;
        this.initState = initState;
        this.person = currentPerson;
        this.serviceModule = module;
    }

    public boolean isCreateEvent () {
        return this.initState == null;
    }

    public boolean isUpdateEvent () {
        return this.initState != null;
    }

    public boolean isCaseStateChanged () {
        return isUpdateEvent() && lastState.getState() != initState.getState();
    }

    public boolean isCaseImportanceChanged () {
        return isUpdateEvent() && !lastState.getImpLevel().equals(initState.getImpLevel());
    }

    public boolean isManagerChanged () {
        return isUpdateEvent() && !HelperFunc.equals(lastState.getManagerId(), initState.getManagerId());
    }

    public boolean isProductChanged() {
        return isUpdateEvent() && !HelperFunc.equals( lastState.getProductId(), initState.getProductId() );
    }

    public boolean isInitiatorChanged() {
        return isUpdateEvent() && !HelperFunc.equals( lastState.getInitiatorId(), initState.getInitiatorId() );
    }

    public boolean isInitiatorCompanyChanged() {
        return isUpdateEvent() && !HelperFunc.equals( lastState.getInitiatorCompanyId(), initState.getInitiatorCompanyId() );
    }

    public boolean isInfoChanged() {
        return isUpdateEvent() && !HelperFunc.equals( lastState.getInfo(), initState.getInfo() );
    }

    public boolean isNameChanged() {
        return isUpdateEvent() && !HelperFunc.equals( lastState.getName(), initState.getName() );
    }

    public boolean isPrivacyChanged(){
        return isUpdateEvent() && lastState.isPrivateCase() != initState.isPrivateCase();
    }

    public long timeElapsed() {
        return Duration.between(timeCreated, LocalTime.now(ZoneOffset.UTC)).toMillis() / 1000L;
    }

    public void attachCaseObjectEvent() {

    }

    public ServiceModule getServiceModule() {
        return serviceModule != null ? serviceModule : ServiceModule.GENERAL;
    }

    public Date getEventDate () {
        return new Date(getTimestamp());
    }

    public CaseObject getCaseObject () {
        return lastState != null ? lastState : initState;
    }

    public CaseObject getLastState() {
        return lastState;
    }

    public CaseObject getInitState() {
        return initState;
    }

    public CaseService getCaseService () {
        return (CaseService) getSource();
    }

    public Person getPerson() {
        return person;
    }
}
