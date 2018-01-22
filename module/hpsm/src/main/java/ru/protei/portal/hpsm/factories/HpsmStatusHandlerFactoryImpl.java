package ru.protei.portal.hpsm.factories;

import protei.utils.common.Tuple;
import static ru.protei.portal.hpsm.api.HpsmStatus.*;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.hpsm.api.HpsmStatus;
import ru.protei.portal.hpsm.handlers.HpsmStatusHandler;
import ru.protei.portal.hpsm.logic.HpsmEvent;
import ru.protei.portal.hpsm.logic.ServiceInstance;

import java.util.HashMap;
import java.util.Map;

public final class HpsmStatusHandlerFactoryImpl implements HpsmStatusHandlerFactory {
    private HpsmStatusHandlerFactoryImpl() {
        statusHandlerMap = new HashMap<>();
        statusHandlerMap.put(new Tuple<>(INFO_REQUEST, IN_PROGRESS), new InfoRequestToOpenHandler());
        statusHandlerMap.put(new Tuple<>(WORKAROUND, TEST_WA), new WAToWACheckHandler());
        statusHandlerMap.put(new Tuple<>(TEST_WA, REJECT_WA), new WACheckToWARejectHandler());
        statusHandlerMap.put(new Tuple<>(TEST_WA, CONFIRM_WA), new WACheckToWAAcceptHandler());
        statusHandlerMap.put(new Tuple<>(SOLVED, TEST_SOLUTION), new SolvedToSolvedCheckHandler());
        statusHandlerMap.put(new Tuple<>(TEST_SOLUTION, IN_PROGRESS), new SolvedCheckToOpenHandler());
        statusHandlerMap.put(new Tuple<>(TEST_SOLUTION, CLOSED), new SolvedCheckToClosedHandler());
    }

    public static HpsmStatusHandlerFactory getInstance() {
        return instance;
    }

    @Override
    public HpsmStatusHandler createHandler(CaseObject object, CaseComment comment) {
        return null;
    }

    public final class InfoRequestToOpenHandler implements HpsmStatusHandler {

        @Override
        public void handle(HpsmEvent request, ServiceInstance instance) throws Exception {

        }
    }

    public final class WAToWACheckHandler implements HpsmStatusHandler {
        @Override
        public void handle(HpsmEvent request, ServiceInstance instance) throws Exception {

        }
    }

    public final class WACheckToWARejectHandler implements HpsmStatusHandler {
        @Override
        public void handle(HpsmEvent request, ServiceInstance instance) throws Exception {

        }
    }

    public final class WACheckToWAAcceptHandler implements HpsmStatusHandler {

        @Override
        public void handle(HpsmEvent request, ServiceInstance instance) throws Exception {

        }
    }

    public final class SolvedToSolvedCheckHandler implements HpsmStatusHandler {

        @Override
        public void handle(HpsmEvent request, ServiceInstance instance) throws Exception {

        }
    }

    public final class SolvedCheckToOpenHandler implements HpsmStatusHandler {

        @Override
        public void handle(HpsmEvent request, ServiceInstance instance) throws Exception {

        }
    }

    public final class SolvedCheckToClosedHandler implements HpsmStatusHandler {

        @Override
        public void handle(HpsmEvent request, ServiceInstance instance) throws Exception {

        }
    }

    private final Map<Tuple<HpsmStatus, HpsmStatus>, HpsmStatusHandler> statusHandlerMap;

    private final static HpsmStatusHandlerFactory instance = new HpsmStatusHandlerFactoryImpl();
}
