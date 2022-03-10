package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.event.DeliveryCreateEvent;
import ru.protei.portal.core.event.DeliveryUpdateEvent;
import ru.protei.portal.core.exception.RollbackTransactionException;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.DeliveryQuery;
import ru.protei.portal.core.model.query.WorkerEntryQuery;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.core.utils.services.lock.LockService;
import ru.protei.winter.core.utils.services.lock.LockStrategy;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.dict.En_Privilege.DELIVERY_VIEW;
import static ru.protei.portal.core.model.dict.En_ResultStatus.INCORRECT_PARAMS;
import static ru.protei.portal.core.model.dict.En_ResultStatus.PERMISSION_DENIED;
import static ru.protei.portal.core.model.ent.Delivery.Fields.*;
import static ru.protei.portal.core.model.dto.Project.Fields.*;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.helper.StringUtils.isBlank;
import static ru.protei.portal.core.model.util.CrmConstants.Masks.DELIVERY_KIT_SERIAL_NUMBER_PATTERN;
import static ru.protei.portal.core.utils.HistoryUtils.*;

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
    WorkerEntryDAO workerEntryDAO;
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
    @Autowired
    private LockService lockService;

    private final Pattern deliverySerialNumber = Pattern.compile(DELIVERY_KIT_SERIAL_NUMBER_PATTERN);

    @Override
    public Result<SearchResult<Delivery>> getDeliveries(AuthToken token, DeliveryQuery query) {

        Set<UserRole> roles = token.getRoles();
        if (policyService.hasScopeForPrivilege( roles, DELIVERY_VIEW, En_Scope.USER )) {
            return error(PERMISSION_DENIED);
        }

        query = applyFilterByScope(token, roles, query);
        if (query == null){
            return error(INCORRECT_PARAMS);
        }

        SearchResult<Delivery> sr = deliveryDAO.getSearchResult(query);

        for (Delivery delivery : emptyIfNull(sr.getResults())){
            if (delivery.getProject() == null){
                continue;
            }
            delivery.getProject().setProducts(new HashSet<>(devUnitDAO.getProjectProducts(delivery.getProject().getId())));

            jdbcManyRelationsHelper.fill(delivery, KITS);
        }

        return ok(sr);
    }

    @Override
    public Result<Delivery> getDelivery(AuthToken token, Long id) {
        return ok(getDelivery(id));
    }

    @Override
    @Transactional
    public Result<Delivery> createDelivery(AuthToken token, Delivery delivery) {
        if (delivery == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (!isValid(delivery, true)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        if (kitDAO.isExistAnySerialNumbers(stream(delivery.getKits())
                .map(Kit::getSerialNumber).collect(Collectors.toList()))) {
            return error(En_ResultStatus.NOT_AVAILABLE_DELIVERY_KIT_SERIAL_NUMBER);
        }

        Date now = new Date();
        CaseObject deliveryCaseObject = createDeliveryCaseObject(null, delivery, token.getPersonId(), now, now);
        Long deliveryCaseId = caseObjectDAO.persist(deliveryCaseObject);
        if (deliveryCaseId == null) {
            log.warn("createDelivery(): case object not created, delivery={}", delivery);
            throw new RollbackTransactionException(En_ResultStatus.NOT_CREATED);
        }
        delivery.setId(deliveryCaseId);
        Long deliveryId = deliveryDAO.persist(delivery);
        if (deliveryId == null) {
            log.warn("createDelivery(): delivery not created, delivery={}", delivery);
            throw new RollbackTransactionException(En_ResultStatus.NOT_CREATED);
        }

        delivery.getKits().forEach(kit -> {
            CaseObject kitCaseObject = createKitCaseObject(null, kit, token.getPersonId(), now, now);
            Long kitCaseObjectId = caseObjectDAO.persist(kitCaseObject);
            if (kitCaseObjectId == null) {
                log.warn("createDelivery(): case object not created, kit={}", kit);
                throw new RollbackTransactionException(En_ResultStatus.NOT_CREATED);
            }
            kit.setId(kitCaseObjectId);
            kit.setDeliveryId(deliveryId);
            Long kitId = kitDAO.persist(kit);
            if (kitId == null) {
                log.warn("createDelivery(): kit not created, kit={}", kit);
                throw new RollbackTransactionException(En_ResultStatus.NOT_CREATED);
            }
            Result<Long> resultState = addDeliveryStateHistory(token, kitCaseObjectId, kit.getStateId(), caseStateDAO.get(kit.getStateId()).getState());
            if (resultState.isError()) {
                log.error("State for the kit {} not saved!", kitCaseObjectId);
            }
        });

        if(isNotEmpty(deliveryCaseObject.getNotifiers())){
            caseNotifierDAO.persistBatch(
                    deliveryCaseObject.getNotifiers()
                            .stream()
                            .map(person -> new CaseNotifier(deliveryCaseId, person.getId()))
                            .collect(Collectors.toList()));

            jdbcManyRelationsHelper.fill(deliveryCaseObject.getNotifiers(), Person.Fields.CONTACT_ITEMS);
        }

        long stateId = delivery.getStateId();
        Result<Long> resultState = addDeliveryStateHistory(token, deliveryId, stateId, caseStateDAO.get(stateId).getState());
        if (resultState.isError()) {
            log.error("State message for the delivery {} not saved!", deliveryCaseId);
        }

        Date departureDate = delivery.getDepartureDate();
        if (departureDate != null) {
            Result<Long> resultDate = addDateHistory(token, delivery.getId(), departureDate);
            if (resultDate.isError()) {
                log.error("Departure date message with oldDate={}, newDate={} for delivery {} not saved!",
                           null, departureDate, delivery.getName());
            }
        }

        DeliveryCreateEvent deliveryCreateEvent = new DeliveryCreateEvent(this, token.getPersonId(), delivery.getId());

        return ok(deliveryDAO.get(delivery.getId()), Collections.singletonList(deliveryCreateEvent));
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
    public Result<Void> updateKitListStates(AuthToken token, List<Long> kitsIds, Long caseStateId) {
        if (caseStateId == null){
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        List<CaseObject> objects = caseObjectDAO.getListByKeys(kitsIds);
        stream(objects).forEach(caseObject -> caseObject.setStateId(caseStateId));
        caseObjectDAO.mergeBatch(objects);
        return ok();
    }

    @Override
    @Transactional
    public Result<Delivery> updateMeta(AuthToken token, Delivery meta) {
        if (meta == null || meta.getId() == null) {
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

        oldMeta.getProject().setProducts(new HashSet<>(devUnitDAO.getProjectProducts(oldMeta.getProject().getId())));
        jdbcManyRelationsHelper.fillAll( oldMeta );

        CaseObject caseObject = caseObjectDAO.get(meta.getId());
        caseObject = createDeliveryCaseObject(caseObject, meta, null, null, new Date());
        boolean isUpdated = caseObjectDAO.merge(caseObject);
        if (!isUpdated) {
            log.info("Failed to update delivery meta data {} at db", meta);
            throw new RollbackTransactionException(En_ResultStatus.NOT_UPDATED);
        }

        isUpdated = deliveryDAO.merge(meta);
        if (!isUpdated) {
            log.warn("updateMeta(): delivery not updated. delivery={}",  meta);
            throw new RollbackTransactionException(En_ResultStatus.NOT_UPDATED);
        }

        // update kits

        if (meta.getStateId() != oldMeta.getStateId()) {
            Result<Long> resultState = changeDeliveryStateHistory(token, meta.getId(), oldMeta.getStateId(), caseStateDAO.get(oldMeta.getStateId()).getState(),
                                                          meta.getStateId(), caseStateDAO.get(meta.getStateId()).getState());

            if (resultState.isError()) {
                log.error("State message for the delivery {} not saved!", meta.getId());
            }
        }

        Date oldDate = oldMeta.getDepartureDate();
        Date newDate = meta.getDepartureDate();
        Result<Long> resultDate = ok();

        if (dateAdded(oldDate, newDate)) {
            resultDate = addDateHistory(token, meta.getId(), meta.getDepartureDate());
        }

        if (dateChanged(oldDate, newDate)) {
            resultDate = changeDateHistory(token, meta.getId(), oldMeta.getDepartureDate(), meta.getDepartureDate());
        }

        if (dateRemoved(oldDate, newDate)) {
            resultDate = removeDateHistory(token, meta.getId(), oldMeta.getDepartureDate());
        }

        if (resultDate.isError()) {
            log.error("Departure date message with oldDate={}, newDate={} for delivery {} not saved!",
                       oldDate, newDate, meta.getName());
        }

        DeliveryUpdateEvent deliveryUpdateEvent = new DeliveryUpdateEvent(this, oldMeta, meta, token.getPersonId());

        return ok(getDelivery(caseObject.getId()), Collections.singletonList(deliveryUpdateEvent));
    }

    @Override
    public Result<String> getLastSerialNumber(AuthToken token, boolean isMilitaryNumbering) {
        String lastSerialNumber = kitDAO.getLastSerialNumber(isMilitaryNumbering);
        if (lastSerialNumber == null) {
            lastSerialNumber = isMilitaryNumbering? "100.000" : makeFirstCivilNumberOfYear(getCurrentYear());
        } else {
            if (!isMilitaryNumbering) {
                int serialYear = Integer.parseInt(lastSerialNumber.split("\\.")[0]);
                int currentYear = getCurrentYear();
                if (serialYear < currentYear) {
                    lastSerialNumber = makeFirstCivilNumberOfYear(currentYear);
                }
            }
        }
        log.debug("getLastSerialNumber(): isMilitaryNumbering = {}, result = {}", isMilitaryNumbering, lastSerialNumber);
        return ok(lastSerialNumber);
    }

    @Override
    public Result<String> getLastSerialNumber(AuthToken token, Long deliveryId) {
        if (deliveryId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        String lastSerialNumber = kitDAO.getLastSerialNumber(deliveryId);
        if (lastSerialNumber == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }
        log.debug("getLastSerialNumber(): deliveryId = {}, result = {}", deliveryId, lastSerialNumber);
        return ok(lastSerialNumber);
    }

    @Override
    @Transactional
    public Result<List<Kit>> addKits(AuthToken token, List<Kit> kits, Long deliveryId) {
        if (isEmpty(kits) || deliveryId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (!kits.stream().allMatch(this::isValid)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        Delivery delivery = deliveryDAO.get(deliveryId);
        if (delivery == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        if (isInvalidKitStates(kits, delivery.getStateId())) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        if (isNotAvailableAdding(delivery.getProject())) {
            return error(En_ResultStatus.NOT_AVAILABLE);
        }

        jdbcManyRelationsHelper.fill(delivery, KITS);
        if (isKitSerialNumberNotMatchDeliveryNumber(kits, delivery.getNumber())) {
            return error(En_ResultStatus.KIT_SERIAL_NUMBER_NOT_MATCH_DELIVERY_NUMBER);
        }

        if (kitDAO.isExistAnySerialNumbers(kits.stream()
                .map(Kit::getSerialNumber).collect(Collectors.toList()))) {
            return error(En_ResultStatus.NOT_AVAILABLE_DELIVERY_KIT_SERIAL_NUMBER);
        }

        Date now = new Date();
        kits.forEach(kit -> kit.setDeliveryId(deliveryId));
        return lockService.doWithLock(Delivery.class, deliveryId, LockStrategy.TRANSACTION, TimeUnit.SECONDS, 5, () -> {

            kits.forEach(kit -> {
                CaseObject kitCaseObject = createKitCaseObject(null, kit, token.getPersonId(), now, now);
                Long kitCaseObjectId = caseObjectDAO.persist(kitCaseObject);
                if (kitCaseObjectId == null) {
                    log.warn("createKits(): case object not created, kit={}", kit);
                    throw new RollbackTransactionException(En_ResultStatus.NOT_CREATED);
                }
                kit.setId(kitCaseObjectId);
                Long kitId = kitDAO.persist(kit);
                if (kitId == null) {
                    log.warn("createKits(): kit not created, kit={}", kit);
                    throw new RollbackTransactionException(En_ResultStatus.NOT_CREATED);
                }
                createKitHistory(token, kit);
            });

            return ok(kitDAO.listByDeliveryId(deliveryId));
        });
    }

    @Override
    public Result<Kit> getKit(AuthToken token, Long kitId) {

        if (kitId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Kit kit = kitDAO.get(kitId);
        if (kit == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }
        return ok(kit);
    }

    @Override
    @Transactional
    public Result<Kit> updateKit(AuthToken token, Kit kit) {
        if (kit == null || kit.getId() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (!isValid(kit)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        Kit oldKit = kitDAO.get(kit.getId());
        if (oldKit == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        Delivery delivery = deliveryDAO.get(kit.getDeliveryId());
        if (delivery == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        if (isInvalidKitStates(listOf(kit), delivery.getStateId())) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        CaseObject caseObject = caseObjectDAO.get(kit.getId());
        caseObject = createKitCaseObject(caseObject, kit, null, null, new Date());
        boolean isUpdated = caseObjectDAO.merge(caseObject);
        if (!isUpdated) {
            log.info("Failed to update kit meta data {} at db", caseObject.getId());
            throw new RollbackTransactionException(En_ResultStatus.NOT_UPDATED);
        }

        isUpdated = kitDAO.merge(kit);
        if (!isUpdated) {
            log.warn("updateKit(): kit not updated = {}",  caseObject.getId());
            throw new RollbackTransactionException(En_ResultStatus.NOT_UPDATED);
        }

        updateKitHistory(token, kit, oldKit);

        return ok(kit);
    }

    @Override
    public Result<Long> getDeliveryStateId(AuthToken token, Long deliveryId) {
        CaseObject deliveryCaseObject = caseObjectDAO.partialGet(deliveryId, CaseObject.Columns.STATE);
        if (deliveryCaseObject == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }
        return ok(deliveryCaseObject.getStateId());
    }

    private void createKitHistory(AuthToken token, Kit kit) {

        Result<Long> resultState = addModuleStateHistory(token, kit.getId(), kit.getStateId(), caseStateDAO.get(kit.getStateId()).getState());

        if (resultState.isError()) {
            log.error("State message for the kit {} not created!", kit.getId());
        }
    }

    private DeliveryQuery applyFilterByScope(AuthToken token, Set<UserRole> roles, DeliveryQuery query) {
        if (policyService.hasGrantAccessFor(roles, En_Privilege.DELIVERY_VIEW)) {
            return query;
        }
        if (policyService.hasScopeForPrivilege( roles, DELIVERY_VIEW, En_Scope.COMPANY )) {

            List<WorkerEntry> workers = workerEntryDAO.getWorkers(new WorkerEntryQuery(token.getPersonId()));
            if (isEmpty(workers)) {
                log.error("Can't get getDeliveries. No WorkerEntry specified for personId = {}", token.getPersonId());
                return null;
            }
            query.setCreatorCompanyIds(stream(workers).map(WorkerEntry::getCompanyId).collect(Collectors.toList()));
        }
        return query;
    }

    private void updateKitHistory(AuthToken token, Kit newKit, Kit oldKit) {
        if (!Objects.equals(newKit.getStateId(), oldKit.getStateId())) {
            Result<Long> resultState = changeModuleStateHistory(token, newKit.getId(), oldKit.getStateId(), caseStateDAO.get(oldKit.getStateId()).getState(),
                    newKit.getStateId(), caseStateDAO.get(newKit.getStateId()).getState());

            if (resultState.isError()) {
                log.error("State message for the kit {} not saved!", newKit.getId());
            }
        }
    }

    private Delivery getDelivery(Long id) {
        Delivery delivery = deliveryDAO.get(id);
        jdbcManyRelationsHelper.fill(delivery, KITS);
        jdbcManyRelationsHelper.fill(delivery, SUBSCRIBERS);
        jdbcManyRelationsHelper.fill(delivery.getProject(), PROJECT_MEMBERS);
        delivery.getProject().setProducts(new HashSet<>(devUnitDAO.getProjectProducts(delivery.getProject().getId())));
        fillModulesCount(delivery.getId(), delivery.getKits());
        return delivery;
    }

    private void fillModulesCount(Long deliveryId, List<Kit> kits) {

        List<Kit> modulesGroupedByKit = kitDAO.getModulesGroupedByKit(deliveryId);

        for (Kit kit : kits){
            stream(modulesGroupedByKit)
                    .filter(o -> Objects.equals(o.getId(), kit.getId()))
                    .findAny().ifPresent(kitShort -> kit.setModulesCount(kitShort.getModulesCount()));
        }
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
        if (isNew && CrmConstants.State.PRELIMINARY != delivery.getStateId()) {
            return false;
        }
        if (delivery.getType() == null) {
            return false;
        }
        if (delivery.getProjectId() == null) {
            return false;
        }
        if (isEmpty(delivery.getKits())) {
            return false;
        }
        if (isNew && delivery.getKits().stream().anyMatch(kit -> kit.getStateId() != CrmConstants.State.PRELIMINARY)) {
            return false;
        }
        return delivery.getKits().stream().allMatch(this::isValid);
    }

    private boolean isValid(Kit kit) {
        return kit != null &&
                kit.getStateId() != null &&
                StringUtils.isNotBlank(kit.getSerialNumber()) &&
                deliverySerialNumber.matcher(kit.getSerialNumber()).matches();
    }

    private boolean isForbiddenToChangeState(AuthToken token, long oldState, long newState) {
        return oldState == CrmConstants.State.PRELIMINARY
                && oldState != newState
                && !policyService.hasPrivilegeFor(En_Privilege.DELIVERY_CHANGE_PRELIMINARY_STATUS, token.getRoles());
    }

    private boolean isNotAvailableAdding(Project project) {
        return project == null || project.getCustomerType() != En_CustomerType.MINISTRY_OF_DEFENCE;
    }

    private boolean isKitSerialNumberNotMatchDeliveryNumber(List<Kit> kits, String deliveryNumber) {
        String deliveryNumberPrefix = StringUtils.isBlank(deliveryNumber) ? null : deliveryNumber.substring(0,3);
        return !kits.stream().allMatch(kit -> Objects.equals(kit.getSerialNumber().substring(0,3), deliveryNumberPrefix));
    }

    private boolean isInvalidKitStates(List<Kit> kits, long deliveryStateId) {
        return deliveryStateId == CrmConstants.State.PRELIMINARY &&
                kits.stream().anyMatch(kit -> kit.getStateId() != CrmConstants.State.PRELIMINARY);
    }

    private CaseObject createDeliveryCaseObject(CaseObject caseObject, Delivery delivery, Long creatorId,
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

    private CaseObject createKitCaseObject(CaseObject caseObject, Kit kit, Long creatorId,
                                                Date created, Date modified) {
        if (caseObject == null){
            caseObject = new CaseObject();
            caseObject.setCaseNumber(caseTypeDAO.generateNextId(En_CaseType.KIT));
            caseObject.setType(En_CaseType.KIT);
            caseObject.setCreated(created);
            caseObject.setModified(created);
            caseObject.setCreatorId(creatorId);
        } else {
            caseObject.setModified(modified);
        }

        caseObject.setId(kit.getId());
        caseObject.setName(StringUtils.emptyIfNull(kit.getName()));
        caseObject.setStateId(kit.getStateId());

        return caseObject;
    }

    private Result<Long> addDeliveryStateHistory(AuthToken authToken, Long caseObjectId, Long stateId, String stateName) {
        return historyService.createHistory(authToken, caseObjectId, En_HistoryAction.ADD, En_HistoryType.DELIVERY_STATE, null, null, stateId, stateName);
    }

    private Result<Long> addModuleStateHistory(AuthToken authToken, Long caseObjectId, Long stateId, String stateName) {
        return historyService.createHistory(authToken, caseObjectId, En_HistoryAction.ADD, En_HistoryType.MODULE_STATE, null, null, stateId, stateName);
    }

    private Result<Long> changeDeliveryStateHistory(AuthToken token, Long caseObjectId, Long oldStateId, String oldStateName, Long newStateId, String newStateName) {
        return historyService.createHistory(token, caseObjectId, En_HistoryAction.CHANGE, En_HistoryType.DELIVERY_STATE, oldStateId, oldStateName, newStateId, newStateName);
    }

    private Result<Long> changeModuleStateHistory(AuthToken token, Long caseObjectId, Long oldStateId, String oldStateName, Long newStateId, String newStateName) {
        return historyService.createHistory(token, caseObjectId, En_HistoryAction.CHANGE, En_HistoryType.MODULE_STATE, oldStateId, oldStateName, newStateId, newStateName);
    }

    private Result<Long> addDateHistory(AuthToken token, Long deliveryId, Date date) {
        return historyService.createHistory(token, deliveryId, En_HistoryAction.ADD, En_HistoryType.DEPARTURE_DATE,
                                            null, null, deliveryId, DEPARTURE_DATE_FORMAT.format(date));
    }

    private Result<Long> changeDateHistory(AuthToken token, Long deliveryId, Date oldDate, Date newDate) {
        return historyService.createHistory(token, deliveryId, En_HistoryAction.CHANGE, En_HistoryType.DEPARTURE_DATE, null,
                                            oldDate != null ? DEPARTURE_DATE_FORMAT.format(oldDate) : null, deliveryId,
                                            newDate != null ? DEPARTURE_DATE_FORMAT.format(newDate) : null);
    }

    private Result<Long> removeDateHistory(AuthToken token, Long deliveryId, Date oldDate) {
        return historyService.createHistory(token, deliveryId, En_HistoryAction.REMOVE, En_HistoryType.DEPARTURE_DATE,
                                            null, DEPARTURE_DATE_FORMAT.format(oldDate), deliveryId, null);
    }
}
