package ru.protei.portal.hpsm.api;

import javax.mail.internet.MimeMessage;

/**
 * Created by michael on 25.04.17.
 */
public interface MailHandler {
    /**
     * @param msg
     * @return true if this message was handled
     */
    boolean handle (MimeMessage msg);
}
