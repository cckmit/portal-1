package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.event.MailReportEvent;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.dao.ReportDAO;
import ru.protei.portal.core.model.dict.En_ReportScheduledType;
import ru.protei.portal.core.model.dict.En_ReportStatus;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.struct.ReportContent;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.report.caseobjects.ReportCase;
import ru.protei.portal.core.report.caseresolution.ReportCaseResolutionTime;
import ru.protei.portal.core.report.casetimeelapsed.ReportCaseTimeElapsed;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.portal.core.utils.TimeFormatter;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.size;
public class ReportControlServiceImpl implements ReportControlService {

    private static Logger log = LoggerFactory.getLogger(ReportControlServiceImpl.class);
    private final ThreadPoolExecutor reportExecutorService = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    private final Object sync = new Object();
    private final Set<Long> reportsInProcess = new HashSet<>();

    @Autowired
    Lang lang;
    @Autowired
    PortalConfig config;
    @Autowired
    ReportDAO reportDAO;
    @Autowired
    CaseCommentDAO caseCommentDAO;

    @Autowired
    ReportStorageService reportStorageService;
    @Autowired
    ReportCase reportCase;
    @Autowired
    ReportCaseTimeElapsed reportCaseTimeElapsed;
    @Autowired
    EventPublisherService publisherService;

    @PostConstruct
    public void init() {
        processOldReports();
    }

    // -------------------
    // Process new reports
    // -------------------

    @Override
    public Result processNewReports() {
        synchronized (sync) {
            int reportThreadsNumber = config.data().reportConfig().getThreadsNumber();
            int activeThreads = reportExecutorService.getActiveCount();
            if (activeThreads >= reportThreadsNumber) {
                log.info("all threads to process reports are busy");
                return error(En_ResultStatus.NOT_AVAILABLE);
            }
            Result<List<Report>> result = getReportsToProcess(reportThreadsNumber - activeThreads);
            if (!result.isOk()) {
                return result;
            }
            log.info( "reports to process : {}", size( result.getData() ) );
            if (size( result.getData() ) == 0) {
                return ok();
            }
            for (final Report report : result.getData()) {
                reportExecutorService.submit(() -> processReport(report));
            }
            return ok();
        }
    }

    private Result<List<Report>> getReportsToProcess( final int limit) {
        try {
            List<Report> reports = reportDAO.getReportsByStatuses(
                    Collections.singletonList(En_ReportStatus.CREATED),
                    limit
            );
            if (CollectionUtils.isEmpty(reports)) {
                return ok(reports);
            }
            Date now = new Date();
            for (Report report : reports) {
                report.setStatus(En_ReportStatus.PROCESS);
                report.setModified(now);
            }
            reportDAO.mergeBatch(reports);
            return ok(reports);
        } catch (Throwable t) {
            log.info("fail get reports to process", t);
            return error(En_ResultStatus.INTERNAL_ERROR);
        }
    }

    private void processReport(final Report report) {
        synchronized (reportsInProcess) {
            if (!reportsInProcess.add(report.getId())) {
                log.error("report is already processed in another thread : reportId={}", report.getId());
                return;
            }
        }
        Result storageResult = null;
        try {
            log.debug("start process report : reportId={}", report.getId());
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            if (!writeReport(report, buffer)) {
                mergeerroratus(report);
                return;
            }

            ReportContent reportContent = new ReportContent(report.getId(), new ByteArrayInputStream(buffer.toByteArray()));
            storageResult = reportStorageService.saveContent(reportContent);

            if (!storageResult.isOk()) {
                mergeerroratus(report);
                return;
            }

            mergeReport(report, En_ReportStatus.READY);
        } catch (Throwable th) {
            log.debug("process report : reportId={}, throwable={}", report.getId(), th.getMessage());
            th.printStackTrace();
            if (storageResult != null) {
                reportStorageService.removeContent(report.getId());
            }
            mergeerroratus(report);
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

    private void mergeerroratus(Report report) {
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
            case CASE_RESOLUTION_TIME:
                log.info( "writeReport(): Start report {}", report.getName() );
                ReportCaseResolutionTime caseCompletionTimeReport = new ReportCaseResolutionTime( report.getCaseQuery(), caseCommentDAO  );
                caseCompletionTimeReport.run();
                Lang.LocalizedLang localizedLang = lang.getFor(Locale.forLanguageTag(report.getLocale()));
                return caseCompletionTimeReport.writeReport(  buffer, localizedLang );
        }
        return false;
    }

    // -------------------
    // Process old reports
    // -------------------

    @Override
    public Result processOldReports() {
        List<Report> reports = reportDAO.getReportsByStatuses(
                Arrays.asList(En_ReportStatus.READY, En_ReportStatus.ERROR),
                new Date(System.currentTimeMillis() - config.data().reportConfig().getLiveTime())
        );
        if (CollectionUtils.isEmpty(reports)) {
            log.debug("old reports to process : 0");
            return ok();
        }
        log.info("old reports to process : {}", reports.size());
        removeReports(reports);
        return ok();
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
    public Result processHangReports() {
        synchronized (reportsInProcess) {
            List<Report> reports = reportDAO.getReportsByStatuses(
                    Collections.singletonList(En_ReportStatus.PROCESS),
                    new Date(System.currentTimeMillis() - config.data().reportConfig().getHangInterval())
            );
            if (CollectionUtils.isEmpty(reports)) {
                log.debug("hang reports to process : 0");
                return ok();
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
            return ok();
        }
    }

    @Override
    public Result<Void> processScheduledMailReports(En_ReportScheduledType enReportScheduledType) {
        log.info("processScheduledMailReports start");
        CompletableFuture[] futures = reportDAO.getScheduledReports(enReportScheduledType).stream()
                .map(report -> {
                    log.info("Scheduled Mail Reports = {}", report);
                    setRange(report, enReportScheduledType);
                    return createScheduledMailReportsTask(report);
                })
                .map(f -> f.thenAccept(mailReportEvent -> publisherService.publishEvent(mailReportEvent)))
                .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(futures).join();
        log.info("processScheduledMailReports end");
        return ok();
    }

    private CompletableFuture<MailReportEvent> createScheduledMailReportsTask(Report report) {
        return CompletableFuture.supplyAsync(() -> {
            processReport(report);
            Report processedReport = reportDAO.get(report.getId());
            if (!processedReport.getStatus().equals(En_ReportStatus.READY)) {
                log.error("Scheduled Mail Reports failed process, report = {}, status = {}", processedReport, processedReport.getStatus());
                return new MailReportEvent(this, processedReport, null);
            }
            Result<ReportContent> reportContentResult = reportStorageService.getContent(processedReport.getId());
            if (reportContentResult.isOk()) {
                return new MailReportEvent(this, processedReport, reportContentResult.getData().getContent());
            } else {
                log.error("Scheduled Mail Reports failed get content, report = {}, error = {}", processedReport, reportContentResult.getStatus());
                return new MailReportEvent(this, processedReport, null);
            }}, reportExecutorService);
    }

    private void setRange(Report report, En_ReportScheduledType enReportScheduledType) {
        int days;
        switch (enReportScheduledType) {
            case WEEKLY: days = 7; break;
            case DAILY:
            default: days = 1;
        }
        Date now = new Date();
        report.getCaseQuery().setCreatedFrom(new Date(now.getTime() - days * CrmConstants.Time.DAY));
        report.getCaseQuery().setCreatedTo(now);
    }
}
