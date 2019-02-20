package ru.protei.portal.core.model.query;

/**
 *
 */
public class CommentTimeElapsedQuery  extends BaseQuery {

    public CommentTimeElapsedQuery( CaseQuery caseQuery ) {
        this.caseQuery = caseQuery;
    }

    public CommentTimeElapsedQuery() {
    }

    public Boolean isTimeElapsedNotNull() {
        return timeElapsedNotNull;
    }

    public void setTimeElapsedNotNull(Boolean timeElapsedNotNull) {
        this.timeElapsedNotNull = timeElapsedNotNull;
    }

    public CaseQuery getCaseQuery() {
        return caseQuery;
    }

    private Boolean timeElapsedNotNull;
    private CaseQuery caseQuery;
}
