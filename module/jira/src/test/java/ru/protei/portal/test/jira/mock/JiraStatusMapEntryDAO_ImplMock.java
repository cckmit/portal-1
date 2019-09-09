package ru.protei.portal.test.jira.mock;

import ru.protei.portal.core.model.dao.impl.JiraStatusMapEntryDAO_Impl;
import ru.protei.portal.core.model.dict.En_CaseState;

import java.util.HashMap;
import java.util.Map;

public class JiraStatusMapEntryDAO_ImplMock extends JiraStatusMapEntryDAO_Impl {

    private static Map<String, En_CaseState> statusMap = null;

    private synchronized static void buildStatusMap() {
        statusMap = new HashMap<>();
        statusMap.put("Authorized", En_CaseState.CREATED);
        statusMap.put("Studying", En_CaseState.OPENED);
        statusMap.put("Postpone", En_CaseState.PAUSED);
        statusMap.put("Soft Close", En_CaseState.DONE);
        statusMap.put("Nothing to change", En_CaseState.VERIFIED);
        statusMap.put("Request to customer", En_CaseState.CUST_REQUEST);
        statusMap.put("Request to NX", En_CaseState.NX_REQUEST);
    }

    @Override
    public En_CaseState getByJiraStatus(long mapId, String statusName) {
        if (statusMap == null) buildStatusMap();
        return statusName == null ? null : statusMap.get(statusName);
    }
}
