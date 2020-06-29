package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_HistoryType;

import java.util.Date;

public class HistoryQuery extends BaseQuery {

    private Long initiatorId;
    private Date dateFrom;
    private Date dateTo;
    private Long caseObjectId;
    private Long caseNumber;
    private En_HistoryType valueType;
    private Long oldId;
    private Long newId;

    public HistoryQuery() {}

    public HistoryQuery(Long caseObjectId) {
        this.caseObjectId = caseObjectId;
    }

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

    public En_HistoryType getValueType() {
        return valueType;
    }

    public void setValueType(En_HistoryType valueType) {
        this.valueType = valueType;
    }

    public Long getOldId() {
        return oldId;
    }

    public void setOldId(Long oldId) {
        this.oldId = oldId;
    }

    public Long getNewId() {
        return newId;
    }

    public void setNewId(Long newId) {
        this.newId = newId;
    }

    @Override
    public String toString() {
        return "HistoryQuery{" +
                "initiatorId=" + initiatorId +
                ", dateFrom=" + dateFrom +
                ", dateTo=" + dateTo +
                ", caseObjectId=" + caseObjectId +
                ", caseNumber=" + caseNumber +
                ", valueType=" + valueType +
                ", oldId=" + oldId +
                ", newId=" + newId +
                ", searchString='" + searchString + '\'' +
                ", sortField=" + sortField +
                ", sortDir=" + sortDir +
                ", limit=" + limit +
                ", offset=" + offset +
                '}';
    }
}
