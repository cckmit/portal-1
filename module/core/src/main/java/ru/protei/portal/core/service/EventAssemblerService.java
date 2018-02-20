package ru.protei.portal.core.service;

import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.event.CaseAttachmentEvent;
import ru.protei.portal.core.event.CaseCommentEvent;
import ru.protei.portal.core.event.CaseObjectEvent;
import ru.protei.portal.core.model.ent.Person;


public interface EventAssemblerService {
    void publishEvent(CaseObjectEvent event);

    void publishEvent(CaseCommentEvent event);

    void publishEvent(CaseAttachmentEvent event);

    AssembledCaseEvent getEvent(Person person, long caseId);

    int getEventsCount();
}
