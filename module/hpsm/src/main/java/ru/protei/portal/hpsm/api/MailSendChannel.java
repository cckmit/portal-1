package ru.protei.portal.hpsm.api;

import javax.mail.internet.MimeMessage;

/**
 * Created by michael on 25.04.17.
 */
public interface MailSendChannel {
    void send (MimeMessage msg);
}
