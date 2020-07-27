package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.exception.ResultStatusException;
import ru.protei.portal.core.model.dao.CompanyDepartmentDAO;
import ru.protei.portal.core.model.dao.EducationEntryAttendanceDAO;
import ru.protei.portal.core.model.dao.EducationEntryDAO;
import ru.protei.portal.core.model.dao.EducationWalletDAO;
import ru.protei.portal.core.model.dao.WorkerEntryDAO;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.dict.En_Scope;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.emptyIfNull;
import static ru.protei.portal.core.model.helper.CollectionUtils.not;
import static ru.protei.portal.core.model.helper.CollectionUtils.stream;
import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;

public class EducationServiceImpl implements EducationService {

    @Override
    public Result<List<EducationWallet>> getAllWallets(AuthToken token) {
        List<EducationWallet> wallets = emptyIfNull(educationWalletDAO.getAll());
        for (EducationWallet wallet : wallets) {
            List<Long> depIds = getDepartmentGraph(wallet.getDepartmentId());
            List<EducationEntry> educationEntryList = educationEntryDAO.getForWallet(depIds, new Date());
            wallet.setEducationEntryList(emptyIfNull(educationEntryList));
        }
        return ok(wallets);
    }

    @Override
    public Result<List<EducationEntry>> getCurrentEntries(AuthToken token) {
        List<EducationEntry> entries = emptyIfNull(educationEntryDAO.getAllForDate(new Date()));
        jdbcManyRelationsHelper.fillAll(entries);
        return ok(entries);
    }

