package ru.protei.portal.hpsm.utils;

import com.thoughtworks.xstream.XStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.protei.portal.hpsm.api.HpsmMessageFactory;
import ru.protei.portal.hpsm.api.HpsmStatus;
import ru.protei.portal.core.mail.MailMessageFactory;
import ru.protei.portal.hpsm.logic.HpsmEvent;
import ru.protei.portal.hpsm.struct.HpsmMessage;
import ru.protei.portal.hpsm.struct.HpsmMessageHeader;

import javax.mail.internet.MimeMessage;

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