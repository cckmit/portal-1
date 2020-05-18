package ru.protei.portal.test.jira.mock;

import ru.protei.portal.core.model.dao.impl.JiraStatusMapEntryDAO_Impl;
import ru.protei.portal.core.model.util.CrmConstants;

import java.util.HashMap;
import java.util.Map;

public class JiraStatusMapEntryDAO_ImplMock extends JiraStatusMapEntryDAO_Impl {

    private static Map<String, Long> statusMap = null;

    private synchronized static void buildStatusMap() {
        statusMap = new HashMap<>();
        statusMap.put("Authorized", (long) CrmConstants.State.CREATED);
        statusMap.put("Studying", (long)CrmConstants.State.OPENED);
        statusMap.put("Postpone", (long)CrmConstants.State.PAUSED);
        statusMap.put("Soft Close", (long)CrmConstants.State.DONE);
        statusMap.put("Nothing to change", (long)CrmConstants.State.VERIFIED);
        statusMap.put("Request to customer", (long)CrmConstants.State.CUST_REQUEST);
        statusMap.put("Request to NX", (long)CrmConstants.State.NX_REQUEST);
    }

    @Override
    public Long getByJiraStatus(long mapId, String statusName) {
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
