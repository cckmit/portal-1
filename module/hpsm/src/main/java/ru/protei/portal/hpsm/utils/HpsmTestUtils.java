package ru.protei.portal.hpsm.utils;

import com.thoughtworks.xstream.XStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.MimeMessageHelper;
import ru.protei.portal.hpsm.api.HpsmStatus;
import ru.protei.portal.hpsm.api.MailMessageFactory;
import ru.protei.portal.hpsm.handler.HpsmEvent;
import ru.protei.portal.hpsm.struct.HpsmMessage;
import ru.protei.portal.hpsm.struct.HpsmMessageHeader;
import ru.protei.portal.hpsm.struct.HpsmSetup;

import javax.mail.internet.MimeMessage;

import static ru.protei.portal.hpsm.utils.HpsmUtils.RTTS_HPSM_XML;

/**
 * Created by Mike on 01.05.2017.
 */
public class HpsmTestUtils {

    @Autowired
    MailMessageFactory messageFactory;

    @Autowired
    HpsmSetup setup;

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
        helper.setTo(setup.getSenderAddress());
        helper.setFrom(setup.getHpsmMailAddress());
        helper.addAttachment(RTTS_HPSM_XML, new EventMsgInputStreamSource(xstream).attach(requestMessage), "application/xml");

        return mailMessage;
    }


    public HpsmEvent parseEvent (MimeMessage mimeMessage) throws Exception {
        return HpsmUtils.parseEvent(mimeMessage, xstream);
    }
}
