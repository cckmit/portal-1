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
import ru.protei.portal.core.service.policy.PolicyService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.helper.StringUtils.isBlank;
import static ru.protei.portal.core.utils.HistoryUtils.*;

public class ModuleServiceImpl implements ModuleService {

    private final static DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    private final static String DEFAULT_MODULE_SERIAL_NUMBER = "001";


    private static Logger log = LoggerFactory.getLogger(ModuleServiceImpl.class);

    @Autowired
    ModuleDAO moduleDAO;
    @Autowired
    PolicyService policyService;
    @Autowired
    CaseObjectDAO caseObjectDAO;
    @Autowired
    CaseTypeDAO caseTypeDAO;
    @Autowired
    HistoryService historyService;
    @Autowired
    CaseStateDAO caseStateDAO;
    @Autowired
    KitDAO kitDAO;

    @Override
    public Result<Module> getModule(AuthToken token, Long id) {
        Module module = moduleDAO.get(id);
        if (module == null) return error(En_ResultStatus.NOT_FOUND);

        return ok(module);
    }

    @Override
    @Transactional
    public Result<Module> createModule(AuthToken token, Module module) {
        if (module == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (!isValid(module)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        if (moduleDAO.isExistSerialNumber(module.getSerialNumber())) {
            return error(En_ResultStatus.NOT_AVAILABLE_MODULE_SERIAL_NUMBER);
        }

        Date now = new Date();
        CaseObject caseObject = createModuleCaseObject(null, module, token.getPersonId(), now, now);
        Long moduleCaseId = caseObjectDAO.persist(caseObject);
        if (moduleCaseId == null) {
            log.warn("createModule(): case object not created, module={}", module);
            throw new RollbackTransactionException(En_ResultStatus.NOT_CREATED);
        }

        module.setId(moduleCaseId);
        Long moduleId = moduleDAO.persist(module);
        if (moduleId == null) {
            log.warn("createModule(): module not created, module={}", module);
            throw new RollbackTransactionException(En_ResultStatus.NOT_CREATED);
        }

        updateModuleHistory(token, module, null);

        return ok(moduleDAO.get(caseObject.getId()));
    }

    @Override
    public Result<Map<Module, List<Module>>> getModulesByKitId(AuthToken token, Long kitId) {
        if (kitId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        List<Module> modules = moduleDAO.getListByKitId(kitId);
        emptyIfNull(modules).sort(Comparator.comparing(Module::getSerialNumber));

        return ok(parentToChild(modules));
    }

    @Override
    @Transactional
    public Result<Module> updateMeta(AuthToken token, Module meta) {
        if (meta == null || meta.getId() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (!isValid(meta)) {
            return error(En_ResultStatus.VALIDATION_ERROR);
        }

        Module oldMeta = moduleDAO.get(meta.getId());
        if (oldMeta == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        CaseObject caseObject = caseObjectDAO.get(meta.getId());
        caseObject = createModuleCaseObject(caseObject, meta, null, null, new Date());
        boolean isUpdated = caseObjectDAO.merge(caseObject);
        if (!isUpdated) {
            log.info("Failed to update module meta data {} at db", caseObject.getId());
            throw new RollbackTransactionException(En_ResultStatus.NOT_UPDATED);
        }

        isUpdated = moduleDAO.merge(meta);
        if (!isUpdated) {
            log.warn("updateMeta(): module not updated. module={}",  caseObject.getId());
            throw new RollbackTransactionException(En_ResultStatus.NOT_UPDATED);
        }

        updateModuleHistory(token, meta, oldMeta);

        return ok(moduleDAO.get(caseObject.getId()));
    }

    @Override
    public Result<String> generateSerialNumber(AuthToken token, Long kitId) {
        if (kitId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Kit kit = kitDAO.get(kitId);
        if (kit == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        List<String> moduleSerialNumbers = moduleDAO.getSerialNumbersByKitId(kitId);

        return makeNewSerialNumber(moduleSerialNumbers, kit.getSerialNumber());
    }

    private Result<String> makeNewSerialNumber(List<String> moduleSerialNumbers, String kitSerialNumber) {
        if (isEmpty(moduleSerialNumbers)) {
            return ok(kitSerialNumber + "." + DEFAULT_MODULE_SERIAL_NUMBER);
        }

        try {
            Integer maxExistSerialNumber = stream(moduleSerialNumbers)
                    .map(num -> Integer.parseInt(num.substring(num.lastIndexOf(".") + 1)))
                    .max(Comparator.naturalOrder())
                    .get();
            Integer newSerialNumber = maxExistSerialNumber + 1;
            return ok(kitSerialNumber + "." + String.format("%03d", newSerialNumber));
        } catch (Exception e) {
            log.warn("makeNewSerialNumber(): failed to generate module serial number", e);
            return error(En_ResultStatus.INTERNAL_ERROR);
        }
    }

    /**
     key - parent module, value - list of its submodules
     */
    private Map<Module, List<Module>> parentToChild(List<Module> modules) {
        return stream(modules)
                .filter(module -> module.getParentModuleId() == null)
                .collect(Collectors.toMap(Function.identity(), module -> modules.stream()
                                .filter(m -> Objects.equals(m.getParentModuleId(), module.getId()))
                                .collect(Collectors.toCollection(ArrayList::new)),
                        (a, b) -> a,
                        LinkedHashMap::new));
    }

    private void updateModuleHistory(AuthToken token, Module meta, Module oldMeta) {
        addStateHistory(token, meta, oldMeta);
        addBuildDateHistory(token, meta, oldMeta);
        addDepartureDateHistory(token, meta, oldMeta);
    }

    private void addStateHistory(AuthToken token, Module meta, Module oldMeta) {
        Result<Long> resultState = ok();
        if (oldMeta == null) {
            resultState = addModuleStateHistory(token, meta.getId(), meta.getStateId(), caseStateDAO.get(meta.getStateId()).getState());
        } else if (meta.getStateId() != oldMeta.getStateId()) {
            resultState = changeModuleStateHistory(token, meta.getId(), oldMeta.getStateId(), caseStateDAO.get(oldMeta.getStateId()).getState(),
                    meta.getStateId(), caseStateDAO.get(meta.getStateId()).getState());
        }
        if (resultState.isError()) {
            log.error("State message for the module {} not saved!", meta.getId());
        }
    }

    private void addDepartureDateHistory(AuthToken token, Module meta, Module oldMeta) {
        Date oldDepartureDate = oldMeta == null ? null : oldMeta.getDepartureDate();
        Date newDepartureDate = meta.getDepartureDate();
        Result<Long> resultDate = ok();

        if (dateAdded(oldDepartureDate, newDepartureDate)) {
            resultDate = addDateHistory(token, meta.getId(), meta.getDepartureDate(), En_HistoryType.DEPARTURE_DATE);
        }

        if (dateChanged(oldDepartureDate, newDepartureDate)) {
            resultDate = changeDateHistory(token, meta.getId(), oldMeta.getDepartureDate(), meta.getDepartureDate(), En_HistoryType.DEPARTURE_DATE);
        }

        if (dateRemoved(oldDepartureDate, newDepartureDate)) {
            resultDate = removeDateHistory(token, meta.getId(), oldMeta.getDepartureDate(), En_HistoryType.DEPARTURE_DATE);
        }

        if (resultDate.isError()) {
            log.error("Departure date message with oldDate={}, newDate={} for module {} not saved!",
                    oldDepartureDate, newDepartureDate, meta.getName());
        }
    }

    private void addBuildDateHistory(AuthToken token, Module meta, Module oldMeta) {
        Date oldBuildDate = oldMeta == null ? null : oldMeta.getBuildDate();
        Date newBuildDate = meta.getBuildDate();
        Result<Long> resultDate = ok();

        if (dateAdded(oldBuildDate, newBuildDate)) {
            resultDate = addDateHistory(token, meta.getId(), meta.getBuildDate(), En_HistoryType.BUILD_DATE);
        }

        if (dateChanged(oldBuildDate, newBuildDate)) {
            resultDate = changeDateHistory(token, meta.getId(), oldMeta.getBuildDate(), meta.getBuildDate(), En_HistoryType.BUILD_DATE);
        }

        if (dateRemoved(oldBuildDate, newBuildDate)) {
            resultDate = removeDateHistory(token, meta.getId(), oldMeta.getBuildDate(), En_HistoryType.BUILD_DATE);
        }

        if (resultDate.isError()) {
            log.error("Build date message with oldDate={}, newDate={} for module {} not saved!",
                    oldBuildDate, newBuildDate, meta.getName());
        }
    }

    private boolean isValid(Module module) {
        if (isBlank(module.getSerialNumber())) {
            return false;
        }
        if (isBlank(module.getName())) {
            return false;
        }
        if (module.getStateId() == 0) {
            return false;
        }
        if (isNewModule(module) && !isDateValid(module.getBuildDate())) {
            return false;
        }
        if (isNewModule(module) && !isDateValid(module.getDepartureDate())) {
            return false;
        }
        return true;
    }

    private boolean isNewModule(Module module) {
        return module.getId() == null;
    }

    private boolean isDateValid(Date date) {
        return date == null || date.getTime() > System.currentTimeMillis();
    }

    private CaseObject createModuleCaseObject(CaseObject caseObject, Module module, Long creatorId,
                                           Date created, Date modified) {
        if (caseObject == null) {
            caseObject = new CaseObject();
            caseObject.setCaseNumber(caseTypeDAO.generateNextId(En_CaseType.MODULE));
            caseObject.setType(En_CaseType.MODULE);
            caseObject.setCreated(created);
            caseObject.setModified(created);
            caseObject.setCreatorId(creatorId);
        } else {
            caseObject.setModified(modified);
        }

        caseObject.setId(module.getId());
        caseObject.setName(module.getName());
        caseObject.setStateId(module.getStateId());
        caseObject.setInfo(module.getDescription());

        return caseObject;
    }

    private Result<Long> addModuleStateHistory(AuthToken authToken, Long caseObjectId, Long stateId, String stateName) {
        return historyService.createHistory(authToken, caseObjectId, En_HistoryAction.ADD, En_HistoryType.MODULE_STATE, null, null, stateId, stateName);
    }

    private Result<Long> changeModuleStateHistory(AuthToken token, Long caseObjectId, Long oldStateId, String oldStateName, Long newStateId, String newStateName) {
        return historyService.createHistory(token, caseObjectId, En_HistoryAction.CHANGE, En_HistoryType.MODULE_STATE, oldStateId, oldStateName, newStateId, newStateName);
    }

    private Result<Long> addDateHistory(AuthToken token, Long moduleId, Date date, En_HistoryType type) {
        return historyService.createHistory(token, moduleId, En_HistoryAction.ADD, type,
                null, null, moduleId, DATE_FORMAT.format(date));
    }

    private Result<Long> changeDateHistory(AuthToken token, Long moduleId, Date oldDate, Date newDate, En_HistoryType type) {
        return historyService.createHistory(token, moduleId, En_HistoryAction.CHANGE, type, null,
                oldDate != null ? DATE_FORMAT.format(oldDate) : null, moduleId,
                newDate != null ? DATE_FORMAT.format(newDate) : null);
    }

    private Result<Long> removeDateHistory(AuthToken token, Long moduleId, Date oldDate, En_HistoryType type) {
        return historyService.createHistory(token, moduleId, En_HistoryAction.REMOVE, type,
                null, DATE_FORMAT.format(oldDate), moduleId, null);
    }
}
