package ru.protei.portal.jira.factory;

import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.jira.utils.JiraHookEventData;

public interface JiraEventTypeHandler {
    CaseObject handle(JiraHookEventData event);
}
