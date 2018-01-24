package ru.protei.portal.hpsm.factories;

import protei.utils.common.Tuple;
import static ru.protei.portal.hpsm.api.HpsmStatus.*;

import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.hpsm.api.HpsmStatus;
import ru.protei.portal.hpsm.handlers.HpsmStatusHandler;

import java.util.HashMap;
import java.util.Map;

public final class HpsmStatusHandlerFactoryImpl implements HpsmStatusHandlerFactory {

    private HpsmStatusHandlerFactoryImpl() {
        statusHandlerMap = new HashMap<>();
        statusHandlerMap.put(new Tuple<>(INFO_REQUEST, IN_PROGRESS), new OpenCaseHandler());
        statusHandlerMap.put(new Tuple<>(WORKAROUND, TEST_WA), new WorkaroundCaseHandler());
        statusHandlerMap.put(new Tuple<>(TEST_WA, REJECT_WA), new OpenCaseHandler());
        statusHandlerMap.put(new Tuple<>(TEST_WA, CONFIRM_WA), new WorkaroundCaseHandler());
        statusHandlerMap.put(new Tuple<>(SOLVED, TEST_SOLUTION), new SolvedCheckHandler());
        statusHandlerMap.put(new Tuple<>(TEST_SOLUTION, IN_PROGRESS), new OpenCaseHandler());
        statusHandlerMap.put(new Tuple<>(TEST_SOLUTION, CLOSED), new ClosedCaseHandler());
    }

    public static HpsmStatusHandlerFactory getInstance() {
        return instance;
    }

    @Override
    public HpsmStatusHandler createHandler(HpsmStatus oldStatus, HpsmStatus newStatus) {
        return statusHandlerMap.get(new Tuple<>(oldStatus, newStatus));
    }

    public final class WorkaroundCaseHandler implements HpsmStatusHandler {
        @Override
        public void handle(CaseObject object, CaseComment comment) {
            if (object.getState() != En_CaseState.WORKAROUND) {
                object.setState(En_CaseState.WORKAROUND);
                comment.setCaseStateId(object.getStateId());
            }
        }
    }

    public final class OpenCaseHandler implements HpsmStatusHandler {
        @Override
        public void handle(CaseObject object, CaseComment comment) {
            if (object.getState() != En_CaseState.OPENED) {
                object.setState(En_CaseState.OPENED);
                comment.setCaseStateId(object.getStateId());
            }
        }
    }

    public final class SolvedCheckHandler implements HpsmStatusHandler {

        @Override
        public void handle(CaseObject object, CaseComment comment) {
            if (object.getState() != En_CaseState.CLOSED) {
                object.setState(En_CaseState.CLOSED);
                comment.setCaseStateId(object.getStateId());
            }
        }
    }

    public final class ClosedCaseHandler implements HpsmStatusHandler {

        @Override
        public void handle(CaseObject object, CaseComment comment) {
            if (object.getState() != En_CaseState.VERIFIED) {
                object.setState(En_CaseState.VERIFIED);
                comment.setCaseStateId(object.getStateId());
            }
        }
    }

    private final Map<Tuple<HpsmStatus, HpsmStatus>, HpsmStatusHandler> statusHandlerMap;

    private final static HpsmStatusHandlerFactory instance = new HpsmStatusHandlerFactoryImpl();
}
