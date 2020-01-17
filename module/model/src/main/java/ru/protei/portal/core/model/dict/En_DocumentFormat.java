package ru.protei.portal.core.model.dict;

import java.util.Objects;

public enum En_DocumentFormat {
    PDF("pdf"),
    DOCX("docx"),
    DOC("doc"),
    AS("as"),
    ;

    En_DocumentFormat(String format) {
        this.format = format;
    }
    private String format;

    public String getFormat() {
        return format;
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
                return documentId + "_approval_sheet.pdf";
            default:
                return documentId + "." + getFormat();
        }
    }

    public static En_DocumentFormat of(String format) {
        for (En_DocumentFormat it : En_DocumentFormat.values()) {
            if (Objects.equals(it.getFormat(), format)) {
                return it;
            }
        }
        return null;
    }
}
