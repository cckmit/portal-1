package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_TimeElapsedType;

import java.io.Serializable;
import java.util.List;

public class IssueCreateRequest implements Serializable {
    private CaseObject caseObject;

    private List<CaseLink> links;

    private En_TimeElapsedType timeElapsedType;

    private Long timeElapsed;

    public IssueCreateRequest() {}

    public IssueCreateRequest(CaseObject caseObject) {
        this.caseObject = caseObject;
    }

    public CaseObject getCaseObject() {
        return caseObject;
    }

    public void setCaseObject(CaseObject caseObject) {
        this.caseObject = caseObject;
    }

    public List<CaseLink> getLinks() {
        return links;
    }

    public void setLinks(List<CaseLink> links) {
        this.links = links;
    }

    public En_TimeElapsedType getTimeElapsedType() {
        return timeElapsedType;
    }

    public void setTimeElapsedType(En_TimeElapsedType timeElapsedType) {
        this.timeElapsedType = timeElapsedType;
    }

    public Long getTimeElapsed() {
        return timeElapsed;
    }

    public void setTimeElapsed(Long timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    public Long getCaseNumber() {
        return caseObject.getCaseNumber();
    }

    public Long getCaseId() {
        return caseObject == null ? null : caseObject.getId();
    }
}
