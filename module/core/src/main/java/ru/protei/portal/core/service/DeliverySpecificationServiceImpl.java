package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.DeliverySpecification;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.DeliverySpecificationQuery;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class DeliverySpecificationServiceImpl implements DeliverySpecificationService {
    private final List<DeliverySpecification> deliverySpecificationList = new CopyOnWriteArrayList<>();
    private long ids = 1;

    @Override
    public Result<SearchResult<DeliverySpecification>> getDeliverySpecifications(AuthToken token, DeliverySpecificationQuery query) {
        return Result.ok(new SearchResult<>(new ArrayList<>(deliverySpecificationList), deliverySpecificationList.size()));
    }

    @Override
    public Result<DeliverySpecification> getDeliverySpecification(AuthToken token, Long id) {
        Optional<DeliverySpecification> any = CollectionUtils.stream(deliverySpecificationList)
                .filter(specification -> Objects.equals(specification.getId(), id))
                .findAny();
        if (any.isPresent()) {
            return Result.ok(any.get());
        } else {
            return Result.error(En_ResultStatus.NOT_FOUND);
        }
    }

    @Override
    public Result<DeliverySpecification> createDeliverySpecification(AuthToken token, DeliverySpecification deliverySpecification) {
        synchronized (deliverySpecificationList) {
            deliverySpecification.setId(ids++);
            deliverySpecificationList.add(deliverySpecification);
        }

        return Result.ok(deliverySpecification);
    }

    @Override
    public Result<Boolean> createDeliverySpecifications(AuthToken token, List<DeliverySpecification> deliverySpecifications) {
        synchronized (deliverySpecificationList) {
            deliverySpecificationList.forEach(specification -> {
                specification.setId(ids++);
                deliverySpecificationList.add(specification);
            });
        }

        return Result.ok(true);
    }
}
