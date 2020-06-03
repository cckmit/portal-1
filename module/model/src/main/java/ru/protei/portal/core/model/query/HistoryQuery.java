package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_HistoryValueType;

import java.util.Date;

public class HistoryQuery {

    private Long initiatorId;
    private Date date;
    private Long caseObjectId;
    private En_HistoryValueType type;
    private String oldValue;
    private String newValue;

    public Long getInitiatorId() {
        return initiatorId;
    }

    public void setInitiatorId(Long initiatorId) {
        this.initiatorId = initiatorId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getCaseObjectId() {
        return caseObjectId;
    }

    public void setCaseObjectId(Long caseObjectId) {
        this.caseObjectId = caseObjectId;
    }

    public En_HistoryValueType getType() {
        return type;
    }

    public void setType(En_HistoryValueType type) {
        this.type = type;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }
}
