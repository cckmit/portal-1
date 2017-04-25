package ru.protei.portal.hpsm.utils;

import org.springframework.mail.javamail.JavaMailSender;
import ru.protei.portal.hpsm.api.MailSendChannel;

import javax.mail.internet.MimeMessage;

/**
 * Created by michael on 25.04.17.
 */
public class JavaMailSendChannel implements MailSendChannel {

    private JavaMailSender sender;

    public JavaMailSendChannel(JavaMailSender sender) {
        this.sender = sender;
    }

    @Override
    public void send(MimeMessage msg) {
        this.sender.send(msg);
    }
}
