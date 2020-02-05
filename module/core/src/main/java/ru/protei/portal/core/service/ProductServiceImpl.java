package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.DevUnitChildRefDAO;
import ru.protei.portal.core.model.dao.DevUnitDAO;
import ru.protei.portal.core.model.dao.ProductSubscriptionDAO;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.dto.DevUnitInfo;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.ent.DevUnitSubscription;
import ru.protei.portal.core.model.query.ProductDirectionQuery;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.core.utils.collections.CollectionUtils;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
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
    public Result<SearchResult<DevUnit>> getProducts( AuthToken token, ProductQuery query) {

        SearchResult<DevUnit> sr = devUnitDAO.getSearchResultByQuery(query);

        return ok(sr);
    }

    @Override
    public Result<List<ProductShortView>> shortViewList( AuthToken token, ProductQuery query ) {
        return makeListProductShortView( devUnitDAO.listByQuery(query) );
    }

    @Override
    public Result<List<ProductShortView>> shortViewListByIds(List<Long> ids) {
        return makeListProductShortView( devUnitDAO.getListByKeys(ids) );
    }

    @Override
    public Result<List<ProductDirectionInfo>> productDirectionList( AuthToken token, ProductDirectionQuery query ) {

        List<DevUnit> list = devUnitDAO.listByQuery(query);

        if (list == null)
            return error(En_ResultStatus.GET_DATA_ERROR);

        List<ProductDirectionInfo> result = list.stream().map(DevUnit::toProductDirectionInfo).collect(Collectors.toList());

        return ok(result);
    }

    @Override
    public Result<DevUnitInfo> getProductInfo( AuthToken authToken, Long productId ) {
        DevUnit devUnit = devUnitDAO.get( productId );
        if (devUnit == null) return error( En_ResultStatus.NOT_FOUND );
        return ok( toInfo(devUnit) );
    }

    @Override
    @Transactional
    public Result<Long> updateProductFromInfo( AuthToken authToken, DevUnitInfo product ) {
        return getProduct( authToken, product.getId() ).map( devUnit ->
                updateFields( devUnit, product ) ).flatMap( devUnit ->
                updateProduct( authToken, devUnit ) ).map(
                DevUnit::getId );
    }

    @Override
    public Result<DevUnit> getProduct( AuthToken token, Long id ) {

        if (id == null)
            return error(En_ResultStatus.INCORRECT_PARAMS);

        DevUnit product = devUnitDAO.get(id);

        if (product == null)
            return error(En_ResultStatus.NOT_FOUND);

        product = helper.fillAll( product );

        return ok(product);
    }

    @Override
    @Transactional
    public Result<DevUnit> createProduct( AuthToken token, DevUnit product) {

        if (product == null)
            return error(En_ResultStatus.INCORRECT_PARAMS);

        if (!checkUniqueProduct(product.getName(), product.getType(), product.getId()))
            return error(En_ResultStatus.ALREADY_EXIST);

        product.setCreated(new Date());
        product.setStateId(En_DevUnitState.ACTIVE.getId());

        Long productId = devUnitDAO.persist(product);

        if (productId == null)
            return error(En_ResultStatus.NOT_CREATED);

        product.setId(productId);

        updateProductSubscriptions(product.getId(), product.getSubscriptions());

        helper.persist(product, "parents");
        helper.persist(product, "children");

        return ok(product);

    }

    @Override
    @Transactional
    public Result<DevUnit> updateProduct( AuthToken token, DevUnit product ) {

        if( product == null || product.getId() == null )
            return error(En_ResultStatus.INCORRECT_PARAMS);

        if (!checkUniqueProduct(product.getName(), product.getType(), product.getId()))
            return error(En_ResultStatus.ALREADY_EXIST);

        DevUnit oldProduct = devUnitDAO.get(product.getId());

        Boolean result = devUnitDAO.merge(product);
        if ( !result )
            return error(En_ResultStatus.NOT_UPDATED);

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

        return ok(product);
    }

    @Override
    @Transactional
    public Result<En_DevUnitState> updateState( AuthToken makeAuthToken, Long productId, En_DevUnitState state) {
        if (productId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        DevUnit product = devUnitDAO.get(productId);

        if (product == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        product.setStateId(state.getId());

        if (devUnitDAO.updateState(product)) {
            return ok(state);
        } else {
            return error(En_ResultStatus.INTERNAL_ERROR);
        }
    }

    @Override
    public Result<Boolean> checkUniqueProductByName( AuthToken token, String name, En_DevUnitType type, Long excludeId) {

        if( name == null || name.isEmpty() )
            return error(En_ResultStatus.INCORRECT_PARAMS);

        return ok(checkUniqueProduct(name, type, excludeId));
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

    private DevUnit updateFields( DevUnit devUnit, DevUnitInfo product ) {
        if (product.getConfiguration() != null) devUnit.setConfiguration( product.getConfiguration() );
        if (product.getCdrDescription() != null) devUnit.setCdrDescription( product.getCdrDescription() );
        if (product.getHistoryVersion() != null) devUnit.setHistoryVersion( product.getHistoryVersion() );
        return devUnit;
    }

    private DevUnitInfo toInfo( DevUnit devUnit) {
        DevUnitInfo info = new DevUnitInfo();
        info.setId( devUnit.getId() );
        info.setConfiguration( devUnit.getConfiguration() );
        info.setCdrDescription( devUnit.getCdrDescription() );
        info.setHistoryVersion( devUnit.getHistoryVersion() );
        return info;
    }

    private Result<List<ProductShortView>> makeListProductShortView(List<DevUnit> devUnits) {
        if (devUnits == null)
            return error(En_ResultStatus.GET_DATA_ERROR);

        List<ProductShortView> result = devUnits.stream().map(DevUnit::toProductShortView).collect(Collectors.toList());

        return ok(result);
    }

    private final static Logger log = LoggerFactory.getLogger( ProductService.class );
}
