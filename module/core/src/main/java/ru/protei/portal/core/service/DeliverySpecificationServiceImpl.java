package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.DeliverySpecification;
import ru.protei.portal.core.model.query.DeliverySpecificationQuery;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.List;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.isNotEmpty;

public class DeliverySpecificationServiceImpl implements DeliverySpecificationService {
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @Autowired
    DeliverySpecificationDAO deliverySpecificationDAO;
    @Autowired
    DeliveryNestedSpecificationDAO deliveryNestedSpecificationDAO;
    @Autowired
    DeliverySpecificationModificationDAO deliverySpecificationModificationDAO;
    @Autowired
    DeliveryDetailToSpecificationDAO deliveryDetailToSpecificationDAO;
    @Autowired
    DeliveryDetailModificationDAO deliveryDetailModificationDAO;

    @Override
    public Result<SearchResult<DeliverySpecification>> getDeliverySpecifications(AuthToken token, DeliverySpecificationQuery query) {
        if (query == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        return ok(deliverySpecificationDAO.getSearchResultByQuery(query));
    }

    @Override
    public Result<DeliverySpecification> getDeliverySpecification(AuthToken token, Long id) {
        if (id == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        DeliverySpecification deliverySpecification = deliverySpecificationDAO.get(id);
        if (deliverySpecification == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        jdbcManyRelationsHelper.fillAll(deliverySpecification);
        if (isNotEmpty(deliverySpecification.getSpecifications())) {
            jdbcManyRelationsHelper.fillAll(deliverySpecification.getSpecifications());
        }
        if (isNotEmpty(deliverySpecification.getDetails())) {
            jdbcManyRelationsHelper.fillAll(deliverySpecification.getDetails());
        }

        return ok(deliverySpecification);
    }

    @Override
    @Transactional
    public Result<DeliverySpecification> createDeliverySpecification(AuthToken token, DeliverySpecification deliverySpecification) {
        if (deliverySpecification == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        return ok(createDeliverySpecification(deliverySpecification));
    }

    @Override
    @Transactional
    public Result<Boolean> createDeliverySpecifications(AuthToken token, List<DeliverySpecification> deliverySpecifications) {
        if (deliverySpecifications == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        deliverySpecifications.forEach(this::createDeliverySpecification);

        return ok(true);
    }

    public DeliverySpecification createDeliverySpecification(DeliverySpecification deliverySpecification) {
        deliverySpecificationDAO.persist(deliverySpecification);

        if (isNotEmpty(deliverySpecification.getSpecifications())) {
            deliverySpecification.getSpecifications()
                    .forEach(specification -> specification.setSpecificationId(deliverySpecification.getId()));
            deliveryNestedSpecificationDAO.persistBatch(deliverySpecification.getSpecifications());
            deliverySpecification.getSpecifications()
                    .forEach(specification -> {
                        if (isNotEmpty(specification.getModifications())) {
                            specification.getModifications().forEach(modification ->
                                    modification.setSpecificationToSpecificationId(specification.getId())
                            );
                            deliverySpecificationModificationDAO.persistBatch(specification.getModifications());
                        }
                    });
        }

        if (isNotEmpty(deliverySpecification.getDetails())) {
            deliverySpecification.getDetails()
                    .forEach(detail -> detail.setSpecificationId(deliverySpecification.getId()));
            deliveryDetailToSpecificationDAO.persistBatch(deliverySpecification.getDetails());
            deliverySpecification.getDetails()
                    .forEach(detail -> {
                        if (isNotEmpty(detail.getModifications())) {
                            detail.getModifications().forEach(modification ->
                                    modification.setDetailToSpecificationId(detail.getId())
                            );
                            deliveryDetailModificationDAO.persistBatch(detail.getModifications());
                        }
                    });
        }
        return deliverySpecification;
    }
}
