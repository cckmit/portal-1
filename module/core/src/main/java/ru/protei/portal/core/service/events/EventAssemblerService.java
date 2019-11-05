package ru.protei.portal.core.service.events;

import ru.protei.portal.core.event.*;
import ru.protei.portal.core.model.ent.Person;

public interface EventAssemblerService {

    void publishEvent(CaseObjectEvent event);

    void publishEvent(CaseCommentEvent event);

    void publishEvent(CaseAttachmentEvent event);

    AssembledCaseEvent getEvent(Person person, Long caseId);

    int getEventsCount();

}
