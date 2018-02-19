package ru.protei.portal.test.hpsm;

import org.junit.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.event.CaseAttachmentEvent;
import ru.protei.portal.core.event.CaseCommentEvent;
import ru.protei.portal.core.event.CaseObjectEvent;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.service.*;
import ru.protei.portal.test.hpsm.config.HpsmTestConfiguration;

import java.util.ArrayList;
import java.util.Collection;

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
        object.setId(1239L);
        newObject.setId(1337L);
        Person person = new Person();
        person.setId(123L);
        Collection<Attachment> attachment = new ArrayList<>();
        Collection<Attachment> removedAttachment = new ArrayList<>();
        Attachment temp = new Attachment();
        Attachment tempRemoved = new Attachment();

        temp.setId(322L);
        tempRemoved.setId(344L);

        attachment.add(temp);
        removedAttachment.add(tempRemoved);

        CaseService caseService = ctx.getBean(CaseService.class);

        //First portion
        CaseObjectEvent objectEvent = new CaseObjectEvent(caseService, object, person);
        CaseCommentEvent commentEvent = new CaseCommentEvent(caseService, object, comment,null, person);
        CaseAttachmentEvent attachmentEvent = new CaseAttachmentEvent(caseService, this, object,
                attachment, removedAttachment, person);

        //Second portion
        CaseObjectEvent secondEvent = new CaseObjectEvent(caseService, newObject, person);
        CaseCommentEvent secondCommentEvent = new CaseCommentEvent(caseService, newObject, comment, null, person);

        //This is where the fun begin
        EventAssemblerService assemblerService = ctx.getBean(EventAssemblerService.class);
        assemblerService.onCaseObjectEvent(objectEvent);
        assemblerService.onCaseCommentEvent(commentEvent);
        assemblerService.onCaseAttachmentEvent(attachmentEvent);

        //Test on event assembling (comment + object)
        Assert.assertEquals(1, assemblerService.getEventsCount());
        Assert.assertEquals(assemblerService.getEvent(person, object.getId()).getLastState(), object);
        Assert.assertEquals(assemblerService.getEvent(person, object.getId()).getCaseComment(), comment);

        Assert.assertNotEquals(assemblerService.getEvent(person, object.getId()).getAddedAttachments(), null);
        Assert.assertNotEquals(assemblerService.getEvent(person, object.getId()).getRemovedAttachments(), null);

        //Test on second case object for same person
        assemblerService.onCaseObjectEvent(secondEvent);
        Assert.assertEquals(assemblerService.getEvent(person, newObject.getId()).getLastState(), newObject);

        //Test on time delay publishing
        Thread.sleep(35000);
        Assert.assertEquals(0, assemblerService.getEventsCount());

        //Test on second case comment for same person
        assemblerService.onCaseObjectEvent(objectEvent);
        assemblerService.onCaseCommentEvent(commentEvent);
        Assert.assertEquals(1, assemblerService.getEventsCount());
        AssembledCaseEvent firstEvent = assemblerService.getEvent(person, object.getId());
        assemblerService.onCaseCommentEvent(secondCommentEvent);
        Assert.assertNotEquals(assemblerService.getEvent(person, object.getId()), firstEvent);
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
