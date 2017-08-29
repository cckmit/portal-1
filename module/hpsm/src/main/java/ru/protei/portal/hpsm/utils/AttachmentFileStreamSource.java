package ru.protei.portal.hpsm.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamSource;
import ru.protei.portal.api.struct.FileStorage;

import java.io.IOException;
import java.io.InputStream;

public class AttachmentFileStreamSource implements InputStreamSource {

    private static Logger logger = LoggerFactory.getLogger(AttachmentFileStreamSource.class);

    private FileStorage storage;
    private String linkId;

    public AttachmentFileStreamSource(FileStorage storage, String linkId) {
        this.storage = storage;
        this.linkId = linkId;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        FileStorage.File file =  storage.getFile(linkId);
        if (file == null) {
            logger.debug("unable to get file from storage for link : {}", linkId);
            return null;
        }
        return file.getData();
    }
}
