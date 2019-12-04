package ru.protei.portal.core.event;

import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;

import java.util.Collection;

public interface AbstractCaseEvent {

    ServiceModule getServiceModule();

    Long getPersonId();

    Long getCaseObjectId();

    Object getSource();

    boolean isEagerEvent();//TODO нужен для Redmine
}
