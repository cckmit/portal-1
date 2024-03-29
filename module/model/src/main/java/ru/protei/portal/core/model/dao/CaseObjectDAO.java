package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;
import java.util.Map;

/**
 * Created by michael on 19.05.16.
 */
public interface CaseObjectDAO extends PortalBaseDAO<CaseObject> {
    Map<Long,Long> getNumberToIdMap (En_CaseType caseType);

    List<CaseObject> getCases( CaseQuery query );

    SearchResult<CaseObject> getSearchResult(CaseQuery query);

    Long insertCase (CaseObject object);

    Long getCaseIdByNumber(En_CaseType caseType, long number);

    Long getCaseNumberById( long caseId);

    CaseObject getCaseByNumber(En_CaseType caseType, long caseNo);

    CaseObject getByExternalAppCaseId (String externalApplicationCaseId);

    Long getAndIncrementEmailLastId( Long caseId );

    List<Long> getCaseNumbersByPlatformId(Long platformId);

    CaseObject getByCaseNameLike(String name);

    @SqlConditionBuilder
    SqlCondition caseQueryCondition (CaseQuery query);

    int countByQuery(CaseQuery query);

    String getExternalAppName( Long caseId );

    List<Long> getCaseIdToAutoOpen();

    boolean isJiraDuplicateByProjectId(String projectId);
}
