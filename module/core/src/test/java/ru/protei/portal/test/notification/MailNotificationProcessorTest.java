package ru.protei.portal.test.notification;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.config.DatabaseConfiguration;
import ru.protei.portal.config.MainTestsConfiguration;
import ru.protei.portal.config.TestNotificationConfiguration;
import ru.protei.portal.core.mail.MailSendChannel;
import ru.protei.portal.core.mail.VirtualMailSendChannel;
import ru.protei.portal.core.model.dao.CompanySubscriptionDAO;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.service.CaseCommentService;
import ru.protei.portal.core.service.CaseControlService;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.core.service.CompanyService;
import ru.protei.portal.test.service.BaseServiceTest;
import ru.protei.portal.tools.notifications.NotificationConfiguration;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import javax.mail.internet.MimeMessage;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Тесты для
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration(classes = {
        CoreConfigurationContext.class, JdbcConfigurationContext.class, DatabaseConfiguration.class,
        MainTestsConfiguration.class, NotificationConfiguration.class, TestNotificationConfiguration.class
})
public class MailNotificationProcessorTest extends BaseServiceTest {

    public static final String JUNIT_EVENT_PUB_01 = "junit-event-pub-02";
    private En_CaseType caseType = En_CaseType.CRM_SUPPORT;

    @Autowired
    CaseService caseService;

    @Autowired
    CaseCommentService caseCommentService;

    @Autowired
    CompanyService companyService;

    @Autowired
    CaseControlService caseControlService;

    @Autowired
    MailSendChannel sendChannel;

    @Autowired
    CompanySubscriptionDAO subscriptionDAO;

    @Test
    public void test001() throws Exception {
        VirtualMailSendChannel mockChannel = (VirtualMailSendChannel) sendChannel;

        Company company = makeCustomerCompany();
        Person initiator = makePerson(company);

        CompanySubscription subscription = new CompanySubscription();
        subscription.setCompanyId( company.getId() );
        subscription.setEmail( "junit-test@protei.ru" );
        subscription.setLangCode( "ru" );
        subscription.setId( subscriptionDAO.persist( subscription ) );

        CaseObject object = new CaseObject();
        object.setCaseType( caseType );
        object.setInitiatorCompany( company );
        object.setInitiator( initiator );
        object.setCreator( initiator );
        object.setState( En_CaseState.CREATED );
        object.setImpLevel( En_ImportanceLevel.BASIC.getId() );
        object.setCreated( new Date() );
        object.setCreatorInfo( "junit-test-events" );
        object.setName( "Event-publisher test" );
        object.setInfo( "some text is here" );
        object.setExtAppType( "junit-test" );

        CoreResponse<CaseObject> response = caseService.saveCaseObject(getAuthToken(), object, initiator);
        Assert.assertTrue(response.isOk());
        object = response.getData();

        // wait for async event
        Thread.sleep(2000);

        MimeMessage msg = mockChannel.get();
        Assert.assertNotNull( msg );

        CaseComment comment = new CaseComment();
        comment.setCaseId(object.getId());
        comment.setCreated(new Date());
        comment.setClientIp(getAuthToken().getIp());
        comment.setCaseStateId(object.getStateId());
        comment.setAuthorId(object.getInitiatorId());
        comment.setText("A new comment, publishing test");
        comment.setCaseAttachments(Collections.emptyList());

        CoreResponse<CaseComment> r2 = caseCommentService.addCaseComment( getAuthToken(), caseType, comment, initiator );
        Assert.assertTrue(r2.isOk());

        // wait for async event
        Thread.sleep(2000);

        msg = mockChannel.get();
        Assert.assertNotNull(msg);

        Assert.assertTrue(removeCaseObjectAndComments(object));
        Assert.assertTrue(personDAO.remove(initiator));
        Assert.assertTrue(subscriptionDAO.remove(subscription));
        Assert.assertTrue(companyDAO.remove(company));
    }
}
