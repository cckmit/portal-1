package ru.protei.portal.core.model.struct;

import org.apache.commons.fileupload.FileItem;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public interface DocumentFile {
    boolean isPresent();

    String getFileName();

    byte[] getBytes();

    InputStream getInputStream();

    class FileItemDocumentFile implements DocumentFile {
        private FileItem fileItem;

        public FileItemDocumentFile(FileItem fileItem) {
            this.fileItem = fileItem;
        }

        @Override
        public boolean isPresent() {
            return fileItem != null;
        }

        @Override
        public String getFileName() {
            return fileItem.getName();
        }

        @Override
        public byte[] getBytes() {
            return fileItem.get();
        }

        @Override
        public InputStream getInputStream() {
            try {
                return fileItem.getInputStream();
            } catch (IOException ex) {
                return null;
            }
        }
    }

    class PortalApiDocumentFile implements DocumentFile {
        private byte[] content;
        private String name;

        public PortalApiDocumentFile(byte[] content, String name) {
            this.content = content;
            this.name = name;
        }

        @Override
        public boolean isPresent() {
            return content != null;
        }

        @Override
        public String getFileName() {
            return name;
        }

        @Override
        public byte[] getBytes() {
            return Arrays.copyOf(content, content.length);
        }

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(content);
        }
    }
}
