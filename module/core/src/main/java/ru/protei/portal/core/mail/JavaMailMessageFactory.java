package ru.protei.portal.core.mail;

import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.internet.MimeMessage;
import java.util.List;

/**
 * Created by michael on 25.04.17.
 */
public class JavaMailMessageFactory implements MailMessageFactory {

    private JavaMailSender sender;
    private MimeMessageProvider mimeMessageProvider;

    public JavaMailMessageFactory(JavaMailSender sender, MimeMessageProvider mimeMessageProvider) {
        this.sender = sender;
        this.mimeMessageProvider = mimeMessageProvider;
    }

    @Override
    public MimeMessage createMailMessage() {
        return sender.createMimeMessage();
    }

    @Override
    public MimeMessage createMailMessage(String messageId, String inReplyTo, List<String> references) {
        return mimeMessageProvider.createMimeMessage(messageId, inReplyTo, references);
    }
}
