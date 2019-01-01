package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;

import java.util.List;

public class CaseObjectComments {

    private CaseObject caseObject;
    private List<CaseComment> caseComments;

    public CaseObjectComments() {}

    public CaseObjectComments(CaseObject caseObject, List<CaseComment> caseComments) {
        this.caseObject = caseObject;
        this.caseComments = caseComments;
    }

    public CaseObject getCaseObject() {
        return caseObject;
    }

    public void setCaseObject(CaseObject caseObject) {
        this.caseObject = caseObject;
    }

    public List<CaseComment> getCaseComments() {
        return caseComments;
    }

    public void setCaseComments(List<CaseComment> caseComments) {
        this.caseComments = caseComments;
    }

    @Override
    public String toString() {
        return "CaseObjectComments{" +
                "caseObject=" + caseObject +
                ", caseComments=" + caseComments +
                '}';
    }
}
