package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.query.SqlCondition;

import java.util.List;

/**
 * Created by michael on 20.05.16.
 */
public class CaseCommentDAO_Impl extends PortalBaseJdbcDAO<CaseComment> implements CaseCommentDAO {

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
        return new SqlCondition().build((condition, args) -> {
            condition.append("1=1");

            if (query.getCaseId() != null) {
                condition.append(" and case_id=?");
                args.add(query.getCaseId());
            }

            if (HelperFunc.isNotEmpty(query.getSearchString())) {
                condition.append(" and comment_text like ?");
                args.add(HelperFunc.makeLikeArg(query.getSearchString(), true));
            }

            if (query.getCreatedBefore() != null) {
                condition.append(" and case_comment.created <= ?");
                args.add(query.getCreatedBefore());
            }
        });
    }

    @Override
    public CaseComment getByRemoteId(String remoteId) {
        return getByCondition(" remote_id=? ", remoteId);
    }

    @Override
    public boolean checkExistsByRemoteIdAndText(String remoteId, String text) {
        return checkExistsByCondition(" remote_id=? and comment_text=?", remoteId, text);
    }
}
