package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.PersonCaseFilterDAO;
import ru.protei.portal.core.model.ent.PersonToCaseFilter;
import ru.protei.portal.core.model.util.sqlcondition.Condition;

import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.query;

public class PersonCaseFilterDAO_Impl extends PortalBaseJdbcDAO<PersonToCaseFilter> implements PersonCaseFilterDAO {
    @Override
    public boolean isExist(Long personId, Long caseFilterId) {
        Condition condition = query().asCondition()
                .and("person_id").equal(personId)
                .and("case_filter_id").equal(caseFilterId);
        return checkExistsByCondition(condition.getSqlCondition(), personId, caseFilterId);
    }

    @Override
    public boolean removeByPersonIdAndCaseFilterId(Long personId, Long caseFilterId) {
        Condition condition = query().asCondition()
                .and("person_id").equal(personId)
                .and("case_filter_id").equal(caseFilterId);
        removeByCondition(condition.getSqlCondition(), condition.getSqlParameters());

        return true;
    }

    @Override
    public PersonToCaseFilter getByPersonIdAndCaseFilterId(Long personId, Long caseFilterId) {
        Condition condition = query().asCondition()
                .and("person_id").equal(personId)
                .and("case_filter_id").equal(caseFilterId);
        return getByCondition(condition.getSqlCondition(), condition.getSqlParameters());
    }
}
