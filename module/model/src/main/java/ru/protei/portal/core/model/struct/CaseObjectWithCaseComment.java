package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;

import java.io.Serializable;
import java.util.Objects;

public class CaseObjectWithCaseComment implements Serializable {

    private CaseObject caseObject;
    private CaseComment caseComment;

    public CaseObjectWithCaseComment() {}

    public CaseObjectWithCaseComment(CaseObject caseObject, CaseComment caseComment) {
        this.caseObject = caseObject;
        this.caseComment = caseComment;
    }

    public CaseObject getCaseObject() {
        return caseObject;
    }

    public CaseComment getCaseComment() {
        return caseComment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CaseObjectWithCaseComment that = (CaseObjectWithCaseComment) o;
        return Objects.equals(caseObject, that.caseObject) &&
                Objects.equals(caseComment, that.caseComment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(caseObject, caseComment);
    }

    @Override
    public String toString() {
        return "CaseObjectWithCaseComment{" +
                "caseObject=" + caseObject +
                ", caseComment=" + caseComment +
                '}';
    }
}
