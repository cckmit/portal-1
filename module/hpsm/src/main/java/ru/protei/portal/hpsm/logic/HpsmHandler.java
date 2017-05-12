package ru.protei.portal.hpsm.logic;

import ru.protei.portal.hpsm.config.HpsmEnvConfig;

import javax.mail.internet.MimeMessage;

/**
 * Created by michael on 25.04.17.
 */
public interface HpsmHandler {
    /**
     * @param msg
     * @return true if this message was handled
     */
    boolean handle (MimeMessage msg, ServiceInstance instance);
}
