package ru.protei.portal.hpsm.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.MimeMessageHelper;
import ru.protei.portal.hpsm.api.MailHandler;
import ru.protei.portal.hpsm.api.MailMessageFactory;
import ru.protei.portal.hpsm.api.MailSendChannel;
import ru.protei.portal.hpsm.struct.HpsmPingMessage;
import ru.protei.portal.hpsm.config.HpsmEnvConfig;
import ru.protei.portal.hpsm.utils.HpsmUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * Created by michael on 25.04.17.
 */
public class HpsmPingEventHandler implements MailHandler {

    private static Logger logger = LoggerFactory.getLogger(HpsmPingEventHandler.class);

    @Autowired
    @Qualifier("hpsmSendChannel")
    private MailSendChannel sendChannel;

    @Autowired
    @Qualifier("hpsmMessageFactory")
    private MailMessageFactory messageFactory;

    @Autowired
    private HpsmEnvConfig setup;


    public HpsmPingEventHandler() {
    }


    public void setSendChannel (MailSendChannel channel) {
        this.sendChannel = channel;
    }


    public boolean handle (MimeMessage msg) {

        HpsmPingMessage cmd = null;

        try {
           cmd = HpsmPingMessage.parse(msg.getSubject());
        }
        catch (Throwable e) {
            logger.debug("unable to parse ping-command subject", e);
        }

        if (cmd != null) {

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

                sendChannel.send(makeMessgae(HpsmUtils.getEmailFromAddress(msg), response));
            }
            catch (Throwable e) {
                logger.debug("unable to send response", e);
            }

        }


        return cmd != null;

    }


    public MimeMessage makeMessgae (String to, HpsmPingMessage cmd) throws MessagingException {

        MimeMessage msg = messageFactory.createMailMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, false);

        helper.setSubject(cmd.toString());
        helper.setTo(to);
        helper.setFrom(setup.getSenderAddress());

        return msg;
    }

}
