package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.winter.jdbc.annotations.*;

import java.util.Date;
import java.util.Objects;

import static ru.protei.portal.core.model.ent.Delivery.Columns.ID;

@JdbcEntity(table = "card")
public class Card extends AuditableObject {
    public static final String AUDIT_TYPE = "Card";
    public static final String CASE_OBJECT_TABLE = "case_object";
    public static final String CASE_OBJECT_ALIAS = "CO";

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;
    /**
     * Дата создания
     */
    @JdbcJoinedColumn(localColumn = ID, remoteColumn = CaseObject.Columns.ID,
            mappedColumn = CaseObject.Columns.CREATED, table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS)
    private Date created;

    /**
     * Создатель
     */
    @JdbcJoinedColumn(localColumn = ID, remoteColumn = CaseObject.Columns.ID,
            mappedColumn = CaseObject.Columns.CREATOR, table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS)
    private Long creatorId;

    @JdbcJoinedObject(joinPath = {
            @JdbcJoinPath(localColumn = ID, remoteColumn = CaseObject.Columns.ID, table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS),
            @JdbcJoinPath(localColumn = CaseObject.Columns.CREATOR, remoteColumn = "id", table = "person")})
    private Person creator;

    /**
     * Дата изменения
     */
    @JdbcJoinedColumn(localColumn = ID, remoteColumn = CaseObject.Columns.ID,
            mappedColumn = CaseObject.Columns.MODIFIED, table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS)
    private Date modified;

    @JdbcColumn(name = "type_id")
    private Long typeId;

    @JdbcJoinedObject(localColumn = Card.Columns.TYPE_ID, remoteColumn = "id", table = "card_type")
    private CardType cardType;

    @JdbcColumn(name = "serial_number")
    private String serialNumber;

    @JdbcColumn(name = "card_batch_id")
    private Long cardBatchId;

    @JdbcColumn(name = "article")
    private String article;

    @JdbcColumn(name = "test_date")
    private Date testDate;

    @JdbcColumn(name = "comment")
    private String comment;

    @JdbcJoinedColumn(localColumn = Card.Columns.ID, remoteColumn = CaseObject.Columns.ID, mappedColumn = CaseObject.Columns.STATE,
            table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS)
    private Long stateId;

    @JdbcJoinedObject(joinPath = {
            @JdbcJoinPath(localColumn = Card.Columns.ID, remoteColumn = CaseObject.Columns.ID, table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS),
            @JdbcJoinPath(localColumn = CaseObject.Columns.STATE, remoteColumn = "id", table = "case_state", sqlTableAlias = CASE_OBJECT_ALIAS),
    })
    private CaseState state;

    @JdbcJoinedObject(joinPath = {
            @JdbcJoinPath(localColumn = Card.Columns.ID, remoteColumn = CaseObject.Columns.ID, table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS),
            @JdbcJoinPath(localColumn = CaseObject.Columns.MANAGER, remoteColumn = "id", table = "person")})
    private PersonShortView manager;

    @JdbcJoinedColumn(localColumn = Card.Columns.ID, remoteColumn = CaseObject.Columns.ID,
            mappedColumn = CaseObject.Columns.INFO, table = CASE_OBJECT_TABLE, sqlTableAlias = CASE_OBJECT_ALIAS)
    private String note;

    public Card() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Person getCreator() {
        return creator;
    }

    public void setCreator(Person creator) {
        this.creator = creator;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Long getCardBatchId() {
        return cardBatchId;
    }

    public void setCardBatchId(Long cardBatchId) {
        this.cardBatchId = cardBatchId;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public Date getTestDate() {
        return testDate;
    }

    public void setTestDate(Date testDate) {
        this.testDate = testDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public PersonShortView getManager() {
        return manager;
    }

    public void setManager(PersonShortView manager) {
        this.manager = manager;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String getAuditType() {
        return AUDIT_TYPE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return Objects.equals(id, card.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", created=" + created +
                ", creatorId=" + creatorId +
                ", creator=" + creator +
                ", modified=" + modified +
                ", typeId=" + typeId +
                ", cardType='" + cardType + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", cardBatchId=" + cardBatchId +
                ", article='" + article + '\'' +
                ", testDate=" + testDate +
                ", comment='" + comment + '\'' +
                ", stateId=" + stateId +
                ", state=" + state +
                ", manager=" + manager +
                ", note='" + note + '\'' +
                '}';
    }

    public interface Columns {
        String ID = "id";
        String TYPE_ID = "type_id";
    }
}
