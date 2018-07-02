package ru.protei.portal.core.mail;

import ru.protei.portal.core.model.helper.HelperFunc;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;

public class JavaMimeMessageProvider implements MimeMessageProvider {

    private Session session;
    private String messageIdPattern = "%id%@smtp.protei.ru";

    private Properties javaMailProperties = new Properties();

    @Override
    public void setMessageIdPattern(String messageIdPattern) {
        if (HelperFunc.isEmpty(messageIdPattern)) {
            return;
        }
        this.messageIdPattern = messageIdPattern;
    }

    @Override
    public MimeMessage createMimeMessage(String messageId, String inReplyTo, List<String> references) {
        return new ReferenceableMimeMessage(getSession(), messageIdPattern, messageId, inReplyTo, references);
    }

    private synchronized Session getSession() {
        if (session == null) {
            session = Session.getInstance(javaMailProperties);
        }
        return session;
    }
}
