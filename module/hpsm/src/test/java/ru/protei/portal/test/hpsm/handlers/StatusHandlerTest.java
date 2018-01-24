package ru.protei.portal.test.hpsm.handlers;

import org.junit.Assert;
import org.junit.Test;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.hpsm.api.HpsmStatus;
import ru.protei.portal.hpsm.factories.HpsmStatusHandlerFactory;
import ru.protei.portal.hpsm.factories.HpsmStatusHandlerFactoryImpl;
import ru.protei.portal.hpsm.handlers.HpsmStatusHandler;

import java.util.Date;

import static ru.protei.portal.hpsm.api.HpsmStatus.*;

public class StatusHandlerTest {

    private final CaseObject object = new CaseObject(1337L);
    private final CaseComment comment = setupComment();
    private final HpsmStatusHandlerFactory statusHandlerFactory = HpsmStatusHandlerFactoryImpl.getInstance();


    private CaseComment setupComment() {
        CaseComment comment = new CaseComment();
        comment.setCreated(new Date());
        comment.setCaseId(object.getId());
        comment.setClientIp("hpsm");
        return comment;
    }


    @Test
    public void objectStateToOpen() {
        object.setState(En_CaseState.DISCUSS);

        statusHandlerFactory.createHandler(TEST_WA, REJECT_WA).handle(object, comment);
        Assert.assertEquals(En_CaseState.OPENED, object.getState());

        object.setState(En_CaseState.CLOSED);
        statusHandlerFactory.createHandler(INFO_REQUEST, IN_PROGRESS).handle(object, comment);
        Assert.assertEquals(En_CaseState.OPENED, object.getState());

        //inprogress -> open
        object.setState(En_CaseState.CLOSED);
        statusHandlerFactory.createHandler(TEST_SOLUTION, IN_PROGRESS).handle(object, comment);
        Assert.assertEquals(En_CaseState.OPENED, object.getState());
    }

    @Test
    public void objectStateToVerified() {
        //closed -> verified
        statusHandlerFactory.createHandler(TEST_SOLUTION, CLOSED).handle(object, comment);
        Assert.assertEquals(En_CaseState.VERIFIED, object.getState());
    }
}
