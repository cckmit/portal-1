package ru.protei.portal.ui.common.client.activity.attachment;

/**
 * Created by bondarenko on 12.01.17.
 */
public enum AttachmentType {

    GIF(AttachmentCategory.IMAGE, "image/gif"),
    JPEG(AttachmentCategory.IMAGE, "image/jpeg"),
    JPG(AttachmentCategory.IMAGE, "image/jpg"),
    PNG(AttachmentCategory.IMAGE, "image/png"),

    RAR(AttachmentCategory.ARCHIVE, "application/x-rar-compressed"),
    ZIP(AttachmentCategory.ARCHIVE, "application/zip"),
    GZIP(AttachmentCategory.ARCHIVE, "application/gzip"),
    GZ(AttachmentCategory.ARCHIVE, "application/x-gzip"),

    TXT(AttachmentCategory.TEXT, "text/plain"),
    PDF(AttachmentCategory.TEXT, "application/pdf"),
    DOC(AttachmentCategory.TEXT, "application/msword"),
    DOCX(AttachmentCategory.TEXT, "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    XLS(AttachmentCategory.TEXT, "application/vnd.ms-excel"),
    XLS2007(AttachmentCategory.TEXT, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    ODT(AttachmentCategory.TEXT, "application/vnd.oasis.opendocument.text"),

    JSON(AttachmentCategory.CODE, "application/json"),
    XML(AttachmentCategory.CODE, "text/xml"),
    CSS(AttachmentCategory.CODE, "text/css"),
    HTML(AttachmentCategory.CODE, "text/html"),
    JS(AttachmentCategory.CODE, "text/javascript"),
    JAR(AttachmentCategory.CODE, "application/java-archive"),
    JAVA(AttachmentCategory.CODE, "text/x-java"),
    LOG(AttachmentCategory.CODE, "text/x-log"),

    BINARY(AttachmentCategory.UNKNOWN, "application/octet-stream"),
    UNKNOWN(AttachmentCategory.UNKNOWN, null);

    public String mimeType;
    public AttachmentCategory category;

    AttachmentType(AttachmentCategory category, String mimeType) {
        this.mimeType = mimeType;
        this.category = category;
    }

    public static AttachmentType getType(String mimeType){
        if(mimeType != null && !mimeType.isEmpty()) {
            for (AttachmentType type : values()) {
                if (mimeType.equalsIgnoreCase(type.mimeType))
                    return type;
            }
        }
        return UNKNOWN;
    }

    public static AttachmentCategory getCategory(String mimeType){
        return getType(mimeType).category;
    }

    public enum AttachmentCategory {
        IMAGE(null),
        TEXT("/Crm/images/textIcon.gif"),
        ARCHIVE("/Crm/images/archiveIcon.gif"),
        CODE("/Crm/images/codeIcon.gif"),
        UNKNOWN("/Crm/images/unknownIcon.gif");

        public String picture;

        AttachmentCategory(String picture) {
            this.picture = picture;
        }
    }
}