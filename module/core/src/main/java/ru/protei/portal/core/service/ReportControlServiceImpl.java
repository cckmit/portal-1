package ru.protei.portal.core.service;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.dao.ReportDAO;
import ru.protei.portal.core.model.dict.En_ReportStatus;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.struct.ReportContent;
import ru.protei.portal.core.report.caseobjects.ReportCase;
import ru.protei.portal.core.report.casetimeelapsed.ReportCaseTimeElapsed;
import ru.protei.portal.core.utils.TimeFormatter;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ReportControlServiceImpl implements ReportControlService {

    private static Logger log = LoggerFactory.getLogger(ReportControlServiceImpl.class);
    private final ThreadPoolExecutor reportExecutorService = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    private final Object sync = new Object();
    private final Set<Long> reportsInProcess = new HashSet<>();

    @Autowired
    PortalConfig config;
    @Autowired
    ReportDAO reportDAO;
    @Autowired
    ReportStorageService reportStorageService;
    @Autowired
    ReportCase reportCase;
    @Autowired
    ReportCaseTimeElapsed reportCaseTimeElapsed;

    @PostConstruct
    public void init() {
        processOldReports();
    }

    // -------------------
    // Process new reports
    // -------------------

    @Override
    @Scheduled(fixedRate = 30 * 1000) // every 30 seconds
    public void processNewReportsSchedule() {
        CoreResponse response = processNewReports();
        if (!response.isOk()) {
            log.warn("fail to process reports : status={}", response.getStatus());
        }
    }

    @Override
    public CoreResponse processNewReports() {
        synchronized (sync) {
            int reportThreadsNumber = config.data().reportConfig().getThreadsNumber();
            int activeThreads = reportExecutorService.getActiveCount();
            if (activeThreads >= reportThreadsNumber) {
                log.debug("all threads to process reports are busy");
                return new CoreResponse().error(En_ResultStatus.NOT_AVAILABLE);
            }
            CoreResponse<List<Report>> result = getReportsToProcess(reportThreadsNumber - activeThreads);
            if (!result.isOk()) {
                return result;
            }
            log.debug("reports to process : {}", result.getDataAmountTotal());
            if (result.getDataAmountTotal() == 0) {
                return new CoreResponse().success(null);
            }
            for (final Report report : result.getData()) {
                reportExecutorService.submit(() -> processReport(report));
            }
            return new CoreResponse().success(null);
        }
    }

    private CoreResponse<List<Report>> getReportsToProcess(final int limit) {
        try {
            List<Report> reports = reportDAO.getReportsByStatuses(
                    Collections.singletonList(En_ReportStatus.CREATED),
                    limit
            );
            if (CollectionUtils.isEmpty(reports)) {
                return new CoreResponse<List<Report>>().success(reports);
            }
            Date now = new Date();
            for (Report report : reports) {
                report.setStatus(En_ReportStatus.PROCESS);
                report.setModified(now);
            }
            reportDAO.mergeBatch(reports);
            return new CoreResponse<List<Report>>().success(reports);
        } catch (Throwable t) {
            log.info("fail get reports to process", t);
            return new CoreResponse<List<Report>>().error(En_ResultStatus.INTERNAL_ERROR);
        }
    }

    private void processReport(final Report report) {
        synchronized (reportsInProcess) {
            if (!reportsInProcess.add(report.getId())) {
                log.error("report is already processed in another thread : reportId={}", report.getId());
                return;
            }
        }
        CoreResponse storageResult = null;
        try {
            log.debug("start process report : reportId={}", report.getId());
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            if (!writeReport(report, buffer)) {
                mergeErrorStatus(report);
                return;
            }

            ReportContent reportContent = new ReportContent(report.getId(), new ByteArrayInputStream(buffer.toByteArray()));
            storageResult = reportStorageService.saveContent(reportContent);

            if (!storageResult.isOk()) {
                mergeErrorStatus(report);
                return;
            }

            mergeReport(report, En_ReportStatus.READY);
        } catch (Throwable th) {
            log.debug("process report : reportId={}, throwable={}", report.getId(), th.getMessage());
            th.printStackTrace();
            if (storageResult != null) {
                reportStorageService.removeContent(report.getId());
            }
            mergeErrorStatus(report);
        } finally {
            reportsInProcess.remove(report.getId());
        }
    }

    private void mergeReport(Report report, En_ReportStatus status) {
        report.setStatus(status);
        report.setModified(new Date());
        reportDAO.merge(report);
        log.debug("process report : reportId={}, status={}", report.getId(), status.name());
    }

    private void mergeErrorStatus(Report report) {
        report.setStatus(En_ReportStatus.ERROR);
        report.setModified(new Date());
        reportDAO.partialMerge(report, "status", "modified");
        log.debug("process report : reportId={}, status={}", report.getId(), En_ReportStatus.ERROR);
    }

    private boolean writeReport(Report report, ByteArrayOutputStream buffer) throws IOException {
        if (report.getReportType() == null) {
            log.warn("write report : reportId={} - report type is null", report.getId());
            return false;
        }
        switch (report.getReportType()) {
            case CASE_OBJECTS:
                return reportCase.writeReport(
                        buffer, report,
                        new SimpleDateFormat("dd.MM.yyyy HH:mm"),
                        new TimeFormatter()
                );
            case CASE_TIME_ELAPSED:
                return reportCaseTimeElapsed.writeReport(
                        buffer, report,
                        new SimpleDateFormat("dd.MM.yyyy HH:mm"),
                        new TimeFormatter()
                );
        }
        return false;
    }

    // -------------------
    // Process old reports
    // -------------------

    @Override
    @Scheduled(cron = "0 0 5 * * ?") // at 05:00:00 am every day
    public void processOldReportsSchedule() {
        CoreResponse response = processOldReports();
        if (!response.isOk()) {
            log.warn("fail to process reports : status={}", response.getStatus());
        }
    }

    @Override
    public CoreResponse processOldReports() {
        List<Report> reports = reportDAO.getReportsByStatuses(
                Arrays.asList(En_ReportStatus.READY, En_ReportStatus.ERROR),
                new Date(System.currentTimeMillis() - config.data().reportConfig().getLiveTime())
        );
        if (CollectionUtils.isEmpty(reports)) {
            log.debug("old reports to process : 0");
            return new CoreResponse().success(null);
        }
        log.info("old reports to process : {}", reports.size());
        removeReports(reports);
        return new CoreResponse().success(null);
    }

    private void removeReports(List<Report> reports) {
        List<Long> idsToRemove = new ArrayList<>();
        for (Report report : reports) {
            idsToRemove.add(report.getId());
        }
        reportStorageService.removeContent(idsToRemove);
        reportDAO.removeByKeys(idsToRemove);
    }

    // --------------------
    // Process hang reports
    // --------------------

    @Override
    @Scheduled(fixedRate = 60 * 60 * 1000) // every hour
    public void processHangReportsSchedule() {
        CoreResponse response = processHangReports();
        if (!response.isOk()) {
            log.warn("fail to process reports : status={}", response.getStatus());
        }
    }

    @Override
    public CoreResponse processHangReports() {
        synchronized (reportsInProcess) {
            List<Report> reports = reportDAO.getReportsByStatuses(
                    Collections.singletonList(En_ReportStatus.PROCESS),
                    new Date(System.currentTimeMillis() - config.data().reportConfig().getHangInterval())
            );
            if (CollectionUtils.isEmpty(reports)) {
                log.debug("hang reports to process : 0");
                return new CoreResponse().success(null);
            }
            log.debug("hang reports to process : {}", reports.size());
            Date currentDate = new Date();
            Iterator<Report> it = reports.iterator();
            while (it.hasNext()) {
                Report report = it.next();
                if (!reportsInProcess.contains(report.getId())) {
                    it.remove();
                } else {
                    log.info("report is hang : reportId={}", report.getId());
                    report.setStatus(En_ReportStatus.CREATED);
                    report.setModified(currentDate);
                }
            }
            reportDAO.mergeBatch(reports);
            return new CoreResponse().success(null);
        }
    }
}
