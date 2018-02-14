package ru.protei.portal.hpsm.service;

import com.thoughtworks.xstream.XStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import ru.protei.portal.api.struct.FileStorage;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.mail.MailSendChannel;
import ru.protei.portal.core.model.dao.AttachmentDAO;
import ru.protei.portal.core.model.dao.ExternalCaseAppDAO;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.CaseAttachment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.ExternalCaseAppData;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.hpsm.api.HpsmMessageFactory;
import ru.protei.portal.hpsm.config.HpsmEnvConfig;
import ru.protei.portal.hpsm.factories.BackChannelHandlerFactory;
import ru.protei.portal.hpsm.handlers.BackChannelEventHandler;
import ru.protei.portal.hpsm.logic.InboundMessageHandler;
import ru.protei.portal.hpsm.logic.ServiceInstance;
import ru.protei.portal.hpsm.logic.ServiceInstanceRegistry;
import ru.protei.portal.hpsm.struct.HpsmAttachment;
import ru.protei.portal.hpsm.struct.HpsmMessage;
import ru.protei.portal.hpsm.struct.HpsmMessageHeader;
import ru.protei.portal.hpsm.utils.AttachmentFileStreamSource;
import ru.protei.portal.hpsm.utils.CompanyBranchMap;
import ru.protei.portal.hpsm.utils.HpsmUtils;
import ru.protei.portal.hpsm.utils.TestServiceInstance;

import javax.annotation.PostConstruct;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michael on 27.04.17.
 */
public class HpsmServiceImpl implements HpsmService {

    private static Logger logger = LoggerFactory.getLogger(HpsmService.class);

    @Autowired
    MailSendChannel outboundChannel;

    @Autowired
    HpsmEnvConfig config;

    @Autowired
    CompanyBranchMap companyBranchMap;

    @Autowired
    ExternalCaseAppDAO externalCaseAppDAO;

    @Autowired
    AttachmentDAO attachmentDAO;

    @Autowired
    HpsmMessageFactory hpsmMessageFactory;

    @Autowired
    BackChannelHandlerFactory backChannelHandlerFactory;

    @Autowired(required = false)
    TestServiceInstance testServiceInstance;

    @Autowired
    ServiceInstanceRegistry serviceInstanceRegistry;

    @Autowired
    FileStorage fileStorage;

    @Autowired
    @Qualifier("hpsmSerializer")
    XStream xStream;

    private InboundMessageHandler[] inboundHandlers;


    public HpsmServiceImpl(InboundMessageHandler... handlers) {
        this.inboundHandlers = handlers;
    }

    @PostConstruct
    private void postConstruct() {
        if (testServiceInstance != null) {
            serviceInstanceRegistry.add(testServiceInstance);
        }
    }

    @Override
    public void handleInboundRequest() {
        logger.debug("check for incoming requests");

        serviceInstanceRegistry.each(s -> {
            logger.debug("try read mail message from {} instance", s.id());

            MimeMessage msg = s.read();

            if (msg != null) {

                logger.debug("service {}, got message to handle", s.id());

                boolean handled = false;

                for (InboundMessageHandler h : inboundHandlers) {
                    if (handled = h.handle(msg, s))
                        break;
                }

                if (!handled) {
                    logger.warn("unable to handle message, subject : {}", HpsmUtils.getMessageSubject(msg));
                }
            } else {
                logger.debug("no incoming messages for {} instance", s.id());
            }
        });
    }

    @Override
    @EventListener
    public void onAssembledCaseEvent(AssembledCaseEvent event) {
        if (event.getServiceModule() == ServiceModule.HPSM) {
            logger.debug("skip handle self-published event for {}", event.getCaseObject().getExtId());
            return;
        }

        CaseObject object = event.getCaseObject();
        ExternalCaseAppData appData = externalCaseAppDAO.get(object.getId());

        logger.debug("hpsm, case-comment event, case {}, comment #{}", object.getExtId(), event.getCaseComment().getId());
        ServiceInstance instance = serviceInstanceRegistry.find(event.getCaseObject());

        if (instance == null) {
            logger.debug("no handler instance found for case {}", object.getExtId());
            return;
        }

        HpsmMessage msg = this.hpsmMessageFactory.parseMessage(appData.getExtAppData());

        if (msg == null) {
            logger.error("unable to parse app-data, case {}", object.getExtId());
            return;
        }

        BackChannelEventHandler handler = backChannelHandlerFactory.createHandler(msg, event);
        if (handler == null) {
            logger.debug("unable to create event handler, case {}", event.getCaseObject().getExtId());
            return;
        }

        try {
            handler.handle(event, msg, instance);
            logger.debug("case-object event handled for case {}", object.getExtId());
        } catch (Exception e) {
            logger.debug("error while handling event for case {}", event.getCaseObject().getExtId(), e);
        }
    }
}
