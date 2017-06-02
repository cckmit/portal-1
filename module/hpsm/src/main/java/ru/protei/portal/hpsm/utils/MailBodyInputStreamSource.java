package ru.protei.portal.hpsm.utils;

import org.springframework.core.io.InputStreamSource;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by michael on 02.06.17.
 */
public class MailBodyInputStreamSource implements InputStreamSource {

    BodyPart part;

    public MailBodyInputStreamSource(BodyPart part) {
        this.part = part;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        try {
            return part.getInputStream();
        }
        catch (MessagingException ex) {
            throw new IOException ("error on getting stream from mail-body part", ex);
        }
    }
}
