package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.CaseTag;

import java.util.List;

public class CaseObjectReportRequest {

    private CaseObject caseObject;
    private List<CaseComment> caseComments;
    private List<CaseTag> caseTags;

    public CaseObjectReportRequest() {}

    public CaseObjectReportRequest(CaseObject caseObject, List<CaseComment> caseComments, List<CaseTag> caseTags) {
        this.caseObject = caseObject;
        this.caseComments = caseComments;
        this.caseTags = caseTags;
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

    public List<CaseTag> getCaseTags() {
        return caseTags;
    }

    public void setCaseTags(List<CaseTag> caseTags) {
        this.caseTags = caseTags;
    }

    @Override
    public String toString() {
        return "CaseObjectReportRequest{" +
                "caseObject=" + caseObject +
                ", caseComments=" + caseComments +
                ", caseTags=" + caseTags +
                '}';
    }
}
