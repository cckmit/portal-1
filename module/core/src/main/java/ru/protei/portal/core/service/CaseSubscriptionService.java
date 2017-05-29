package ru.protei.portal.core.service;

import ru.protei.portal.core.event.CaseCommentEvent;
import ru.protei.portal.core.event.CaseObjectEvent;
import ru.protei.portal.core.model.struct.NotificationEntry;

import java.util.Set;

/**
 * Created by michael on 26.05.17.
 */
public interface CaseSubscriptionService {

    Set<NotificationEntry> subscribers (CaseObjectEvent event);
    Set<NotificationEntry> subscribers (CaseCommentEvent event);

}
