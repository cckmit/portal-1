package ru.protei.portal.test.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.config.DatabaseConfiguration;
import ru.protei.portal.config.MainTestsConfiguration;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfigurationContext.class, JdbcConfigurationContext.class, DatabaseConfiguration.class, MainTestsConfiguration.class})
public class CaseServiceServiceTest extends BaseServiceTest {

    @Test
    public void getCaseObjectsTest() throws Exception {
        assertNotNull(caseService);
        SearchResult<CaseShortView> all = checkResultAndGetData(caseService.getCaseObjects(getAuthToken(), new CaseQuery()));
        assertNotNull(all);
    }
}
