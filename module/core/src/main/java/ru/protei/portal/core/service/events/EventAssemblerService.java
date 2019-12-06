package ru.protei.portal.core.service.events;

import org.springframework.context.event.EventListener;
import ru.protei.portal.core.event.*;

public interface EventAssemblerService {

    void onCaseObjectCreateEvent(CaseObjectCreateEvent event);

    void onCaseNameAndDescriptionEvent(CaseNameAndDescriptionEvent event);

    void onCaseObjectMetaEvent(CaseObjectMetaEvent event);

    void onCaseCommentEvent(CaseCommentEvent event);

    void onCaseAttachmentEvent(CaseAttachmentEvent event);

    @EventListener
    void onCaseLinkEvent(CaseLinksEvent event );

    AssembledCaseEvent getEvent(Long personId, Long caseId);

    int getEventsCount();
}
