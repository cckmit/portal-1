package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.ent.*;

import java.util.List;

public class CaseObjectReportRequest {

    private CaseObject caseObject;
    private List<CaseComment> caseComments;
    private List<History> stateHistories;
    private List<CaseTag> caseTags;
    private List<CaseLink> caseLinks;
    private DateRange createdRange;
    private DateRange modifiedRange;

    public CaseObjectReportRequest() {}

    public CaseObjectReportRequest(CaseObject caseObject, List<CaseComment> caseComments, List<History> stateHistories,
                                   List<CaseTag> caseTags, List<CaseLink> caseLinks, DateRange createdRange, DateRange modifiedRange) {
        this.caseObject = caseObject;
        this.caseComments = caseComments;
        this.stateHistories = stateHistories;
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

    public List<History> getStateHistories() {
        return stateHistories;
    }

    public void setStateHistories(List<History> stateHistories) {
        this.stateHistories = stateHistories;
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
                ", stateHistory=" + stateHistories +
                ", caseTags=" + caseTags +
                ", caseLinks=" + caseLinks +
                ", createdRange=" + createdRange +
                ", modifiedRange=" + modifiedRange +
                '}';
    }
}
