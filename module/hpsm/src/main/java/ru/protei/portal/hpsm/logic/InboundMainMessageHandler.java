package ru.protei.portal.hpsm.logic;

import com.thoughtworks.xstream.XStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.hpsm.factories.HpsmEventHandlerFactory;
import ru.protei.portal.hpsm.factories.HpsmEventHandlerFactoryImpl;
import ru.protei.portal.hpsm.handlers.HpsmEventHandler;
import ru.protei.portal.hpsm.struct.HpsmMessageHeader;
import ru.protei.portal.hpsm.utils.HpsmUtils;

import javax.mail.internet.MimeMessage;

/**
 * Created by michael on 25.04.17.
 */
public class InboundMainMessageHandler implements InboundMessageHandler {

    @Override
    public boolean handle(MimeMessage msg, ServiceInstance instance) {

        HpsmMessageHeader subject = null;

        try {
            subject = HpsmMessageHeader.parse(msg.getSubject());

            if (subject == null)
                return false;

            logger.debug("main message parsed, subject {}", subject.toString());

            HpsmEvent request = buildRequest(subject, instance, msg);

            if (request == null) {
                logger.debug("unable to create request");
                instance.sendReject(HpsmUtils.getEmailFromAddress(msg), subject, "wrong request data");
                return true;
            }

            HpsmEventHandler handler = handlerFactory.createHandler(request, instance);
            logger.debug("created handler : {}", handler);

            handler.handle(request, instance);

            logger.debug("handler invocation completed for {}", request.getSubject().getHpsmId());

        } catch (Throwable e) {
            logger.debug("error on event message handle", e);
        }


        return subject != null;
    }

    private HpsmEvent buildRequest(HpsmMessageHeader subject, ServiceInstance instance, MimeMessage msg) throws Exception {

        logger.debug("Got inbound event-message {}", subject.toString());

        HpsmEvent hpsmEvent = HpsmUtils.parseEvent(msg, xstream);

        if (hpsmEvent.getHpsmMessage() != null) {
            logger.debug("event message parsed");
        } else {
            logger.debug("unable to parse event data");
            return null;
        }

        Company company = instance.getCompanyByBranch(hpsmEvent.getHpsmMessage().getCompanyBranch());

        if (company == null && subject.isNewCaseRequest()) {
            logger.debug("unable to map company by branch name : {}", hpsmEvent.getHpsmMessage().getCompanyBranch());
            return null;
        } else {
            hpsmEvent.assign(company);
        }

        return hpsmEvent;
    }

    @Autowired
    @Qualifier("hpsmSerializer")
    private XStream xstream;

    @Autowired
    private HpsmEventHandlerFactory handlerFactory;

    private static Logger logger = LoggerFactory.getLogger(InboundMainMessageHandler.class);
}
