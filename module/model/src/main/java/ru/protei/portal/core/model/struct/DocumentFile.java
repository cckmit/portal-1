package ru.protei.portal.core.model.struct;

import org.apache.commons.fileupload.FileItem;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public interface DocumentFile {

    boolean isPresent();

    String getName();

    byte[] getBytes();

    InputStream getInputStream();

    class FileItemDocumentFile implements DocumentFile {

        private final FileItem fileItem;

        public FileItemDocumentFile(FileItem fileItem) {
            this.fileItem = fileItem;
        }

        @Override
        public boolean isPresent() {
            return fileItem != null;
        }

        @Override
        public String getName() {
            return isPresent() ? fileItem.getName() : "";
        }

        @Override
        public byte[] getBytes() {
            return isPresent() ? fileItem.get() : null;
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

        private final byte[] content;
        private final String extension;

        public PortalApiDocumentFile(byte[] content, String extension) {
            this.content = content;
            this.extension = extension;
        }

        @Override
        public boolean isPresent() {
            return content != null;
        }

        @Override
        public String getName() {
            return extension == null ? "" : "." + extension;
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
