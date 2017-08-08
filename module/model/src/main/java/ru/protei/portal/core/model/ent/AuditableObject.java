package ru.protei.portal.core.model.ent;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ru.protei.portal.core.model.struct.ProjectInfo;

import java.io.Serializable;

/**
 * Родительский класс для аудируемого объекта
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "Type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = UserLogin.class, name = "UserLogin"),
        @JsonSubTypes.Type(value = CaseObject.class, name = "CaseObject"),
        @JsonSubTypes.Type(value = CaseComment.class, name = "CaseComment"),
        @JsonSubTypes.Type(value = Company.class, name = "Company"),
        @JsonSubTypes.Type(value = Person.class, name = "Person"),
        @JsonSubTypes.Type(value = Equipment.class, name = "Equipment"),
        @JsonSubTypes.Type(value = DevUnit.class, name = "DevUnit"),
        @JsonSubTypes.Type(value = ProjectInfo.class, name = "ProjectInfo"),
        @JsonSubTypes.Type(value = UserRole.class, name = "UserRole"),
})
public class AuditableObject implements Serializable {

    public Long id;

    private Long auditableId;

    public void setAuditableId( Long auditableId ) {
        this.auditableId = auditableId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
