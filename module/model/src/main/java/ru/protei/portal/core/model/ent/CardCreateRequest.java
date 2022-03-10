package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.Date;

public class CardCreateRequest extends AuditableObject {

    public static final String AUDIT_TYPE = "CardCreateRequest";

    private Long typeId;

    private String serialNumber;

    private Long cardBatchId;

    private String article;

    private Date testDate;

    private String comment;

    private Long stateId;

    private PersonShortView manager;

    private String note;

    private int amount;

    @Override
    public Long getId() { return null; }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public String getAuditType() {
        return AUDIT_TYPE;
    }
}
