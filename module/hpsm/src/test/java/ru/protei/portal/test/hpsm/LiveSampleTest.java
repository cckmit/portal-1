package ru.protei.portal.test.hpsm;

import com.thoughtworks.xstream.XStream;
import org.junit.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.event.CaseObjectEvent;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.ExternalCaseAppDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.ExternalCaseAppData;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.service.CaseControlService;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.hpsm.api.HpsmStatus;
import ru.protei.portal.hpsm.factories.BackChannelHandlerFactory;
import ru.protei.portal.hpsm.handlers.BackChannelEventHandler;
import ru.protei.portal.hpsm.logic.HpsmEvent;
import ru.protei.portal.hpsm.logic.InboundMainMessageHandler;
import ru.protei.portal.hpsm.struct.HpsmMessage;
import ru.protei.portal.hpsm.struct.HpsmMessageHeader;
import ru.protei.portal.hpsm.utils.HpsmTestUtils;
import ru.protei.portal.hpsm.utils.TestServiceInstance;
import ru.protei.portal.test.hpsm.config.HpsmTestConfiguration;
import ru.protei.portal.test.service.BaseServiceTest;

import javax.mail.internet.MimeMessage;
import java.util.List;

import static ru.protei.portal.core.model.dict.En_CaseState.WORKAROUND;

/**
 * Created by Mike on 01.05.2017.
 */
public class LiveSampleTest extends BaseServiceTest {
    private static final long LOCAL_PERSON_ID = 18L;
    private static final String HPSM_TEST_CASE_ID1 = "hpsm-live-test1642";
    private static final String HPSM_TEST_REJECT_NO_PRODUCT = "hpsm-live-test-no-product";
    private static ApplicationContext ctx;

    @BeforeClass
    public static void init () {
        ctx = new AnnotationConfigApplicationContext(HpsmTestConfiguration.class);
    }

    @Test
    public void backChannelFactoryTest() throws Exception {
        TestServiceInstance testServiceInstance = ctx.getBean(TestServiceInstance.class);
        InboundMainMessageHandler handler = ctx.getBean(InboundMainMessageHandler.class);
        CaseObjectDAO caseObjectDAO = ctx.getBean(CaseObjectDAO.class);
        ExternalCaseAppDAO externalCaseAppDAO = ctx.getBean(ExternalCaseAppDAO.class);
        PersonDAO personDAO = ctx.getBean(PersonDAO.class);
        CaseService caseService = ctx.getBean(CaseService.class);
        HpsmTestUtils testUtils = ctx.getBean(HpsmTestUtils.class);
        CaseCommentDAO commentDAO = ctx.getBean(CaseCommentDAO.class);

        ExternalCaseAppData appData = externalCaseAppDAO.getByExternalAppId(HPSM_TEST_CASE_ID1);

        Assert.assertNotNull(appData);

        CaseObject resultCase = caseObjectDAO.get(appData.getId());
        Person testPerson = personDAO.getEmployee(LOCAL_PERSON_ID);

        caseService.updateCaseObject(getAuthToken(), resultCase, testPerson);

        MimeMessage responseMail = testServiceInstance.getSentMessage();
    }

    @Test
    public void rejectNoProductTest() throws Exception {
        final TestServiceInstance testServiceInstance = ctx.getBean(TestServiceInstance.class);
        final InboundMainMessageHandler handler = ctx.getBean(InboundMainMessageHandler.class);
        final HpsmTestUtils testUtils = ctx.getBean(HpsmTestUtils.class);
        final XStream xStream = ctx.getBean(XStream.class);

/*
  prepare new request
 */
        final String newReqXml = testUtils.loadTestRequest("reject-test-no-product.xml");
        Assert.assertNotNull(newReqXml);
        Assert.assertFalse(newReqXml.isEmpty());

        System.out.println("---- req start ----");

        System.out.println("send request: " + newReqXml);


        System.out.println("---- req end ----");

        final HpsmMessageHeader subject = new HpsmMessageHeader(HPSM_TEST_REJECT_NO_PRODUCT, null, HpsmStatus.NEW);

        /* execute create request and validate results */
        boolean result = handler.handle(testUtils.createRequest(subject, newReqXml), testServiceInstance);

        Assert.assertTrue(result);

        final MimeMessage responseMail = testServiceInstance.getSentMessage();

        Assert.assertNotNull(responseMail);

        final HpsmEvent responseEvent = testUtils.parseEvent(responseMail);

        Assert.assertNotNull(responseEvent);

        System.out.println("---- response ----");

        System.out.println(xStream.toXML(responseEvent.getHpsmMessage()));
    }


