package ru.protei.portal.core.model.dict;

import java.util.Objects;

public enum En_DocumentFormat {
    PDF("pdf"),
    DOCX("docx"),
    DOC("doc"),
    AS("pdf"),
    ;

    En_DocumentFormat(String extension) {
        this.extension = extension;
    }
    private String extension;

    public String getExtension() {
        return extension;
    }

    public String getMimeType() {
        switch (this) {
            case PDF:
            case AS:
                return AttachmentType.PDF.mimeType;
            case DOC: return AttachmentType.DOC.mimeType;
            case DOCX: return AttachmentType.DOCX.mimeType;
            default: return AttachmentType.BINARY.mimeType;
        }
    }

    public String getFilename(Long documentId) {
        switch (this) {
            case AS:
                return documentId + "_approval_sheet." + getExtension();
            default:
                return documentId + "." + getExtension();
        }
    }

    public static En_DocumentFormat of(String format) {
        for (En_DocumentFormat it : En_DocumentFormat.values()) {
            if (Objects.equals(it.name().toLowerCase(), format)) {
                return it;
            }
        }
        return null;
    }
}
