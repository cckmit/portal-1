package ru.protei.portal.core.service.dict;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.HttpListResult;
import ru.protei.portal.core.model.dao.DevUnitDAO;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.core.utils.HelperFunc;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.jdbc.JdbcSort;

/**
 * Created by michael on 27.09.16.
 */
public class ProductServiceImpl implements ProductService {

    @Autowired
    DevUnitDAO devUnitDAO;

    @Override
    public HttpListResult<DevUnit> list(ProductQuery query) {
        String condition = HelperFunc.makeLikeArg(query.getSearchString(), true);

        JdbcSort sort = TypeConverters.createSort(query);

        return new HttpListResult<DevUnit>(devUnitDAO.getUnitsByCondition(En_DevUnitType.PRODUCT, query.getState(), condition, sort), false);
    }

}
