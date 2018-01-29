package ru.protei.portal.hpsm.factories;

import com.thoughtworks.xstream.XStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import protei.utils.common.Tuple;
import ru.protei.portal.core.event.CaseObjectEvent;
import ru.protei.portal.core.model.dao.ExternalCaseAppDAO;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.ExternalCaseAppData;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.hpsm.api.HpsmStatus;
import ru.protei.portal.hpsm.handlers.BackChannelEventHandler;
import ru.protei.portal.hpsm.logic.ServiceInstance;
import ru.protei.portal.hpsm.struct.HpsmMessage;
import ru.protei.portal.hpsm.struct.HpsmMessageHeader;

import java.util.HashMap;
import java.util.Map;

import static ru.protei.portal.core.model.dict.En_CaseState.*;

/**
 * Created by michael on 15.05.17.
 */
public class BackChannelHandlerFactoryImpl implements BackChannelHandlerFactory {

    private static Logger logger = LoggerFactory.getLogger(BackChannelHandlerFactoryImpl.class);

    @Autowired
    @Qualifier("hpsmSerializer")
    private XStream xstream;

    @Autowired
    ExternalCaseAppDAO externalCaseAppDAO;

    private Map<Tuple<En_CaseState, En_CaseState>, BackChannelEventHandler> stateHandlerMap;


    public BackChannelHandlerFactoryImpl() {
        stateHandlerMap = new HashMap<>();
        stateHandlerMap.put(new Tuple<>(CREATED, OPENED), new OpenStateHandler ());
        stateHandlerMap.put(new Tuple<>(OPENED, INFO_REQUEST), new InfoRequestStateHandler());
        stateHandlerMap.put(new Tuple<>(OPENED, WORKAROUND), new WorkAroundStateHandler());
        stateHandlerMap.put(new Tuple<>(OPENED, DONE), new DoneStateHandler());
        stateHandlerMap.put(new Tuple<>(DONE, VERIFIED), new VerifiedStateHandler());
        stateHandlerMap.put(new Tuple<>(CREATED, ACTIVE), new InProcessStateHandler());
        stateHandlerMap.put(new Tuple<>(OPENED, TEST_LOCAL), new LocalTestStateHandler());
        stateHandlerMap.put(new Tuple<>(TEST_LOCAL, TEST_CUST), new CustomerTestStateHandler());
    }


    @Override
    public BackChannelEventHandler createHandler(HpsmMessage currentState, CaseObjectEvent event) {

        if (event.isCreateEvent()) {
            return new NoActionHandler ();
        }

        if (event.isCaseStateChanged()) {
            return stateHandlerMap.getOrDefault(new Tuple<>(event.getOldState().getState(), event.getNewState().getState()), new MakeMessageAction ());
        }

        // by default
        return new MakeMessageAction ();
    }



    public class OpenStateHandler implements BackChannelEventHandler {

        @Override
        public void handle(CaseObjectEvent event, HpsmMessage message, ServiceInstance instance) throws Exception {

            message.status(HpsmStatus.IN_PROGRESS);

            if (HelperFunc.isEmpty(message.getTxOurOpenTime())) {
                message.setOurOpenTime(event.getEventDate());
            }

            updateAppDataAndSend(message, instance, event.getNewState());
        }
    }

    public class InfoRequestStateHandler implements BackChannelEventHandler {

        @Override
        public void handle(CaseObjectEvent event, HpsmMessage message, ServiceInstance instance) throws Exception {
            message.status(HpsmStatus.INFO_REQUEST);
            if (HelperFunc.isEmpty(message.getTxOurOpenTime())) {
                message.setOurOpenTime(event.getEventDate());
            }
            updateAppDataAndSend(message, instance, event.getNewState());
        }
    }


    public class WorkAroundStateHandler implements BackChannelEventHandler {

        @Override
        public void handle(CaseObjectEvent event, HpsmMessage message, ServiceInstance instance) throws Exception {
            message.status(HpsmStatus.WORKAROUND);

            if (HelperFunc.isEmpty(message.getTxOurOpenTime())) {
                message.setOurOpenTime(event.getEventDate());
            }

            if (HelperFunc.isEmpty(message.getTxOurWorkaroundTime())) {
                message.setOurWorkaroundTime(event.getEventDate());
            }

            if (HelperFunc.isEmpty(message.getTxWorkaroundTime())) {
                message.setWorkaroundTime(event.getEventDate());
            }

            updateAppDataAndSend(message, instance, event.getNewState());
        }
    }


