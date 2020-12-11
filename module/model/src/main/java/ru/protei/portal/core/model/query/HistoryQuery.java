package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_HistoryAction;
import ru.protei.portal.core.model.dict.En_HistoryType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoryQuery extends BaseQuery {

    private Long initiatorId;
    private Date dateFrom;
    private Date dateTo;
    private Long caseObjectId;
    private Long caseNumber;
    private List<En_HistoryType> valueTypes;
    private List<En_HistoryAction> historyActions;
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

    public List<En_HistoryType> getValueTypes() {
        return valueTypes;
    }

    public void addValueType(En_HistoryType valueType) {
        if (this.valueTypes == null) {
            this.valueTypes = new ArrayList<>();
        }
        this.valueTypes.add(valueType);
    }

    public List<En_HistoryAction> getHistoryActions() {
        return historyActions;
    }

    public void setHistoryActions(List<En_HistoryAction> historyActions) {
        this.historyActions = historyActions;
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
                ", valueTypes=" + valueTypes +
                ", historyActions=" + historyActions +
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
