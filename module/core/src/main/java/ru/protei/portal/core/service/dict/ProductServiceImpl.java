package ru.protei.portal.core.service.dict;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
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

        return new HttpListResult<DevUnit>(devUnitDAO.getUnitsByCondition(En_DevUnitType.PRODUCT, query.getState(), condition.trim(), sort), false);
    }

    @Override
    public CoreResponse<DevUnit> getProductById(Long id) {

        if (id != null) {
            DevUnit product = devUnitDAO.get(id);

            if (product != null)
                return new CoreResponse<DevUnit>().success(product);
        }

        return createUndefinedError();
    }


    @Override
    public CoreResponse<Long> createProduct(DevUnit product) {

        if (product != null && product.getId() == null) {

            String name = product.getName();

            if (name != null && !name.trim().isEmpty())
            {
                product.setName(name.trim());

                Long productId = devUnitDAO.persist(product);

                if (productId != null)
                    return new CoreResponse<Long>().success(productId);
            }
        }

        return createUndefinedError();
    }

    @Override
    public CoreResponse<Boolean> updateProduct(DevUnit product) {

        if (product != null && product.getId() != null) {

            Boolean result = devUnitDAO.merge(product);
            return new CoreResponse().success(result);
        }

        return createUndefinedError();
    }

    @Override
    public CoreResponse<Boolean> isNameExist(String name, Long id) {

        return new CoreResponse().success(new Boolean(devUnitDAO.checkExistProductByName(name, id)));
    }

    private <T> CoreResponse<T> createUndefinedError() {
        return new CoreResponse<T>().error("undefined error", "internal_error");
    }
}
