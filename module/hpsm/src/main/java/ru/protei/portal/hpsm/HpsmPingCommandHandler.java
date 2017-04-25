package ru.protei.portal.hpsm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import ru.protei.portal.hpsm.struct.HpsmPingCmd;
import ru.protei.portal.hpsm.struct.HpsmSetup;

import javax.mail.internet.MimeMessage;

/**
 * Created by michael on 25.04.17.
 */
public class HpsmPingCommandHandler implements MailHandler {

    private static Logger logger = LoggerFactory.getLogger(HpsmPingCommandHandler.class);

    @Autowired
    @Qualifier("hpsmSender")
    private JavaMailSender sender;

    @Autowired
    private HpsmSetup setup;

    public HpsmPingCommandHandler() {
    }


    public boolean handle (MimeMessage msg) {

        HpsmPingCmd cmd = null;

        try {
           cmd = HpsmPingCmd.parse(msg.getSubject());
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

                HpsmPingCmd response = cmd.response();

                logger.debug("send response {}", response.toString());

                MimeMessage message = sender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, false);

                helper.setSubject(response.toString());
                helper.setTo(setup.hpsmAddress);
                helper.setFrom(setup.senderAddress);
            }
            catch (Throwable e) {
                logger.debug("unable to send response", e);
            }

        }


        return cmd != null;

    }

}
