package ru.protei.portal.test.jira;

import org.junit.Assert;
import org.junit.Test;
import ru.protei.portal.core.model.dao.JiraStatusMapEntryDAO;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.test.jira.mock.JiraStatusMapEntryDAO_ImplMock;

import java.util.HashMap;
import java.util.Map;

public class FieldMappingTest {
    public static final int FIRST_MAP_ID = 1;

    JiraStatusMapEntryDAO statusMapEntryDAO = new JiraStatusMapEntryDAO_ImplMock();

    @Test
    public void testStatusMapping () {
        Map<String, En_CaseState> expectedMapping = new HashMap<>();
        expectedMapping.put("Authorized", En_CaseState.CREATED);
        expectedMapping.put("Studying", En_CaseState.OPENED);
        expectedMapping.put("Request to customer", En_CaseState.CUST_REQUEST);
        expectedMapping.put("Postpone", En_CaseState.PAUSED);
        expectedMapping.put("Soft Close", En_CaseState.DONE);
        expectedMapping.put("Nothing to change", En_CaseState.VERIFIED);

        expectedMapping.forEach((key,state) -> {
            Assert.assertEquals(state, statusMapEntryDAO.getByJiraStatus(FIRST_MAP_ID, key));
            Assert.assertEquals(key, statusMapEntryDAO.getJiraStatus(FIRST_MAP_ID, state));
        });
    }
}
