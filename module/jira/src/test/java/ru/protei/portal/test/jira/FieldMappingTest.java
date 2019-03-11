package ru.protei.portal.test.jira;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import ru.protei.portal.core.model.dao.JiraStatusMapEntryDAO;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.test.jira.config.JiraTestConfiguration;

import java.util.HashMap;
import java.util.Map;

@RunWith( SpringJUnit4ClassRunner.class )
@WebAppConfiguration
@ContextConfiguration(classes = JiraTestConfiguration.class)
public class FieldMappingTest {

    @Autowired
    JiraStatusMapEntryDAO statusMapEntryDAO;


    @Test
    public void testStatusMapping () {
        Map<String, En_CaseState> expectedMapping = new HashMap<>();
        expectedMapping.put("authorized", En_CaseState.CREATED);
        expectedMapping.put("studying", En_CaseState.OPENED);
        expectedMapping.put("request to customer", En_CaseState.INFO_REQUEST);
        expectedMapping.put("postpone", En_CaseState.PAUSED);
        expectedMapping.put("soft close", En_CaseState.DONE);
        expectedMapping.put("nothing to change", En_CaseState.VERIFIED);

        expectedMapping.forEach((key,state) -> {
            Assert.assertEquals(state, statusMapEntryDAO.getByJiraStatus(1, key));
            Assert.assertEquals(key, statusMapEntryDAO.getJiraStatus(1, state));
        });
    }
}
