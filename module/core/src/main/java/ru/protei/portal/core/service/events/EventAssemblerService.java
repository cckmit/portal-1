package ru.protei.portal.core.service.events;

import ru.protei.portal.core.event.*;
import ru.protei.portal.core.model.ent.Person;

public interface EventAssemblerService {

    void onCaseObjectEvent( CaseObjectEvent event);

    void onCaseObjectMetaEvent( CaseObjectMetaEvent event);

    void onCaseCommentEvent( CaseCommentEvent event);

    void onCaseAttachmentEvent( CaseAttachmentEvent event);

    void onCaseLinkEvent( CaseLinkEvent event );

    AssembledCaseEvent getEvent( Person person, Long caseId);

    int getEventsCount();

}
