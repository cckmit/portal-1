package ru.protei.portal.hpsm.utils;

import com.thoughtworks.xstream.XStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.MimeMessageHelper;
import ru.protei.portal.hpsm.api.HpsmStatus;
import ru.protei.portal.hpsm.api.MailMessageFactory;
import ru.protei.portal.hpsm.logic.HpsmEvent;
import ru.protei.portal.hpsm.struct.HpsmMessage;
import ru.protei.portal.hpsm.struct.HpsmMessageHeader;
import ru.protei.portal.hpsm.config.HpsmEnvConfig;

import javax.mail.internet.MimeMessage;

import static ru.protei.portal.hpsm.utils.HpsmUtils.RTTS_HPSM_XML;

/**
 * Created by Mike on 01.05.2017.
 */
public class HpsmTestUtils {

    public static final String SENDER_ADDRESS = "crm_test@protei.ru";
    public static final String SEND_TO_ADDRESS = "crm_test@protei.ru";
    public static final String HPSM_MAIL_ADDRESS = "zavedeev@protei.ru";

    @Autowired
    @Qualifier("hpsmMailFactory")
    MailMessageFactory messageFactory;

    @Autowired
    @Qualifier("hpsmSerializer")
    XStream xstream;


    public MimeMessage createNewRequest (String hpsmCaseId) throws Exception {
        MimeMessage mailMessage = messageFactory.createMailMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mailMessage, true);

        HpsmMessageHeader requestSubject = new HpsmMessageHeader(hpsmCaseId, null, HpsmStatus.NEW);
        HpsmMessage requestMessage = new HpsmMessage();
        requestMessage.setHpsmId(requestSubject.getHpsmId());
        requestMessage.setOurId(requestSubject.getOurId());
        requestMessage.status(requestSubject.getStatus());
        requestMessage.setCompanyBranch("Северо-Западный Филиал");
        requestMessage.setContactPerson("Андреев Артем");
        requestMessage.setContactPersonEmail("Andreev.A@nwgsm.com");
        requestMessage.setProductName("ПРОТЕЙ_RG");

        helper.setSubject(requestSubject.toString());
        helper.setTo(SEND_TO_ADDRESS);
        helper.setFrom(HPSM_MAIL_ADDRESS);
        helper.addAttachment(RTTS_HPSM_XML, new EventMsgInputStreamSource(xstream).attach(requestMessage), "application/xml");

        return mailMessage;
    }


    public HpsmEvent parseEvent (MimeMessage mimeMessage) throws Exception {
        return HpsmUtils.parseEvent(mimeMessage, xstream);
    }
}
