package ru.protei.portal.core.model.youtrack.dto.issue;

import ru.protei.portal.core.model.youtrack.dto.YtDto;
import ru.protei.portal.core.model.youtrack.dto.user.YtUser;

/**
 * https://www.jetbrains.com/help/youtrack/standalone/api-entity-IssueAttachment.html
 */
public class YtIssueAttachment extends YtDto {

    public String name;
    public String url;
    public String thumbnailURL;
    public Long size;
    public Long created;
    public Long updated;
    public String extension;
    public String charset;
    public String mimeType;
    public String metaData;
    public Boolean removed;
    public YtUser author;

    @Override
    public String toString() {
        return "YtIssueAttachment{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", thumbnailURL='" + thumbnailURL + '\'' +
                ", size=" + size +
                ", created=" + created +
                ", updated=" + updated +
                ", extension='" + extension + '\'' +
                ", charset='" + charset + '\'' +
                ", mimeType='" + mimeType + '\'' +
                ", metaData='" + metaData + '\'' +
                ", removed=" + removed +
                ", author=" + author +
                '}';
    }
}
