package ru.protei.portal.hpsm.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.protei.portal.hpsm.struct.HpsmPingMessage;
import ru.protei.portal.hpsm.utils.HpsmUtils;

import javax.mail.internet.MimeMessage;

/**
 * Created by michael on 25.04.17.
 */
public class InboundPingMessageHandler implements InboundMessageHandler {

    private static Logger logger = LoggerFactory.getLogger(InboundPingMessageHandler.class);


    public InboundPingMessageHandler() {
    }


    @Override
    public boolean handle(MimeMessage msg, ServiceInstance instance) {

        HpsmPingMessage cmd = null;

        try {
           cmd = HpsmPingMessage.parse(msg.getSubject());
        }
        catch (Throwable e) {
            logger.debug("unable to parse ping-command subject", e);
        }

        if (cmd != null) {

            logger.debug("ping-message parsed: {}", cmd.toString());

            // защита от рекурсии при использовании одного почтового ящика для приема и отправки в тестовом окружении
            if (!cmd.isRequest()) {
                logger.debug("skip ping-response handling: {}", cmd.toString());
                // но мы все-таки обработали это письмо :)
                return true;
            }

            try {
                logger.debug("got ping-command: {}", cmd.toString());

                HpsmPingMessage response = cmd.response();

                logger.debug("send response {}", response.toString());

                instance.sendReply(HpsmUtils.getEmailFromAddress(msg), response);
            }
            catch (Throwable e) {
                logger.debug("unable to send response", e);
            }
        }

        return cmd != null;
    }



}
