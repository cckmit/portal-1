package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.CaseShortViewDAO;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.view.CaseShortView;

import java.util.List;

/**
 * Created by michael on 19.05.16.
 */
public class CaseShortViewDAO_Impl extends PortalBaseJdbcDAO<CaseShortView> implements CaseShortViewDAO {

    @Autowired
    private CaseObjectDAOHelper caseObjectDAOHelper;

    @Override
    public List< CaseShortView > getCases( CaseQuery query ) {
        return listByQuery(caseObjectDAOHelper.getQueryWithSearchAtComments(query));
    }

    @SqlConditionBuilder
    public SqlCondition caseQueryCondition ( CaseQuery query) {
        return caseObjectDAOHelper.caseCommonQuery(query);
    }
}
