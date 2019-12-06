package ru.protei.portal.core.service.events;

import ru.protei.portal.core.event.*;

public interface EventAssemblerService {

    void onCaseObjectEvent(CaseObjectEvent event);

    void onCaseNameAndDescriptionEvent(CaseNameAndDescriptionEvent event);

    void onCaseObjectMetaEvent(CaseObjectMetaEvent event);

    void onCaseCommentEvent(CaseCommentEvent event);

    void onCaseAttachmentEvent(CaseAttachmentEvent event);

    void onCaseLinkEvent(CaseLinkEvent event);

    AssembledCaseEvent getEvent(Long personId, Long caseId);

    int getEventsCount();
}
