package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.JdbcEmbed;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcJoinedObject;

import java.io.Serializable;

@JdbcEntity(table = "case_comment")
public class CaseCommentCaseObject implements Serializable {

    @JdbcEmbed
    private CaseComment caseComment;

    @JdbcJoinedObject(localColumn = "case_id", table = "case_object", remoteColumn = "id")
    private CaseObject caseObject;

    public CaseComment getCaseComment() {
        return caseComment;
    }

    public void setCaseComment(CaseComment caseComment) {
        this.caseComment = caseComment;
    }

    public CaseObject getCaseObject() {
        return caseObject;
    }

    public void setCaseObject(CaseObject caseObject) {
        this.caseObject = caseObject;
    }

    @Override
    public String toString() {
        return "CaseCommentCaseObject{" +
                "caseComment=" + caseComment +
                ", caseObject=" + caseObject +
                '}';
    }
}
