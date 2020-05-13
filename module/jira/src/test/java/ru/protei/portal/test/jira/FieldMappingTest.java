package ru.protei.portal.test.jira;

import org.junit.Assert;
import org.junit.Test;
import ru.protei.portal.core.model.dao.JiraStatusMapEntryDAO;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.test.jira.mock.JiraStatusMapEntryDAO_ImplMock;

import java.util.HashMap;
import java.util.Map;

public class FieldMappingTest {
    public static final int FIRST_MAP_ID = 1;

    JiraStatusMapEntryDAO statusMapEntryDAO = new JiraStatusMapEntryDAO_ImplMock();

    @Test
    public void testStatusMapping () {
        Map<String, Long> expectedMapping = new HashMap<>();
        expectedMapping.put("Authorized", (long)En_CaseState.CREATED.getId());
        expectedMapping.put("Studying", (long)En_CaseState.OPENED.getId());
        expectedMapping.put("Postpone", (long)En_CaseState.PAUSED.getId());
        expectedMapping.put("Soft Close", (long)En_CaseState.DONE.getId());
        expectedMapping.put("Nothing to change", (long)En_CaseState.VERIFIED.getId());
        expectedMapping.put("Request to customer", (long)En_CaseState.CUST_REQUEST.getId());
        expectedMapping.put("Request to NX", (long)En_CaseState.NX_REQUEST.getId());

        expectedMapping.forEach((key,stateId) -> {
            Assert.assertEquals(stateId, statusMapEntryDAO.getByJiraStatus(FIRST_MAP_ID, key).getId());
            Assert.assertEquals(key, statusMapEntryDAO.getJiraStatus(FIRST_MAP_ID, stateId));
        });
    }
}
