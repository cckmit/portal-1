package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@JdbcEntity(table = "card_batch")
public class CardBatch implements Serializable {
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
    private Date deadline;

    @JdbcJoinedColumn(localColumn = CardBatch.Columns.ID, remoteColumn = CaseObject.Columns.ID, mappedColumn = CaseObject.Columns.IMPORTANCE,
            table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS)
    private Long importance;

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

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Long getImportance() {
        return importance;
    }

    public void setImportance(Long importance) {
        this.importance = importance;
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
                ", importance=" + importance +
                '}';
    }

    public interface Columns {
        String ID = "id";
        String TYPE_ID = "type_id";
        String NUMBER = "number";
    }
}
