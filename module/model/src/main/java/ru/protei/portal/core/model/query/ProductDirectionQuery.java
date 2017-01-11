package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;

/**
 * Запрос на список продуктовых направлений
 */
public class ProductDirectionQuery extends BaseQuery {
    public ProductDirectionQuery() {
        sortField = En_SortField.prod_name;
        sortDir = En_SortDir.ASC;
    }

    public ProductDirectionQuery( String searchString, En_SortField sortField, En_SortDir sortDir) {
        super(searchString, sortField, sortDir);
    }

    @Override
    public String toString() {
        return "ProductDirectionQuery{"+super.toString()+"}";
    }
}
