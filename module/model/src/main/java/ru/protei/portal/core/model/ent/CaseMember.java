package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_PersonRoleType;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;

/**
 * Информация о человеке с ролью
 */
@JdbcEntity(table = "case_member")
public class CaseMember implements Serializable{

    @JdbcId(name="id" , idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name="CASE_ID")
    private Long caseId;

    @JdbcColumn(name="MEMBER_ID")
    private Long memberId;

    @JdbcJoinedObject(localColumn = "MEMBER_ID", remoteColumn = "id", updateLocalColumn = false )
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

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId( Long memberId ) {
        this.memberId = memberId;
    }

    public En_PersonRoleType getRole() {
        return En_PersonRoleType.forId( roleId );
    }

    public void setRole( En_PersonRoleType role ) {
        this.roleId = role.getId();
    }

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId( Long caseId ) {
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
