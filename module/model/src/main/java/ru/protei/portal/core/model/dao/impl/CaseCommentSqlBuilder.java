package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.query.SqlCondition;

public class CaseCommentSqlBuilder {

    public SqlCondition createSqlCondition(CaseCommentQuery query) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("1=1");

            if (query.getCaseId() != null) {
                condition.append(" and case_comment.case_id=?");
                args.add(query.getCaseId());
            }

            if (HelperFunc.isNotEmpty(query.getSearchString())) {
                condition.append(" and case_comment.comment_text like ?");
                args.add(HelperFunc.makeLikeArg(query.getSearchString(), true));
            }

            if (query.getCreatedBefore() != null) {
                condition.append(" and case_comment.created <= ?");
                args.add(query.getCreatedBefore());
            }

            if (query.isTimeElapsedNotNull() != null && query.isTimeElapsedNotNull()) {
                condition.append(" and case_comment.time_elapsed is not null");
            }
        });
    }
}
