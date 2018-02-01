package ru.protei.portal.hpsm.factories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protei.utils.common.Tuple;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.hpsm.api.HpsmStatus;
import ru.protei.portal.hpsm.handlers.HpsmStatusHandler;
import ru.protei.portal.hpsm.struct.HpsmMessage;

import java.util.HashMap;
import java.util.Map;

import static ru.protei.portal.hpsm.api.HpsmStatus.*;

public final class HpsmStatusHandlerFactoryImpl implements HpsmStatusHandlerFactory {

    public HpsmStatusHandlerFactoryImpl() {
        statusHandlerMap.put(new Tuple<>(INFO_REQUEST, IN_PROGRESS), new OpenCaseHandler());
        statusHandlerMap.put(new Tuple<>(WORKAROUND, TEST_WA), new WorkaroundCaseHandler());
        statusHandlerMap.put(new Tuple<>(TEST_WA, REJECT_WA), new RejectWAHandler());
        statusHandlerMap.put(new Tuple<>(TEST_WA, CONFIRM_WA), new ConfirmWACaseHandler());
        statusHandlerMap.put(new Tuple<>(SOLVED, TEST_SOLUTION), new SolvedCheckHandler());
        statusHandlerMap.put(new Tuple<>(TEST_SOLUTION, IN_PROGRESS), new RejectSolutionHandler());
        statusHandlerMap.put(new Tuple<>(TEST_SOLUTION, CLOSED), new ClosedCaseHandler());
    }

    @Override
    public HpsmStatusHandler createHandler(HpsmMessage msg, HpsmStatus newStatus) {
        logger.debug("Creating handler for HPSM status");
        this.newState = newStatus.getCaseState();
        this.msg = msg;
        return statusHandlerMap.getOrDefault(new Tuple<>(msg.status(), newStatus), new DefaultCaseHandler());
    }

    public final class WorkaroundCaseHandler implements HpsmStatusHandler {
        @Override
        public void handle(CaseObject object, CaseComment comment) {
            logger.debug("Applying handler for {} -> {} state", WORKAROUND.getHpsmCode(), TEST_WA.getHpsmCode());
            if (object.getState() != En_CaseState.WORKAROUND) {
                object.setState(En_CaseState.WORKAROUND);
                comment.setCaseStateId(object.getStateId());
            }
        }
    }

    public final class ConfirmWACaseHandler implements HpsmStatusHandler {
        @Override
        public void handle(CaseObject object, CaseComment comment) {
            logger.debug("Applying handler for {} -> {} state", TEST_WA.getHpsmCode(), CONFIRM_WA.getHpsmCode());
            if (object.getState() != En_CaseState.WORKAROUND) {
                object.setState(En_CaseState.WORKAROUND);
                comment.setCaseStateId(object.getStateId());
            }
        }
    }

    public final class RejectWAHandler implements HpsmStatusHandler {
        @Override
        public void handle(CaseObject object, CaseComment comment) {
            logger.debug("Applying handler for {} -> {} state", TEST_WA.getHpsmCode(), REJECT_WA.getHpsmCode());
            msg.setTxOurWorkaroundTime("");
            msg.setWorkaroundText("");
            if (object.getState() != En_CaseState.OPENED) {
                object.setState(En_CaseState.OPENED);
                comment.setCaseStateId(object.getStateId());
            }
        }
    }

    public final class RejectSolutionHandler implements HpsmStatusHandler {
        @Override
        public void handle(CaseObject object, CaseComment comment) {
            logger.debug("Applying handler for {} -> {} state", TEST_SOLUTION.getHpsmCode(), IN_PROGRESS.getHpsmCode());
            msg.setTxOurSolutionTime("");
            msg.setResolutionText("");
            if (object.getState() != En_CaseState.OPENED) {
                object.setState(En_CaseState.OPENED);
                comment.setCaseStateId(object.getStateId());
            }
        }
    }

    public final class DefaultCaseHandler implements HpsmStatusHandler {
        @Override
        public void handle(CaseObject object, CaseComment comment) {
            logger.debug("Applying default status handler");
            object.setState(newState);
            comment.setCaseStateId((long) newState.getId());
        }
    }

    public final class OpenCaseHandler implements HpsmStatusHandler {
        @Override
        public void handle(CaseObject object, CaseComment comment) {
            logger.debug("Applying handler for {} -> {} state", INFO_REQUEST.getHpsmCode(), IN_PROGRESS.getHpsmCode());
            if (object.getState() != En_CaseState.OPENED) {
                object.setState(En_CaseState.OPENED);
                comment.setCaseStateId(object.getStateId());
            }
        }
    }

    public final class SolvedCheckHandler implements HpsmStatusHandler {
        @Override
        public void handle(CaseObject object, CaseComment comment) {
            logger.debug("Applying handler for {} -> {} state", SOLVED.getHpsmCode(), TEST_SOLUTION.getHpsmCode());
            if (object.getState() != En_CaseState.CLOSED) {
                object.setState(En_CaseState.CLOSED);
                comment.setCaseStateId(object.getStateId());
            }
        }
    }

    public final class ClosedCaseHandler implements HpsmStatusHandler {
        @Override
        public void handle(CaseObject object, CaseComment comment) {
            logger.debug("Applying handler for {} -> {} state", TEST_SOLUTION.getHpsmCode(), CLOSED.getHpsmCode());
            if (object.getState() != En_CaseState.VERIFIED) {
                object.setState(En_CaseState.VERIFIED);
                comment.setCaseStateId(object.getStateId());
            }
        }
    }

    private final Map<Tuple<HpsmStatus, HpsmStatus>, HpsmStatusHandler> statusHandlerMap = new HashMap<>();
    private En_CaseState newState;
    private HpsmMessage msg;

    private final static Logger logger = LoggerFactory.getLogger(HpsmStatusHandlerFactoryImpl.class);
}
