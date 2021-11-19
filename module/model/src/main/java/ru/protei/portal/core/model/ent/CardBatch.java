package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_PersonRoleType;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.winter.jdbc.annotations.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;

@JdbcEntity(table = "card_batch")
public class CardBatch extends AuditableObject {
    public static final String AUDIT_TYPE = "CardBatch";
    public static final String CASE_OBJECT_TABLE = "case_object";
    public static final String CASE_STATE_TABLE = "case_state";
    public static final String CARD_TYPE_TABLE = "card_type";
    public static final String CASE_OBJECT_ALIAS = "CO";
    public static final String CASE_STATE_ALIAS = "CS";
    public static final String CARD_TYPE_ALIAS = "CT";

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "type_id")
    private Long typeId;

    @JdbcJoinedColumn(localColumn = CardBatch.Columns.TYPE_ID, remoteColumn = "id", mappedColumn = "name", table = CARD_TYPE_TABLE, sqlTableAlias = CARD_TYPE_ALIAS)
    private String typeName;

    @JdbcJoinedColumn(localColumn = CardBatch.Columns.TYPE_ID, remoteColumn = "id", mappedColumn = "code", table = CARD_TYPE_TABLE, sqlTableAlias = CARD_TYPE_ALIAS)
    private String code;

    @JdbcColumn(name = "number")
    private String number;

    @JdbcColumn(name = "article")
    private String article;

    @JdbcColumn(name = "amount")
    private Integer amount;

    private Long manufacturedAmount;

    private Long freeAmount;

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

    @JdbcJoinedColumn(localColumn = CardBatch.Columns.ID, remoteColumn = CaseObject.Columns.ID,
            mappedColumn = CaseObject.Columns.DEADLINE, table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS)
    private Long deadline;

    @JdbcJoinedColumn(localColumn = CardBatch.Columns.ID, remoteColumn = CaseObject.Columns.ID, mappedColumn = CaseObject.Columns.IMPORTANCE,
            table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS)
    private Integer importance;

    @JdbcJoinedColumn(joinPath = {
            @JdbcJoinPath(localColumn = CardBatch.Columns.ID, remoteColumn = CaseObject.Columns.ID, table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS),
            @JdbcJoinPath(localColumn = CaseObject.Columns.IMPORTANCE, remoteColumn = "id", table = "importance_level")}, mappedColumn = "code")
    private String importanceCode;

    @JdbcJoinedColumn(joinPath = {
            @JdbcJoinPath(localColumn = CardBatch.Columns.ID, remoteColumn = CaseObject.Columns.ID, table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS),
            @JdbcJoinPath(localColumn = CaseObject.Columns.IMPORTANCE, remoteColumn = "id", table = "importance_level")}, mappedColumn = "color")
    private String importanceColor;

    @JdbcJoinedObject(joinPath = {
            @JdbcJoinPath(localColumn = CardBatch.Columns.ID, remoteColumn = CaseObject.Columns.ID, table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS),
            @JdbcJoinPath(localColumn = CaseObject.Columns.CREATOR, remoteColumn = "id", table = "person")})
    private Person creator;

    @JdbcJoinedColumn(localColumn = CardBatch.Columns.ID, remoteColumn = CaseObject.Columns.ID,
            mappedColumn = CaseObject.Columns.CREATED, table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS)
    private Date created;

    @JdbcOneToMany( table = "case_member", localColumn = "id", remoteColumn = "CASE_ID" )
    private List<CaseMember> members;

    /**
     * Исполнители
     */
    private Set<PersonProjectMemberView> сontractors;


    public CardBatch() {
    }

    public CardBatch(Long id) {
        this.id = id;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public Long getManufacturedAmount() {
        return manufacturedAmount;
    }

    public void setManufacturedAmount(Long manufacturedAmount) {
        this.manufacturedAmount = manufacturedAmount;
    }

    public Long getFreeAmount() {
        return freeAmount;
    }

    public void setFreeAmount(Long freeAmount) {
        this.freeAmount = freeAmount;
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

    public Integer getImportance() {
        return importance;
    }

    public void setImportance(Integer importance) {
        this.importance = importance;
    }

    public Person getCreator() {
        return creator;
    }

    public Date getCreated() {
        return created;
    }

    public Set<PersonProjectMemberView> getContractors() {
        if ( сontractors == null && !isEmpty( members ) ) {
            сontractors = CollectionUtils.stream( members )
                    .filter( member -> En_PersonRoleType.isCardBatchRole( member.getRole() ) )
                    .map( member -> new PersonProjectMemberView( member.getMember(), member.getRole() ) )
                    .collect( Collectors.toSet() );
        }
        return сontractors;
    }

    public void setContractors(Set<PersonProjectMemberView> сontractors) {
        this.сontractors = сontractors;
    }

    public String getImportanceCode() {
        return importanceCode;
    }

    public void setImportanceCode(String importanceCode) {
        this.importanceCode = importanceCode;
    }

    public String getImportanceColor() {
        return importanceColor;
    }

    public void setImportanceColor(String importanceColor) {
        this.importanceColor = importanceColor;
    }

    public List<CaseMember> getMembers() {
        return members;
    }

    public void setMembers(List<CaseMember> members) {
        this.members = members;
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
                ", manufacturedAmount=" + manufacturedAmount +
                ", freeAmount=" + freeAmount +
                ", stateId=" + stateId +
                ", state=" + state +
                ", params='" + params + '\'' +
                ", deadline=" + deadline +
                ", importance=" + importanceCode +
                ", importanceCode='" + importanceCode + '\'' +
                ", importanceColor='" + importanceColor + '\'' +
                ", members=" + members +
                '}';
    }

    public interface Columns {
        String ID = "id";
        String TYPE_ID = "type_id";
        String NUMBER = "number";
    }
}
