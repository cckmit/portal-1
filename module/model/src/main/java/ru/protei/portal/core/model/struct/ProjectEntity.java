package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.util.sqlcondition.Query;
import ru.protei.winter.jdbc.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * Информация о проекте в регионе
 */
@JdbcEntity(table = "case_object")
public class ProjectEntity extends AuditableObject {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

//    @JdbcColumn(name = "CASENO")
//    private Long caseNumber;

    @JdbcColumn(name = Columns.CASE_TYPE)
    @JdbcEnumerated( EnumType.ID )
    private En_CaseType type;

    @JdbcColumn(name = "CASE_NAME")
    private String name;

    @JdbcColumn(name = "INFO")
    private String description;

    @JdbcColumn(name = "STATE")
    private Long stateId;

    /**
     * Тип заказчика
     */
    @JdbcColumn(name = "ISLOCAL")
    @JdbcEnumerated( EnumType.ID )
    private En_CustomerType customerType;


    @JdbcColumn(name = "CREATED")
    private Date created;

    @JdbcColumn(name = "CREATOR")
    private Long creatorId;

//    @JdbcJoinedObject( localColumn = "CREATOR", remoteColumn = "id", updateLocalColumn = false )
//    private Person creator;

    @JdbcColumn(name = Columns.DELETED)
    private boolean deleted;

    @JdbcColumn(name = Columns.PAUSE_DATE)
    private Long pauseDate;

    @JdbcOneToMany( table = "case_member", localColumn = "id", remoteColumn = "CASE_ID" )
    private List<CaseMember> members;

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

//    public Long getCaseNumber() {
//        return caseNumber;
//    }
//
//    public void setCaseNumber( Long caseNumber ) {
//        this.caseNumber = caseNumber;
//    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public En_RegionState getState() {
        return stateId == null ? En_RegionState.UNKNOWN : En_RegionState.forId( stateId );
    }

    public void setState( En_RegionState state ) {
        this.stateId = state.getId();
    }


    public Date getCreated() {
        return created;
    }

    public void setCreated( Date created ) {
        this.created = created;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId( Long creatorId ) {
        this.creatorId = creatorId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public En_CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(En_CustomerType customerType) {
        this.customerType = customerType;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public String getAuditType() {
        return Project.AUDIT_TYPE_PROJECT;
    }

    public void setPauseDate( Long pauseDateTimestamp ) {
        this.pauseDate = pauseDateTimestamp;
    }

    public Long getPauseDate() {
        return pauseDate;
    }

    public List<CaseMember> getMembers() {
        return members;
    }

    public void setMembers( List<CaseMember> members ) {
        this.members = members;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        ProjectEntity that = (ProjectEntity) o;

        if ( id != null ? !id.equals( that.id ) : that.id != null ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", stateId=" + stateId +
                ", customerType=" + customerType +
                ", created=" + created +
                ", creatorId=" + creatorId +
                ", deleted=" + deleted +
                '}';
    }

    public interface Columns {
        String ID = "id";
        String PAUSE_DATE = CaseObject.Columns.PAUSE_DATE;
        String CASE_TYPE = CaseObject.Columns.CASE_TYPE;
        String DELETED = CaseObject.Columns.DELETED;
    }

    public static final int NOT_DELETED = CaseObject.NOT_DELETED;
}
