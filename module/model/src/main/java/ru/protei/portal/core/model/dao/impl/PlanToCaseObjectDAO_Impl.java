package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.PlanToCaseObjectDAO;
import ru.protei.portal.core.model.ent.PlanToCaseObject;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.winter.jdbc.JdbcQueryParameters;
import ru.protei.winter.jdbc.JdbcSort;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlanToCaseObjectDAO_Impl extends PortalBaseJdbcDAO<PlanToCaseObject> implements PlanToCaseObjectDAO {

    @Override
    public List<PlanToCaseObject> getSortedListByPlanId(Long planId) {
        return getList(new JdbcQueryParameters()
                .withCondition("plan_id=?", planId)
                .withSort(new JdbcSort(JdbcSort.Direction.ASC, "order_number")));
    }

    @Override
    public List<PlanToCaseObject> getSortedListByCaseId(Long caseId) {
        return getList(new JdbcQueryParameters()
                .withCondition("case_object_id=?", caseId)
                .withSort(new JdbcSort(JdbcSort.Direction.ASC, "order_number")));
    }

    @Override
    public PlanToCaseObject getByPlanIdAndIssueId(Long planId, Long caseId) {
        return getByCondition("plan_id=? and case_object_id=?", planId, caseId);
    }

    @Override
    public int removeByPlanIdAndIssueId(Long planId, Long caseId) {
        return removeByCondition("plan_id=? and case_object_id=?", planId, caseId);
    }

    @Override
    public Map<Long, Long> countByPlanIds(List<Long> planIds) {
        String sql = "SELECT plan_id, COUNT(*) AS cnt FROM " + getTableName() + " " +
                "WHERE plan_id IN " + HelperFunc.makeInArg(planIds, String::valueOf) + " " +
                "GROUP BY plan_id";
        Map<Long, Long> result = new HashMap<>();
        jdbcTemplate.query(sql, (rs, rowNum) -> {
            long id = rs.getLong("plan_id");
            long count = rs.getLong("cnt");
            result.put(id, count);
            return null;
        });
        return result;
    }

}
