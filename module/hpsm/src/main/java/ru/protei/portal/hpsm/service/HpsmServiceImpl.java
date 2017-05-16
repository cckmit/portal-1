package ru.protei.portal.hpsm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import ru.protei.portal.core.event.CaseCommentEvent;
import ru.protei.portal.core.event.CaseObjectEvent;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.hpsm.api.HpsmMessageFactory;
import ru.protei.portal.hpsm.api.MailSendChannel;
import ru.protei.portal.hpsm.config.HpsmEnvConfig;
import ru.protei.portal.hpsm.logic.BackChannelEventHandler;
import ru.protei.portal.hpsm.logic.BackChannelHandlerFactory;
import ru.protei.portal.hpsm.logic.InboundMessageHandler;
import ru.protei.portal.hpsm.logic.ServiceInstance;
import ru.protei.portal.hpsm.struct.HpsmMessage;
import ru.protei.portal.hpsm.struct.HpsmMessageHeader;
import ru.protei.portal.hpsm.utils.CompanyBranchMap;
import ru.protei.portal.hpsm.utils.HpsmUtils;
import ru.protei.portal.hpsm.utils.TestServiceInstance;

import javax.annotation.PostConstruct;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;

/**
 * Created by michael on 27.04.17.
 */
public class HpsmServiceImpl implements HpsmService {

    private static Logger logger = LoggerFactory.getLogger(HpsmService.class);

    @Autowired
    @Qualifier("hpsmSendChannel")
    MailSendChannel outboundChannel;

    @Autowired
    HpsmEnvConfig config;

    @Autowired
    CompanyBranchMap companyBranchMap;

    @Autowired
    HpsmMessageFactory hpsmMessageFactory;

    @Autowired
    BackChannelHandlerFactory backChannelHandlerFactory;

    @Autowired
    TestServiceInstance testServiceInstance;

    private InboundMessageHandler[] inboundHandlers;
    private HashMap<String, ServiceInstance> serviceMap;



    public HpsmServiceImpl(InboundMessageHandler...handlers) {
        this.inboundHandlers = handlers;
        this.serviceMap = new HashMap<>();
    }

    @PostConstruct
    private void postConstruct () {
        config.getInstanceList().forEach(cfg -> addService(cfg));
        if (testServiceInstance != null) {
            serviceMap.put(testServiceInstance.id(), testServiceInstance);
        }
    }

    private void addService (HpsmEnvConfig.ServiceConfig instCfg) {
        serviceMap.put(instCfg.getId(), new ServiceInstanceImpl(instCfg, companyBranchMap, outboundChannel, hpsmMessageFactory));
    }


    @Override
    public void handleInboundRequest() {
        logger.debug("check for incoming requests");

        serviceMap.values().forEach(s -> {
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
            }
            else {
                logger.debug("no incoming messages for {} instance", s.id());
            }
        });
    }


    private ServiceInstance findInstance (CaseObject object) {
        for (ServiceInstance instance : serviceMap.values()) {
            if (instance.acceptCase(object))
                return instance;
        }

        return null;
    }

    @Override
    @EventListener
    public void onCaseCommentEvent(CaseCommentEvent event) {

        CaseObject object = event.getCaseObject();

        logger.debug("hpsm, case-comment event, case {}, comment #{}", object.getExtId(),event.getCaseComment().getId());
        ServiceInstance instance = findInstance(event.getCaseObject());

        if (instance == null) {
            logger.debug("no handler instance found for case {}", object.getExtId());
            return;
        }

        HpsmMessage msg = this.hpsmMessageFactory.parseMessage(object.getExtAppData());

        if (msg == null) {
            logger.error("unable to parse app-data, case {}", object.getExtId());
            return;
        }

        HpsmMessageHeader header = new HpsmMessageHeader(object.getExtAppCaseId(), object.getExtId(), msg.status());
        msg.setMessage(event.getCaseComment().getText());

        try {
            instance.sendReply(header, msg);
            logger.debug("case-comment event handled for case {}", object.getExtId());
        }
        catch (Exception e) {
            logger.error("error while attempt to send mail", e);
        }
    }

    @Override
    @EventListener
    public void onCaseObjectEvent(CaseObjectEvent event) {

        ServiceInstance instance = findInstance(event.getCaseObject());
        if (instance == null) {
            logger.debug("no handler instance found for case {}", event.getCaseObject().getExtId());
            return;
        }

        HpsmMessage msg = this.hpsmMessageFactory.parseMessage(event.getCaseObject().getExtAppData());

        if (msg == null) {
            logger.error("unable to parse app-data, case {}", event.getCaseObject().getExtId());
            return;
        }

        BackChannelEventHandler handler = backChannelHandlerFactory.createHandler(msg, event);
        if (handler == null) {
            logger.debug("unable to create event handler, case {}", event.getCaseObject().getExtId());
            return;
        }

        try {
            handler.handle(event, msg, instance);
        }
        catch (Exception e) {
            logger.debug("error while handling event for case {}", event.getCaseObject().getExtId(), e);
        }
    }
}
