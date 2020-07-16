package ru.protei.portal.test.jira;

import org.junit.Assert;
import org.junit.Test;
import ru.protei.portal.core.model.dao.JiraStatusMapEntryDAO;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.test.jira.mock.JiraStatusMapEntryDAO_ImplMock;

import java.util.HashMap;
import java.util.Map;

public class FieldMappingTest {
    public static final int FIRST_MAP_ID = 1;

    JiraStatusMapEntryDAO statusMapEntryDAO = new JiraStatusMapEntryDAO_ImplMock();

    @Test
    public void testStatusMapping () {
        Map<String, Long> expectedMapping = new HashMap<>();
        expectedMapping.put("Authorized", CrmConstants.State.CREATED);
        expectedMapping.put("Studying", CrmConstants.State.OPENED);
        expectedMapping.put("Postpone", CrmConstants.State.PAUSED);
        expectedMapping.put("Soft Close", CrmConstants.State.DONE);
        expectedMapping.put("Nothing to change", CrmConstants.State.VERIFIED);
        expectedMapping.put("Request to customer", CrmConstants.State.CUST_REQUEST);
        expectedMapping.put("Request to NX", CrmConstants.State.NX_REQUEST);

        expectedMapping.forEach((key,stateId) -> {
            Assert.assertEquals(stateId, statusMapEntryDAO.getByJiraStatus(FIRST_MAP_ID, key).getLocalStatusId());
            Assert.assertEquals(key, statusMapEntryDAO.getJiraStatus(FIRST_MAP_ID, stateId));
        });
    }
}
