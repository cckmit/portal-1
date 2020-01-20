package ru.protei.portal.core.service.events;

import ru.protei.portal.core.event.*;

public interface EventAssemblerService {

    void onCaseObjectCreateEvent(CaseObjectCreateEvent event);

    void onCaseNameAndDescriptionEvent(CaseNameAndDescriptionEvent event);

    void onCaseObjectMetaEvent(CaseObjectMetaEvent event);

    void onCaseCommentEvent(CaseCommentEvent event);

    void onCaseAttachmentEvent(CaseAttachmentEvent event);

    void onCaseLinkEvent(CaseLinkEvent event);

}
