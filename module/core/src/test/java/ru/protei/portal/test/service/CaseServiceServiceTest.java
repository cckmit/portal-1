package ru.protei.portal.test.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.config.DaoMockTestConfiguration;
import ru.protei.portal.config.ServiceTestsConfiguration;
import ru.protei.portal.core.model.dao.CaseShortViewDAO;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.winter.core.utils.beans.SearchResult;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        DaoMockTestConfiguration.class,
        ServiceTestsConfiguration.class
})
public class CaseServiceServiceTest extends BaseServiceTest {
    @Autowired
    CaseShortViewDAO caseShortViewDAO;

    @Test
    public void getCaseObjectsSearchResultByQuery() throws Exception {
        when( caseShortViewDAO.getSearchResult( any( CaseQuery.class ) ) ).thenReturn( new SearchResult<>() );
        SearchResult<CaseShortView> searchResult = checkResultAndGetData( caseService.getCaseObjects( getAuthToken(), new CaseQuery() ) );
        assertNotNull( searchResult );
    }
}
