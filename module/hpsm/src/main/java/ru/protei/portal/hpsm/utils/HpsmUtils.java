package ru.protei.portal.hpsm.utils;

import com.thoughtworks.xstream.XStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protei.utils.common.ThreadLocalDateFormat;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.hpsm.logic.HpsmEvent;
import ru.protei.portal.hpsm.logic.ServiceInstance;
import ru.protei.portal.hpsm.struct.HpsmAttachment;
import ru.protei.portal.hpsm.struct.HpsmMessage;
import ru.protei.portal.hpsm.struct.HpsmMessageHeader;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by michael on 25.04.17.
 */
public class HpsmUtils {

    public static final String COMMON_HPSM_TAG = "hpsm";
    public static final String RTTS_HPSM_XML_REQUEST = "rtts_hpsm.xml";
    public static final String RTTS_HPSM_XML_REPLY = "rtts_vendor.xml";

    private static Logger logger = LoggerFactory.getLogger(HpsmUtils.class);


    public static final ThreadLocalDateFormat OLD_DATE_FMT = new ThreadLocalDateFormat("dd/MM/yyyy HH:mm:ss");
    public static final ThreadLocalDateFormat DATE_FMT = new ThreadLocalDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");


    public static String formatDate(Date date) {
        return date == null ? "" : DATE_FMT.format(date);
    }

    public static Date parseDate(String x) throws ParseException{
        return x == null || x.isEmpty() ? null : x.contains("/") ? OLD_DATE_FMT.parse(x) : DATE_FMT.parse(x);
    }

    public static void bindCase (CaseObject object, ServiceInstance instance) {

        object.setExtAppType(COMMON_HPSM_TAG);
        object.setCreatorInfo(createInstanceTag(instance));

    }

    private static String createInstanceTag(ServiceInstance instance) {
        return COMMON_HPSM_TAG + "_" + instance.id();
    }

    public static boolean testBind (CaseObject object, ServiceInstance instance) {
        return object.getExtAppType() != null && object.getExtAppType().equalsIgnoreCase(COMMON_HPSM_TAG)
                && object.getCreatorInfo() != null && object.getCreatorInfo().equalsIgnoreCase(createInstanceTag(instance));
    }


    public static String extractOption (String x, String name, String defaultValue) {
        if (x == null || x.isEmpty())
            return defaultValue;

        String tag = name+"=[";
        int tag_len = tag.length();

        int s = x.indexOf(tag);
        if (s < 0)
            return defaultValue;

        int e = x.indexOf(']', s + tag_len);

        if (e < 0)
            return defaultValue;

        return x.substring(s+tag_len, e);
    }


    public static String getEmailFromAddress (MimeMessage msg) throws MessagingException{
        return msg.getFrom()[0].toString();
    }

    public static String getMessageSubject (MimeMessage mail) {
        try {
            return mail.getSubject();
        }
        catch (Exception e) {
            logger.error("unable to get mail subject", e);
        }

        return "";
    }

    public static HpsmEvent parseEvent (MimeMessage mailMessage, XStream xstream) throws Exception {
        HpsmMessageHeader header = HpsmMessageHeader.parse(mailMessage.getSubject());


        HpsmEvent event = new HpsmEvent(header,null,mailMessage);

        logger.debug("parse event-message {}", header.toString());

        if (mailMessage.getContent() instanceof MimeMultipart){
            MimeMultipart mparts = (MimeMultipart) mailMessage.getContent();

            for (int i = 0; i < mparts.getCount(); i++) {
                logger.debug("process part #{}", i);
                logger.debug(" message part #{}, Content type: {}", i, mparts.getBodyPart(i).getContentType());

                BodyPart part = mparts.getBodyPart(i);

                String fileName = part.getFileName();
                logger.debug(" message part #{}, File name: {}", i, fileName);

                if (HelperFunc.isEmpty(fileName)) {
                    logger.debug(" message part #{}, file name is empty, skip handling this attachment");
                    continue;
                }

                if (fileName.equalsIgnoreCase(RTTS_HPSM_XML_REQUEST) || fileName.equalsIgnoreCase(RTTS_HPSM_XML_REPLY)) {
                    logger.debug(" message part #{}, file: {}, trying parse incoming xml-event data", i, fileName);
                    try (InputStream contentStream = part.getInputStream()) {
                        event.assign((HpsmMessage) xstream.fromXML(contentStream));

                        if (event.getHpsmMessage() != null) {
                            logger.debug(" >> parsed message data: {}", xstream.toXML(event.getHpsmMessage()));
                        }
                        else {
                            logger.debug(" >> parsing is failed");
                        }
                    }
                }
                else {
                    HpsmAttachment att = new HpsmAttachment(part);

                    logger.debug(" message part #{} -> add attachment f={},ct={},size={},info={}",
                            i, att.getFileName(), att.getContentType(), att.getSize(), att.getDescription());

                    event.addAttachment(att);
                }
            }
        }
        else {
            event.setMailBodyText(mailMessage.getContent().toString());
        }

        return event;
    }
}
