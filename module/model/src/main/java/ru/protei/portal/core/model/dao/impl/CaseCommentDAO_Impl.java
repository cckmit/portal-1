package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.query.SqlCondition;

import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.getFirst;
import static ru.protei.portal.core.model.helper.CollectionUtils.isNotEmpty;
import static ru.protei.portal.core.model.helper.StringUtils.isBlank;

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

            if (isNotEmpty(query.getCaseIds())) {
                if (query.getCaseIds().size() == 1) {
                    condition.append(" and case_id=?");
                    args.add(getFirst(query.getCaseIds()));
                } else {
                    condition.append(" and case_id in ");
                    condition.append(HelperFunc.makeInArg(query.getCaseIds()));
                }
            }

            if (!isBlank(query.getSearchString())) {
                condition.append(" and comment_text like ?");
                args.add(HelperFunc.makeLikeArg(query.getSearchString(), true));
            }
        });
    }
}
