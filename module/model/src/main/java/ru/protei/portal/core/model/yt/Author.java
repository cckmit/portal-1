package ru.protei.portal.core.model.yt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by admin on 15/11/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Author {

    private String login;

    private String ringId;

    private String url;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getRingId() {
        return ringId;
    }

    public void setRingId(String ringId) {
        this.ringId = ringId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Author{" +
                "login='" + login + '\'' +
                ", ringId='" + ringId + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
