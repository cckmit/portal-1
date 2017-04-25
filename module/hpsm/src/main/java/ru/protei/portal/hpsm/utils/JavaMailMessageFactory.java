package ru.protei.portal.hpsm.utils;

import org.springframework.mail.javamail.JavaMailSender;
import ru.protei.portal.hpsm.api.MailMessageFactory;

import javax.mail.internet.MimeMessage;

/**
 * Created by michael on 25.04.17.
 */
public class JavaMailMessageFactory implements MailMessageFactory {

    private JavaMailSender sender;

    public JavaMailMessageFactory(JavaMailSender sender) {
        this.sender = sender;
    }

    @Override
    public MimeMessage createMailMessage() {
        return sender.createMimeMessage();
    }
}
