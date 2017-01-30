package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_DevUnitPersonRoleType;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Date;

/**
 * Информация о человеке с ролью
 */
@JdbcEntity(table = "case_member")
public class CaseMember implements Serializable{

    @JdbcId(name="id" , idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name="CASE_ID")
    private Integer caseId;

    @JdbcJoinedObject(localColumn = "member_id", remoteColumn = "id", updateLocalColumn = true )
    private Person member;

    @JdbcColumn(name="MEMBER_ROLE_ID")
    private Integer roleId;

    public CaseMember() {
    }

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    public Person getMember() {
        return member;
    }

    public void setMember( Person member ) {
        this.member = member;
    }

    public En_DevUnitPersonRoleType getRole() {
        return En_DevUnitPersonRoleType.forId( roleId );
    }

    public void setRole( En_DevUnitPersonRoleType role ) {
        this.roleId = role.getId();
    }

    public Integer getCaseId() {
        return caseId;
    }

    public void setCaseId( Integer caseId ) {
        this.caseId = caseId;
    }

    @Override
    public String toString() {
        return "CaseMember{" +
            "id=" + id +
            ", member=" + member +
            ", caseId=" + caseId +
            ", roleId=" + roleId +
            '}';
    }
}
