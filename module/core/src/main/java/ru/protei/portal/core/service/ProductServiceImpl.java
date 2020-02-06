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
import ru.protei.portal.core.model.ent.DevUnitChildRef;
import ru.protei.portal.core.model.ent.DevUnitSubscription;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.ProductDirectionQuery;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.core.model.struct.ProductDirectionInfo;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.core.utils.collections.CollectionUtils;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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

        List<DevUnit> list = devUnitDAO.listByQuery(query);

        if (list == null)
            return error(En_ResultStatus.GET_DATA_ERROR);

        List<ProductShortView> result = list.stream().map(DevUnit::toProductShortView).collect(Collectors.toList());

        return ok(result);
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

        product.setParents(devUnitDAO.getParents(id));
        product.setChildren(devUnitDAO.getChildren(id));
        product.setProductDirection(devUnitDAO.getProductDirection(id));

        return ok(product);
    }

    @Override
    @Transactional
    public Result<DevUnit> createProduct( AuthToken token, DevUnit product) {

        if (!validateFields(product)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (!checkUniqueProduct(product.getName(), product.getType(), product.getId()))
            return error(En_ResultStatus.ALREADY_EXIST);

        product.setCreated(new Date());
        product.setStateId(En_DevUnitState.ACTIVE.getId());

        Long productId = devUnitDAO.persist(product);

        if (productId == null)
            return error(En_ResultStatus.NOT_CREATED);

        product.setId(productId);

        updateProductSubscriptions(product.getId(), product.getSubscriptions());

        saveProductDirection(product);
        saveParents(product);
        saveChildren(product);

        return ok(product);

    }

    @Override
    @Transactional
    public Result<DevUnit> updateProduct( AuthToken token, DevUnit product ) {

        if (!validateFields(product) || product.getId() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        DevUnit oldProduct = devUnitDAO.get(product.getId());

        if (!Objects.equals(oldProduct.getType(), product.getType())) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (!checkUniqueProduct(product.getName(), product.getType(), product.getId())) {
            return error(En_ResultStatus.ALREADY_EXIST);
        }

        Boolean result = devUnitDAO.merge(product);
        if ( !result ) {
            return error(En_ResultStatus.NOT_UPDATED);
        }

        updateProductSubscriptions( product.getId(), product.getSubscriptions() );

        saveProductDirection(product);
        saveParents(product);
        saveChildren(product);

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

    private boolean validateFields(DevUnit product) {
        if (product == null) {
            return false;
        }

        if (product.getType() == null) {
            return false;
        }

        if (StringUtils.isBlank(product.getName())) {
            return false;
        }

        return true;
    }

    private void saveProductDirection(DevUnit product) {
        if (En_DevUnitType.COMPONENT.equals(product.getType())) {
            return;
        }

        devUnitChildRefDAO.removeProductDirection(product.getId());

        if (product.getProductDirection() == null) {
            return;
        }

        devUnitChildRefDAO.persist(new DevUnitChildRef(product.getProductDirection().getId(), product.getId()));
    }

    private void saveParents(DevUnit product) {
        devUnitChildRefDAO.removeParents(product.getId());

        if (CollectionUtils.isEmpty(product.getParents())) {
            return;
        }

        List<DevUnitChildRef> parents = product.getParents().stream().map(parent -> new DevUnitChildRef(parent.getId(), product.getId())).collect(Collectors.toList());
        devUnitChildRefDAO.persistBatch(parents);
    }

    private void saveChildren(DevUnit product) {
        devUnitChildRefDAO.removeChildren(product.getId());

        if (CollectionUtils.isEmpty(product.getChildren())) {
            return;
        }

        List<DevUnitChildRef> children = product.getChildren().stream().map(child -> new DevUnitChildRef(product.getId(), child.getId())).collect(Collectors.toList());
        devUnitChildRefDAO.persistBatch(children);
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

    private final static Logger log = LoggerFactory.getLogger( ProductService.class );
}
