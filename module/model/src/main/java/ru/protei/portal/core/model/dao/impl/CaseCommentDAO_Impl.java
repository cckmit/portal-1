package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.query.SqlCondition;

import java.util.List;

/**
 * Created by michael on 20.05.16.
 */
public class CaseCommentDAO_Impl extends PortalBaseJdbcDAO<CaseComment> implements CaseCommentDAO {

    @Autowired
    CaseCommentSqlBuilder sqlBuilder;

    @Override
    public List<CaseComment> getCaseComments(long caseId) {
        return getCaseComments(new CaseCommentQuery(caseId));
    }

    @Override
    public List<CaseComment> getCaseComments(CaseCommentQuery query) {
        return listByQuery(query);
    }

    @Override
    public List<Long> getCaseCommentsCaseIds(CaseCommentQuery query) {
        SqlCondition where = createSqlCondition(query);
        return listColumnValue("case_id", Long.class, where.condition, where.args.toArray());
    }

    @SqlConditionBuilder
    public SqlCondition createSqlCondition(CaseCommentQuery query) {
        return sqlBuilder.createSqlCondition(query);
    }

    @Override
    public CaseComment getByRemoteId(String remoteId) {
        return getByCondition(" case_comment.remote_id=? ", remoteId);
    }

    @Override
    public boolean checkExistsByRemoteIdAndRemoteLinkId(String remoteId, Long remoteLinkId) {
        return checkExistsByCondition(" case_comment.remote_id=? and case_comment.remote_link_id=?", remoteId, remoteLinkId);
    }
}
