package ru.protei.portal.core.service.dict;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.api.struct.HttpListResult;
import ru.protei.portal.core.model.dao.DevUnitDAO;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.core.utils.HelperFunc;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.jdbc.JdbcSort;

import java.util.Date;

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

        return new HttpListResult<DevUnit>(devUnitDAO.getUnitsByCondition(En_DevUnitType.PRODUCT, query.getState(), condition.trim(), sort), false);
    }

    @Override
    public CoreResponse<DevUnit> getProductById(Long id) {

        if (id == null)
            return new CoreResponse().error("getByIdError", "undefined id");

        DevUnit product = devUnitDAO.get(id);

        if (product == null)
            new CoreResponse().error("getByIdError", "object not found");

        return new CoreResponse<DevUnit>().success(product);
    }


    @Override
    public CoreResponse<Long> createProduct(DevUnit product) {

        if (product == null)
            return new CoreResponse().error("createError", "undefined object");

        if (!checkUniqueProduct(product.getName(), product.getId()))
            return new CoreResponse().error("createError", "not unique object");

        product.setCreated(new Date());
        product.setTypeId(En_DevUnitType.PRODUCT.getId());
        product.setStateId(En_DevUnitState.ACTIVE.getId());

        Long productId = devUnitDAO.persist(product);

        if (productId == null)
            new CoreResponse().error("createError", null);

        return new CoreResponse<Long>().success(productId);

    }

    @Override
    public CoreResponse<Boolean> updateProduct(DevUnit product) {

        if( product == null || product.getId() == null )
            return new CoreResponse().error("updateError", "undefined object");

        if (!checkUniqueProduct(product.getName(), product.getId()))
            return new CoreResponse().error("updateError", "not unique object");

        Boolean result = devUnitDAO.merge(product);
        if ( !result )
            new CoreResponse().error("updateError", null);

        return new CoreResponse<Boolean>().success(result);
    }

    @Override
    public CoreResponse<Boolean> checkUniqueProductByName(String name, Long excludeId) {

        if( name == null || name.isEmpty() )
            return new CoreResponse().error("checkExistError", "undefined parameter");

        return new CoreResponse<Boolean>().success(checkUniqueProduct(name, excludeId));
    }

    private boolean checkUniqueProduct (String name, Long excludeId) {
        DevUnit product = devUnitDAO.checkExistsProductByName(name);

        return product == null || product.getId().equals(excludeId);
    }

    private <T> CoreResponse<T> createUndefinedError() {
        return new CoreResponse<T>().error("undefined error", "internal_error");
    }

}
