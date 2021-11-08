package ru.protei.portal.core.model.struct;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ru.protei.portal.core.model.dto.DevUnitInfo;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.ent.*;

import java.io.Serializable;

/**
 * Родительский класс для аудируемого объекта
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "AuditType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SimpleAuditableObject.class, name = "SimpleAuditableObject"),
        @JsonSubTypes.Type(value = UserLogin.class, name = "UserLogin"),
        @JsonSubTypes.Type(value = CaseObject.class, name = "CaseObject"),
        @JsonSubTypes.Type(value = CaseComment.class, name = "CaseComment"),
        @JsonSubTypes.Type(value = Company.class, name = "Company"),
        @JsonSubTypes.Type(value = Person.class, name = "Person"),
        @JsonSubTypes.Type(value = Equipment.class, name = "Equipment"),
        @JsonSubTypes.Type(value = DevUnit.class, name = "DevUnit"),
        @JsonSubTypes.Type(value = Project.class, name = Project.AUDIT_TYPE),
        @JsonSubTypes.Type(value = UserRole.class, name = "UserRole"),
        @JsonSubTypes.Type(value = CompanyDepartment.class, name = "Department"),
        @JsonSubTypes.Type(value = WorkerPosition.class, name = "Position"),
        @JsonSubTypes.Type(value = WorkerEntry.class, name = "Worker"),
        @JsonSubTypes.Type(value = Photo.class, name = "Photo"),
        @JsonSubTypes.Type(value = EmployeeRegistration.class, name = "EmployeeRegistration"),
        @JsonSubTypes.Type(value = LongAuditableObject.class, name = "LongAuditableObject"),
        @JsonSubTypes.Type(value = CaseNameAndDescriptionChangeRequest.class, name = "CaseNameAndDescriptionChangeRequest"),
        @JsonSubTypes.Type(value = CaseObjectCreateRequest.class, name = "CaseObjectCreateRequest"),
        @JsonSubTypes.Type(value = CaseObjectMeta.class, name = "CaseObjectMeta"),
        @JsonSubTypes.Type(value = DevUnitInfo.class, name = DevUnitInfo.DEV_UNIT_INFO),
        @JsonSubTypes.Type(value = Attachment.class, name = Attachment.AUDIT_TYPE),
        @JsonSubTypes.Type(value = CaseLink.class, name = CaseLink.AUDIT_TYPE),
        @JsonSubTypes.Type(value = Document.class, name = Document.AUDIT_TYPE),
        @JsonSubTypes.Type(value = DocumentType.class, name = DocumentType.AUDIT_TYPE),
        @JsonSubTypes.Type(value = Platform.class, name = Platform.AUDIT_TYPE),
        @JsonSubTypes.Type(value = Server.class, name = Server.AUDIT_TYPE),
        @JsonSubTypes.Type(value = Application.class, name = Application.AUDIT_TYPE),
        @JsonSubTypes.Type(value = RoomReservation.class, name = RoomReservation.AUDIT_TYPE),
        @JsonSubTypes.Type(value = EmployeeRegistrationShortView.class, name = EmployeeRegistrationShortView.AUDIT_TYPE),
        @JsonSubTypes.Type(value = ReservedIpRequest.class, name = ReservedIpRequest.AUDIT_TYPE),
        @JsonSubTypes.Type(value = ReservedIp.class, name = ReservedIp.AUDIT_TYPE),
        @JsonSubTypes.Type(value = Subnet.class, name = Subnet.AUDIT_TYPE),
        @JsonSubTypes.Type(value = Plan.class, name = Plan.AUDIT_TYPE),
        @JsonSubTypes.Type(value = PersonAbsence.class, name = PersonAbsence.AUDIT_TYPE),
        @JsonSubTypes.Type(value = PersonSubscriptionChangeRequest.class, name = PersonSubscriptionChangeRequest.AUDIT_TYPE),
        @JsonSubTypes.Type(value = DutyLog.class, name = DutyLog.AUDIT_TYPE),
        @JsonSubTypes.Type(value = ServerGroup.class, name = ServerGroup.AUDIT_TYPE),
        @JsonSubTypes.Type(value = Delivery.class, name = Delivery.AUDIT_TYPE),
        @JsonSubTypes.Type(value = Kit.class, name = Kit.AUDIT_TYPE),
        @JsonSubTypes.Type(value = YoutrackWorkDictionary.class, name = YoutrackWorkDictionary.AUDIT_TYPE),
        @JsonSubTypes.Type(value = Card.class, name = Card.AUDIT_TYPE),
        @JsonSubTypes.Type(value = CardBatch.class, name = CardBatch.AUDIT_TYPE),
        @JsonSubTypes.Type(value = CardGroupChangeRequest.class, name = CardGroupChangeRequest.AUDIT_TYPE),
        @JsonSubTypes.Type(value = CardCreateRequest.class, name = CardCreateRequest.AUDIT_TYPE)
})
public abstract class AuditableObject implements Serializable {

    @JsonIgnore
    public abstract String getAuditType();

    public abstract Long getId ();
}
