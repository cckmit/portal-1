package ru.protei.portal.core.model.struct;

import java.io.Serializable;
import java.util.Objects;

public class CaseNameAndDescriptionChangeRequest implements Serializable {
    private Long id;
    private String name;
    private String info;

    public CaseNameAndDescriptionChangeRequest() {}

    public CaseNameAndDescriptionChangeRequest(Long id, String name, String info) {
        this.id = id;
        this.name = name;
        this.info = info;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
