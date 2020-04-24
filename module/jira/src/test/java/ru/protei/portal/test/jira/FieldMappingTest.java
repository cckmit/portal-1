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
        Map<String, CaseState> expectedMapping = new HashMap<>();
        expectedMapping.put("Authorized", new CaseState((long)En_CaseState.CREATED.getId()));
        expectedMapping.put("Studying", new CaseState((long)En_CaseState.OPENED.getId()));
        expectedMapping.put("Postpone", new CaseState((long)En_CaseState.PAUSED.getId()));
        expectedMapping.put("Soft Close", new CaseState((long)En_CaseState.DONE.getId()));
        expectedMapping.put("Nothing to change", new CaseState((long)En_CaseState.VERIFIED.getId()));
        expectedMapping.put("Request to customer", new CaseState((long)En_CaseState.CUST_REQUEST.getId()));
        expectedMapping.put("Request to NX", new CaseState((long)En_CaseState.NX_REQUEST.getId()));

        expectedMapping.forEach((key,state) -> {
            Assert.assertEquals(state, statusMapEntryDAO.getByJiraStatus(FIRST_MAP_ID, key));
            Assert.assertEquals(key, statusMapEntryDAO.getJiraStatus(FIRST_MAP_ID, state));
        });
    }
}
