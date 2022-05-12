package ru.protei.portal.test.event;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.config.DaoMockTestConfiguration;
import ru.protei.portal.config.PortalConfigTestConfiguration;
import ru.protei.portal.config.ServiceTestsConfiguration;
import ru.protei.portal.config.TestEventConfiguration;
import ru.protei.portal.core.model.dao.CaseStateDAO;
import ru.protei.portal.core.model.dao.CompanySubscriptionDAO;
import ru.protei.portal.core.model.dao.ImportanceLevelDAO;
import ru.protei.portal.core.model.dict.En_CaseStateUsageInCompanies;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.service.CaseCommentService;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.portal.test.service.BaseServiceTest;
import ru.protei.sn.remote_services.configuration.RemoteServiceFactory;
import ru.protei.winter.http.HttpConfigurationContext;
import ru.protei.winter.http.client.factory.HttpClientFactory;

import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.protei.portal.core.model.dict.En_CaseType.CRM_SUPPORT;
import static ru.protei.portal.core.model.helper.CollectionUtils.listOf;

/**
 * Created by michael on 04.05.17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        PortalConfigTestConfiguration.class,
        DaoMockTestConfiguration.class,
        ServiceTestsConfiguration.class,
        TestEventConfiguration.class,
        RemoteServiceFactory.class,
        HttpClientFactory.class,
        HttpConfigurationContext.class
})
public class TestCaseEvents extends BaseServiceTest {

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks( this );
    }

    @Autowired
    EventPublisherService publisherService;

    @Autowired
    CaseCommentService caseCommentService;

    @Autowired
    CaseStateDAO caseStateDAO;

    @Autowired
    CompanySubscriptionDAO companySubscriptionDAO;

    @Autowired
    private ImportanceLevelDAO importanceLevelDAO;

    private static final Long CASE_ID = 222L;
    private static final long COMPANY_ID = 1L;
    private static final long PERSON_ID = 2L;
    private static final long COMMENT_ID = 4L;

    @Test
    public void caseObjectEvent_on_createCaseObject() throws Exception {
        Company company = createNewCustomerCompany();
        company.setId( COMPANY_ID );
        Person person = createNewPerson( company );
        person.setId( PERSON_ID );
        CaseObject object = createNewCaseObject( person );
        object.setId( CASE_ID );
        object.setInitiatorCompany( company );

        CaseState caseState = new CaseState();
        caseState.setId(object.getStateId());
        caseState.setUsageInCompanies(En_CaseStateUsageInCompanies.ALL);
        caseState.setCompanies(new ArrayList<>());

        ImportanceLevel importanceLevel = new ImportanceLevel(object.getImpLevel(), "");

        CompanyImportanceItem companyImportanceItem = new CompanyImportanceItem(object.getInitiatorCompanyId(), object.getImpLevel(), 0);

        object.setImportanceLevel(importanceLevel);

        when( caseObjectDAO.insertCase( object ) ).thenReturn( CASE_ID );
        when( caseObjectDAO.get( CASE_ID ) ).thenReturn( object );
        when( personDAO.getPersons( any() ) ).thenReturn( listOf( person ) );
        when( personDAO.get( PERSON_ID ) ).thenReturn( person );
        when( companyDAO.get( COMPANY_ID ) ).thenReturn( company );
        when( caseStateDAO.getAllByCaseType(CRM_SUPPORT) ).thenReturn(Collections.singletonList(caseState));
        when( caseStateDAO.get(any()) ).thenReturn(caseState);
        when( companyImportanceItemDAO.getSortedImportanceLevels(object.getInitiatorCompanyId()) ).thenReturn(Collections.singletonList(companyImportanceItem));
        when( historyDAO.persist(any()) ).thenReturn(0L);
        when( importanceLevelDAO.get(object.getImpLevel()) ).thenReturn(importanceLevel);

        companyImportanceItemDAO.persist(new CompanyImportanceItem(company.getId(), object.getImpLevel(), 0));

        Assert.assertTrue( "CaseObject must be created",
                caseService.createCaseObject( getAuthToken(), new CaseObjectCreateRequest(object) ).isOk() );

        verify( publisherService, atLeastOnce() ).publishEvent( any() );
    }

    @Test
    public void caseCommentEvent_on_addCaseComment() throws Exception {
        Company company = createNewCustomerCompany();
        company.setId( COMPANY_ID );
        Person person = createNewPerson( company );
        person.setId( PERSON_ID );
        CaseObject object = createNewCaseObject( person );

        CaseComment comment = createNewComment( person, CASE_ID, "A new comment, publishing test" );

        when( caseObjectDAO.checkExistsByKey( CASE_ID ) ).thenReturn( true );
        when( caseObjectDAO.partialMerge( any(), any() ) ).thenReturn( true );
        when( caseObjectDAO.get( any() ) ).thenReturn( object );
        when( caseCommentDAO.get( COMMENT_ID ) ).thenReturn( comment );
        when( caseCommentDAO.persist( any() ) ).thenReturn( COMMENT_ID );
        when( personDAO.get( PERSON_ID ) ).thenReturn( person );
        when( companyDAO.get( COMPANY_ID ) ).thenReturn( company );

        Assert.assertTrue( "CaseComment must be created",
                caseCommentService.addCaseComment( getAuthToken(), En_CaseType.CRM_SUPPORT, comment ).isOk() );

        verify( publisherService, atLeastOnce() ).publishEvent( any() );
    }

}
