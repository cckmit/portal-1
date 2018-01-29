package ru.protei.portal.hpsm.struct;

import org.springframework.core.io.InputStreamSource;
import ru.protei.portal.hpsm.utils.MailBodyInputStreamSource;

import javax.mail.BodyPart;
import javax.mail.MessagingException;

/**
 * Created by michael on 02.06.17.
 */
public class HpsmAttachment {

    private String fileName;
    private String contentType;
    private String description;

    private int size;
    private InputStreamSource streamSource;

    public HpsmAttachment() {
    }

    public HpsmAttachment(BodyPart part) throws MessagingException {
        this (part.getFileName(),
                part.getContentType(),
                part.getDescription(),
                part.getSize(),
                new MailBodyInputStreamSource(part));
    }

    public HpsmAttachment(String fileName, String contentType, String desc, int size, InputStreamSource streamSource) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.streamSource = streamSource;
        this.description = desc;
        this.size = size;
    }

    public String getDescription() {
        return description;
    }

    public int getSize() {
        return size;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public InputStreamSource getStreamSource() {
        return streamSource;
    }
}
