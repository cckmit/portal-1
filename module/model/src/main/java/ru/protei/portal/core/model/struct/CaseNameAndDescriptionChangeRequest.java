package ru.protei.portal.core.model.struct;

public class CaseNameAndDescriptionChangeRequest extends AuditableObject {
    private Long id;
    private String name;
    private String info;

    public CaseNameAndDescriptionChangeRequest() {}

    public CaseNameAndDescriptionChangeRequest(Long id, String name, String info) {
        this.id = id;
        this.name = name;
        this.info = info;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public void setInfo( String info ) {
        this.info = info;
    }

    @Override
    public String getAuditType() {
        return "CaseNameAndDescriptionChangeRequest";
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
