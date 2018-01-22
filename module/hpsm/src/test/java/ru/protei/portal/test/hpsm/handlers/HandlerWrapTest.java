package ru.protei.portal.test.hpsm.handlers;

import org.junit.Assert;
import org.junit.Test;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.hpsm.api.HpsmStatus;
import ru.protei.portal.hpsm.handlers.HandlerController;

import java.util.Date;

public class HandlerWrapTest {

    private final CaseObject object = new CaseObject(1337L);
    private final CaseComment comment = setupComment();
    private final HandlerController handlerWrap = new HandlerController();


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

        //wait -> open
        handlerWrap.handle(HpsmStatus.WAIT_SOLUTION, comment, object);
        Assert.assertEquals(En_CaseState.OPENED, object.getState());

        //rejectWA -> open
        object.setState(En_CaseState.CLOSED);
        handlerWrap.handle(HpsmStatus.REJECT_WA, comment, object);
        Assert.assertEquals(En_CaseState.OPENED, object.getState());

        //inprogress -> open
        object.setState(En_CaseState.CLOSED);
        handlerWrap.handle(HpsmStatus.IN_PROGRESS, comment, object);
        Assert.assertEquals(En_CaseState.OPENED, object.getState());
    }

    @Test
    public void objectStateToVerified() {
        //closed -> verified
        handlerWrap.handle(HpsmStatus.CLOSED, comment, object);
        Assert.assertEquals(En_CaseState.VERIFIED, object.getState());
    }

    @Test
    public void objectStateToTestCust() {
        //test_wa -> test_cust
        handlerWrap.handle(HpsmStatus.TEST_WA, comment, object);
        Assert.assertEquals(En_CaseState.TEST_CUST, object.getState());

        //test_solution -> test_cust
        object.setState(En_CaseState.CLOSED);
        handlerWrap.handle(HpsmStatus.TEST_SOLUTION, comment, object);
        Assert.assertEquals(En_CaseState.TEST_CUST, object.getState());
    }
}
