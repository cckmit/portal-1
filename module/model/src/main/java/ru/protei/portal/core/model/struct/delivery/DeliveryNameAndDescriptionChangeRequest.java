package ru.protei.portal.core.model.struct.delivery;

import ru.protei.portal.core.model.struct.AuditableObject;

public class DeliveryNameAndDescriptionChangeRequest extends AuditableObject {
    private Long id;
    private String name;
    private String description;

    public DeliveryNameAndDescriptionChangeRequest() {}

    public DeliveryNameAndDescriptionChangeRequest(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getAuditType() {
        return "DeliveryNameAndDescriptionChangeRequest";
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
