package ru.protei.portal.core.model.dao.impl;

import org.apache.commons.lang3.StringUtils;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.DocumentTypeDAO;
import ru.protei.portal.core.model.ent.DocumentType;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.DocumentTypeQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.winter.core.utils.collections.CollectionUtils;

import java.util.stream.Collectors;

public class DocumentTypeDAO_Impl extends PortalBaseJdbcDAO<DocumentType> implements DocumentTypeDAO {

    @Override
    public String makeSelectIdByCategoryQuery() {
        String tableName = getTableName();
        String idColumnName = tableName + "." + getIdColumnName();
        String categoryColumnName = tableName + ".document_category";
        return "select " + idColumnName + " from " + tableName + " where " + categoryColumnName + " = ?";
    }

    @SqlConditionBuilder
    public SqlCondition createSqlCondition(DocumentTypeQuery query) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("1=1");

            if ( !StringUtils.isEmpty( query.getSearchString() ) ) {
                condition.append( " and ( document_type.name like ? or document_type.short_name like ? )" );
                String likeArg = HelperFunc.makeLikeArg(query.getSearchString(), true);
                args.add(likeArg);
                args.add(likeArg);
            }

            if ( !CollectionUtils.isEmpty( query.getCategories() ) ) {
                condition.append(" and document_type.document_category in (" + query.getCategories().stream().map((s) -> "\'" + s + "\'").collect( Collectors.joining(",")) + ")");
            }
       });
    }

}