    @Override
    @Transactional
    public Result<EducationEntry> requestNewEntry(AuthToken token, EducationEntry entry, List<Long> workerIds) {

        if (CollectionUtils.isEmpty(workerIds)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        Result<EducationEntry> validation = validateEducationEntry(entry);
        if (validation.isError()) {
            return error(validation.getStatus());
        }

        Map<Long, EducationWallet> worker2wallet = new HashMap<>();
        for (Long workerId : workerIds) {
            EducationWallet wallet = selectWalletForWorker(workerId).getData();
            if (wallet == null) {
                return error(En_ResultStatus.PERMISSION_DENIED);
            }
            worker2wallet.put(workerId, wallet);
        }

        for (EducationWallet wallet : worker2wallet.values()) {
            if (wallet.getCoins() == null || wallet.getCoins() < 0) {
                return error(En_ResultStatus.NOT_AVAILABLE);
            }
            if (isQuotaExceededForDepartment(wallet.getDepartmentId())) {
                return error(En_ResultStatus.NOT_AVAILABLE);
            }
        }

        entry.setId(educationEntryDAO.persist(entry));

        educationEntryAttendanceDAO.persistBatch(stream(workerIds)
                .map(workerId -> {
                    EducationEntryAttendance attendance = new EducationEntryAttendance();
                    attendance.setEducationEntryId(entry.getId());
                    attendance.setWorkerId(workerId);
                    attendance.setApproved(false);
                    attendance.setDateRequested(new Date());
                    return attendance;
                })
                .collect(Collectors.toList()));

        return ok(entry);
    }

    @Override
    @Transactional
    public Result<EducationEntryAttendance> requestNewAttendance(AuthToken token, Long educationEntryId, Long personId) {

        WorkerEntry workerEntry = workerEntryDAO.getByPersonId(personId);
        if (workerEntry == null) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        Result<EducationWallet> walletResult = selectWalletForWorker(workerEntry.getId());
        if (walletResult.isError()) {
            return error(walletResult.getStatus());
        }
        EducationWallet wallet = walletResult.getData();
        if (wallet.getCoins() == null || wallet.getCoins() < 0) {
            return error(En_ResultStatus.NOT_AVAILABLE);
        }
        if (isQuotaExceededForDepartment(wallet.getDepartmentId())) {
            return error(En_ResultStatus.NOT_AVAILABLE);
        }

        EducationEntry entry = educationEntryDAO.get(educationEntryId);
        if (entry == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        try {
            EducationEntryAttendance attendance = new EducationEntryAttendance();
            attendance.setEducationEntryId(educationEntryId);
            attendance.setWorkerId(workerEntry.getId());
            attendance.setApproved(false);
            attendance.setDateRequested(new Date());
            attendance.setId(educationEntryAttendanceDAO.persist(attendance));
            return ok(attendance);
        } catch (DuplicateKeyException e) {
            return error(En_ResultStatus.ALREADY_EXIST);
        }
    }

    @Override
    public Result<SearchResult<EducationEntry>> adminGetEntries(AuthToken token, int offset, int limit, boolean showOnlyNotApproved, boolean showOutdated) {

        boolean hasAccess = isAdmin(token);
        if (!hasAccess) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        SearchResult<EducationEntry> entries = educationEntryDAO.getResultForDate(
                offset,
                limit,
                showOutdated ? null : new Date(),
                showOnlyNotApproved ? false : null
        );

        jdbcManyRelationsHelper.fillAll(entries.getResults());

        return ok(entries);
    }

    @Override
    @Transactional
    public Result<EducationEntry> adminSaveEntryAndAttendance(AuthToken token, EducationEntry entry, Map<Long, Boolean> worker2approve) {

        boolean hasAccess = isAdmin(token);
        if (!hasAccess) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        if (entry.getId() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        Result<EducationEntry> validation = validateEducationEntry(entry);
        if (validation.isError()) {
            return error(validation.getStatus());
        }

        EducationEntry oldEntry = educationEntryDAO.get(entry.getId());
        if (oldEntry == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        if (!educationEntryDAO.merge(entry)) {
            return error(En_ResultStatus.NOT_UPDATED);
        }

        boolean isCostModifyAction = !Objects.equals(oldEntry.getCoins(), entry.getCoins());
        if (isCostModifyAction) {
            reChargeWalletsForCostModification(entry.getId(), oldEntry.getCoins(), entry.getCoins());
        }

        List<Long> workersApproved = stream(worker2approve.entrySet())
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(workersApproved)) {
            chargeWalletsForAttendances(entry.getId(), workersApproved, entry.getCoins());
        }

        List<Long> workersDeclined = stream(worker2approve.entrySet())
                .filter(not(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(workersDeclined)) {
            removeAttendances(entry.getId(), workersDeclined, entry.getCoins());
        }

        return ok(entry);
    }

    private void reChargeWalletsForCostModification(Long entryId, int oldCoins, int newCoins) throws ResultStatusException {
        List<EducationEntryAttendance> attendanceList = stream(educationEntryAttendanceDAO.getAllForEntry(entryId))
                .filter(EducationEntryAttendance::isApproved)
                .collect(Collectors.toList());
        for (EducationEntryAttendance attendance : attendanceList) {
            int delta = oldCoins - newCoins;
            EducationWallet wallet = selectWalletForWorker(attendance.getWorkerId()).getData();
            if (wallet == null) {
                log.warn("Failed to find wallet for attendance : {}", attendance);
                throw new ResultStatusException(En_ResultStatus.NOT_UPDATED);
            }
            wallet.setCoins(wallet.getCoins() + delta);
            if (!educationWalletDAO.partialMerge(wallet, "coins")) {
                log.warn("Failed to modify wallet coins by delta {} for attendance : {}", delta, attendance);
                throw new ResultStatusException(En_ResultStatus.NOT_UPDATED);
            }
        }
    }

    private void chargeWalletsForAttendances(Long entryId, List<Long> workerIds, int coins) throws ResultStatusException {
        List<EducationEntryAttendance> attendanceList = educationEntryAttendanceDAO.getAllForEntryAndWorkers(entryId, workerIds);
        for (EducationEntryAttendance attendance : attendanceList) {
            if (attendance.isApproved()) {
                log.warn("Attendance already approved, skipping : {}", attendance);
                continue;
            }
            EducationWallet wallet = selectWalletForWorker(attendance.getWorkerId()).getData();
            if (wallet == null) {
                log.warn("Failed to find wallet for attendance : {}", attendance);
                throw new ResultStatusException(En_ResultStatus.NOT_UPDATED);
            }
            wallet.setCoins(wallet.getCoins() - coins);
            if (!educationWalletDAO.partialMerge(wallet, "coins")) {
                log.warn("Failed to charge wallet for attendance : {}", attendance);
                throw new ResultStatusException(En_ResultStatus.NOT_UPDATED);
            }
            attendance.setApproved(true);
            if (!educationEntryAttendanceDAO.partialMerge(attendance, "approved")) {
                log.warn("Failed to set approved flag for attendance : {}", attendance);
                throw new ResultStatusException(En_ResultStatus.NOT_UPDATED);
            }
        }
    }

    private void removeAttendances(Long entryId, List<Long> workerIds, int coins) throws ResultStatusException {
        List<EducationEntryAttendance> attendanceList = educationEntryAttendanceDAO.getAllForEntryAndWorkers(entryId, workerIds);
        for (EducationEntryAttendance attendance : attendanceList) {
            if (attendance.isApproved()) {
                EducationWallet wallet = selectWalletForWorker(attendance.getWorkerId()).getData();
                if (wallet == null) {
                    log.warn("Failed to find wallet for attendance : {}", attendance);
                    throw new ResultStatusException(En_ResultStatus.NOT_UPDATED);
                }
                wallet.setCoins(wallet.getCoins() + coins);
                if (!educationWalletDAO.partialMerge(wallet, "coins")) {
                    log.warn("Failed to return coins to wallet for attendance : {}", attendance);
                    throw new ResultStatusException(En_ResultStatus.NOT_UPDATED);
                }
            }
            if (!educationEntryAttendanceDAO.removeByKey(attendance.getId())) {
                log.warn("Failed to remove attendance : {}", attendance);
                throw new ResultStatusException(En_ResultStatus.NOT_UPDATED);
            }
        }
    }


    private Result<EducationWallet> selectWalletForWorker(Long workerId) {

        Long depId = workerEntryDAO.getDepIdForWorker(workerId);

        List<Long> departmentIdList = getDepartmentGraph(depId);
        if (CollectionUtils.isEmpty(departmentIdList)) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        List<EducationWallet> wallets = educationWalletDAO.getByDepartments(departmentIdList);
        if (CollectionUtils.isEmpty(wallets)) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        if (wallets.size() == 1) {
            return ok(wallets.get(0));
        }

        EducationWallet wallet = null;
        for (Long departmentId : departmentIdList) {
            wallet = stream(wallets)
                    .filter(w -> Objects.equals(w.getDepartmentId(), departmentId))
                    .findFirst()
                    .orElse(null);
            if (wallet != null) {
                break;
            }
        }

        if (wallet == null) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        log.info("Found multiple wallets for worker with id={} : {}. Selected wallet is {}",
                workerId, stream(wallets).map(EducationWallet::getId).collect(Collectors.toList()), wallet.getId());

        return ok(wallet);
    }

    private List<Long> getDepartmentGraph(Long depId) {
        // Список подразделений по уменьшению (родитель->ребенок)
        List<Long> departments = new ArrayList<>();
        fillParentDepartments(departments, depId);
        departments.add(depId);
        fillChildDepartments(departments, depId);
        return departments;
    }

    private void fillParentDepartments(List<Long> departments, Long depId) {
        Long parentDepId = depId;
        for (;;) {
            parentDepId = companyDepartmentDAO.getParentDepIdByDepId(parentDepId);
            if (parentDepId == null) break;
            departments.add(0, parentDepId);
        }
    }

    private void fillChildDepartments(List<Long> departments, Long depId) {
        List<Long> childDepIds = companyDepartmentDAO.getDepIdsByParentDepId(depId);
        departments.addAll(childDepIds);
        for (Long childDepId : childDepIds) {
            fillChildDepartments(departments, childDepId);
        }
    }

    private boolean isQuotaExceededForDepartment(Long depId) {
        LocalDate now = LocalDate.now();
        Date monthStart = java.sql.Date.valueOf(now.withDayOfMonth(1));
        Date monthEnd = java.sql.Date.valueOf(now.withDayOfMonth(now.lengthOfMonth()));
        List<Long> depIds = getDepartmentGraph(depId);
        long spent = stream(educationEntryAttendanceDAO.getAllForDepAndDates(depIds, monthStart, monthEnd))
                .filter(attendance -> attendance.getCoins() != null)
                .mapToLong(EducationEntryAttendance::getCoins)
                .sum();
        long workersCount = stream(workerEntryDAO.getWorkersByDepartment(depId))
                .count();
        long quota = getQuota(workersCount);
        return spent > quota;
    }

    private long getQuota(long workersCount) {
        long monthIncome = (workersCount * 2) + (long) Math.ceil(((double) workersCount) / 4);
        long quota = monthIncome * 4;
        return quota;
    }

    private Result<EducationEntry> validateEducationEntry(EducationEntry entry) {
        if (entry.getType() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        if (entry.getCoins() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        switch (entry.getType()) {
            case CONFERENCE:
            case COURSE: return validateConferenceOrCourse(entry);
            case LITERATURE: return validateLiterature(entry);
        }
        log.error("validateEducationEntry() missed EducationEntryType handler");
        return error(En_ResultStatus.INTERNAL_ERROR);
    }

    private Result<EducationEntry> validateConferenceOrCourse(EducationEntry entry) {
        if (isEmpty(entry.getTitle())) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        if (isEmpty(entry.getDescription())) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        if (isEmpty(entry.getLink())) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        if (isEmpty(entry.getImage())) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        if (entry.getDateStart() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        if (entry.getDateEnd() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        if (isEmpty(entry.getLocation())) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        return ok(entry);
    }

    private Result<EducationEntry> validateLiterature(EducationEntry entry) {
        if (isEmpty(entry.getTitle())) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        if (isEmpty(entry.getDescription())) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        if (isEmpty(entry.getLink())) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        return ok(entry);
    }

    private boolean isAdmin(AuthToken token) {
        Set<UserRole> roles = token.getRoles();
        boolean isAdmin = policyService.hasScopeForPrivilege(roles, En_Privilege.EDUCATION_VIEW, En_Scope.SYSTEM);
        return isAdmin;
    }

    @Autowired
    EducationWalletDAO educationWalletDAO;
    @Autowired
    EducationEntryDAO educationEntryDAO;
    @Autowired
    EducationEntryAttendanceDAO educationEntryAttendanceDAO;
    @Autowired
    WorkerEntryDAO workerEntryDAO;
    @Autowired
    CompanyDepartmentDAO companyDepartmentDAO;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;
    @Autowired
    PolicyService policyService;

    private static Logger log = LoggerFactory.getLogger(EducationServiceImpl.class);
}