package ru.protei.portal.core.model.query;

/**
 * Параметры фильтрации истории обращений, для получения через API
 */
public class HistoryApiQuery extends BaseQuery {

    private Long caseNumber;

    public Long getCaseNumber() { return caseNumber; }
    public void setCaseNumber(Long caseNumber) { this.caseNumber = caseNumber; }

    @Override
    public String toString() {
        return "HistoryApiQuery{" +
                "caseNumber=" + caseNumber +
                '}';
    }
}
