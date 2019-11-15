package ru.protei.portal.test.notification;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.config.*;
import ru.protei.portal.core.mail.MailSendChannel;
import ru.protei.portal.core.mail.VirtualMailSendChannel;
import ru.protei.portal.core.model.dao.CompanySubscriptionDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.service.CaseCommentService;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.core.service.CompanyService;
import ru.protei.portal.core.service.events.CaseSubscriptionService;
import ru.protei.portal.core.service.events.EventAssemblerService;
import ru.protei.portal.test.service.BaseServiceTest;
import ru.protei.portal.tools.notifications.NotificationConfiguration;

import javax.mail.internet.MimeMessage;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static ru.protei.portal.core.model.helper.CollectionUtils.listOf;
import static ru.protei.portal.core.model.util.CrmConstants.Time.SEC;

/**
 * Тесты для
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration(classes = {
        PortalConfigTestConfiguration.class,
        DaoMockTestConfiguration.class,
        ServiceTestsConfiguration.class,
        TestEventConfiguration.class,
        NotificationConfiguration.class, TestNotificationConfiguration.class
})
public class MailNotificationProcessorTest extends BaseServiceTest {

    @Autowired
    CaseService caseService;

    @Autowired
    CaseCommentService caseCommentService;

    @Autowired
    CompanyService companyService;

    @Autowired
    MailSendChannel sendChannel;

    @Autowired
    CompanySubscriptionDAO subscriptionDAO;

    @Autowired
    EventAssemblerService eventAssemblerService;

    @Autowired
    PortalConfig portalConfig;

    @Autowired
    CaseSubscriptionService subscriptionService;
    @Autowired
    CompanySubscriptionDAO companySubscriptionDAO;

    private static final Long CASE_ID = 222L;
    private static final long COMPANY_ID = 1L;
    private static final long PERSON_ID = 2L;
    private static final long SUBSCRIPTION_ID = 3L;
    private static final long COMMENT_ID = 4L;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks( this );
    }

    @Test
    public void mail_on_createCaseObject() throws Exception {
        VirtualMailSendChannel mockChannel = (VirtualMailSendChannel) sendChannel;

        Company company = createNewCustomerCompany();
        company.setId( COMPANY_ID );
        Person initiator = createNewPerson(company);
        initiator.setId( PERSON_ID );

        CompanySubscription subscription = new CompanySubscription();
        subscription.setCompanyId( company.getId() );
        subscription.setEmail( "junit-test@protei.ru" );
        subscription.setLangCode( "ru" );
        subscription.setId( SUBSCRIPTION_ID );

        when(companyDAO.get( COMPANY_ID )).thenReturn( company );
        when(companySubscriptionDAO.listByCompanyId( COMPANY_ID )).thenReturn( listOf(subscription) );

        En_CaseType caseType = En_CaseType.CRM_SUPPORT;
        CaseObject object = createNewCaseObject(initiator);
        object.setInitiatorCompany( company );
        object.setInitiator( initiator );

        when( caseObjectDAO.insertCase( object ) ).thenReturn( CASE_ID );
        when( caseObjectDAO.get( CASE_ID ) ).thenReturn( object );
        when( personDAO.getPersons( any() ) ).thenReturn( listOf( initiator ) );

        Assert.assertTrue("CaseObject must be created",
                caseService.createCaseObject(getAuthToken(), object, initiator).isOk());

        long waitSchedule = portalConfig.data().eventAssemblyConfig().getWaitingPeriodMillis();
        long waitScheduleAndEventAssembler = 2 * waitSchedule + 1 * SEC;
        Thread.sleep( waitScheduleAndEventAssembler );

        MimeMessage msg = mockChannel.get();
        Assert.assertNotNull( msg );

    }

    @Test
    public void mail_on_createCaseComment() throws Exception {
        VirtualMailSendChannel mockChannel = (VirtualMailSendChannel) sendChannel;

        Company company = createNewCustomerCompany();
        company.setId( COMPANY_ID );
        Person initiator = createNewPerson(company);
        initiator.setId( PERSON_ID );
        CaseObject object = createNewCaseObject( initiator );
        object.setId( CASE_ID );
        object.setInitiatorCompany( company );

        CompanySubscription subscription = new CompanySubscription();
        subscription.setCompanyId( company.getId() );
        subscription.setEmail( "junit-test@protei.ru" );
        subscription.setLangCode( "ru" );
        subscription.setId( SUBSCRIPTION_ID );

        when(companyDAO.get( COMPANY_ID )).thenReturn( company );
        when(companySubscriptionDAO.listByCompanyId( COMPANY_ID )).thenReturn( listOf(subscription) );

        long commentId = COMMENT_ID;
        CaseComment comment = createNewComment( initiator, CASE_ID, "A new comment, publishing test" );
        comment.setId(commentId );

        when( caseObjectDAO.checkExistsByKey( CASE_ID ) ).thenReturn( true );
        when( caseObjectDAO.partialMerge( any(), any() ) ).thenReturn( true );
        when( caseObjectDAO.get( any() ) ).thenReturn( object );

        when( caseCommentDAO.get( commentId ) ).thenReturn( comment );
        when( caseCommentDAO.persist( any() ) ).thenReturn( commentId );

        Assert.assertTrue( "CaseComment must be created",
                caseCommentService.addCaseComment( getAuthToken(), En_CaseType.CRM_SUPPORT, comment, initiator ).isOk() );

        long waitSchedule = portalConfig.data().eventAssemblyConfig().getWaitingPeriodMillis();
        long waitScheduleAndEventAssembler = 2 * waitSchedule + 1 * SEC;
        Thread.sleep( waitScheduleAndEventAssembler );

        MimeMessage msg = mockChannel.get();
        Assert.assertNotNull(msg);
    }

}
