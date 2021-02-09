package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.struct.Interval;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.DateRangeUtils.makeInterval;

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

            if (CollectionUtils.isNotEmpty(query.getCommentTypes())) {
                condition.append(" and (");

                List<String> conditions = new ArrayList<>();

                if (query.getCommentTypes().contains(CaseCommentQuery.CommentType.CASE_STATE)) {
                    conditions.add("case_comment.cstate_id IS NOT NULL");
                }

                if (query.getCommentTypes().contains(CaseCommentQuery.CommentType.IMPORTANCE)) {
                    conditions.add("case_comment.cimp_level IS NOT NULL");
                }

                if (query.getCommentTypes().contains(CaseCommentQuery.CommentType.TIME_ELAPSED)) {
                    conditions.add("case_comment.time_elapsed IS NOT NULL");
                }

                if (query.getCommentTypes().contains(CaseCommentQuery.CommentType.TEXT)) {
                    conditions.add("case_comment.COMMENT_TEXT IS NOT NULL");
                }

                if (query.getCommentTypes().contains(CaseCommentQuery.CommentType.MANAGER)) {
                    conditions.add("case_comment.cmanager_id IS NOT NULL OR " +
                            "(case_comment.cstate_id IS NULL AND case_comment.cimp_level IS NULL " +
                                "AND case_comment.time_elapsed IS NULL AND case_comment.COMMENT_TEXT IS NULL)");
                }

                condition
                        .append(String.join(" or ", conditions))
                        .append(")");
            }

            if (CollectionUtils.isNotEmpty(query.getCaseObjectIds())) {
                condition.append(" and case_comment.case_id in (")
                        .append(query.getCaseObjectIds().stream()
                                .map(Object::toString)
                                .collect(Collectors.joining(","))
                        )
                        .append(")");
            }

            if (query.getCaseNumber() != null) {
                condition.append(" and case_comment.case_id in (SELECT id FROM case_object WHERE CASENO = ")
                        .append(query.getCaseNumber())
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

            if (query.isViewPrivate() != null) {
                condition.append(" and case_comment.privacy_type ").append(query.isViewPrivate() ? "=" : "!=").append(" 'PRIVATE' ");
            }

            if (HelperFunc.isNotEmpty(query.getRemoteId())) {
                condition.append( " and case_comment.remote_id=?" );
                args.add( query.getRemoteId() );
            }

            if (query.getCreationRange() != null) {
                Interval interval = makeInterval(query.getCreationRange());
                if (interval.from != null) {
                    condition.append( " and case_comment.created >= ?" );
                    args.add(interval.from );
                }
                if (interval.to != null) {
                    condition.append( " and case_comment.created < ?" );
                    args.add( interval.to );
                }
            }
        });
    }
}
