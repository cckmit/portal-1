package ru.protei.portal.core.model.struct;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by turik on 19.08.16.
 */
@XmlRootElement(name = "photo")
public class Photo extends AuditableObject {

    private Long id;
    private String content;

    @XmlElement(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonIgnore
    @XmlElement(name = "content")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String getAuditType() {
        return "Photo";
    }

    @Override
    public String toString() {
        return "Photo{" +
                "id=" + id +
                ", length of content=" + (content == null ? null : content.length()) +
                '}';
    }
}
