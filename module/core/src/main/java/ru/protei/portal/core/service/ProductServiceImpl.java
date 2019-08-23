package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.DevUnitChildRefDAO;
import ru.protei.portal.core.model.dao.DevUnitDAO;
import ru.protei.portal.core.model.dao.ProductSubscriptionDAO;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.ent.DevUnitSubscription;
import ru.protei.portal.core.model.query.ProductDirectionQuery;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.core.utils.collections.CollectionUtils;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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
    DevUnitChildRefDAO devUnitChildRefDAO;

    @Autowired
    PolicyService policyService;

    @Autowired
    JdbcManyRelationsHelper helper;

    @Autowired
    ProductSubscriptionDAO productSubscriptionDAO;

    @Override
    public CoreResponse<SearchResult<DevUnit>> getProducts(AuthToken token, ProductQuery query) {

        SearchResult<DevUnit> sr = devUnitDAO.getSearchResultByQuery(query);

        return new CoreResponse<SearchResult<DevUnit>>().success(sr);
    }

    @Override
    public CoreResponse<List<ProductShortView>> shortViewList(AuthToken token, ProductQuery query ) {

        List<DevUnit> list = devUnitDAO.listByQuery(query);

        if (list == null)
            return new CoreResponse<List<ProductShortView>>().error(En_ResultStatus.GET_DATA_ERROR);

        List<ProductShortView> result = list.stream().map(DevUnit::toProductShortView).collect(Collectors.toList());

        return new CoreResponse<List<ProductShortView>>().success(result,result.size());
    }

    @Override
    public CoreResponse<List<ProductDirectionInfo>> productDirectionList( AuthToken token, ProductDirectionQuery query ) {

        List<DevUnit> list = devUnitDAO.listByQuery(query);

        if (list == null)
            return new CoreResponse<List<ProductDirectionInfo>>().error(En_ResultStatus.GET_DATA_ERROR);

        List<ProductDirectionInfo> result = list.stream().map(DevUnit::toProductDirectionInfo).collect(Collectors.toList());

        return new CoreResponse<List<ProductDirectionInfo>>().success(result,result.size());
    }

    @Override
    public CoreResponse<DevUnit> getProduct( AuthToken token, Long id ) {

        if (id == null)
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);

        DevUnit product = devUnitDAO.get(id);

        if (product == null)
            return new CoreResponse().error(En_ResultStatus.NOT_FOUND);

        product = helper.fillAll( product );

        return new CoreResponse<DevUnit>().success(product);
    }

    @Override
    @Transactional
    public CoreResponse<Long> createProduct( AuthToken token, DevUnit product) {

        if (product == null)
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);

        if (!checkUniqueProduct(product.getName(), product.getType(), product.getId()))
            return new CoreResponse().error(En_ResultStatus.ALREADY_EXIST);

        product.setCreated(new Date());
        product.setStateId(En_DevUnitState.ACTIVE.getId());

        Long productId = devUnitDAO.persist(product);

        if (productId == null)
            return new CoreResponse().error(En_ResultStatus.NOT_CREATED);

        product.setId(productId);

        updateProductSubscriptions( product.getId(), product.getSubscriptions() );

        helper.persist(product, "parents");
        helper.persist(product, "children");

        return new CoreResponse<Long>().success(productId);

    }

    @Override
    @Transactional
    public CoreResponse<Boolean> updateProduct( AuthToken token, DevUnit product ) {

        if( product == null || product.getId() == null )
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);

        if (!checkUniqueProduct(product.getName(), product.getType(), product.getId()))
            return new CoreResponse().error(En_ResultStatus.ALREADY_EXIST);

        DevUnit oldProduct = devUnitDAO.get(product.getId());

        Boolean result = devUnitDAO.merge(product);
        if ( !result )
            return new CoreResponse().error(En_ResultStatus.NOT_UPDATED);

        updateProductSubscriptions( product.getId(), product.getSubscriptions() );

        if (!Objects.equals(oldProduct.getType(), product.getType())) {
            if (product.isProduct()) {
                devUnitChildRefDAO.removeByChildId(product.getId());
                product.setParents(null);
            } else {
                devUnitChildRefDAO.removeByParentId(product.getId());
            }
        }
        helper.persist(product, "parents");
        helper.persist(product, "children");

        return new CoreResponse<Boolean>().success(result);
    }

    @Override
    @Transactional
    public CoreResponse<En_DevUnitState> updateState(AuthToken makeAuthToken, Long productId, En_DevUnitState state) {
        if (productId == null) {
            return new CoreResponse<En_DevUnitState>().error(En_ResultStatus.INCORRECT_PARAMS);
        }

        DevUnit product = devUnitDAO.get(productId);

        if (product == null) {
            return new CoreResponse<En_DevUnitState>().error(En_ResultStatus.NOT_FOUND);
        }

        product.setStateId(state.getId());

        if (devUnitDAO.updateState(product)) {
            return new CoreResponse<En_DevUnitState>().success(state);
        } else {
            return new CoreResponse<En_DevUnitState>().error(En_ResultStatus.INTERNAL_ERROR);
        }
    }

    @Override
    public CoreResponse<Boolean> checkUniqueProductByName( AuthToken token, String name, En_DevUnitType type, Long excludeId) {

        if( name == null || name.isEmpty() )
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);

        return new CoreResponse<Boolean>().success(checkUniqueProduct(name, type, excludeId));
    }

    private boolean checkUniqueProduct (String name, En_DevUnitType type, Long excludeId) {
        DevUnit product = devUnitDAO.checkExistsByName(type, name);

        return product == null || product.getId().equals(excludeId);
    }

    private boolean updateProductSubscriptions( Long devUnitId, List<DevUnitSubscription> devUnitSubscriptions ) {
        log.info( "binding update to linked product subscription for devUnitId = {}", devUnitId );

        List<Long> toRemoveNumberIds = productSubscriptionDAO.listIdsByDevUnitId( devUnitId );
        if ( CollectionUtils.isEmpty(devUnitSubscriptions) && CollectionUtils.isEmpty(toRemoveNumberIds) ) {
            return true;
        }

        List<DevUnitSubscription> newSubscriptions = new ArrayList<>();
        List<DevUnitSubscription> oldSubscriptions = new ArrayList<>();
        devUnitSubscriptions.forEach( subscription -> {
            if ( subscription.getId() == null ) {
                subscription.setDevUnitId( devUnitId );
                newSubscriptions.add( subscription );
            } else {
                oldSubscriptions.add( subscription );
            }
        } );

        toRemoveNumberIds.removeAll( oldSubscriptions.stream().map(DevUnitSubscription::getId).collect( Collectors.toList() ) );
        if ( !CollectionUtils.isEmpty( toRemoveNumberIds ) ) {
            log.info( "remove devunit subscriptions = {}", toRemoveNumberIds );
            int countRemoved = productSubscriptionDAO.removeByKeys( toRemoveNumberIds );
            if ( countRemoved != toRemoveNumberIds.size() ) {
                return false;
            }
        }

        if ( !CollectionUtils.isEmpty( newSubscriptions ) ) {
            log.info( "persist product subscriptions = {}", newSubscriptions );
            productSubscriptionDAO.persistBatch( newSubscriptions );
        }

        if ( !CollectionUtils.isEmpty( oldSubscriptions ) ) {
            log.info( "merge product subscriptions = {}cre", oldSubscriptions );
            int countMerged = productSubscriptionDAO.mergeBatch( oldSubscriptions );
            if ( countMerged != oldSubscriptions.size() ) {
                return false;
            }
        }

        return true;
    }

    private final static Logger log = LoggerFactory.getLogger( ProductService.class );
}
