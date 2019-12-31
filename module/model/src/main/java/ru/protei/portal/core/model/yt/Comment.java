package ru.protei.portal.core.model.yt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@Deprecated
public class Comment {
    private String id;
    private String author;
    private String authorFullName;
    private String issueId;
    private String parentId;
    private Boolean deleted;
    private String text;
    private Date created;
    private Date updated;
    private Boolean markdown;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorFullName() {
        return authorFullName;
    }

    public void setAuthorFullName(String authorFullName) {
        this.authorFullName = authorFullName;
    }

    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Boolean getMarkdown() {
        return markdown;
    }

    public void setMarkdown(Boolean markdown) {
        this.markdown = markdown;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id='" + id + '\'' +
                ", author='" + author + '\'' +
                ", authorFullName='" + authorFullName + '\'' +
                ", issueId='" + issueId + '\'' +
                ", parentId='" + parentId + '\'' +
                ", deleted=" + deleted +
                ", text='" + text + '\'' +
                ", created=" + created +
                ", updated=" + updated +
                ", markdown=" + markdown +
                '}';
    }
}
