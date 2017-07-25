package ru.protei.portal.test.notification;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.mail.MailSendChannel;
import ru.protei.portal.core.mail.VirtualMailSendChannel;
import ru.protei.portal.core.model.dao.CompanySubscriptionDAO;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.service.CaseControlService;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.core.service.CompanyService;

import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Тесты для
 */
@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration(classes=TestNotificationConfiguration.class)
public class MailNotificationProcessorTest {

    public static final String JUNIT_EVENT_PUB_01 = "junit-event-pub-02";

    @Autowired
    CaseService caseService;

    @Autowired
    CompanyService companyService;

    @Autowired
    CaseControlService caseControlService;

    @Autowired
    MailSendChannel sendChannel;

    @Autowired
    CompanySubscriptionDAO subscriptionDAO;

    CompanySubscription subscription;

    @Test
    public void test001() throws Exception {
        VirtualMailSendChannel mockChannel = (VirtualMailSendChannel) sendChannel;

        subscription = new CompanySubscription();
        subscription.setCompanyId( 1L );
        subscription.setEmail( "dsh99@mail.ru" );
        subscription.setLangCode( "ru" );
        subscription.setId( subscriptionDAO.persist( subscription ) );

        Person initiator = new Person();
        initiator.setId( -10L );
        initiator.setDisplayShortName( "vassili" );
//        initiator.setDisplayName(  );

        CaseObject object = new CaseObject();
        object.setCaseType( En_CaseType.CRM_SUPPORT );
        object.setInitiatorCompanyId( 1L );
        object.setInitiatorId( 1L );
        object.setCreatorId( 1L );
        object.setState( En_CaseState.CREATED );
        object.setImpLevel( 1 );
        object.setCreated( new Date() );
        object.setCreatorInfo( "junit-test-events" );
        object.setName( "Event-publisher test" );
        object.setInfo( "some text is here" );
        object.setExtAppType( "junit" );
        object.setProductId( 18827L ); // космос

        CoreResponse<CaseObject> response = caseService.saveCaseObject(null, object, initiator );

        // wait 500ms for async event
        Thread.sleep(1000);

        MimeMessage msg = mockChannel.get();
        Assert.assertNotNull( msg );

        CaseComment comment = new CaseComment();
        comment.setCaseId(response.getData().getId());
        comment.setCreated(new Date());
        comment.setClientIp("-");
        comment.setCaseStateId(response.getData().getStateId());
        comment.setAuthorId(response.getData().getInitiatorId());
        comment.setText("A new comment, publishing test");

        CoreResponse<CaseComment> r2 = caseService.addCaseComment( null, comment, initiator );

        Assert.assertTrue(r2.isOk());

        // wait 500ms for async event
        Thread.sleep(500);

        msg = mockChannel.get();
        Assert.assertNotNull(msg);
    }


    @Before
    @After
    public void cleanup () {
        caseControlService.deleteByExtAppId( JUNIT_EVENT_PUB_01 );
        if ( subscription != null && subscription.getId() != null ) {
            subscriptionDAO.remove( subscription );
        }
    }
}
