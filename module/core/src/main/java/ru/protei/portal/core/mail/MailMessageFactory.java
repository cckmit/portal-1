package ru.protei.portal.core.mail;

import javax.mail.internet.MimeMessage;

/**
 * Created by michael on 25.04.17.
 */
public interface MailMessageFactory {
    MimeMessage createMailMessage ();
}
