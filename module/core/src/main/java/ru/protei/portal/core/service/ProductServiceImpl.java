package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.DevUnitDAO;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.query.ProductQuery;

import java.util.Date;
import java.util.List;

/**
 * Created by michael on 27.09.16.
 */
public class ProductServiceImpl implements ProductService {

    /**
     *  @TODO
     *  - вынести обработку ответов БД в отдельный Interceptor
     */

    @Autowired
    DevUnitDAO devUnitDAO;

    @Override
    public CoreResponse<List<DevUnit>> list(ProductQuery query) {

        return new CoreResponse<List<DevUnit>>().success(devUnitDAO.listByQuery(query));
    }

    @Override
    public CoreResponse<DevUnit> getProductById(Long id) {

        if (id == null)
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);

        DevUnit product = devUnitDAO.get(id);

        if (product == null)
            new CoreResponse().error(En_ResultStatus.NOT_FOUND);

        return new CoreResponse<DevUnit>().success(product);
    }


    @Override
    public CoreResponse<Long> createProduct(DevUnit product) {

        if (product == null)
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);

        if (!checkUniqueProduct(product.getName(), product.getId()))
            return new CoreResponse().error(En_ResultStatus.ALREADY_EXIST);

        product.setCreated(new Date());
        product.setTypeId(En_DevUnitType.PRODUCT.getId());
        product.setStateId(En_DevUnitState.ACTIVE.getId());

        Long productId = devUnitDAO.persist(product);

        if (productId == null)
            new CoreResponse().error(En_ResultStatus.NOT_CREATED);

        return new CoreResponse<Long>().success(productId);

    }

    @Override
    public CoreResponse<Boolean> updateProduct(DevUnit product) {

        if( product == null || product.getId() == null )
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);

        if (!checkUniqueProduct(product.getName(), product.getId()))
            return new CoreResponse().error(En_ResultStatus.ALREADY_EXIST);

        Boolean result = devUnitDAO.merge(product);
        if ( !result )
            new CoreResponse().error(En_ResultStatus.NOT_UPDATED);

        return new CoreResponse<Boolean>().success(result);
    }

    @Override
    public CoreResponse<Boolean> checkUniqueProductByName(String name, Long excludeId) {

        if( name == null || name.isEmpty() )
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);

        return new CoreResponse<Boolean>().success(checkUniqueProduct(name, excludeId));
    }

    private boolean checkUniqueProduct (String name, Long excludeId) {
        DevUnit product = devUnitDAO.checkExistsByName(En_DevUnitType.PRODUCT, name);

        return product == null || product.getId().equals(excludeId);
    }

    private <T> CoreResponse<T> createUndefinedError() {
        return new CoreResponse<T>().error(En_ResultStatus.INTERNAL_ERROR);
    }


    @Override
    public CoreResponse<Long> count(ProductQuery query) {
        return new CoreResponse<Long>().success(devUnitDAO.count(query));
    }
}