    @Test
    public void workaroundTest() throws Exception {
        final BackChannelHandlerFactory backChannelFactory = ctx.getBean(BackChannelHandlerFactory.class);
        final TestServiceInstance testServiceInstance = ctx.getBean(TestServiceInstance.class);
        final HpsmTestUtils testUtils = ctx.getBean(HpsmTestUtils.class);
        final XStream xStream = ctx.getBean(XStream.class);

        final HpsmMessage msg = new HpsmMessage();
        msg.status(HpsmStatus.CONFIRM_WA);
        msg.setMessage("123");

        final CaseObject object = new CaseObject();
        object.setState(WORKAROUND);
        object.setId(100005000L);
        object.setExtAppType("qwerty");

        final CaseComment comment = new CaseComment("qwe");
        final CaseService caseService = ctx.getBean(CaseService.class);
        CaseObjectEvent caseObjectEvent = new CaseObjectEvent( caseService, ServiceModule.HPSM, new Person(), null, object );
        final AssembledCaseEvent assembledCaseEvent = new AssembledCaseEvent(caseObjectEvent);
        assembledCaseEvent.attachCaseObjectEvent( caseObjectEvent );
//        assembledCaseEvent.attachCaseComment(comment);

        final BackChannelEventHandler handler = backChannelFactory.createHandler(msg, assembledCaseEvent);
        handler.handle(assembledCaseEvent, msg, testServiceInstance);

        final MimeMessage response = testServiceInstance.getSentMessage();

        Assert.assertNotNull(response);

        final HpsmEvent event = testUtils.parseEvent(response);

        Assert.assertNotNull(event);

        Assert.assertEquals(HpsmStatus.WORKAROUND, event.getSubject().getStatus());

        System.out.println("---- response ----");

        System.out.println(xStream.toXML(event.getHpsmMessage()));
    }

