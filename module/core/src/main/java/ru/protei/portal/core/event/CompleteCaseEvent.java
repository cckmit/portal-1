package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.service.CaseService;

import java.util.Date;

import static java.lang.System.nanoTime;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * @review Вот название AssemblyService мне очень понравилось, может и событие тогда будет AssembledCaseEvent ?
 */
public class CompleteCaseEvent extends ApplicationEvent {

    private CaseObject lastState;
    private CaseObject initState;
    private CaseComment comment;

    /** @review лучше бы назвать initiator, а то непонятно, что за person и почему он тут
     */
    private Person person;
    private ServiceModule serviceModule;
    private final long timeCreated;
    private long lastUpdated;

    public CompleteCaseEvent(CaseService caseService, CaseObject lastState, Person initiator) {
        this(ServiceModule.GENERAL, caseService, lastState, initiator);
    }

    public CompleteCaseEvent(CaseService caseService, CaseObject lastState, CaseObject initState,
                             Person currentPerson) {
        this(ServiceModule.GENERAL, caseService, lastState, currentPerson);
    }

    public CompleteCaseEvent(CaseObjectEvent objectEvent) {
        this(objectEvent.getServiceModule(), objectEvent.getCaseService(), objectEvent.getNewState()
                , objectEvent.getPerson());
    }

    public CompleteCaseEvent(CaseCommentEvent commentEvent) {
        this(commentEvent.getServiceModule(), commentEvent.getCaseService(), commentEvent.getCaseObject(),
                commentEvent.getPerson());
    }

    public CompleteCaseEvent(ServiceModule module, CaseService caseService,
                             CaseObject initState, Person currentPerson) {
        super(caseService);
        this.lastState = null;
        this.initState = initState;
        this.person = currentPerson;
        this.serviceModule = module;
        /** @review ну, а чем System.currentTimeMillis() плох ?
         *  Я не очень понял, зачем нужно именно нано-секунды превращать в секунды?
         *  Я бы понял, если бы у тебя время фиксировалось в нано-секундах, типа для
         *  точности и разрешения конфликтов, но в данном случае ты всеравно приводишь
         *  значение к секундам, что кстати, хуже, чем хранить привычное значение для мс
         *
         *  Рекомендация: замени хранение времени на миллисекунды либо добавь хотя бы комментарии
         *  к мемберам, чтобы все видели, в чем измеряется хранимое значение.
         *
         */
        this.timeCreated = NANOSECONDS.toSeconds(nanoTime());
        this.lastUpdated = timeCreated;
    }

    public CaseComment getCaseComment() {
        return comment;
    }

    public void setComment(CaseComment comment) {
        this.comment = comment;
    }

    public boolean isLastStateSet() {
        return lastState != null;
    }

    public boolean isCreateEvent() {
        return this.initState == null;
    }

    public boolean isUpdateEvent() {
        return this.initState != null;
    }

    public boolean isCaseStateChanged() {
        return isUpdateEvent() && lastState.getState() != initState.getState();
    }

    public boolean isCaseImportanceChanged() {
        return isUpdateEvent() && !lastState.getImpLevel().equals(initState.getImpLevel());
    }

    public boolean isManagerChanged() {
        return isUpdateEvent() && !HelperFunc.equals(lastState.getManagerId(), initState.getManagerId());
    }

    public boolean isProductChanged() {
        return isUpdateEvent() && !HelperFunc.equals(lastState.getProductId(), initState.getProductId());
    }

    public boolean isInitiatorChanged() {
        return isUpdateEvent() && !HelperFunc.equals(lastState.getInitiatorId(), initState.getInitiatorId());
    }

    public boolean isInitiatorCompanyChanged() {
        return isUpdateEvent() && !HelperFunc.equals(lastState.getInitiatorCompanyId(), initState.getInitiatorCompanyId());
    }

    public boolean isInfoChanged() {
        return isUpdateEvent() && !HelperFunc.equals(lastState.getInfo(), initState.getInfo());
    }

    public boolean isNameChanged() {
        return isUpdateEvent() && !HelperFunc.equals(lastState.getName(), initState.getName());
    }

    public boolean isPrivacyChanged() {
        return isUpdateEvent() && lastState.isPrivateCase() != initState.isPrivateCase();
    }

    public void attachCaseObjectEvent(CaseObjectEvent caseObjectEvent) {
        lastState = caseObjectEvent.getCaseObject();
        lastUpdated = NANOSECONDS.toSeconds(nanoTime());
    }

    public void attachCaseCommentEvent(CaseCommentEvent caseCommentEvent) {
        comment = caseCommentEvent.getCaseComment();
        lastUpdated = NANOSECONDS.toSeconds(nanoTime());
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public ServiceModule getServiceModule() {
        return serviceModule != null ? serviceModule : ServiceModule.GENERAL;
    }

    public Date getEventDate() {
        return new Date(getTimestamp());
    }

    public CaseObject getCaseObject() {
        return lastState != null ? lastState : initState;
    }

    public CaseObject getLastState() {
        return lastState;
    }

    public CaseObject getInitState() {
        return initState;
    }

    public CaseService getCaseService() {
        return (CaseService) getSource();
    }

    public Person getPerson() {
        return person;
    }
}
