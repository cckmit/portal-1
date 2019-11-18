package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.ent.CaseObject;

public class CaseNameAndDescriptionChangeRequest {
    private Long id;
    private String name;
    private String info;
    private String extAppType;

    public CaseNameAndDescriptionChangeRequest(Long id, String name, String info) {
        this.id = id;
        this.name = name;
        this.info = info;
    }

    public CaseNameAndDescriptionChangeRequest(CaseObject caseObject) {
        if (caseObject == null) return;
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
}
