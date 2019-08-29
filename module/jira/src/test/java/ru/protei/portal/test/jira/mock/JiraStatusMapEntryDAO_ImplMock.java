package ru.protei.portal.test.jira.mock;

import ru.protei.portal.core.model.dao.impl.JiraStatusMapEntryDAO_Impl;
import ru.protei.portal.core.model.dict.En_CaseState;

public class JiraStatusMapEntryDAO_ImplMock extends JiraStatusMapEntryDAO_Impl {

    @Override
    public En_CaseState getByJiraStatus(long mapId, String statusName) {
        return En_CaseState.CREATED;
    }
}
