package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.event.CaseCommentEvent;
import ru.protei.portal.core.event.CaseObjectEvent;
import ru.protei.portal.core.model.ent.Person;

import java.util.Map;

public interface EventAssemblerService {
    void onCaseObjectEvent(CaseObjectEvent event);

    void onCaseCommentEvent(CaseCommentEvent event);

    AssembledCaseEvent getPersonsEvent(Person person);

    int getEventsCount();
}
