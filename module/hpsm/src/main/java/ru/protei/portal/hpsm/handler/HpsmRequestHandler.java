package ru.protei.portal.hpsm.handler;

import javax.mail.internet.MimeMessage;

/**
 * Created by michael on 28.04.17.
 */
interface HpsmRequestHandler {
    MimeMessage handle(HpsmRequest request) throws Exception;
}
