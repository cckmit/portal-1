package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.exception.RollbackTransactionException;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.DeliveryQuery;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
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

    private final static DateFormat DEPARTURE_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

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
    DevUnitDAO devUnitDAO;
    @Autowired
    PersonDAO personDAO;
    @Autowired
    CaseObjectMetaNotifiersDAO caseObjectMetaNotifiersDAO;

    @Autowired
    DeliveryDAO deliveryDAO;
    @Autowired
    KitDAO kitDAO;

    @Autowired
    PolicyService policyService;

    @Autowired
    AuthService authService;
    @Autowired
    HistoryService historyService;
    @Autowired
    CaseStateDAO caseStateDAO;

    private final Pattern deliverySerialNumber = Pattern.compile(DELIVERY_KIT_SERIAL_NUMBER_PATTERN);

    @Override
    public Result<SearchResult<Delivery>> getDeliveries(AuthToken token, DeliveryQuery query) {
        SearchResult<Delivery> sr = deliveryDAO.getSearchResult(query);

        for (Delivery delivery : emptyIfNull(sr.getResults())){
            if (delivery.getProject() == null){
                continue;
            }
            delivery.getProject().setProducts(new HashSet<>(devUnitDAO.getProjectProducts(delivery.getProject().getId())));

            jdbcManyRelationsHelper.fill(delivery, "kits");
        }

        return ok(sr);
    }

    @Override
    public Result<Delivery> getDelivery(AuthToken token, Long id) {
        Delivery delivery = deliveryDAO.get(id);
        jdbcManyRelationsHelper.fill(delivery, "kits");
        jdbcManyRelationsHelper.fill(delivery, "subscribers");
        delivery.getProject().setProducts(new HashSet<>(devUnitDAO.getProjectProducts(delivery.getProject().getId())));
        return ok(delivery);
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

        long stateId = delivery.getStateId();
        Result<Long> resultState = addStateHistory(token, deliveryId, stateId, caseStateDAO.get(stateId).getState());
        if (resultState.isError()) {
            log.error("State message for the delivery {} not saved!", caseId);
        }

        Date departureDate = delivery.getDepartureDate();
        if (departureDate != null) {
            Result<Long> resultDate = addDateHistory(token, delivery.getId(), departureDate);
            if (resultDate.isError()) {
                log.error("Departure date message with oldDate={}, newDate={} for delivery {} not saved!",
                           null, departureDate, delivery.getName());
            }
        }

        return ok(deliveryDAO.get(delivery.getId()));
    }

    @Override
    @Transactional
    public Result<Long> removeDelivery(AuthToken token, Long deliveryId) {
        CaseObject caseObject = new CaseObject(deliveryId);
        caseObject.setDeleted(true);

        if (!caseObjectDAO.partialMerge(caseObject, CaseObject.Columns.DELETED)) {
            return error(En_ResultStatus.NOT_UPDATED, "Delivery was not removed");
        }

        return ok(deliveryId);
    }

    @Override
    @Transactional
    public Result<Delivery> updateMeta(AuthToken token, Delivery meta) {
        if (meta.getId() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (!isValid(meta, false)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        Delivery oldMeta = deliveryDAO.get(meta.getId());
        if (oldMeta == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        if (isForbiddenToChangeState(token, oldMeta.getStateId(), meta.getStateId())) {
            return error(En_ResultStatus.DELIVERY_FORBIDDEN_CHANGE_STATUS);
        }

        if (!Objects.equals(oldMeta.getProjectId(), meta.getProjectId())) {
            return error(En_ResultStatus.DELIVERY_FORBIDDEN_CHANGE_PROJECT);
        }

        CaseObject caseObject = caseObjectDAO.get(meta.getId());
        caseObject = createCaseObject(caseObject, meta, null, null, new Date());
        boolean isUpdated = caseObjectDAO.merge(caseObject);
        if (!isUpdated) {
            log.info("Failed to update issue meta data {} at db", caseObject.getId());
            throw new RollbackTransactionException(En_ResultStatus.NOT_UPDATED);
        }

        isUpdated = deliveryDAO.merge(meta);
        if (!isUpdated) {
            log.warn("createDelivery(): delivery not created. delivery={}",  caseObject.getId());
            throw new RollbackTransactionException(En_ResultStatus.NOT_UPDATED);
        }

        // update kits

        if (meta.getStateId() != oldMeta.getStateId()) {
            Result<Long> resultState = changeStateHistory(token, meta.getId(), oldMeta.getStateId(), caseStateDAO.get(oldMeta.getStateId()).getState(),
                                                          meta.getStateId(), caseStateDAO.get(meta.getStateId()).getState());

            if (resultState.isError()) {
                log.error("State message for the delivery {} not saved!", meta.getId());
            }
        }

        Date oldDate = oldMeta.getDepartureDate();
        Date newDate = meta.getDepartureDate();
        Result<Long> resultDate = ok();

        if (departureDateAdded(oldDate, newDate)) {
            resultDate = addDateHistory(token, meta.getId(), meta.getDepartureDate());
        }

        if (departureDateChanged(oldDate, newDate)) {
            resultDate = changeDateHistory(token, meta.getId(), oldMeta.getDepartureDate(), meta.getDepartureDate());
        }

        if (departureDateRemoved(oldDate, newDate)) {
            resultDate = removeDateHistory(token, meta.getId(), oldMeta.getDepartureDate());
        }

        if (resultDate.isError()) {
            log.error("Departure date message with oldDate={}, newDate={} for delivery {} not saved!",
                       oldDate, newDate, meta.getName());
        }

        return getDelivery(token, caseObject.getId());
    }

    @Override
    public Result<String> getLastSerialNumber(AuthToken token, boolean isArmyProject) {
        String lastSerialNumber = kitDAO.getLastSerialNumber(isArmyProject);
        if (lastSerialNumber == null) {
            lastSerialNumber = isArmyProject? "100.000" : makeFirstCivilNumberOfYear(getCurrentYear());
        } else {
            if (!isArmyProject) {
                int serialYear = Integer.parseInt(lastSerialNumber.split("\\.")[0]);
                int currentYear = getCurrentYear();
                if (serialYear < currentYear) {
                    lastSerialNumber = makeFirstCivilNumberOfYear(currentYear);
                }
            }
        }
        log.debug("getLastSerialNumber(): isArmyProject = {}, result = {}", isArmyProject, lastSerialNumber);
        return ok(lastSerialNumber);
    }

    private int getCurrentYear() {
        return (new GregorianCalendar().get(Calendar.YEAR) - 2000);
    }

    private String makeFirstCivilNumberOfYear(int year) {
        return "0" + year + ".000";
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
        } else if (isNew && En_DeliveryState.PRELIMINARY.getId() != stateId) {
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
                        || kit.getStateId() == null) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean isForbiddenToChangeState(AuthToken token, Long oldState, Long newState) {
        return Objects.equals(oldState, (long)En_DeliveryState.PRELIMINARY.getId())
                && !Objects.equals(oldState, newState)
                && !policyService.hasPrivilegeFor(En_Privilege.DELIVERY_CHANGE_PRELIMINARY_STATUS, token.getRoles());
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

    private boolean departureDateAdded(Date oldDate, Date newDate) {
        return oldDate == null && newDate != null;
    }

    private boolean departureDateChanged(Date oldDate, Date newDate) {
        return oldDate != null && newDate != null && oldDate.getTime() != newDate.getTime();
    }

    private boolean departureDateRemoved(Date oldDate, Date newDate) {
        return oldDate != null && newDate == null;
    }

    private Result<Long> addStateHistory(AuthToken authToken, Long deliveryId, Long stateId, String stateName) {
        return historyService.createHistory(authToken, deliveryId, En_HistoryAction.ADD, En_HistoryType.DELIVERY_STATE, null, null, stateId, stateName);
    }

    private Result<Long> changeStateHistory(AuthToken token, Long deliveryId, Long oldStateId, String oldStateName, Long newStateId, String newStateName) {
        return historyService.createHistory(token, deliveryId, En_HistoryAction.CHANGE, En_HistoryType.DELIVERY_STATE, oldStateId, oldStateName, newStateId, newStateName);
    }

    private Result<Long> addDateHistory(AuthToken token, Long deliveryId, Date date) {
        return historyService.createHistory(token, deliveryId, En_HistoryAction.ADD, En_HistoryType.DATE,
                                            null, null, deliveryId, DEPARTURE_DATE_FORMAT.format(date));
    }

    private Result<Long> changeDateHistory(AuthToken token, Long deliveryId, Date oldDate, Date newDate) {
        return historyService.createHistory(token, deliveryId, En_HistoryAction.CHANGE, En_HistoryType.DATE, null,
                                            oldDate != null ? DEPARTURE_DATE_FORMAT.format(oldDate) : null, deliveryId,
                                            newDate != null ? DEPARTURE_DATE_FORMAT.format(newDate) : null);
    }

    private Result<Long> removeDateHistory(AuthToken token, Long deliveryId, Date oldDate) {
        return historyService.createHistory(token, deliveryId, En_HistoryAction.REMOVE, En_HistoryType.DATE,
                                            null, DEPARTURE_DATE_FORMAT.format(oldDate), deliveryId, null);
    }
}
