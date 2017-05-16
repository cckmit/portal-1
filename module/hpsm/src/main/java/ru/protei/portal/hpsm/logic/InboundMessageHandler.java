package ru.protei.portal.hpsm.logic;

import javax.mail.internet.MimeMessage;

/**
 * Created by michael on 25.04.17.
 */
public interface InboundMessageHandler {
    /**
     * @param msg
     * @return true if this message was handled
     */
    boolean handle (MimeMessage msg, ServiceInstance instance);
}
