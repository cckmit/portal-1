package ru.protei.portal.core.service.events;

import ru.protei.portal.core.event.EmployeeRegistrationEvent;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.CaseObjectMeta;
import ru.protei.portal.core.model.struct.NotificationEntry;

import java.util.List;
import java.util.Set;

/**
 * Created by michael on 26.05.17.
 */
public interface CaseSubscriptionService {

    Set<NotificationEntry> subscribers(CaseObjectMeta caseMeta);

    Set<NotificationEntry> subscribers(EmployeeRegistrationEvent event);

    Set<NotificationEntry> subscribers(List<Long> personIds);
}