    @Test
    public void test001 () throws Exception {
/*
  prepare test env
  */
        TestServiceInstance testServiceInstance = ctx.getBean(TestServiceInstance.class);
        InboundMainMessageHandler handler = ctx.getBean(InboundMainMessageHandler.class);
        CaseObjectDAO caseObjectDAO = ctx.getBean(CaseObjectDAO.class);
        ExternalCaseAppDAO externalCaseAppDAO = ctx.getBean(ExternalCaseAppDAO.class);
        PersonDAO personDAO = ctx.getBean(PersonDAO.class);
        CaseService caseService = ctx.getBean(CaseService.class);
        HpsmTestUtils testUtils = ctx.getBean(HpsmTestUtils.class);
        CaseCommentDAO commentDAO = ctx.getBean(CaseCommentDAO.class);


/*
  prepare new request
 */
        String newReqXml = testUtils.loadTestRequest("1642_new.xml");
        Assert.assertNotNull(newReqXml);
        Assert.assertFalse(newReqXml.isEmpty());

        System.out.println("send request: " + newReqXml);

        HpsmMessageHeader subject = new HpsmMessageHeader(HPSM_TEST_CASE_ID1, null, HpsmStatus.NEW);

        /* execute create request and validate results */
        boolean result = handler.handle(testUtils.createRequest(subject, newReqXml), testServiceInstance);

        Assert.assertTrue(result);

        MimeMessage responseMail = testServiceInstance.getSentMessage();

        Assert.assertNotNull(responseMail);

        HpsmEvent responseEvent = testUtils.parseEvent(responseMail);

        Assert.assertNotNull(responseEvent);

        System.out.println(responseEvent.getMailBodyText());

        Assert.assertEquals(HpsmStatus.REGISTERED, responseEvent.getSubject().getStatus());

        Assert.assertNotNull(responseEvent.getHpsmMessage());

        System.out.println(responseEvent.getSubject());

        ExternalCaseAppData appData = externalCaseAppDAO.getByExternalAppId(HPSM_TEST_CASE_ID1);

        Assert.assertNotNull(appData);

        CaseObject resultCase = caseObjectDAO.get(appData.getId());

        Assert.assertNotNull(resultCase.getInitiator());
        Assert.assertNotNull(resultCase.getInitiatorCompany());

        Assert.assertEquals(En_ImportanceLevel.IMPORTANT, resultCase.importanceLevel());


        /* do local update test */
        Person testPerson = personDAO.getEmployee(LOCAL_PERSON_ID);

        resultCase.setState(En_CaseState.OPENED);
        resultCase.setManager(testPerson);
        caseService.updateCaseObject(getAuthToken(), resultCase, testPerson);

        // wait event handling
        Thread.sleep(45000);

        responseMail = testServiceInstance.getSentMessage();

        Assert.assertNotNull(responseMail);

        HpsmEvent event = testUtils.parseEvent(responseMail);

        Assert.assertEquals(HpsmStatus.IN_PROGRESS, event.getSubject().getStatus());
        Assert.assertTrue(HelperFunc.isNotEmpty(event.getHpsmMessage().getTxOurOpenTime()));

        Assert.assertNotNull(event.getHpsmMessage().getOurManager());
        Assert.assertNotNull(event.getHpsmMessage().getOurManagerEmail());
        Assert.assertEquals(En_ImportanceLevel.IMPORTANT, resultCase.importanceLevel());

        /* do next inbound request */

        String updateXmlReq = testUtils.loadTestRequest("1642_up01.xml");
        Assert.assertNotNull(updateXmlReq);
        Assert.assertFalse(updateXmlReq.isEmpty());
        updateXmlReq = updateXmlReq.replace("generated-case-ext-id", resultCase.getExtId());

        System.out.println("send request: " + updateXmlReq);

        // special test, omit status
        subject = new HpsmMessageHeader(HPSM_TEST_CASE_ID1, resultCase.getExtId(), null);

        result = handler.handle(testUtils.createRequest(subject, updateXmlReq), testServiceInstance);

        Assert.assertTrue(result);

        resultCase = caseObjectDAO.get(resultCase.getId());

        Assert.assertEquals(En_CaseState.OPENED, resultCase.getState());
        Assert.assertEquals(En_ImportanceLevel.IMPORTANT, resultCase.importanceLevel());


        /* do next inbound request */

        updateXmlReq = testUtils.loadTestRequest("1642_up02.xml");
        Assert.assertNotNull(updateXmlReq);
        Assert.assertFalse(updateXmlReq.isEmpty());
        updateXmlReq = updateXmlReq.replace("generated-case-ext-id", resultCase.getExtId());

        System.out.println("send request: " + updateXmlReq);

        // special test, omit status
        subject = new HpsmMessageHeader(HPSM_TEST_CASE_ID1, resultCase.getExtId(), HpsmStatus.IN_PROGRESS);

        result = handler.handle(testUtils.createRequest(subject, updateXmlReq), testServiceInstance);

        Assert.assertTrue(result);

        resultCase = caseObjectDAO.get(resultCase.getId());

        Assert.assertEquals(En_CaseState.OPENED, resultCase.getState());
        Assert.assertEquals(En_ImportanceLevel.IMPORTANT, resultCase.importanceLevel());

        List<CaseComment> commentList = commentDAO.getCaseComments(new CaseCommentQuery(resultCase.getId()));

        Assert.assertFalse(commentList.isEmpty());

        commentList.forEach(c -> System.out.println(c.toString()));

        Assert.assertEquals(4, commentList.size());

    }


    @Before
    public void beforeTest () {
        cleanup();
    }

    @After
    public void cleanup () {
        ctx.getBean(CaseControlService.class).deleteByExtAppId (HPSM_TEST_CASE_ID1);
    }
}
