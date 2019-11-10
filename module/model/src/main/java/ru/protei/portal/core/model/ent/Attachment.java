package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Created by michael on 24.05.16.
 */
@JdbcEntity(table = "attachment")
public class Attachment implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "created")
    private Date created;

    @JdbcColumn(name = "creator")
    private Long creatorId;

    @JdbcColumn(name = "at_label")
    private String labelText;

    @JdbcColumn(name = "ext_link")
    private String extLink;

    @JdbcColumn(name = "DATA_MIMETYPE")
    private String mimeType;

    @JdbcColumn(name = "DATA_SIZE")
    private Long dataSize;

    @JdbcColumn(name = "file_name")
    private String fileName;

    public Attachment (Long attachmentId) {
        id = attachmentId;
    }

    public Attachment () {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public String getLabelText() {
        return labelText;
    }

    public void setLabelText(String labelText) {
        this.labelText = labelText;
    }

    public String getExtLink() {
        return extLink;
    }

    public void setExtLink(String extLink) {
        this.extLink = extLink;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getDataSize() {
        return dataSize;
    }

    public void setDataSize(Long dataSize) {
        this.dataSize = dataSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int toHashCodeForRedmineCheck() {
        return ((created == null ? "" : created.getTime()) + (fileName == null ? "" : fileName)).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Attachment && Objects.equals(id, ((Attachment) obj).getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash( id );
    }

    @Override
    public String toString() {
        return "Attachment{" +
                "id=" + id +
                " fileName=" + fileName +
                '}';
    }
}
