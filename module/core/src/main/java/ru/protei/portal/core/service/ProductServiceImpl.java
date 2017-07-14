package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.DevUnitDAO;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.query.ProductDirectionQuery;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.core.model.view.ProductShortView;

import java.util.Date;
import java.util.List;
import java.util.Set;
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

    @Autowired
    PolicyService policyService;

    @Override
    public CoreResponse<List<ProductShortView>> shortViewList(AuthToken token, ProductQuery query ) {

        List<DevUnit> list = devUnitDAO.listByQuery(query);

        if (list == null)
            new CoreResponse<List<ProductShortView>>().error(En_ResultStatus.GET_DATA_ERROR);

        List<ProductShortView> result = list.stream().map(DevUnit::toProductShortView).collect(Collectors.toList());

        return new CoreResponse<List<ProductShortView>>().success(result,result.size());
    }

    @Override
    public CoreResponse<List<DevUnit>> productList( AuthToken token, ProductQuery query ) {

        List<DevUnit> list = devUnitDAO.listByQuery(query);

        if (list == null)
            new CoreResponse<List<DevUnit>>().error(En_ResultStatus.GET_DATA_ERROR);

        return new CoreResponse<List<DevUnit>>().success(list);
    }

    @Override
    public CoreResponse<List<ProductDirectionInfo>> productDirectionList( AuthToken token, ProductDirectionQuery query ) {

        List<DevUnit> list = devUnitDAO.listByQuery(query);

        if (list == null)
            new CoreResponse<List<ProductDirectionInfo>>().error(En_ResultStatus.GET_DATA_ERROR);

        List<ProductDirectionInfo> result = list.stream().map(DevUnit::toProductDirectionInfo).collect(Collectors.toList());

        return new CoreResponse<List<ProductDirectionInfo>>().success(result,result.size());
    }

    @Override
    public CoreResponse<DevUnit> getProduct( AuthToken token, Long id ) {

        if (id == null)
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);

        DevUnit product = devUnitDAO.get(id);

        if (product == null)
            new CoreResponse().error(En_ResultStatus.NOT_FOUND);

        return new CoreResponse<DevUnit>().success(product);
    }


    @Override
    public CoreResponse<Long> createProduct( AuthToken token, DevUnit product) {

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
    public CoreResponse<Boolean> updateProduct( AuthToken token, DevUnit product ) {

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
    public CoreResponse<Boolean> checkUniqueProductByName( AuthToken token, String name, Long excludeId) {

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
    public CoreResponse<Long> count(AuthToken token, ProductQuery query) {
        return new CoreResponse<Long>().success(devUnitDAO.count(query));
    }
}
