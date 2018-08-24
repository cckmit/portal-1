package ru.protei.portal.hpsm.factories;

import com.thoughtworks.xstream.XStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import protei.utils.common.Tuple;
import ru.protei.portal.api.struct.FileStorage;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.model.dao.AttachmentDAO;
import ru.protei.portal.core.model.dao.ExternalCaseAppDAO;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.hpsm.api.HpsmStatus;
import ru.protei.portal.hpsm.handlers.BackChannelEventHandler;
import ru.protei.portal.hpsm.logic.ServiceInstance;
import ru.protei.portal.hpsm.struct.HpsmAttachment;
import ru.protei.portal.hpsm.struct.HpsmMessage;
import ru.protei.portal.hpsm.struct.HpsmMessageHeader;
import ru.protei.portal.hpsm.utils.AttachmentFileStreamSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.protei.portal.core.model.dict.En_CaseState.*;

/**
 * Created by michael on 15.05.17.
 */
public class BackChannelHandlerFactoryImpl implements BackChannelHandlerFactory {

    public BackChannelHandlerFactoryImpl() {
        stateHandlerMap = new HashMap<>();
        stateHandlerMap.put(new Tuple<>(CREATED, ACTIVE), new OpenStateHandler());
        stateHandlerMap.put(new Tuple<>(CREATED, OPENED), new OpenStateHandler());
        stateHandlerMap.put(new Tuple<>(ACTIVE, INFO_REQUEST), new InfoRequestStateHandler());
        stateHandlerMap.put(new Tuple<>(OPENED, INFO_REQUEST), new InfoRequestStateHandler());
        stateHandlerMap.put(new Tuple<>(ACTIVE, WORKAROUND), new WorkAroundStateHandler());
        stateHandlerMap.put(new Tuple<>(OPENED, WORKAROUND), new WorkAroundStateHandler());
        stateHandlerMap.put(new Tuple<>(TEST_CUST, WORKAROUND), new WorkaroundNoChangesHandler());
        stateHandlerMap.put(new Tuple<>(ACTIVE, DONE), new DoneStateHandler());
        stateHandlerMap.put(new Tuple<>(OPENED, DONE), new DoneStateHandler());
        stateHandlerMap.put(new Tuple<>(DONE, VERIFIED), new VerifiedStateHandler());
        stateHandlerMap.put(new Tuple<>(ACTIVE, TEST_LOCAL), new LocalTestStateHandler());
        stateHandlerMap.put(new Tuple<>(OPENED, TEST_LOCAL), new LocalTestStateHandler());
        stateHandlerMap.put(new Tuple<>(TEST_LOCAL, TEST_CUST), new CustomerTestStateHandler());
    }


    @Override
    public BackChannelEventHandler createHandler(HpsmMessage currentState, AssembledCaseEvent event) {

        if (event.isCreateEvent()) {
            return new NoActionHandler();
        }

        return stateHandlerMap.getOrDefault(new Tuple<>(currentState.status().getCaseState(),
                event.getLastState().getState()), new MakeMessageAction());
    }


    public class OpenStateHandler implements BackChannelEventHandler {

        @Override
        public void handle(AssembledCaseEvent event, HpsmMessage message, ServiceInstance instance) throws Exception {
            logger.debug("Applying back-channel handler for {} -> {} status", CREATED.getName(), ACTIVE.getName());
            message.status(HpsmStatus.IN_PROGRESS);
            if (HelperFunc.isEmpty(message.getTxOurOpenTime())) {
                message.setOurOpenTime(event.getEventDate());
            }

            updateAppDataAndSend(message, instance, event.getCaseObject());
        }
    }

    public class InfoRequestStateHandler implements BackChannelEventHandler {

