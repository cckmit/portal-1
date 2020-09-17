package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.ContactItemDAO;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.ContactItemQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.struct.ContactItem;

import static ru.protei.portal.core.model.helper.CollectionUtils.isNotEmpty;
import static ru.protei.portal.core.model.helper.StringUtils.isNotEmpty;

public class ContactItemDAO_Impl extends PortalBaseJdbcDAO<ContactItem> implements ContactItemDAO {

    @SqlConditionBuilder
    public SqlCondition createSqlCondition(ContactItemQuery query) {
        return new SqlCondition().build(((condition, args) -> {
            condition.append("1=1");

            if (isNotEmpty(query.getSearchString())) {
                condition.append(" AND value LIKE ?");
                args.add(HelperFunc.makeLikeArg(query.getSearchString(), true));
            }

            if (isNotEmpty(query.getItemTypes())) {
                condition.append(" AND item_type IN ");
                condition.append(HelperFunc.makeInArg(
                    query.getItemTypes(),
                    value -> String.valueOf(value.getId())
                ));
            }

            if (isNotEmpty(query.getAccessTypes())) {
                condition.append(" AND access_type IN ");
                condition.append(HelperFunc.makeInArg(
                    query.getAccessTypes(),
                    value -> String.valueOf(value.getId())
                ));
            }

            if (isNotEmpty(query.getPersonIds())) {
                condition.append(" AND contact_item.id IN (");
                condition.append(" SELECT cip.contact_item_id FROM contact_item_person AS cip WHERE cip.person_id IN (");
                condition.append(HelperFunc.makeInArg(
                    query.getPersonIds(),
                    String::valueOf
                ));
                condition.append(" ))");
            }

            if (isNotEmpty(query.getCompanyIds())) {
                condition.append(" AND contact_item.id IN (");
                condition.append(" SELECT cic.contact_item_id FROM contact_item_company AS cic WHERE cic.company_id IN (");
                condition.append(HelperFunc.makeInArg(
                    query.getCompanyIds(),
                    String::valueOf
                ));
                condition.append(" ))");
            }

        }));
    }
}
