package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_PcbOrderPromptness;
import ru.protei.portal.core.model.dict.En_PcbOrderState;
import ru.protei.portal.core.model.dict.En_PcbOrderType;
import ru.protei.portal.core.model.dict.En_StencilType;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@JdbcEntity(table = "pcb_order")
public class PcbOrder extends AuditableObject {

    public static final String AUDIT_TYPE = "PcbOrder";
    public static final String CARD_TYPE_TABLE = "card_type";
    public static final String CARD_TYPE_ALIAS = "CT";

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name="created")
    private Date created;

    @JdbcColumn(name = "creator_id")
    private Long creatorId;

    @JdbcJoinedColumn(mappedColumn = "displayShortName", localColumn = "creator_id", remoteColumn = "id", table = "person")
    private String creatorShortName;

    @JdbcColumn(name = "card_type_id")
    private Long cardTypeId;

    @JdbcJoinedColumn(localColumn = "card_type_id", remoteColumn = "id", mappedColumn = "name", table = CARD_TYPE_TABLE, sqlTableAlias = CARD_TYPE_ALIAS)
    private String cardTypeName;

    @JdbcJoinedColumn(localColumn = "card_type_id", remoteColumn = "id", mappedColumn = "code", table = CARD_TYPE_TABLE, sqlTableAlias = CARD_TYPE_ALIAS)
    private String cardTypeCode;

    @JdbcColumn(name = "modification")
    private String modification;

    @JdbcColumn(name = "type")
    @JdbcEnumerated(EnumType.ID)
    private En_PcbOrderType type;

    @JdbcColumn(name = "stencil_type")
    @JdbcEnumerated(EnumType.ID)
    private En_StencilType stencilType;

    @JdbcColumn(name = "state")
    @JdbcEnumerated(EnumType.ID)
    private En_PcbOrderState state;

    @JdbcColumn(name = "company_id")
    private Long companyId;

    @JdbcJoinedObject (localColumn = "company_id", remoteColumn = "id" )
    private Company company;

    @JdbcColumn(name = "parent_id")
    private Long parentId;

    @JdbcColumn(name = "promptness")
    @JdbcEnumerated(EnumType.ID)
    private En_PcbOrderPromptness promptness;

    @JdbcColumn(name = "amount")
    private Integer amount;

    @JdbcColumn(name="order_date")
    private Date orderDate;

    @JdbcColumn(name="ready_date")
    private Date readyDate;

    @JdbcColumn(name="receipt_date")
    private Date receiptDate;

    @JdbcColumn(name = "recipient_id")
    private Long recipientId;

    @JdbcJoinedColumn(mappedColumn = "displayShortName", localColumn = "recipient_id", remoteColumn = "id", table = "person")
    private String recipientShortName;

    @JdbcColumn(name = "comment")
    private String comment;

    public PcbOrder() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorShortName() {
        return creatorShortName;
    }

    public void setCreatorShortName(String creatorShortName) {
        this.creatorShortName = creatorShortName;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    public String getRecipientShortName() {
        return recipientShortName;
    }

    public void setRecipientShortName(String recipientShortName) {
        this.recipientShortName = recipientShortName;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Long getCardTypeId() {
        return cardTypeId;
    }

    public void setCardTypeId(Long cardTypeId) {
        this.cardTypeId = cardTypeId;
    }

    public String getCardTypeName() {
        return cardTypeName;
    }

    public void setCardTypeName(String cardTypeName) {
        this.cardTypeName = cardTypeName;
    }

    public String getCardTypeCode() {
        return cardTypeCode;
    }

    public void setCardTypeCode(String cardTypeCode) {
        this.cardTypeCode = cardTypeCode;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Date getReadyDate() {
        return readyDate;
    }

    public void setReadyDate(Date readyDate) {
        this.readyDate = readyDate;
    }

    public Date getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(Date receiptDate) {
        this.receiptDate = receiptDate;
    }

    public String getModification() {
        return modification;
    }

    public void setModification(String modification) {
        this.modification = modification;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public En_PcbOrderType getType() {
        return type;
    }

    public void setType(En_PcbOrderType type) {
        this.type = type;
    }

    public En_StencilType getStencilType() {
        return stencilType;
    }

    public void setStencilType(En_StencilType stencilType) {
        this.stencilType = stencilType;
    }

    public En_PcbOrderState getState() {
        return state;
    }

    public void setState(En_PcbOrderState state) {
        this.state = state;
    }

    public En_PcbOrderPromptness getPromptness() {
        return promptness;
    }

    public void setPromptness(En_PcbOrderPromptness promptness) {
        this.promptness = promptness;
    }

    @Override
    public String getAuditType() {
        return AUDIT_TYPE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PcbOrder card = (PcbOrder) o;
        return Objects.equals(id, card.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "PcbOrder{" +
                "id=" + id +
                ", created=" + created +
                ", creatorId=" + creatorId +
                ", cardTypeId=" + cardTypeId +
                ", modification='" + modification + '\'' +
                ", type=" + type +
                ", stencilType=" + stencilType +
                ", state=" + state +
                ", companyId=" + companyId +
                ", parentId=" + parentId +
                ", promptness=" + promptness +
                ", amount=" + amount +
                ", orderDate=" + orderDate +
                ", readyDate=" + readyDate +
                ", receiptDate=" + receiptDate +
                ", recipientId=" + recipientId +
                ", comment='" + comment + '\'' +
                '}';
    }
}
