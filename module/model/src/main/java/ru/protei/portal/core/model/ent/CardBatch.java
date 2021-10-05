package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_PersonRoleType;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;

@JdbcEntity(table = "card_batch")
public class CardBatch extends AuditableObject {
    public static final String AUDIT_TYPE = "CardBatch";
    public static final String CASE_OBJECT_TABLE = "case_object";
    public static final String CASE_STATE_TABLE = "case_state";
    public static final String CASE_OBJECT_ALIAS = "CO";
    public static final String CASE_STATE_ALIAS = "CS";

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "type_id")
    private Long typeId;

    @JdbcJoinedColumn(localColumn = CardBatch.Columns.TYPE_ID, remoteColumn = "id", mappedColumn = "name", table = "card_type")
    private String typeName;

    @JdbcColumn(name = "number")
    private String number;

    @JdbcColumn(name = "article")
    private String article;

    @JdbcColumn(name = "amount")
    private Integer amount;

    @JdbcJoinedColumn(localColumn = CardBatch.Columns.ID, remoteColumn = CaseObject.Columns.ID, mappedColumn = CaseObject.Columns.STATE,
            table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS)
    private Long stateId;

    @JdbcJoinedObject(joinPath = {
            @JdbcJoinPath(localColumn = CardBatch.Columns.ID, remoteColumn = CaseObject.Columns.ID, table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS),
            @JdbcJoinPath(localColumn = CaseObject.Columns.STATE, remoteColumn = "id", table = CASE_STATE_TABLE, sqlTableAlias = CASE_STATE_ALIAS),
    })
    private CaseState state;

    @JdbcJoinedColumn(localColumn = CardBatch.Columns.ID, remoteColumn = CaseObject.Columns.ID,
            mappedColumn = CaseObject.Columns.INFO, table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS)
    private String params;

    @JdbcJoinedColumn(localColumn = CardBatch.Columns.ID, table = CASE_OBJECT_TABLE, remoteColumn = CaseObject.Columns.ID,
            mappedColumn = CaseObject.Columns.DEADLINE, sqlTableAlias = CASE_OBJECT_ALIAS)
    private Long deadline;

    @JdbcJoinedColumn(localColumn = CardBatch.Columns.ID, remoteColumn = CaseObject.Columns.ID, mappedColumn = CaseObject.Columns.IMPORTANCE,
            table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS)
    private Integer priority;

    @JdbcOneToMany( table = "case_member", localColumn = "id", remoteColumn = "CASE_ID" )
    private List<CaseMember> members;

    /**
     * Исполнители
     */
    private List<PersonProjectMemberView> сontractors;


    public CardBatch() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Long getStateId() {
        return stateId;
    }

    public void setStateId(Long stateId) {
        this.stateId = stateId;
    }

    public CaseState getState() {
        return state;
    }

    public void setState(CaseState state) {
        this.state = state;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public Long getDeadline() {
        return deadline;
    }

    public void setDeadline(Long deadline) {
        this.deadline = deadline;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public List<PersonProjectMemberView> getContractors() {
        if ( сontractors == null && !isEmpty( members ) ) {
            сontractors = CollectionUtils.stream( members )
                    .filter( member -> En_PersonRoleType.isCardBatchRole( member.getRole() ) )
                    .map( member -> new PersonProjectMemberView( member.getMember(), member.getRole() ) )
                    .collect( Collectors.toList() );
        }
        return сontractors;
    }

    public void setContractors(List<PersonProjectMemberView> сontractors) {
        this.сontractors = сontractors;
    }

    @Override
    public String getAuditType() {
        return AUDIT_TYPE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardBatch card = (CardBatch) o;
        return Objects.equals(id, card.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CardBatch{" +
                "id=" + id +
                ", typeId=" + typeId +
                ", typeName='" + typeName + '\'' +
                ", number='" + number + '\'' +
                ", article='" + article + '\'' +
                ", amount=" + amount +
                ", stateId=" + stateId +
                ", state=" + state +
                ", info='" + params + '\'' +
                ", deadline=" + deadline +
                ", priority=" + priority +
                '}';
    }

    public interface Columns {
        String ID = "id";
        String TYPE_ID = "type_id";
        String NUMBER = "number";
    }
}
