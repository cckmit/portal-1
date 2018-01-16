package ru.protei.portal.hpsm.handlers;

import ru.protei.portal.hpsm.annotations.CaseHandler;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.hpsm.api.HpsmStatus;

import java.util.function.BiConsumer;

@CaseHandler(hpsmStatus = HpsmStatus.TEST_SOLUTION)
@CaseHandler(hpsmStatus = HpsmStatus.TEST_WA)
public class TestWACaseHandler implements BiConsumer<CaseComment, CaseObject> {
    @Override
    public void accept(CaseComment comment, CaseObject object) {
        if (object.getState() != En_CaseState.TEST_CUST) {
            object.setState(En_CaseState.TEST_CUST);
            comment.setCaseStateId(object.getStateId());
        }
    }
}
