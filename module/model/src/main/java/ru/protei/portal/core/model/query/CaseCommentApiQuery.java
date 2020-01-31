package ru.protei.portal.core.model.query;

/**
 * Параметры фильтрации comment, полученные через API
 */
public class CaseCommentApiQuery extends BaseQuery {

    private Long caseNumber;

    public Long getCaseNumber() { return caseNumber; }
    public void setCaseNumber(Long caseNumber) { this.caseNumber = caseNumber; }

    @Override
    public String toString() {
        return "CommentApiQuery{" +
                "caseId=" + caseNumber +
                '}';
    }
}
