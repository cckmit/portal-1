package ru.protei.portal.core.model.struct;

import java.io.InputStream;

public class FileStream {

    private InputStream inputStream;
    private long fileSize;
    private String contentType;

    public FileStream(InputStream inputStream, long fileSize, String contentType) {
        this.inputStream = inputStream;
        this.fileSize = fileSize;
        this.contentType = contentType;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getContentType() {
        return contentType;
    }
}
