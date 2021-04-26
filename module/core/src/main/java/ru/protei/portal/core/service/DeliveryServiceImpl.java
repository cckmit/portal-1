package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.DeliveryDAO;
import ru.protei.portal.core.model.dao.KitDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.core.model.query.DataQuery;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.Date;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

/**
 * Реализация сервиса управления поставками
 */
public class DeliveryServiceImpl implements DeliveryService {
    private static Logger log = LoggerFactory.getLogger(DeliveryServiceImpl.class);

    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @Autowired
    DeliveryDAO deliveryDAO;

    @Autowired
    KitDAO kitDAO;

    @Autowired
    PolicyService policyService;

    @Autowired
    AuthService authService;

    @Override
    public Result<SearchResult<Delivery>> getDeliveries(AuthToken token, DataQuery query) {
        SearchResult<Delivery> sr = deliveryDAO.getSearchResultByQuery(query);
        return ok(sr);
    }

    @Override
    @Transactional
    public Result<Delivery> createDelivery(AuthToken token, Delivery delivery) {
        if (!isValid(delivery)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }
        Date now = new Date();
        delivery.setCreated(now);
        delivery.setModified(now);
        deliveryDAO.persist(delivery);

        return ok(deliveryDAO.get(delivery.getId()));
    }

    @Override
    @Transactional
    public Result<Delivery> updateDelivery(AuthToken token, Delivery delivery) {
        if (!isValid(delivery)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }
        Date now = new Date();
        delivery.setModified(now);
        deliveryDAO.persist(delivery);

        return ok(deliveryDAO.get(delivery.getId()));
    }

    private boolean isValid(Delivery delivery) {
        return true;
    }
}
