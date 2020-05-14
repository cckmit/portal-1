package ru.protei.portal.test.jira.mock;

import ru.protei.portal.core.model.dao.impl.JiraStatusMapEntryDAO_Impl;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.CaseState;

import java.util.HashMap;
import java.util.Map;

public class JiraStatusMapEntryDAO_ImplMock extends JiraStatusMapEntryDAO_Impl {

    private static Map<String, Long> statusMap = null;

    private synchronized static void buildStatusMap() {
        statusMap = new HashMap<>();
        statusMap.put("Authorized", (long)En_CaseState.CREATED.getId());
        statusMap.put("Studying", (long)En_CaseState.OPENED.getId());
        statusMap.put("Postpone", (long)En_CaseState.PAUSED.getId());
        statusMap.put("Soft Close", (long)En_CaseState.DONE.getId());
        statusMap.put("Nothing to change", (long)En_CaseState.VERIFIED.getId());
        statusMap.put("Request to customer", (long)En_CaseState.CUST_REQUEST.getId());
        statusMap.put("Request to NX", (long)En_CaseState.NX_REQUEST.getId());
    }

    @Override
    public Long getByJiraStatusId(long mapId, String statusName) {
        if (statusMap == null) buildStatusMap();
        return statusName == null ? null : statusMap.get(statusName);
    }

    @Override
    public String getJiraStatus(long mapId, long stateId) {
        if (statusMap == null) buildStatusMap();

        return statusMap.entrySet().stream()
                .filter(entry -> stateId == entry.getValue())
                .map(Map.Entry::getKey)
                .findFirst().orElse(null);
    }
}
