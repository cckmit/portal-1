package ru.protei.portal.test.hpsm;

import org.junit.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
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
import ru.protei.portal.core.service.CaseControlService;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.hpsm.api.HpsmStatus;
import ru.protei.portal.hpsm.logic.HpsmEvent;
import ru.protei.portal.hpsm.logic.InboundMainMessageHandler;
import ru.protei.portal.hpsm.struct.HpsmMessageHeader;
import ru.protei.portal.hpsm.utils.HpsmTestUtils;
import ru.protei.portal.hpsm.utils.TestServiceInstance;
import ru.protei.portal.test.hpsm.config.HpsmTestConfiguration;

import javax.mail.internet.MimeMessage;
import java.util.List;

/**
 * Created by Mike on 01.05.2017.
 */
public class LiveSampleTest {
    private static final long LOCAL_PERSON_ID = 18L;
    private static final String HPSM_TEST_CASE_ID1 = "hpsm-live-test1642";
    private static ApplicationContext ctx;

    @BeforeClass
    public static void init () {
        ctx = new AnnotationConfigApplicationContext(HpsmTestConfiguration.class);
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
        caseService.updateCaseObject(resultCase, testPerson);

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

        List<CaseComment> commentList = commentDAO.getCaseComments(resultCase.getId());

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
