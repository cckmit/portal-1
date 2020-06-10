package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_TimeElapsedType;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.model.view.PlanOption;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CaseObjectCreateRequest extends AuditableObject {

    private CaseObject caseObject;

    private List<CaseLink> links;

    private List<CaseTag> tags;

    private En_TimeElapsedType timeElapsedType;

    private Long timeElapsed;

    private Set<PlanOption> plans;

    public CaseObjectCreateRequest() {
        caseObject = new CaseObject();
    }

    public CaseObjectCreateRequest(CaseObject caseObject) {
        this.caseObject = caseObject;
    }

    public CaseObject getCaseObject() {
        return caseObject;
    }

    public void setCaseObject(CaseObject caseObject) {
        this.caseObject = caseObject;
    }

    public List<CaseTag> getTags() {
        return tags;
    }

    public void addTag(CaseTag tag) {
        if (tags == null) {
            tags = new ArrayList<>();
        }
        tags.add(tag);
    }

    public List<CaseLink> getLinks() {
        return links;
    }

    public void addLink(CaseLink link) {
        if (links == null) {
            links = new ArrayList<>();
        }
        links.add(link);
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

    public Set<PlanOption> getPlans() {
        return plans;
    }

    public void setPlans(Set<PlanOption> plans) {
        this.plans = plans;
    }

    @Override
    public String getAuditType() {
        return "CaseObjectCreateRequest";
    }

    @Override
    public Long getId() {
        return getCaseId();
    }
}
