package ru.protei.portal.test.notification;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.config.*;
import ru.protei.portal.core.mail.MailSendChannel;
import ru.protei.portal.core.mail.VirtualMailSendChannel;
import ru.protei.portal.core.model.dao.CaseStateDAO;
import ru.protei.portal.core.model.dao.CompanySubscriptionDAO;
import ru.protei.portal.core.model.dao.ImportanceLevelDAO;
import ru.protei.portal.core.model.dict.En_CaseStateUsageInCompanies;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.service.AssemblerService;
import ru.protei.portal.core.service.AssemblerServiceImpl;
import ru.protei.portal.core.service.CaseCommentService;
import ru.protei.portal.core.service.events.EventAssemblerService;
import ru.protei.portal.test.service.BaseServiceTest;
import ru.protei.portal.tools.notifications.NotificationConfiguration;

import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static ru.protei.portal.core.model.dict.En_CaseType.CRM_SUPPORT;
import static ru.protei.portal.core.model.ent.CaseObject.Columns.CASE_NAME;
import static ru.protei.portal.core.model.ent.CaseObject.Columns.INFO;
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
        NotificationConfiguration.class, TestNotificationConfiguration.class,
        MailNotificationProcessorTest.LocalConfiguration.class
})
public class MailNotificationProcessorTest extends BaseServiceTest {

    public static class LocalConfiguration {
        @Bean
        public AssemblerService getAssemblerService() {
            return new AssemblerServiceImpl();
        }

    }

    @Autowired
    CaseCommentService caseCommentService;

    @Autowired
    MailSendChannel sendChannel;

    @Autowired
    EventAssemblerService eventAssemblerService;

    @Autowired
    PortalConfig portalConfig;

    @Autowired
    CaseStateDAO caseStateDAO;

    @Autowired
    CompanySubscriptionDAO companySubscriptionDAO;

    @Autowired
    private ImportanceLevelDAO importanceLevelDAO;

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

        getAuthToken().setPersonId( PERSON_ID );

        when(companyDAO.get( COMPANY_ID )).thenReturn( company );
        when(companySubscriptionDAO.listByCompanyId( COMPANY_ID )).thenReturn( listOf(subscription) );
        when(personDAO.get( PERSON_ID )).thenReturn( initiator );

        CaseObject object = createNewCaseObject(initiator);
        object.setInitiatorCompany( company );
        object.setInitiator( initiator );

        CaseState caseState = new CaseState();
        caseState.setId(object.getStateId());
        caseState.setUsageInCompanies(En_CaseStateUsageInCompanies.ALL);
        caseState.setCompanies(new ArrayList<>());

        ImportanceLevel importanceLevel = new ImportanceLevel(object.getImpLevel(), "");

        CompanyImportanceItem companyImportanceItem = new CompanyImportanceItem(object.getInitiatorCompanyId(), object.getImpLevel(), 0);

        object.setImportanceLevel(importanceLevel);

        CaseObjectMeta meta = new CaseObjectMeta(object);
        meta.setId( CASE_ID );
        CaseObjectMetaNotifiers metaNotifiers = new CaseObjectMetaNotifiers(object);
        metaNotifiers.setId( CASE_ID );

        when( caseObjectDAO.insertCase( object ) ).thenReturn( CASE_ID );
        when( caseObjectDAO.get( CASE_ID ) ).thenReturn( object );
        when( caseObjectDAO.partialGet( CASE_ID, CASE_NAME, INFO ) ).thenReturn( object );
        when( caseObjectMetaDAO.get( CASE_ID ) ).thenReturn( meta );
        when( caseObjectMetaNotifiersDAO.get( CASE_ID ) ).thenReturn( metaNotifiers );
        when( personDAO.getPersons( any() ) ).thenReturn( listOf( initiator ) );
        when( caseStateDAO.getAllByCaseType(CRM_SUPPORT) ).thenReturn(Collections.singletonList(caseState));
        when( caseStateDAO.get(any()) ).thenReturn(caseState);
        when( companyImportanceItemDAO.getSortedImportanceLevels(meta.getInitiatorCompanyId()) ).thenReturn(Collections.singletonList(companyImportanceItem));
        when( historyDAO.persist(any()) ).thenReturn(0L);
        when( importanceLevelDAO.get(meta.getImpLevel()) ).thenReturn(importanceLevel);

        Assert.assertTrue("CaseObject must be created",
                caseService.createCaseObject(getAuthToken(), new CaseObjectCreateRequest(object)).isOk());

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
        getAuthToken().setPersonId( PERSON_ID );

        CompanySubscription subscription = new CompanySubscription();
        subscription.setCompanyId( company.getId() );
        subscription.setEmail( "junit-test@protei.ru" );
        subscription.setLangCode( "ru" );
        subscription.setId( SUBSCRIPTION_ID );

        when(companyDAO.get( COMPANY_ID )).thenReturn( company );
        when(companySubscriptionDAO.listByCompanyId( COMPANY_ID )).thenReturn( listOf(subscription) );
        when(personDAO.get( PERSON_ID )).thenReturn( initiator );

        long commentId = COMMENT_ID;
        CaseComment comment = createNewComment( initiator, CASE_ID, "A new comment, publishing test" );
        comment.setId(commentId );

        when( caseObjectDAO.checkExistsByKey( CASE_ID ) ).thenReturn( true );
        when( caseObjectDAO.partialMerge( any(), any() ) ).thenReturn( true );
        when( caseObjectDAO.get( any() ) ).thenReturn( object );
        when( caseObjectMetaDAO.get( any() ) ).thenReturn( new CaseObjectMeta(object) );
        when( caseObjectMetaNotifiersDAO.get( any() ) ).thenReturn( new CaseObjectMetaNotifiers(object) );

        when( caseCommentDAO.get( commentId ) ).thenReturn( comment );
        when( caseCommentDAO.persist( any() ) ).thenReturn( commentId );

        companyImportanceItemDAO.persist(new CompanyImportanceItem(company.getId(), object.getImpLevel(), 0));

        Assert.assertTrue( "CaseComment must be created",
                caseCommentService.addCaseComment( getAuthToken(), En_CaseType.CRM_SUPPORT, comment ).isOk() );

        long waitSchedule = portalConfig.data().eventAssemblyConfig().getWaitingPeriodMillis();
        long waitScheduleAndEventAssembler = 2 * waitSchedule + 1 * SEC;
        Thread.sleep( waitScheduleAndEventAssembler );

        MimeMessage msg = mockChannel.get();
        Assert.assertNotNull(msg);
    }

}
