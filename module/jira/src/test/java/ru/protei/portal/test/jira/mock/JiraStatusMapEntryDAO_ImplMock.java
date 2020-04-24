package ru.protei.portal.test.jira.mock;

import ru.protei.portal.core.model.dao.impl.JiraStatusMapEntryDAO_Impl;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.CaseState;

import java.util.HashMap;
import java.util.Map;

public class JiraStatusMapEntryDAO_ImplMock extends JiraStatusMapEntryDAO_Impl {

    private static Map<String, CaseState> statusMap = null;

    private synchronized static void buildStatusMap() {
        statusMap = new HashMap<>();
        statusMap.put("Authorized", new CaseState((long)En_CaseState.CREATED.getId()));
        statusMap.put("Studying", new CaseState((long)En_CaseState.OPENED.getId()));
        statusMap.put("Postpone", new CaseState((long)En_CaseState.PAUSED.getId()));
        statusMap.put("Soft Close", new CaseState((long)En_CaseState.DONE.getId()));
        statusMap.put("Nothing to change", new CaseState((long)En_CaseState.VERIFIED.getId()));
        statusMap.put("Request to customer", new CaseState((long)En_CaseState.CUST_REQUEST.getId()));
        statusMap.put("Request to NX", new CaseState((long)En_CaseState.NX_REQUEST.getId()));
    }

    @Override
    public CaseState getByJiraStatus(long mapId, String statusName) {
        if (statusMap == null) buildStatusMap();
        return statusName == null ? null : statusMap.get(statusName);
    }

    @Override
    public String getJiraStatus(long mapId, CaseState state) {
        if (statusMap == null) buildStatusMap();

        return statusMap.entrySet().stream()
                .filter(entry -> state.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .findFirst().orElse(null);
    }
}
