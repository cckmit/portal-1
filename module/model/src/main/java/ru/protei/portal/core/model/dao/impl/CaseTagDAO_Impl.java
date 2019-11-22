package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.CaseTagDAO;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CaseTagQuery;
import ru.protei.portal.core.model.query.SqlCondition;

import java.util.List;

public class CaseTagDAO_Impl extends PortalBaseJdbcDAO<CaseTag> implements CaseTagDAO {

    @Override
    public List<CaseTag> getListByQuery(CaseTagQuery query) {
        if (query.getSortDir() == null) {
            query.setSortField(En_SortField.name);
            query.setSortDir(En_SortDir.ASC);
        }
        return listByQuery(query);
    }

    @SqlConditionBuilder
    public SqlCondition createSqlCondition(CaseTagQuery query) {
        return new SqlCondition().build(((condition, args) -> {
            condition.append("1=1");

            if (StringUtils.isNotBlank(query.getName())) {
                condition.append(" and case_tag.name like ?");
                args.add(HelperFunc.makeLikeArg(query.getName(), true));
            }

            if (query.getCaseType() != null) {
                condition.append(" and case_tag.case_type = ?");
                args.add(query.getCaseType().getId());
            }

            if (query.getCompanyId() != null) {
                condition.append(" and case_tag.company_id = ?");
                args.add(query.getCompanyId());
            }

            if (CollectionUtils.isNotEmpty(query.getIds())) {
                condition.append(" and case_tag.id in ");
                args.add(HelperFunc.makeInArg(query.getIds()));
            }

            if (query.getCaseId() != null ) {
                condition.append(" and case_tag.id in (select case_object_tag.tag_id " +
                                                      "from case_object_tag " +
                                                      "where case_object_tag.case_id = ?)");
                args.add(query.getCaseId());
            }
        }));
    }
}
