package ru.protei.portal.hpsm.utils;

import com.thoughtworks.xstream.XStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.MimeMessageHelper;
import protei.utils.common.ThreadLocalDateFormat;
import ru.protei.portal.hpsm.handler.HpsmEvent;
import ru.protei.portal.hpsm.struct.HpsmMessage;
import ru.protei.portal.hpsm.struct.HpsmMessageHeader;

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

    public static final String RTTS_HPSM_XML = "rtts_hpsm.xml";
    private static Logger logger = LoggerFactory.getLogger(HpsmUtils.class);


    public static final ThreadLocalDateFormat DATE_FMT = new ThreadLocalDateFormat("dd/MM/yyyy HH:mm:ss");

    public static String formatDate(Date date) {
        return date == null ? "" : DATE_FMT.format(date);
    }

    public static Date parseDate(String x) throws ParseException{
        return x == null || x.isEmpty() ? null : DATE_FMT.parse(x);
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


    public static HpsmEvent parseEvent (MimeMessage mailMessage, XStream xstream) throws Exception {
        HpsmMessageHeader header = HpsmMessageHeader.parse(mailMessage.getSubject());


        HpsmEvent event = new HpsmEvent(header,null,mailMessage);

        logger.debug("parse event-message {}", header.toString());

        if (mailMessage.getContent() instanceof MimeMultipart){
            MimeMultipart mparts = (MimeMultipart) mailMessage.getContent();

            for (int i = 0; i < mparts.getCount(); i++) {
                logger.debug("process part #{}", i);
                logger.debug(" message part #{}, Content type: {}", i, mparts.getBodyPart(i).getContentType());

                String fileName = mparts.getBodyPart(i).getFileName();
                logger.debug(" message part #{}, File name: {}", i, fileName);

                if (fileName != null && fileName.equalsIgnoreCase(RTTS_HPSM_XML)) {
                    try (InputStream contentStream = mparts.getBodyPart(i).getInputStream()) {
                        event.assign((HpsmMessage) xstream.fromXML(contentStream));
                    }
                }
            }
        }
        else {
            event.setMailBodyText(mailMessage.getContent().toString());
        }

        return event;
    }
}
