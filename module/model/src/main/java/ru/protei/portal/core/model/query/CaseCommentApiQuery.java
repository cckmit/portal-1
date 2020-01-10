package ru.protei.portal.core.model.query;

/**
 * Параметры фильтрации comment, полученные через API
 */
public class CaseCommentApiQuery extends BaseQuery {

    private Long caseId;

    public Long getCaseId() { return caseId; }
    public void setCaseId(Long caseId) { this.caseId = caseId; }

    @Override
    public String toString() {
        return "CommentApiQuery{" +
                "caseId=" + caseId +
                '}';
    }
}
