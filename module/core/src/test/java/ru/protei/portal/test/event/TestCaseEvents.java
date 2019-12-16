package ru.protei.portal.test.event;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.config.*;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.service.CaseCommentService;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.portal.test.service.BaseServiceTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.protei.portal.core.model.helper.CollectionUtils.listOf;

/**
 * Created by michael on 04.05.17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        PortalConfigTestConfiguration.class,
        DaoMockTestConfiguration.class,
        ServiceTestsConfiguration.class,
        TestEventConfiguration.class
})
public class TestCaseEvents extends BaseServiceTest {

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks( this );
    }

    @Autowired
    EventPublisherService publisherService;

    @Autowired
    CaseObjectDAO caseObjectDAO;
    @Autowired
    CaseService caseService;
    @Autowired
    CaseCommentService caseCommentService;

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

        when( caseObjectDAO.insertCase( object ) ).thenReturn( CASE_ID );
        when( caseObjectDAO.get( CASE_ID ) ).thenReturn( object );
        when( personDAO.getPersons( any() ) ).thenReturn( listOf( person ) );
        when( personDAO.get( PERSON_ID ) ).thenReturn( person );
        when( companyDAO.get( COMPANY_ID ) ).thenReturn( company );

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

        CaseComment comment = createNewComment( person, CASE_ID, "A new comment, publishing test" );

        when( caseObjectDAO.checkExistsByKey( CASE_ID ) ).thenReturn( true );
        when( caseObjectDAO.partialMerge( any(), any() ) ).thenReturn( true );
        when( caseCommentDAO.get( COMMENT_ID ) ).thenReturn( comment );
        when( caseCommentDAO.persist( any() ) ).thenReturn( COMMENT_ID );
        when( personDAO.get( PERSON_ID ) ).thenReturn( person );
        when( companyDAO.get( COMPANY_ID ) ).thenReturn( company );

        Assert.assertTrue( "CaseComment must be created",
                caseCommentService.addCaseComment( getAuthToken(), En_CaseType.CRM_SUPPORT, comment ).isOk() );

        verify( publisherService, atLeastOnce() ).publishEvent( any() );
    }

}
