package ru.protei.portal.hpsm.handlers;

import ru.protei.portal.hpsm.annotations.CaseHandler;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.hpsm.annotations.Handler;
import ru.protei.portal.hpsm.api.HpsmStatus;

import java.util.function.BiConsumer;

@Handler({
        @CaseHandler(hpsmStatus = HpsmStatus.CLOSED)
})
public class ClosedCaseHandler implements BiConsumer<CaseComment, CaseObject> {
    @Override
    public void accept(CaseComment comment, CaseObject obj) {
        if (obj.getState() != En_CaseState.VERIFIED) {
            obj.setState(En_CaseState.VERIFIED);
            comment.setCaseStateId(obj.getStateId());
        }
    }
}
