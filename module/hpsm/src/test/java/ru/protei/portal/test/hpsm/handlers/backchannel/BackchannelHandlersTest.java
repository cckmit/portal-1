package ru.protei.portal.test.hpsm.handlers.backchannel;

import org.junit.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.service.CaseControlService;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.hpsm.api.HpsmStatus;
import ru.protei.portal.hpsm.factories.BackChannelHandlerFactory;
import ru.protei.portal.hpsm.factories.BackChannelHandlerFactoryImpl;
import ru.protei.portal.hpsm.handlers.BackChannelEventHandler;
import ru.protei.portal.hpsm.logic.ServiceInstance;
import ru.protei.portal.hpsm.logic.ServiceInstanceImpl;
import ru.protei.portal.hpsm.struct.HpsmMessage;
import ru.protei.portal.hpsm.utils.HpsmUtils;
import ru.protei.portal.test.hpsm.config.HpsmTestConfiguration;

import static ru.protei.portal.core.model.dict.En_CaseState.WORKAROUND;

public class BackchannelHandlersTest {

    static ApplicationContext ctx;
    public static final String HPSM_TEST_CASE_ID3 = "hpsm-backchannel-test-1";

    @BeforeClass
    public static void init () {
        ctx = new AnnotationConfigApplicationContext(HpsmTestConfiguration.class);
    }

    @Test
    public void backchannelTest() {
        BackChannelHandlerFactory factory = new BackChannelHandlerFactoryImpl();
        HpsmMessage message = new HpsmMessage();
        message.status(HpsmStatus.IN_PROGRESS);
        CaseObject object = new CaseObject();
        object.setState(WORKAROUND);
        CaseComment comment = new CaseComment("qwe");
        CaseService caseService = ctx.getBean(CaseService.class);
        AssembledCaseEvent assembledCaseEvent = new AssembledCaseEvent(caseService, object, new Person());
        assembledCaseEvent.attachCaseComment(comment);
        BackChannelEventHandler handler = factory.createHandler(message, assembledCaseEvent);
        ServiceInstance instance = ctx.getBean(ServiceInstanceImpl.class);
        try {
            handler.handle(assembledCaseEvent, message, instance);
        } catch (Exception e) {
            Assert.assertEquals(HpsmUtils.formatDate(assembledCaseEvent.getEventDate()), message.getTxOurWorkaroundTime());
        }
    }

    @Before
    public void beforeTest () {
        cleanup();
    }

    @After
    public void cleanup () {
        ctx.getBean(CaseControlService.class).deleteByExtAppId (HPSM_TEST_CASE_ID3);
    }
}
