package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseCommentNightWork;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.struct.Interval;
import ru.protei.portal.core.model.struct.reportytwork.ReportYtWorkCaseCommentTimeElapsedSum;

import java.util.List;

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

    List<CaseComment> getLastNotNullTextPartialCommentsForReport(List<Long> caseId);

    List<CaseComment> getPartialCommentsForReport(CaseCommentQuery query);

    List<CaseCommentNightWork> getCaseCommentNightWork(CaseQuery query);
    
    List<ReportYtWorkCaseCommentTimeElapsedSum> getCaseCommentReportYtWork(Interval interval, int offset, int limit);
}
