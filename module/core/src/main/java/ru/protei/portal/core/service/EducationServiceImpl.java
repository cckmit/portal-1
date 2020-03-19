package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.exception.ResultStatusException;
import ru.protei.portal.core.model.dao.EducationEntryAttendanceDAO;
import ru.protei.portal.core.model.dao.EducationEntryDAO;
import ru.protei.portal.core.model.dao.EducationWalletDAO;
import ru.protei.portal.core.model.dao.WorkerEntryDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.emptyIfNull;
import static ru.protei.portal.core.model.helper.CollectionUtils.stream;
import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;

public class EducationServiceImpl implements EducationService {

    @Override
    public Result<List<EducationWallet>> getAllWallets(AuthToken token) {
        List<EducationWallet> wallets = emptyIfNull(educationWalletDAO.getAll());
        for (EducationWallet wallet : wallets) {
            List<EducationEntry> educationEntryList = educationEntryDAO.getForWallet(wallet.getDepartmentId(), new Date());
            wallet.setEducationEntryList(emptyIfNull(educationEntryList));
        }
        return ok(wallets);
    }

    @Override
    public Result<List<EducationEntry>> getCurrentEntries(AuthToken token) {
        List<EducationEntry> entries = emptyIfNull(educationEntryDAO.getApprovedForDate(new Date()));
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

        List<EducationWallet> wallets = educationWalletDAO.getByWorkers(workerIds);

        Map<Long, Long> workersToDepartments = stream(workerEntryDAO.getPartialWorkersDepartments(workerIds))
                .collect(Collectors.toMap(
                        WorkerEntry::getId,
                        WorkerEntry::getDepartmentId
                ));

        for (Long depId : workersToDepartments.values()) {
            if (!walletsContainsDepartment(wallets, depId)) {
                return error(En_ResultStatus.PERMISSION_DENIED);
            }
        }

        for (EducationWallet wallet : wallets) {
            if (wallet == null) {
                return error(En_ResultStatus.PERMISSION_DENIED);
            }
            if (wallet.getCoins() == null || wallet.getCoins() < 0) {
                return error(En_ResultStatus.NOT_AVAILABLE);
            }
            if (isQuotaExceededForDepartment(wallet.getDepartmentId())) {
                return error(En_ResultStatus.NOT_AVAILABLE);
            }
        }

        entry.setApproved(false);
        entry.setId(educationEntryDAO.persist(entry));

        educationEntryAttendanceDAO.persistBatch(stream(workerIds)
                .map(workerId -> {
                    EducationEntryAttendance attendance = new EducationEntryAttendance();
                    attendance.setEducationEntryId(entry.getId());
                    attendance.setWorkerId(workerId);
                    attendance.setCharged(false);
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

        EducationWallet wallet = educationWalletDAO.getByWorker(workerEntry.getId());
        if (wallet == null) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }
        if (wallet.getCoins() == null || wallet.getCoins() < 0) {
            return error(En_ResultStatus.NOT_AVAILABLE);
        }
        if (isQuotaExceededForDepartment(wallet.getDepartmentId())) {
            return error(En_ResultStatus.NOT_AVAILABLE);
        }

        EducationEntry entry = educationEntryDAO.get(educationEntryId);
        if (entry == null) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }
        if (!entry.isApproved()) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        try {
            EducationEntryAttendance attendance = new EducationEntryAttendance();
            attendance.setEducationEntryId(educationEntryId);
            attendance.setWorkerId(workerEntry.getId());
            attendance.setCharged(true);
            attendance.setDateRequested(new Date());
            attendance.setId(educationEntryAttendanceDAO.persist(attendance));

            wallet.setCoins(wallet.getCoins() - entry.getCoins());
            if (!educationWalletDAO.partialMerge(wallet, "coins")) {
                log.warn("Failed to reduce wallet coins for attendance : {}", attendance);
                throw new ResultStatusException(En_ResultStatus.NOT_CREATED);
            }

            return ok(attendance);

        } catch (DuplicateKeyException e) {
            return error(En_ResultStatus.ALREADY_EXIST);
        }
    }

    @Override
    public Result<List<EducationEntry>> adminGetEntries(AuthToken token, boolean showOnlyNotApproved, boolean showOutdated) {

        List<EducationEntry> entries = showOutdated
                ? emptyIfNull(educationEntryDAO.getAll())
                : emptyIfNull(educationEntryDAO.getAllForDate(new Date()));

        if (showOnlyNotApproved) {
            entries = stream(entries)
                    .filter(entry -> !entry.isApproved())
                    .collect(Collectors.toList());
        }

        return ok(entries);
    }

    @Override
    @Transactional
    public Result<EducationEntry> adminModifyEntry(AuthToken token, EducationEntry entry) {

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

        boolean isDeclineApprovedAction = oldEntry.isApproved() && !entry.isApproved();
        if (isDeclineApprovedAction) {
            return error(En_ResultStatus.NOT_AVAILABLE);
        }

        if (!educationEntryDAO.merge(entry)) {
            return error(En_ResultStatus.NOT_UPDATED);
        }

        boolean isApproveAction = !oldEntry.isApproved() && entry.isApproved();
        if (isApproveAction) {
            List<EducationEntryAttendance> attendanceList = educationEntryAttendanceDAO.getAllForEntry(entry.getId());
            for (EducationEntryAttendance attendance : attendanceList) {
                if (attendance.isCharged()) {
                    log.warn("Attendance was charged before entry approval! Charge for it again : {}", attendance);
                }
                EducationWallet wallet = educationWalletDAO.get(attendance.getEducationEntryId());
                wallet.setCoins(wallet.getCoins() - entry.getCoins());
                if (!educationWalletDAO.partialMerge(wallet, "coins")) {
                    log.warn("Failed to reduce wallet coins for attendance : {}", attendance);
                    throw new ResultStatusException(En_ResultStatus.NOT_UPDATED);
                }
                attendance.setCharged(true);
                if (!educationEntryAttendanceDAO.partialMerge(attendance, "charged")) {
                    log.warn("Failed to set charged for attendance : {}", attendance);
                    throw new ResultStatusException(En_ResultStatus.NOT_UPDATED);
                }
            }
        }

        boolean isApprovedCostModifyAction = oldEntry.isApproved() && !Objects.equals(oldEntry.getCoins(), entry.getCoins());
        if (isApprovedCostModifyAction) {
            List<EducationEntryAttendance> attendanceList = stream(educationEntryAttendanceDAO.getAllForEntry(entry.getId()))
                    .filter(EducationEntryAttendance::isCharged)
                    .collect(Collectors.toList());
            for (EducationEntryAttendance attendance : attendanceList) {
                int delta = oldEntry.getCoins() - entry.getCoins();
                EducationWallet wallet = educationWalletDAO.get(attendance.getEducationEntryId());
                wallet.setCoins(wallet.getCoins() + delta);
                if (!educationWalletDAO.partialMerge(wallet, "coins")) {
                    log.warn("Failed to modify wallet coins by delta {} for attendance : {}", delta, attendance);
                    throw new ResultStatusException(En_ResultStatus.NOT_UPDATED);
                }
            }
        }

        return ok(entry);
    }

    @Override
    @Transactional
    public Result<EducationEntry> adminDeleteEntry(AuthToken token, Long entryId) {

        if (entryId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        EducationEntry entry = educationEntryDAO.get(entryId);
        if (entry == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        List<EducationEntryAttendance> attendanceList = stream(educationEntryAttendanceDAO.getAllForEntry(entry.getId()))
                .filter(EducationEntryAttendance::isCharged)
                .collect(Collectors.toList());
        for (EducationEntryAttendance attendance : attendanceList) {
            EducationWallet wallet = educationWalletDAO.get(attendance.getEducationEntryId());
            wallet.setCoins(wallet.getCoins() + entry.getCoins());
            if (!educationWalletDAO.partialMerge(wallet, "coins")) {
                log.warn("Failed to put back wallet coins for attendance : {}", attendance);
                throw new ResultStatusException(En_ResultStatus.NOT_REMOVED);
            }
        }

        if (!educationEntryDAO.removeByKey(entryId)) {
            log.warn("Failed to remove entry : {}", entry);
            throw new ResultStatusException(En_ResultStatus.NOT_REMOVED);
        }

        return ok(entry);
    }


    private boolean isQuotaExceededForDepartment(Long depId) {
        LocalDate now = LocalDate.now();
        Date monthStart = java.sql.Date.valueOf(now.withDayOfMonth(1));
        Date monthEnd = java.sql.Date.valueOf(now.withDayOfMonth(now.lengthOfMonth()));
        long spent = stream(educationEntryAttendanceDAO.getAllForDepAndDates(depId, monthStart, monthEnd))
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

    private boolean walletsContainsDepartment(List<EducationWallet> wallets, Long depId) {
        for (EducationWallet wallet : wallets) {
            if (Objects.equals(wallet.getDepartmentId(), depId)) {
                return true;
            }
        }
        return false;
    }

    private Result<EducationEntry> validateEducationEntry(EducationEntry entry) {
        if (entry.getType() == null) {
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


    @Autowired
    EducationWalletDAO educationWalletDAO;
    @Autowired
    EducationEntryDAO educationEntryDAO;
    @Autowired
    EducationEntryAttendanceDAO educationEntryAttendanceDAO;
    @Autowired
    WorkerEntryDAO workerEntryDAO;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    private static Logger log = LoggerFactory.getLogger(EducationServiceImpl.class);
}