        @Override
        public void handle(AssembledCaseEvent event, HpsmMessage message, ServiceInstance instance) throws Exception {
            logger.debug("Applying back-channel handler for {} -> {} status", ACTIVE.getName(), INFO_REQUEST.getName());
            message.status(HpsmStatus.INFO_REQUEST);
            if (HelperFunc.isEmpty(message.getTxOurOpenTime())) {
                message.setOurOpenTime(event.getEventDate());
            }
            updateAppDataAndSend(message, instance, event.getLastState());
        }
    }

    public class WorkAroundStateHandler implements BackChannelEventHandler {

        @Override
        public void handle(AssembledCaseEvent event, HpsmMessage message, ServiceInstance instance) throws Exception {
            logger.debug("Applying back-channel handler for {} -> {} status", ACTIVE.getName(), WORKAROUND.getName());
            final CaseObject object = event.getCaseObject();

            message.status(HpsmStatus.WORKAROUND);
            message.setWorkaroundText(event.getCaseComment().getText());
            message.setOurWorkaroundTime(event.getEventDate());
            message.setWorkaroundTime(event.getEventDate());
            message.setMessage(event.getCaseComment().getText());

            instance.fillReplyMessageAttributes(message, object);

            updateAppDataAndSend(message, instance, event.getLastState(), event.getCaseComment());
        }
    }


    public class DoneStateHandler implements BackChannelEventHandler {

        @Override
        public void handle(AssembledCaseEvent event, HpsmMessage message, ServiceInstance instance) throws Exception {
            logger.debug("Applying back-channel handler for {} -> {} status", ACTIVE.getName(), DONE.getName());
            message.status(HpsmStatus.SOLVED);

            if (HelperFunc.isEmpty(message.getTxOurSolutionTime())) {
                message.setOurSolutionTime(event.getEventDate());
            }

            updateAppDataAndSend(message, instance, event.getLastState());
        }
    }

    public class ToNewStateHandler implements BackChannelEventHandler {

        @Override
        public void handle(AssembledCaseEvent event, HpsmMessage message, ServiceInstance instance) throws Exception {
            logger.debug("Applying back-channel handler for {} -> {} status", INFO_REQUEST.getName(), CREATED.getName());
            message.status(HpsmStatus.INFO_REQUEST);
            updateAppDataAndSend(message, instance, event.getLastState());
        }
    }

    public class WorkaroundNoChangesHandler implements BackChannelEventHandler {
        @Override
        public void handle(AssembledCaseEvent event, HpsmMessage message, ServiceInstance instance) throws Exception {
            logger.debug("Keeping workaround status");
            message.status(HpsmStatus.WORKAROUND);
            updateAppDataAndSend(message, instance, event.getLastState());
        }
    }

    public class VerifiedStateHandler implements BackChannelEventHandler {

        @Override
        public void handle(AssembledCaseEvent event, HpsmMessage message, ServiceInstance instance) throws Exception {
            logger.debug("Applying back-channel handler for {} -> {} status", DONE.getName(), VERIFIED.getName());
            message.status(HpsmStatus.CLOSED);

            if (HelperFunc.isEmpty(message.getTxOurOpenTime())) {
                message.setOurOpenTime(event.getEventDate());
            }

            if (HelperFunc.isEmpty(message.getTxOurSolutionTime())) {
                message.setOurSolutionTime(event.getEventDate());
            }

            updateAppDataAndSend(message, instance, event.getLastState());
        }
    }

    public class LocalTestStateHandler implements BackChannelEventHandler {

        @Override
        public void handle(AssembledCaseEvent event, HpsmMessage message, ServiceInstance instance) throws Exception {
            logger.debug("Applying back-channel handler for {} -> {} status", ACTIVE.getName(), TEST_LOCAL.getName());
            message.status(HpsmStatus.IN_PROGRESS);
            if (HelperFunc.isEmpty(message.getTxOurOpenTime())) {
                message.setOurOpenTime(event.getEventDate());
            }
            updateAppDataAndSend(message, instance, event.getLastState());
        }
    }


    public class CustomerTestStateHandler implements BackChannelEventHandler {

