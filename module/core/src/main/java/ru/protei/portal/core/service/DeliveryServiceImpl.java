package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.exception.RollbackTransactionException;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_DeliveryAttribute;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.DataQuery;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.helper.StringUtils.isBlank;
import static ru.protei.portal.core.model.util.CrmConstants.Masks.DELIVERY_KIT_SERIAL_NUMBER_PATTERN;

/**
 * Реализация сервиса управления поставками
 */
public class DeliveryServiceImpl implements DeliveryService {
    private static Logger log = LoggerFactory.getLogger(DeliveryServiceImpl.class);

    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @Autowired
    CaseTypeDAO caseTypeDAO;
    @Autowired
    CaseObjectDAO caseObjectDAO;
    @Autowired
    CaseNotifierDAO caseNotifierDAO;

    @Autowired
    DeliveryDAO deliveryDAO;

    @Autowired
    KitDAO kitDAO;

    @Autowired
    PolicyService policyService;

    @Autowired
    AuthService authService;

    private Pattern deliverySerialNumber = Pattern.compile(DELIVERY_KIT_SERIAL_NUMBER_PATTERN);

    @Override
    public Result<SearchResult<Delivery>> getDeliveries(AuthToken token, DataQuery query) {
        SearchResult<Delivery> sr = deliveryDAO.getSearchResultByQuery(query);
        return ok(sr);
    }

    @Override
    @Transactional
    public Result<Delivery> createDelivery(AuthToken token, Delivery delivery) {
        if (!isValid(delivery, true)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        if (!kitDAO.isAvailableSerialNumbers(stream(delivery.getKits())
                .map(Kit::getSerialNumber).collect(Collectors.toList()))) {
            return error(En_ResultStatus.DELIVERY_KIT_SERIAL_NUMBER_NOT_AVAILABLE);
        }

        Date now = new Date();
        CaseObject caseObject = createCaseObject(null, delivery, token.getPersonId(), now, now);
        Long caseId = caseObjectDAO.persist(caseObject);
        if (caseId == null) {
            log.warn("createDelivery(): caseObject not created. delivery={}", caseId);
            throw new RollbackTransactionException(En_ResultStatus.NOT_CREATED);
        }
        delivery.setId(caseId);
        Long deliveryId = deliveryDAO.persist(delivery);
        if (deliveryId == null) {
            log.warn("createDelivery(): delivery not created. delivery={}", deliveryId);
            throw new RollbackTransactionException(En_ResultStatus.NOT_CREATED);
        }

        stream(delivery.getKits()).forEach(kit -> {
            kit.setCreated(now);
            kit.setModified(now);
        });

        jdbcManyRelationsHelper.persist(delivery, "kits");

        if(isNotEmpty(caseObject.getNotifiers())){
            caseNotifierDAO.persistBatch(
                    caseObject.getNotifiers()
                            .stream()
                            .map(person -> new CaseNotifier(caseId, person.getId()))
                            .collect(Collectors.toList()));

            jdbcManyRelationsHelper.fill(caseObject.getNotifiers(), Person.Fields.CONTACT_ITEMS);
        }

        return ok(deliveryDAO.get(delivery.getId()));
    }

    @Override
    @Transactional
    public Result<Delivery> updateDelivery(AuthToken token, Delivery delivery) {
        if (!isValid(delivery, false)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }
        Date now = new Date();
        delivery.setModified(now);
        deliveryDAO.persist(delivery);

        return ok(deliveryDAO.get(delivery.getId()));
    }

    @Override
    public Result<String> getLastSerialNumber(AuthToken token, boolean isArmyProject) {
        String nextAvailableSerialNumber = kitDAO.getLastSerialNumber(isArmyProject);
        if (nextAvailableSerialNumber == null) {
            return ok(isArmyProject? "100.001" :
                    "0" + (new GregorianCalendar().get(Calendar.YEAR) - 2000) + ".001");
        }
        return ok(nextAvailableSerialNumber);
    }

    private boolean isValid(Delivery delivery, boolean isNew) {
        if (isNew && delivery.getId() != null) {
            return false;
        }
        if (isBlank(delivery.getName())) {
            return false;
        }
        Long stateId = delivery.getStateId();
        if (stateId == null) {
            return false;
        } else if (isNew && 39L != stateId) {
            return false;
        }
        if (delivery.getType() == null) {
            return false;
        }
        if (delivery.getProjectId() == null) {
            return false;
        }
        En_DeliveryAttribute attribute = delivery.getAttribute();
        if (En_DeliveryAttribute.DELIVERY == attribute && delivery.getContractId() == null) {
            return false;
        }

        if (isEmpty(delivery.getKits())) {
            return false;
        } else {
            for (Kit kit : delivery.getKits()) {
                if (StringUtils.isEmpty(kit.getSerialNumber())
                        || !deliverySerialNumber.matcher(kit.getSerialNumber()).matches()
                        || kit.getState() == null) {
                    return false;
                }
            }
        }

        return true;
    }

    private CaseObject createCaseObject(CaseObject caseObject, Delivery delivery, Long creatorId,
                                        Date created, Date modified) {
        if (caseObject == null){
            caseObject = new CaseObject();
            caseObject.setCaseNumber(caseTypeDAO.generateNextId(En_CaseType.DELIVERY));
            caseObject.setType(En_CaseType.DELIVERY);
            caseObject.setCreated(created);
            caseObject.setModified(created);
            caseObject.setCreatorId(creatorId);
        } else {
            caseObject.setModified(modified);
        }

        caseObject.setId(delivery.getId());
        caseObject.setName(delivery.getName());
        caseObject.setInfo(delivery.getDescription());
        caseObject.setStateId(delivery.getStateId());
        caseObject.setInitiatorId(delivery.getInitiatorId());
        caseObject.setNotifiers(delivery.getSubscribers());

        return caseObject;
    }
}
