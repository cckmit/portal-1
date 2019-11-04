package ru.protei.portal.core.event;

import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;

import java.util.Collection;

public interface AbstractCaseEvent {

    ServiceModule getServiceModule();

    Person getPerson();

    Long getCaseObjectId();
//    CaseObject getCaseObject();
//
//    CaseObject getNewState();
//
//    CaseObject getOldState();
//
//    CaseComment getCaseComment();

//    CaseComment getOldCaseComment();
//
//    CaseComment getRemovedCaseComment();

//    Collection<Attachment> getAddedAttachments();
//
//    Collection<Attachment> getRemovedAttachments();
//
    Object getSource();

    boolean isEagerEvent();// нужен для Redmine
}
