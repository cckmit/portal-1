package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.PlanToCaseObject;

import java.util.List;
import java.util.Map;

public interface PlanToCaseObjectDAO extends PortalBaseDAO<PlanToCaseObject>{
    List<PlanToCaseObject> getSortedListByPlanId(Long planId);
    List<PlanToCaseObject> getSortedListByCaseId(Long caseId);
    PlanToCaseObject getByPlanIdAndIssueId(Long planId, Long caseId);

    int removeByPlanIdAndIssueId(Long planId, Long caseId);

    Map<Long, Long> countByPlanIds(List<Long> planIds);
}
