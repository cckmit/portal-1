package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.DataQuery;
import ru.protei.portal.core.model.query.SqlCondition;

import java.util.List;
import java.util.Map;

/**
 * Created by michael on 19.05.16.
 */
public interface CaseObjectDAO extends PortalBaseDAO<CaseObject> {
    Map<Long,Long> getNumberToIdMap (En_CaseType caseType);

    List<CaseObject> getCases( CaseQuery query );

    //public Long getNextCaseNumber (En_CaseType caseType);

    Long count(CaseQuery query);

    Long insertCase (CaseObject object);

    CaseObject getCase(En_CaseType caseType, long number);
    Long getCaseId(En_CaseType caseType, long number);
    CaseObject getCaseByCaseno(long caseno);

    CaseObject getByExternalAppCaseId (String externalApplicationCaseId);

    @SqlConditionBuilder
    SqlCondition caseQueryCondition (CaseQuery query);
}
