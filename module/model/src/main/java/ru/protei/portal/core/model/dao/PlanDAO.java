package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.Plan;
import ru.protei.portal.core.model.query.PlanQuery;

import java.util.List;

public interface PlanDAO extends PortalBaseDAO<Plan> {

    List<Plan> getListByQuery(PlanQuery query);
}
