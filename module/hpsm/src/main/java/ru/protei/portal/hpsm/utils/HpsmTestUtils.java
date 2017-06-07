package ru.protei.portal.hpsm.utils;

import com.thoughtworks.xstream.XStream;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.MimeMessageHelper;
import ru.protei.portal.hpsm.api.HpsmMessageFactory;
import ru.protei.portal.hpsm.api.HpsmStatus;
import ru.protei.portal.core.mail.MailMessageFactory;
import ru.protei.portal.hpsm.logic.HpsmEvent;
import ru.protei.portal.hpsm.struct.HpsmMessage;
import ru.protei.portal.hpsm.struct.HpsmMessageHeader;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import java.io.InputStream;

import static ru.protei.portal.hpsm.utils.HpsmUtils.RTTS_HPSM_XML_REQUEST;

/**
 * Created by Mike on 01.05.2017.
 */
public class HpsmTestUtils {

    public static final String SENDER_ADDRESS = "crm_test@protei.ru";
    public static final String SEND_TO_ADDRESS = "crm_test@protei.ru";
    public static final String HPSM_MAIL_ADDRESS = "zavedeev@protei.ru";

    @Autowired
    MailMessageFactory messageFactory;

    @Autowired
    HpsmMessageFactory hpsmMessageFactory;

    @Autowired
    @Qualifier("hpsmSerializer")
    XStream xstream;

    public String loadTestRequest (String res) throws Exception {

        try (InputStream in = HpsmTestUtils.class.getClassLoader().getResource("samples/request/" + res).openStream()) {
            return IOUtils.toString (in);
        }

    }


    public MimeMessage createRequest (HpsmMessageHeader subject, String msgXmlBody) throws MessagingException {
        MimeMessage mailMessage = messageFactory.createMailMessage();

        MimeMessageHelper helper = new MimeMessageHelper(mailMessage, true);
        helper.setSubject(subject.toString());
        helper.setTo(SEND_TO_ADDRESS);
        helper.setFrom(HPSM_MAIL_ADDRESS);
        helper.addAttachment(HpsmUtils.RTTS_HPSM_XML_REQUEST,
                new StringStreamSource(msgXmlBody),
                "application/xml");

        return mailMessage;
    }

    public MimeMessage createNewRequest (String hpsmCaseId) throws Exception {
        HpsmMessageHeader requestSubject = new HpsmMessageHeader(hpsmCaseId, null, HpsmStatus.NEW);
        HpsmMessage requestMessage = new HpsmMessage();
        requestMessage.setHpsmId(requestSubject.getHpsmId());
        requestMessage.setOurId(requestSubject.getOurId());
        requestMessage.status(requestSubject.getStatus());
        requestMessage.setCompanyBranch("Северо-Западный Филиал");
        requestMessage.setContactPerson("Андреев Артем");
        requestMessage.setContactPersonEmail("Andreev.A@nwgsm.com");
        requestMessage.setProductName("ПРОТЕЙ_RG");

        return hpsmMessageFactory.makeRequestMesssage(SEND_TO_ADDRESS,HPSM_MAIL_ADDRESS,requestSubject, requestMessage);
    }


    public HpsmEvent parseEvent (MimeMessage mimeMessage) throws Exception {
        return HpsmUtils.parseEvent(mimeMessage, xstream);
    }
}
