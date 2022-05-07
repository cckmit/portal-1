package ru.protei.portal.core.model.struct.caseobjectreport;

import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.struct.DateRange;

import java.util.List;

public class CaseObjectReportRequest implements CaseObjectReportRow {

    private CaseObject caseObject;
    private List<CaseComment> caseComments;
    private List<History> histories;
    private List<CaseTag> caseTags;
    private List<CaseLink> caseLinks;
    private DateRange createdRange;
    private DateRange modifiedRange;

    public CaseObjectReportRequest() {}

    public CaseObjectReportRequest(CaseObject caseObject, List<CaseComment> caseComments, List<History> histories,
                                   List<CaseTag> caseTags, List<CaseLink> caseLinks, DateRange createdRange, DateRange modifiedRange) {
        this.caseObject = caseObject;
        this.caseComments = caseComments;
        this.histories = histories;
        this.caseTags = caseTags;
        this.caseLinks = caseLinks;
        this.createdRange = createdRange;
        this.modifiedRange = modifiedRange;
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

    public List<History> getHistories() {
        return histories;
    }

    public void setHistories(List<History> histories) {
        this.histories = histories;
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

    public DateRange getModifiedRange() {
        return modifiedRange;
    }

    @Override
    public String toString() {
        return "CaseObjectReportRequest{" +
                "caseObject=" + caseObject +
                ", caseComments=" + caseComments +
                ", histories=" + histories +
                ", caseTags=" + caseTags +
                ", caseLinks=" + caseLinks +
                ", createdRange=" + createdRange +
                ", modifiedRange=" + modifiedRange +
                '}';
    }
}
