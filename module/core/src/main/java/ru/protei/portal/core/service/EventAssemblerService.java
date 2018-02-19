package ru.protei.portal.core.service;

import org.springframework.context.event.EventListener;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.event.CaseAttachmentEvent;
import ru.protei.portal.core.event.CaseCommentEvent;
import ru.protei.portal.core.event.CaseObjectEvent;
import ru.protei.portal.core.model.ent.Person;

import java.util.Map;

public interface EventAssemblerService {
    void onCaseObjectEvent(CaseObjectEvent event);

    void onCaseCommentEvent(CaseCommentEvent event);

    @EventListener
    void onCaseAttachmentEvent(CaseAttachmentEvent event);

    AssembledCaseEvent getEvent(Person person, long caseId);

    int getEventsCount();
}
