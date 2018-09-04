package ru.protei.portal.core.model.yt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

/**
 * Created by admin on 15/11/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkItem {

    private String url;

    private String id;

    private Date date;

    private Date created;

    private Date updated;

    private Integer duration;

    private String description;

    private Author author;

//    private String worktype;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getAuthorLogin() {
        if ( author == null ) {
            return null;
        }

        return author.getLogin();
    }

//    public String getWorktype() {
//        return worktype;
//    }
//
//    public void setWorktype(String worktype) {
//        this.worktype = worktype;
//    }

    @Override
    public String toString() {
        return "WorkItem{" +
                "url='" + url + '\'' +
                ", id='" + id + '\'' +
                ", date=" + date +
                ", created=" + created +
                ", updated=" + updated +
                ", duration=" + duration +
                ", description='" + description + '\'' +
                ", author=" + author +
//                ", worktype='" + worktype + '\'' +
                '}';
    }
}
