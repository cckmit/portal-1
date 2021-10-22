package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.Date;
import java.util.Set;

public class CardGroupChangeRequest extends AuditableObject {

    public static final String AUDIT_TYPE = "CardGroupChangeRequest";

    private Set<Long> ids;

    private Long stateId;

    private String article;

    private PersonShortView manager;

    private Date testDate;

    private String note;

    private String comment;

    public CardGroupChangeRequest() {}

    @Override
    public Long getId() { return null; }

    public Set<Long> getIds() {
        return ids;
    }

    public void setIds(Set<Long> ids) {
        this.ids = ids;
    }

    public Long getStateId() {
        return stateId;
    }

    public void setStateId(Long stateId) {
        this.stateId = stateId;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public PersonShortView getManager() {
        return manager;
    }

    public void setManager(PersonShortView manager) {
        this.manager = manager;
    }

    public Date getTestDate() {
        return testDate;
    }

    public void setTestDate(Date testDate) {
        this.testDate = testDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String getAuditType() {
        return AUDIT_TYPE;
    }
}