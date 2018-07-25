package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.CaseLinkDAO;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CaseLinkQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.List;

public class CaseLinkDAO_Impl extends PortalBaseJdbcDAO<CaseLink> implements CaseLinkDAO {

    @Override
    public List<CaseLink> getListByQuery(CaseLinkQuery query) {

        if (query.getSortDir() == null) {
            query.setSortField(En_SortField.id);
            query.setSortDir(En_SortDir.ASC);
        }

        SqlCondition where = createSqlCondition(query);
        return getList(new JdbcQueryParameters()
                .withCondition(where.condition, where.args)
                .withDistinct(true)
                .withSort(TypeConverters.createSort(query))
        );
    }


    @SqlConditionBuilder
    public SqlCondition createSqlCondition(CaseLinkQuery query) {
        return new SqlCondition().build(((condition, args) -> {
            condition.append("1=1");

            if (query.getCaseId() != null) {
                condition.append(" and case_link.case_id = ?");
                args.add(query.getCaseId());
            }

            if (query.isShowOnlyPrivate()) {
                condition.append(" and link_type = 'CRM'");
                condition.append(" and (private_flag = FALSE or private_flag IS NULL)");
            }

            if (StringUtils.isNotBlank(query.getRemoteId())) {
                condition.append(" and case_link.remote_id = ?");
                args.add(query.getRemoteId());
            }
        }));
    }
}
