package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.DevUnitDAO;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.jdbc.JdbcSort;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса управления продуктами
 */
public class ProductServiceImpl implements ProductService {

    /**
     *  @TODO
     *  - вынести обработку ответов БД в отдельный Interceptor
     */

    @Autowired
    DevUnitDAO devUnitDAO;

    @Override
    public CoreResponse<List<EntityOption>> productOptionList() {

        ProductQuery query = new ProductQuery();
        String condition = HelperFunc.makeLikeArg(query.getSearchString(), true);
        JdbcSort sort = TypeConverters.createSort(query);

        List<DevUnit> list = devUnitDAO.getUnitsByCondition(En_DevUnitType.PRODUCT, En_DevUnitState.ACTIVE, condition, sort);

        if (list == null)
            new CoreResponse<List<EntityOption>>().error(En_ResultStatus.GET_DATA_ERROR);

        List<EntityOption> result = list.stream().map(DevUnit::toEntityOption).collect(Collectors.toList());

        return new CoreResponse<List<EntityOption>>().success(result,result.size());
    }

    @Override
    public CoreResponse<List<DevUnit>> productList(ProductQuery query) {

        String condition = HelperFunc.makeLikeArg(query.getSearchString(), true);

        JdbcSort sort = TypeConverters.createSort(query);

        List<DevUnit> list = devUnitDAO.getUnitsByCondition(En_DevUnitType.PRODUCT, query.getState(), condition.trim(), sort);

        if (list == null)
            new CoreResponse<List<DevUnit>>().error(En_ResultStatus.GET_DATA_ERROR);

        return new CoreResponse<List<DevUnit>>().success(list);
    }

    @Override
    public CoreResponse<DevUnit> getProduct(Long id) {

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
        DevUnit product = devUnitDAO.checkExistsProductByName(name);

        return product == null || product.getId().equals(excludeId);
    }

    private <T> CoreResponse<T> createUndefinedError() {
        return new CoreResponse<T>().error(En_ResultStatus.INTERNAL_ERROR);
    }

}
