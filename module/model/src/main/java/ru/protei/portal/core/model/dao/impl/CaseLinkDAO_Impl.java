package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.CaseLinkDAO;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.winter.jdbc.JdbcQueryParameters;
import ru.protei.winter.jdbc.JdbcSort;

import java.util.List;

public class CaseLinkDAO_Impl extends PortalBaseJdbcDAO<CaseLink> implements CaseLinkDAO {

    @Override
    public List<CaseLink> getByCaseId(long caseId) {
        SqlCondition where = new SqlCondition().condition("case_link.case_id = ?").add(caseId);
        return getList(new JdbcQueryParameters()
                .withCondition(where.condition, where.args)
                .withDistinct(true)
                .withSort(new JdbcSort(JdbcSort.Direction.ASC, "case_link.id"))
        );
    }
}
