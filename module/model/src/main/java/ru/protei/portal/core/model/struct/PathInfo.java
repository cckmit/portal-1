package ru.protei.portal.core.model.struct;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonAutoDetect
public class PathInfo implements Serializable {

    @JsonProperty("paths")
    private List<PathItem> paths;

    public PathInfo() {
        this(new ArrayList<>());
    }

    public PathInfo(List<PathItem> paths) {
        this.paths = paths;
    }

    public List<PathItem> getPaths() {
        return paths;
    }

    public void setPaths(List<PathItem> paths) {
        this.paths = paths;
    }
}
