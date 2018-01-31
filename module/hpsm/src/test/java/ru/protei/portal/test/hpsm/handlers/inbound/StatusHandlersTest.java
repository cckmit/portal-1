package ru.protei.portal.test.hpsm.handlers.inbound;

import org.junit.Assert;
import org.junit.Test;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.hpsm.api.HpsmStatus;
import ru.protei.portal.hpsm.factories.HpsmStatusHandlerFactory;
import ru.protei.portal.hpsm.factories.HpsmStatusHandlerFactoryImpl;
import ru.protei.portal.hpsm.handlers.HpsmStatusHandler;
import ru.protei.portal.hpsm.struct.HpsmMessage;

public class StatusHandlersTest {
    HpsmStatusHandlerFactory factory = new HpsmStatusHandlerFactoryImpl();

    @Test
    public void statusFactoryTest() {
        HpsmMessage message = new HpsmMessage();
        message.status(HpsmStatus.NEW);
        CaseObject object = new CaseObject();
        CaseComment comment = new CaseComment("qwe");
        HpsmStatusHandler handler = factory.createHandler(message, HpsmStatus.IN_PROGRESS);
        handler.handle(object, comment);

        Assert.assertEquals(En_CaseState.ACTIVE, object.getState());
        Assert.assertEquals(Long.valueOf(En_CaseState.ACTIVE.getId()), comment.getCaseStateId());

        message.status(HpsmStatus.TEST_WA);
        handler = factory.createHandler(message, HpsmStatus.REJECT_WA);
        message.setTxOurWorkaroundTime("13371239");
        handler.handle(object, comment);
        Assert.assertEquals(En_CaseState.OPENED, object.getState());
        Assert.assertEquals("", message.getTxOurWorkaroundTime());
    }
}
