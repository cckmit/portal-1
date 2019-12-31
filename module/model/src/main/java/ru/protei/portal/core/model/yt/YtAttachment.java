package ru.protei.portal.core.model.yt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

/**
 * Created by admin on 22/11/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Deprecated
public class YtAttachment {
    private String id;
    private String value;
    private String url;
    private String name;
    private String authorLogin;
    private String group;
    private Date created;

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue( String value ) {
        this.value = value;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl( String url ) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthorLogin() {
        return authorLogin;
    }

    public void setAuthorLogin(String authorLogin) {
        this.authorLogin = authorLogin;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "YtAttachment{" +
                "id='" + id + '\'' +
                ", value='" + value + '\'' +
                ", url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", authorLogin='" + authorLogin + '\'' +
                ", group='" + group + '\'' +
                ", created=" + created +
                '}';
    }
}
