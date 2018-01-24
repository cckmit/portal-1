package ru.protei.portal.api.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Base64;

/**
 * Created by turik on 19.08.16.
 */
@XmlRootElement(name = "photo")
public class Photo {

    private Long id;
    private String content;

    @XmlElement(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @XmlElement(name = "content")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "id=" + id +
                ", length of content=" + (content == null ? null : content.length()) +
                '}';
    }
}