        @Override
        public void handle(AssembledCaseEvent event, HpsmMessage message, ServiceInstance instance) throws Exception {
            logger.debug("Applying back-channel handler for {} -> {} status", TEST_LOCAL.getName(), TEST_CUST.getName());
            message.status(HpsmStatus.TEST_SOLUTION);

            if (HelperFunc.isEmpty(message.getTxOurOpenTime())) {
                message.setOurOpenTime(event.getEventDate());
            }

            if (HelperFunc.isEmpty(message.getTxOurSolutionTime())) {
                message.setOurSolutionTime(event.getEventDate());
            }

            updateAppDataAndSend(message, instance, event.getLastState());
        }
    }

    public class MakeMessageAction implements BackChannelEventHandler {
        @Override
        public void handle(AssembledCaseEvent event, HpsmMessage message, ServiceInstance instance) throws Exception {
            updateAppDataAndSend(message, instance, event.getLastState());
        }
    }

    public class NoActionHandler implements BackChannelEventHandler {
        @Override
        public void handle(AssembledCaseEvent event, HpsmMessage message, ServiceInstance instance) {
            logger.debug("no action for case {} / instance {}", event.getCaseObject().getExtId(), instance.id());
        }
    }

    private void updateAppDataAndSend(HpsmMessage message, ServiceInstance instance, CaseObject object) throws Exception {
        updateAppDataAndSend(message, instance, object, null);
    }

    private void updateAppDataAndSend(HpsmMessage message, ServiceInstance instance, CaseObject object,
                                      CaseComment comment) throws Exception {
        instance.fillReplyMessageAttributes(message, object);

        ExternalCaseAppData appData = externalCaseAppDAO.get(object.getId());

        appData.setExtAppData(xstream.toXML(message));

        logger.debug("update and send hpsm data, case-id={}, ext={}, data={}", object.getId(),
                appData.getExtAppCaseId(), appData.getExtAppData());

        externalCaseAppDAO.saveExtAppData(appData);

        object.getState();

        HpsmMessageHeader header = new HpsmMessageHeader(appData.getExtAppCaseId(), object.getExtId(),
                message.status());

        logger.debug("ready to send reply mail, case-id={}, ext={}, header={}",
                object.getId(), appData.getExtAppCaseId(), header.toString());

        if (comment != null && comment.getCaseAttachments() != null && !comment.getCaseAttachments().isEmpty()) {
            logger.debug("process attachments case-id={}", object.getId());
            List<HpsmAttachment> replyAttachments = new ArrayList<>();
            for (CaseAttachment in : comment.getCaseAttachments()) {
                Attachment attachment = attachmentDAO.get(in.getAttachmentId());
                if (attachment == null) {
                    logger.debug("case attachment not found, case-id={}, attachment-id={}", object.getId(),
                            in.getAttachmentId());
                    continue;
                }

                logger.debug("append reply attachment file = {}, ext-link={}", attachment.getFileName(),
                        attachment.getExtLink());

                replyAttachments.add(new HpsmAttachment(
                        attachment.getFileName(),
                        attachment.getMimeType(),
                        attachment.getLabelText(),
                        attachment.getDataSize().intValue(),
                        new AttachmentFileStreamSource(fileStorage, attachment.getExtLink())));
            }
            instance.sendReply(header, message, replyAttachments);
            return;
        }

        instance.sendReply(header, message);
    }

    @Autowired
    @Qualifier("hpsmSerializer")
    private XStream xstream;

    @Autowired
    ExternalCaseAppDAO externalCaseAppDAO;

    @Autowired
    AttachmentDAO attachmentDAO;

    @Autowired
    FileStorage fileStorage;

    private Map<Tuple<En_CaseState, En_CaseState>, BackChannelEventHandler> stateHandlerMap;
    private static Logger logger = LoggerFactory.getLogger(BackChannelHandlerFactoryImpl.class);
}
