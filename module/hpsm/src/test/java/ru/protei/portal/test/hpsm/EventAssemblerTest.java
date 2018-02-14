package ru.protei.portal.test.hpsm;

import org.junit.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.event.CaseCommentEvent;
import ru.protei.portal.core.event.CaseObjectEvent;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.ExternalCaseAppDAO;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.service.*;
import ru.protei.portal.hpsm.logic.InboundMainMessageHandler;
import ru.protei.portal.hpsm.utils.HpsmTestUtils;
import ru.protei.portal.hpsm.utils.TestServiceInstance;
import ru.protei.portal.test.hpsm.config.HpsmTestConfiguration;

public class EventAssemblerTest {
    public static final String HPSM_TEST_CASE_ID2 = "hpsm-update-test-1";
    static ApplicationContext ctx;

    @BeforeClass
    public static void init () {
        ctx = new AnnotationConfigApplicationContext(HpsmTestConfiguration.class);
    }

    @Test
    public void eventAssemblingTest() throws InterruptedException {
        CaseObject object = new CaseObject();
        CaseComment comment = new CaseComment();
        CaseObject newObject = new CaseObject();
        Person person = new Person();
        person.setId(123L);
        CaseService caseService = ctx.getBean(CaseService.class);
        CaseObjectEvent objectEvent = new CaseObjectEvent(caseService, object, person);
        CaseCommentEvent commentEvent = new CaseCommentEvent(caseService, object, comment,null, person);
        CaseObjectEvent secondEvent = new CaseObjectEvent(caseService, newObject, person);
        CaseCommentEvent secondCommentEvent = new CaseCommentEvent(caseService, newObject, comment, null, person);
        EventAssemblerService assemblerService = ctx.getBean(EventAssemblerService.class);
        assemblerService.onCaseObjectEvent(objectEvent);
        assemblerService.onCaseCommentEvent(commentEvent);

        //Test on event assembling (comment + object)
        Assert.assertEquals(1, assemblerService.getEventsCount());
        Assert.assertEquals(assemblerService.getPersonsEvent(person).getLastState(), object);
        Assert.assertEquals(assemblerService.getPersonsEvent(person).getCaseComment(), comment);

        //Test on second case object for same person
        assemblerService.onCaseObjectEvent(secondEvent);
        Assert.assertEquals(assemblerService.getPersonsEvent(person).getLastState(), newObject);

        //Test on time delay publishing
        Thread.sleep(35000);
        Assert.assertEquals(0, assemblerService.getEventsCount());

        //Test on second case comment for same person
        assemblerService.onCaseObjectEvent(objectEvent);
        assemblerService.onCaseCommentEvent(commentEvent);
        Assert.assertEquals(1, assemblerService.getEventsCount());
        AssembledCaseEvent firstEvent = assemblerService.getPersonsEvent(person);
        assemblerService.onCaseCommentEvent(secondCommentEvent);
        Assert.assertNotEquals(assemblerService.getPersonsEvent(person), firstEvent);
    }


    @Before
    public void beforeTest () {
        cleanup();
    }

    @After
    public void cleanup () {
        ctx.getBean(CaseControlService.class).deleteByExtAppId (HPSM_TEST_CASE_ID2);
    }
}
