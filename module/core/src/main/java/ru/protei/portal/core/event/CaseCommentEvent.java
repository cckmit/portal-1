package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.service.CaseService;

/**
 * Created by michael on 04.05.17.
 */
public class CaseCommentEvent extends ApplicationEvent {

    private CaseObject caseObject;
    private CaseComment caseComment;
    private CaseComment oldCaseComment;
    private Person person;
    private ServiceModule serviceModule;

    public CaseCommentEvent(CaseService source, CaseObject caseObject, CaseComment oldComment, CaseComment comment, Person currentPerson) {
        this(ServiceModule.GENERAL, source, caseObject, oldComment, comment, currentPerson);
    }

    public CaseCommentEvent(ServiceModule serviceModule, CaseService source, CaseObject caseObject, CaseComment oldComment, CaseComment comment, Person currentPerson) {
        super(source);
        this.caseObject = caseObject;
        this.caseComment = comment;
        this.oldCaseComment = oldComment;
        this.person = currentPerson;
        this.serviceModule = serviceModule;
    }

    public ServiceModule getServiceModule() {
        return serviceModule;
    }

    public CaseObject getCaseObject() {
        return caseObject;
    }

    public CaseComment getCaseComment() {
        return caseComment;
    }

    public CaseComment getOldCaseComment() {
        return oldCaseComment;
    }

    public Person getPerson() {
        return person;
    }
}
