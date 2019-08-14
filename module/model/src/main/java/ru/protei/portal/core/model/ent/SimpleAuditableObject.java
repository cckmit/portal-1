package ru.protei.portal.core.model.ent;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.protei.portal.core.model.struct.AuditableObject;

import java.util.Map;

public class SimpleAuditableObject extends AuditableObject {
    @JsonIgnore
    private transient Map<String, Object> notAuditableContainer;

    private String params;

    public SimpleAuditableObject() {}

    @Override
    public String getAuditType() {
        return "SimpleAuditableObject";
    }

    @Override
    public Long getId() {
        return null;
    }

    public String getParams() {
        return params;
    }

    public Map<String, Object> getNotAuditableContainer() {
        return notAuditableContainer;
    }

    public void setContainer(Map<String, Object> notAuditableContainer) {
        this.notAuditableContainer = notAuditableContainer;
        this.params = notAuditableContainer.toString();
    }

    @Override
    public String toString() {
        return "SimpleAuditableObject{" +
                "container='" + params + '\'' +
                '}';
    }
}
