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
    private String info;

    @JdbcJoinedColumn(localColumn = CardBatch.Columns.ID, table = CASE_OBJECT_TABLE, remoteColumn = CaseObject.Columns.ID,
            mappedColumn = CaseObject.Columns.DEADLINE, sqlTableAlias = CASE_OBJECT_ALIAS)
    private Date deadline;

    @JdbcJoinedColumn(localColumn = CardBatch.Columns.ID, remoteColumn = CaseObject.Columns.ID, mappedColumn = CaseObject.Columns.IMPORTANCE,
            table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS)
    private Long importance;

    public CardBatch() {
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
                ", info='" + info + '\'' +
                ", deadline=" + deadline +
                ", importance=" + importance +
                '}';
    }

    public interface Columns {
        String ID = "id";
        String TYPE_ID = "type_id";
    }
}
