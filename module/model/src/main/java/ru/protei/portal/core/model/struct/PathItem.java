package ru.protei.portal.core.model.struct;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonAutoDetect
public class PathItem implements Serializable {

    @JsonProperty("p")
    private String path;

    @JsonProperty("d")
    private String desc;

    public PathItem() {
        this("", "");
    }

    public PathItem(String path, String desc) {
        this.path = path;
        this.desc = desc;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
