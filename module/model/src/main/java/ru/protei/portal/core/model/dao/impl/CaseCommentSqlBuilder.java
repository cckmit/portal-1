package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.query.SqlCondition;

import java.util.stream.Collectors;

public class CaseCommentSqlBuilder {

    public SqlCondition createSqlCondition(CaseCommentQuery query) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("1=1");

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

            if (query.isCaseStateNotNull() != null && query.isCaseStateNotNull()) {
                condition.append(" and case_comment.cstate_id is not null");
            }

            if (CollectionUtils.isNotEmpty(query.getCaseObjectIds())) {
                condition.append(" and case_comment.case_id in (")
                        .append(query.getCaseObjectIds().stream()
                                .map(Object::toString)
                                .collect(Collectors.joining(","))
                        )
                        .append(")");
            }

            if (CollectionUtils.isNotEmpty(query.getAuthorIds())) {
                condition.append(" and case_comment.author_id in (")
                        .append(query.getAuthorIds().stream()
                                .map(Object::toString)
                                .collect(Collectors.joining(","))
                        )
                        .append(")");
            }

            if ( !query.isAllowViewPrivate() ) {
                condition.append( " and case_comment.private_flag=?" );
                args.add( 0 );
            } else if (query.isViewPrivate() != null) {
                condition.append( " and case_comment.private_flag=?" );
                args.add( query.isViewPrivate() ? 1 : 0 );
            }
        });
    }
}
