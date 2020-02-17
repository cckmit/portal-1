package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dto.CaseResolutionTimeReportDto;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.query.SqlCondition;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by michael on 20.05.16.
 */
public interface CaseCommentDAO extends PortalBaseDAO<CaseComment> {

    List<CaseComment> getCaseComments( CaseCommentQuery query );

    /**
     * Возвращает список идентификаторов CaseObject.id
     * @param query
     * @return
     */
    List<Long> getCaseCommentsCaseIds( CaseCommentQuery query );

    @SqlConditionBuilder
    SqlCondition createSqlCondition( CaseCommentQuery query );

    List<CaseComment> listByRemoteIds(List<String> remoteIds);

    boolean checkExistsByRemoteIdAndRemoteLinkId(String remoteId, Long remoteLinkId);

    List<CaseResolutionTimeReportDto> reportCaseResolutionTime(Date from, Date to, List<Integer> terminatedStates,
                                                               List<Long> companiesIds, Set<Long> productIds, List<Long> managersIds, List<Integer> importanceIds,
                                                               List<Long> tagsIds);
}
