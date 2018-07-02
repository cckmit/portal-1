package ru.protei.portal.core.mail;

import javax.mail.internet.MimeMessage;
import java.util.List;

public interface MimeMessageProvider {

    MimeMessage createMimeMessage(String messageId, String inReplyTo, List<String> references);

    void setMessageIdPattern(String messageIdPattern);
}
