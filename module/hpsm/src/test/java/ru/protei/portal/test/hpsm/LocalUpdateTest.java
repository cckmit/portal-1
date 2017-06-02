package ru.protei.portal.test.hpsm;

import org.junit.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.ExternalCaseAppDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dict.En_CaseState;
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
import ru.protei.portal.hpsm.utils.HpsmTestUtils;
import ru.protei.portal.hpsm.utils.TestServiceInstance;
import ru.protei.portal.test.hpsm.config.HpsmTestConfiguration;

import javax.mail.internet.MimeMessage;
import java.util.Date;

/**
 * Created by Mike on 01.05.2017.
 */
public class LocalUpdateTest {
    public static final String HPSM_TEST_CASE_ID1 = "hpsm-create-test-1";
    public static final long LOCAL_PERSON_ID = 18L;
    public static final String JUNIT_TEST_COMMENT = "junit-test-comment";

    static ApplicationContext ctx;

    @BeforeClass
    public static void init () {
        ctx = new AnnotationConfigApplicationContext(HpsmTestConfiguration.class);
    }

    @Test
    public void test001 () throws Exception {

        CaseService caseService = ctx.getBean(CaseService.class);
        PersonDAO personDAO = ctx.getBean(PersonDAO.class);
        CaseObjectDAO caseObjectDAO = ctx.getBean(CaseObjectDAO.class);
        ExternalCaseAppDAO externalCaseAppDAO = ctx.getBean(ExternalCaseAppDAO.class);



        TestServiceInstance testServiceInstance = ctx.getBean(TestServiceInstance.class);
        InboundMainMessageHandler handler = ctx.getBean(InboundMainMessageHandler.class);

        HpsmTestUtils testUtils = ctx.getBean(HpsmTestUtils.class);

        boolean result = handler.handle(testUtils.createNewRequest(HPSM_TEST_CASE_ID1), testServiceInstance);

        Assert.assertTrue(result);

        MimeMessage responseMail = testServiceInstance.getSentMessage();

        Assert.assertNotNull(responseMail);

        ExternalCaseAppData appData = externalCaseAppDAO.getByExternalAppId(HPSM_TEST_CASE_ID1);

        Assert.assertNotNull(appData);

        CaseObject object = caseObjectDAO.get(appData.getId());

        Assert.assertNotNull(object);

        Person testPerson = personDAO.getEmployee(LOCAL_PERSON_ID);

        Assert.assertNotNull(testPerson);

        CaseComment comment = new CaseComment();
        comment.setText(JUNIT_TEST_COMMENT);

        comment.setAuthorId(testPerson.getId());
        comment.setAuthor(testPerson);
        comment.setCaseStateId(object.getStateId());
        comment.setCreated(new Date());
        comment.setCaseId(object.getId());

        caseService.addCaseComment(comment, testPerson );

        // wait event handling
        Thread.sleep(200);

        responseMail = testServiceInstance.getSentMessage();

        Assert.assertNotNull(responseMail);

        HpsmEvent event = testUtils.parseEvent(responseMail);

        //
        Assert.assertTrue(HelperFunc.isNotEmpty(event.getHpsmMessage().getMessage()));
        Assert.assertEquals(JUNIT_TEST_COMMENT, event.getHpsmMessage().getMessage());

        object.setState(En_CaseState.OPENED);
        object.setManager(testPerson);
        caseService.updateCaseObject(object, testPerson );

        // wait event handling
        Thread.sleep(200);

        responseMail = testServiceInstance.getSentMessage();

        Assert.assertNotNull(responseMail);

        event = testUtils.parseEvent(responseMail);

        Assert.assertEquals(HpsmStatus.IN_PROGRESS, event.getSubject().getStatus());
        Assert.assertTrue(HelperFunc.isNotEmpty(event.getHpsmMessage().getTxOurOpenTime()));

        Assert.assertNotNull(event.getHpsmMessage().getOurManager());
        Assert.assertNotNull(event.getHpsmMessage().getOurManagerEmail());


        object.setState(En_CaseState.DONE);
        caseService.updateCaseObject(object, testPerson );

        // wait event handling
        Thread.sleep(200);

        responseMail = testServiceInstance.getSentMessage();

        Assert.assertNotNull(responseMail);

        event = testUtils.parseEvent(responseMail);

        Assert.assertEquals(HpsmStatus.SOLVED, event.getSubject().getStatus());
        Assert.assertTrue(HelperFunc.isNotEmpty(event.getHpsmMessage().getTxOurOpenTime()));
        Assert.assertTrue(HelperFunc.isNotEmpty(event.getHpsmMessage().getTxOurSolutionTime()));
        Assert.assertNotNull(event.getHpsmMessage().getOurManager());
        Assert.assertNotNull(event.getHpsmMessage().getOurManagerEmail());
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
