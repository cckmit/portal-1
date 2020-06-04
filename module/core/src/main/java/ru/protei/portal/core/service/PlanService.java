package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Plan;
import ru.protei.portal.core.model.ent.PlanToCaseObject;
import ru.protei.portal.core.model.query.PlanQuery;

import java.util.List;

public interface PlanService {

    @Privileged(En_Privilege.PLAN_VIEW)
    Result<List<Plan>> listPlans(AuthToken token, PlanQuery query);

    @Privileged(En_Privilege.PLAN_VIEW)
    Result<List<Plan>> listPlansWithIssues(AuthToken token, PlanQuery query);

    @Privileged(En_Privilege.PLAN_VIEW)
    Result<Plan> getPlanWithIssues(AuthToken token, Long planId);

    @Privileged(En_Privilege.PLAN_CREATE)
    @Auditable(En_AuditType.PLAN_CREATE)
    Result<Long> createPlan(AuthToken token, Plan plan);

    @Privileged(En_Privilege.PLAN_EDIT)
    @Auditable(En_AuditType.PLAN_MODIFY)
    Result<Boolean> editPlanParams(AuthToken token, Plan plan);

    @Privileged(En_Privilege.PLAN_EDIT)
    @Auditable(En_AuditType.PLAN_MODIFY)
    Result<PlanToCaseObject> addIssueToPlan(AuthToken token, Long planId, Long issueId);

    @Privileged(En_Privilege.PLAN_EDIT)
    @Auditable(En_AuditType.PLAN_MODIFY)
    Result<Boolean> removeIssueFromPlan(AuthToken token, Long planId, Long issueId);

    @Privileged(En_Privilege.PLAN_EDIT)
    @Auditable(En_AuditType.PLAN_MODIFY)
    Result<Boolean> changeIssueOrder(AuthToken token, List<PlanToCaseObject> changedList);

    @Privileged(En_Privilege.PLAN_EDIT)
    @Auditable(En_AuditType.PLAN_MODIFY)
    Result<Boolean> moveIssueToOtherPlan(AuthToken token, Long currentPlanId, Long issueId, Long newPlanId);

    @Privileged(En_Privilege.PLAN_REMOVE)
    @Auditable(En_AuditType.PLAN_REMOVE)
    Result<Boolean> removePlan(AuthToken token, Long planId);

}
