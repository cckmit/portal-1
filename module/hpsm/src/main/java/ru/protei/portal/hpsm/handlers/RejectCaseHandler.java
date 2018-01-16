package ru.protei.portal.hpsm.handlers;

import ru.protei.portal.hpsm.annotations.CaseHandler;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.hpsm.api.HpsmStatus;

import java.util.function.BiConsumer;

@CaseHandler(hpsmStatus = HpsmStatus.IN_PROGRESS)
@CaseHandler(hpsmStatus = HpsmStatus.WAIT_SOLUTION)
@CaseHandler(hpsmStatus = HpsmStatus.REJECT_WA)
public class RejectCaseHandler implements BiConsumer<CaseComment, CaseObject> {

    @Override
    public void accept(CaseComment comment, CaseObject obj) {
        if (obj.getState() != En_CaseState.OPENED) {
            obj.setState(En_CaseState.OPENED);
            comment.setCaseStateId(obj.getStateId());
        }
    }
}
