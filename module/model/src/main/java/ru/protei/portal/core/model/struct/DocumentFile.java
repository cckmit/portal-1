package ru.protei.portal.core.model.struct;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FilenameUtils;
import ru.protei.portal.core.model.dict.En_DocumentFormat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public interface DocumentFile {
    boolean isPresent();

    En_DocumentFormat getFormat();

    byte[] getBytes();

    InputStream getInputStream();

    default En_DocumentFormat getFormatByExtension(String fileExt)  {
        En_DocumentFormat documentFormat = En_DocumentFormat.of(fileExt);
        return documentFormat == null ? En_DocumentFormat.DOCX : documentFormat;
    }

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
        public En_DocumentFormat getFormat() {
            return isPresent() ? getFormatByExtension(FilenameUtils.getExtension(fileItem.getName())) : null;
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
        private final En_DocumentFormat format;

        public PortalApiDocumentFile(byte[] content, En_DocumentFormat format) {
            this.content = content;
            this.format = format;
        }

        @Override
        public boolean isPresent() {
            return content != null;
        }

        @Override
        public En_DocumentFormat getFormat() {
            return format;
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
