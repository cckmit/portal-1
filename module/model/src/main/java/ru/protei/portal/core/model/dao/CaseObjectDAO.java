package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.query.CaseQuery;
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

    Long insertCase (CaseObject object);

    CaseObject getCase(En_CaseType caseType, long number);
    Long getCaseId(En_CaseType caseType, long number);

    Long getCaseIdByNumber( long number );

    Long getCaseNo( long caseId);
    CaseObject getCaseByCaseno(long caseno);

    CaseObject getByExternalAppCaseId (String externalApplicationCaseId);

    List<CaseObject> getCaseIdAndNumbersByCaseNumbers(List<Long> caseNumbers);

    Long getAndIncrementEmailLastId( Long caseId );
//    Long getEmailLastId(Long caseId);

    boolean updateNullCreatorByExtAppType(String extAppType);

    int removeByNameLike(String name);

    CaseObject getByCaseNameLike(String name);

    @SqlConditionBuilder
    SqlCondition caseQueryCondition (CaseQuery query);

    int countByQuery(CaseQuery query);

    String getExternalAppName( Long caseId );
}
