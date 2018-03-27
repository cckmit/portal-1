package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.CaseShortViewDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.view.CaseShortView;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by michael on 19.05.16.
 */
public class CaseShortViewDAO_Impl extends PortalBaseJdbcDAO<CaseShortView> implements CaseShortViewDAO {

    @Autowired
    private SqlDefaultBuilder sqlDefaultBuilder;


    @Override
    public List< CaseShortView > getCases( CaseQuery query ) {
        return listByQuery(query);
    }

    @SqlConditionBuilder
    public SqlCondition caseQueryCondition ( CaseQuery query) {
        return sqlDefaultBuilder.caseCommonQuery(query);
    }
}
