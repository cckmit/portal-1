package ru.protei.portal.hpsm.logic;

import com.thoughtworks.xstream.XStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.CaseObjectEvent;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.ExternalCaseAppDAO;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.ExternalCaseAppData;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.hpsm.api.HpsmSeverity;
import ru.protei.portal.hpsm.api.HpsmStatus;
import ru.protei.portal.hpsm.struct.HpsmMessage;
import ru.protei.portal.hpsm.struct.HpsmMessageHeader;

import java.util.HashMap;
import java.util.Map;

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

    private Map<En_CaseState, BackChannelEventHandler> stateHandlerMap;


    public BackChannelHandlerFactoryImpl() {
        stateHandlerMap = new HashMap<>();
        stateHandlerMap.put(En_CaseState.CREATED, new  ToNewStateHandler ());
        stateHandlerMap.put(En_CaseState.OPENED, new  OpenStateHandler ());
        stateHandlerMap.put(En_CaseState.DONE, new DoneStateHandler());
        stateHandlerMap.put(En_CaseState.VERIFIED, new VerifiedStateHandler());
        stateHandlerMap.put(En_CaseState.ACTIVE, new InProcessStateHandler());
        stateHandlerMap.put(En_CaseState.TEST_LOCAL, new LocalTestStateHandler());
        stateHandlerMap.put(En_CaseState.TEST_CUST, new CustomerTestStateHandler());
    }


    @Override
    public BackChannelEventHandler createHandler(HpsmMessage currentState, CaseObjectEvent event) {

        if (event.isCreateEvent()) {
            return new NoActionHandler ();
        }

        if (event.isCaseStateChanged()) {
            return stateHandlerMap.getOrDefault(event.getNewState().getState(), new MakeMessageAction ());
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