    public class DoneStateHandler implements BackChannelEventHandler {

        @Override
        public void handle(CaseObjectEvent event, HpsmMessage message, ServiceInstance instance) throws Exception {
            message.status(HpsmStatus.SOLVED);

            if (HelperFunc.isEmpty(message.getTxOurSolutionTime())) {
                message.setOurSolutionTime(event.getEventDate());
            }

            updateAppDataAndSend(message, instance, event.getNewState());
        }
    }

    public class ToNewStateHandler implements BackChannelEventHandler {

        @Override
        public void handle(CaseObjectEvent event, HpsmMessage message, ServiceInstance instance) throws Exception {
            message.status(HpsmStatus.INFO_REQUEST);
            updateAppDataAndSend(message, instance, event.getNewState());
        }
    }

    public class InProcessStateHandler implements BackChannelEventHandler {

        @Override
        public void handle(CaseObjectEvent event, HpsmMessage message, ServiceInstance instance) throws Exception {
            message.status(HpsmStatus.IN_PROGRESS);
            if (HelperFunc.isEmpty(message.getTxOurOpenTime())) {
                message.setOurOpenTime(event.getEventDate());
            }
            updateAppDataAndSend(message, instance, event.getNewState());
        }
    }

    public class VerifiedStateHandler implements BackChannelEventHandler {

        @Override
        public void handle(CaseObjectEvent event, HpsmMessage message, ServiceInstance instance) throws Exception {
            message.status(HpsmStatus.CLOSED);

            if (HelperFunc.isEmpty(message.getTxOurOpenTime())) {
                message.setOurOpenTime(event.getEventDate());
            }

            if (HelperFunc.isEmpty(message.getTxOurSolutionTime())) {
                message.setOurSolutionTime(event.getEventDate());
            }

            updateAppDataAndSend(message, instance, event.getNewState());
        }
    }

    public class LocalTestStateHandler implements BackChannelEventHandler {

        @Override
        public void handle(CaseObjectEvent event, HpsmMessage message, ServiceInstance instance) throws Exception {
            message.status(HpsmStatus.IN_PROGRESS);
            if (HelperFunc.isEmpty(message.getTxOurOpenTime())) {
                message.setOurOpenTime(event.getEventDate());
            }
            updateAppDataAndSend(message, instance, event.getNewState());
        }
    }


    public class CustomerTestStateHandler implements BackChannelEventHandler {

        @Override
        public void handle(CaseObjectEvent event, HpsmMessage message, ServiceInstance instance) throws Exception {
            message.status(HpsmStatus.TEST_SOLUTION);

            if (HelperFunc.isEmpty(message.getTxOurOpenTime())) {
                message.setOurOpenTime(event.getEventDate());
            }

            if (HelperFunc.isEmpty(message.getTxOurSolutionTime())) {
                message.setOurSolutionTime(event.getEventDate());
            }

            updateAppDataAndSend(message, instance, event.getNewState());
        }
    }

    public class MakeMessageAction implements BackChannelEventHandler {
        @Override
        public void handle(CaseObjectEvent event, HpsmMessage message, ServiceInstance instance) throws Exception {
            updateAppDataAndSend(message, instance, event.getNewState());
        }
    }

    public class NoActionHandler implements BackChannelEventHandler {
        @Override
        public void handle(CaseObjectEvent event, HpsmMessage message, ServiceInstance instance) {
            logger.debug("no action for case {} / instance {}", event.getCaseObject().getExtId(), instance.id());
        }
    }


    private void updateAppDataAndSend(HpsmMessage message, ServiceInstance instance, CaseObject object) throws Exception {
        instance.fillReplyMessageAttributes(message, object);

        ExternalCaseAppData appData = externalCaseAppDAO.get(object.getId());

        appData.setExtAppData(xstream.toXML(message));

        logger.debug("update and send hpsm data, case-id={}, ext={}, data={}", object.getId(), appData.getExtAppCaseId(), appData.getExtAppData());

        externalCaseAppDAO.saveExtAppData(appData);

        HpsmMessageHeader header = new HpsmMessageHeader(appData.getExtAppCaseId(), object.getExtId(), message.status());

        logger.debug("ready to send reply mail, case-id={}, ext={}, header={}", object.getId(), appData.getExtAppCaseId(), header.toString());

        instance.sendReply(header, message);
    }

}
