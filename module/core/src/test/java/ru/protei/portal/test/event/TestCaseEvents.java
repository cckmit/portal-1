package ru.protei.portal.test.event;

import org.junit.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.service.CaseControlService;
import ru.protei.portal.core.service.CaseService;

import java.util.Date;

/**
 * Created by michael on 04.05.17.
 */
public class TestCaseEvents {

    public static final String JUNIT_EVENT_PUB_01 = "junit-event-pub-01";
    static ApplicationContext ctx;

    @BeforeClass
    public static void init () {
        ctx = new AnnotationConfigApplicationContext(TestEventConfiguration.class);
    }

    @Test
    public void test001 () throws Exception {

        EventHandlerRegistry evRegistry = ctx.getBean(EventHandlerRegistry.class);

        CaseService service = ctx.getBean(CaseService.class);

        CaseObject object = new CaseObject();
        object.setCaseType(En_CaseType.CRM_SUPPORT);
        object.setInitiatorCompanyId(1L);
        object.setInitiatorId(1L);
        object.setState(En_CaseState.CREATED);
        object.setCreated(new Date());
        object.setCreatorInfo("junit-test-events");
        object.setName("Event-publisher test");
        object.setExtAppType("junit");
        object.setExtAppCaseId(JUNIT_EVENT_PUB_01);

        CoreResponse<CaseObject> response = service.saveCaseObject(object);

        Assert.assertTrue(response.isOk());

        CaseComment comment = new CaseComment();
        comment.setCaseId(response.getData().getId());
        comment.setCreated(new Date());
        comment.setClientIp("-");
        comment.setCaseStateId(response.getData().getStateId());
        comment.setAuthorId(response.getData().getInitiatorId());
        comment.setText("A new comment, publishing test");

        CoreResponse<CaseComment> r2 = service.addCaseComment(comment);

        Assert.assertTrue(r2.isOk());

        // wait 500ms for async event
        Thread.sleep(500);


        Assert.assertEquals(1, evRegistry.objectEvents.size());
        Assert.assertEquals(1, evRegistry.commentEvents.size());
    }


    @Before
    @After
    public void cleanup () {
        ctx.getBean(CaseControlService.class).deleteByExtAppId(JUNIT_EVENT_PUB_01);
    }
}
