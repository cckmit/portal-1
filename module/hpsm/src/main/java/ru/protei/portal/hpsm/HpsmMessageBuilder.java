package ru.protei.portal.hpsm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import ru.protei.portal.hpsm.struct.EventMsg;

import javax.mail.internet.MimeMessage;

/**
 * Created by michael on 24.04.17.
 */
public class HpsmMessageBuilder {

    @Autowired
    JavaMailSender sender;

    MimeMessage message;

    public HpsmMessageBuilder () {

    }


    public HpsmMessageBuilder event (EventMsg eventMsg) {
//        helper.addAttachment("rtts_hpsm.xml", ctx.getBean(EventMsgInputStreamSource.class).attach(eventMsg), "application/xml");
        return this;
    }


}
