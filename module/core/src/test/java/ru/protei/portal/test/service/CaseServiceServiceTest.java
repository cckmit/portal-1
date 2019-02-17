package ru.protei.portal.test.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.config.MainTestsConfiguration;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfigurationContext.class, JdbcConfigurationContext.class, MainTestsConfiguration.class})
public class CaseServiceServiceTest extends BaseServiceTest {

    @Test
    public void getCaseObjectsTest() throws Exception {
        assertNotNull(caseService);
        List<CaseShortView> all = checkResultAndGetData(caseService.caseObjectList(TEST_AUTH_TOKEN, new CaseQuery()));
        assertNotNull(all);
    }

}