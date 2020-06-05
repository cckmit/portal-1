package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_HistoryValueType;

import java.util.Date;

public class HistoryQuery extends BaseQuery{

    private Long initiatorId;
    private Date dateFrom;
    private Date dateTo;
    private Long caseObjectId;
    private Long caseNumber;
    private En_HistoryValueType valueType;
    private String oldValue;
    private String newValue;

    public Long getInitiatorId() {
        return initiatorId;
    }

    public void setInitiatorId(Long initiatorId) {
        this.initiatorId = initiatorId;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public Long getCaseObjectId() {
        return caseObjectId;
    }

    public void setCaseObjectId(Long caseObjectId) {
        this.caseObjectId = caseObjectId;
    }

    public Long getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(Long caseNumber) {
        this.caseNumber = caseNumber;
    }

    public En_HistoryValueType getValueType() {
        return valueType;
    }

    public void setValueType(En_HistoryValueType valueType) {
        this.valueType = valueType;
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
