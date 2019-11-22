package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.ent.CaseObject;

import java.io.Serializable;
import java.util.Objects;

public class CaseNameAndDescriptionChangeRequest implements Serializable {
    private Long id;
    private String name;
    private String info;

    public CaseNameAndDescriptionChangeRequest() {}

    public CaseNameAndDescriptionChangeRequest( Long id, String name, String info) {
        this.id = id;
        this.name = name;
        this.info = info;
    }

    public CaseNameAndDescriptionChangeRequest(CaseObject caseObject) {
        this.id = caseObject.getId();
        this.name = caseObject.getName();
        this.info = caseObject.getInfo();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CaseNameAndDescriptionChangeRequest that = (CaseNameAndDescriptionChangeRequest) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
