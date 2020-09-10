package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.CaseTag;

import java.util.List;

public class CaseObjectReportRequest {

    private CaseObject caseObject;
    private List<CaseComment> caseComments;
    private List<CaseTag> caseTags;
    private List<CaseLink> caseLinks;
    private DateRange createdRange;

    public CaseObjectReportRequest() {}

    public CaseObjectReportRequest(CaseObject caseObject, List<CaseComment> caseComments, List<CaseTag> caseTags, List<CaseLink> caseLinks, DateRange createdRange) {
        this.caseObject = caseObject;
        this.caseComments = caseComments;
        this.caseTags = caseTags;
        this.caseLinks = caseLinks;
        this.createdRange = createdRange;
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

    public List<CaseLink> getCaseLinks() {
        return caseLinks;
    }

    public DateRange getCreatedRange() {
        return createdRange;
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
