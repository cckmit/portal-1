package ru.protei.portal.core.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.Address;
import javax.mail.internet.MimeMessage;

/**
 * Created by michael on 25.04.17.
 */
public class JavaMailSendChannel implements MailSendChannel {

    private static Logger logger = LoggerFactory.getLogger(JavaMailSendChannel.class);

    private JavaMailSender sender;

    public JavaMailSendChannel(JavaMailSender sender) {
        this.sender = sender;
    }

    @Override
    public void send(MimeMessage msg) {

        if (logger.isDebugEnabled()) {
            try {
                StringBuilder sb = new StringBuilder();
                for (Address a : msg.getAllRecipients()) {
                    sb.append(a.toString());
                    sb.append(" ");
                }

                logger.debug("send mail message to {}, subject = {}", sb.toString(), msg.getSubject());
            }
            catch (Throwable e) {
                logger.debug("error while make debug-message, mail is sending");
            }
        }

        this.sender.send(msg);
    }
}
