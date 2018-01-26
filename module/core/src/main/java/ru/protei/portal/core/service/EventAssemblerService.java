package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.event.CaseCommentEvent;
import ru.protei.portal.core.event.CaseObjectEvent;

public interface EventAssemblerService {
    void onCaseObjectEvent(CaseObjectEvent event);

    void onCaseCommentEvent(CaseCommentEvent event);
}
