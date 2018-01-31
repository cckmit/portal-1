package ru.protei.portal.core.model.struct;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ru.protei.portal.core.model.ent.*;

import java.io.Serializable;

/**
 * Родительский класс для аудируемого объекта
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "AuditType")
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
        @JsonSubTypes.Type(value = CompanyDepartment.class, name = "Department"),
        @JsonSubTypes.Type(value = WorkerPosition.class, name = "Position"),
        @JsonSubTypes.Type(value = WorkerEntry.class, name = "Worker"),
        @JsonSubTypes.Type(value = Photo.class, name = "Photo"),
        @JsonSubTypes.Type(value = LongAuditableObject.class, name = "LongAuditableObject"),
})
public abstract class AuditableObject implements Serializable {

    @JsonIgnore
    public abstract String getAuditType();
}
