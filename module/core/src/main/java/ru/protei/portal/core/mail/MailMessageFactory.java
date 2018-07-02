package ru.protei.portal.core.mail;

import javax.mail.internet.MimeMessage;
import java.util.List;

/**
 * Created by michael on 25.04.17.
 */
public interface MailMessageFactory {
    MimeMessage createMailMessage ();
    MimeMessage createMailMessage (String messageId, String inReplyTo, List<String> references);
}
