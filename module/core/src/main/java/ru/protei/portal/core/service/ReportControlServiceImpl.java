package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.event.AbsenceReportEvent;
import ru.protei.portal.core.event.MailReportEvent;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.dao.ReportDAO;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.AbsenceQuery;
import ru.protei.portal.core.model.struct.DateRange;
import ru.protei.portal.core.model.struct.ReportContent;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.report.absence.ReportAbsence;
import ru.protei.portal.core.report.caseobjects.ReportCase;
import ru.protei.portal.core.report.caseresolution.ReportCaseResolutionTime;
import ru.protei.portal.core.report.casetimeelapsed.ReportCaseTimeElapsed;
import ru.protei.portal.core.report.projects.ReportProject;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.portal.core.utils.TimeFormatter;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.config.MainConfiguration.REPORT_TASKS;
import static ru.protei.portal.config.MainConfiguration.BACKGROUND_TASKS;
import static ru.protei.portal.core.model.dict.En_ReportStatus.CANCELLED;
import static ru.protei.portal.core.model.dict.En_ResultStatus.NOT_FOUND;
import static ru.protei.portal.core.model.helper.CollectionUtils.size;
public class ReportControlServiceImpl implements ReportControlService {

    private static Logger log = LoggerFactory.getLogger(ReportControlServiceImpl.class);
    private final Object sync = new Object();
    private final Set<Long> reportsInProcess = new HashSet<>();
    private final static DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

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
    ReportProject reportProject;
    @Autowired
    ReportCaseTimeElapsed reportCaseTimeElapsed;
    @Autowired
    EventPublisherService publisherService;
    @Autowired
    ReportAbsence reportAbsence;
    @Autowired
    @Qualifier(REPORT_TASKS)
    Executor reportExecutorService;

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
            int activeThreads = ((ThreadPoolTaskExecutor)reportExecutorService).getActiveCount();
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
                ((ThreadPoolTaskExecutor)reportExecutorService).submit(() -> processReport(report));
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
        String threadName = Thread.currentThread().getName();
        try {
            Thread.currentThread().setName("T-" + Thread.currentThread().getId() + " reportId=" + report.getId());
            log.info("start process report : reportId={} {}", report.getId(), report.getReportType());
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            mergeReport(report, En_ReportStatus.PROCESS);

            if (!writeReport(report, buffer)) {
                mergeerroratus(report);
                return;
            }

            Report currentReportStatus = reportDAO.partialGet( report.getId(), Report.Columns.STATUS );
            if (currentReportStatus == null || currentReportStatus.isRemoved()) {
                log.warn( "processReport(): Can't get processed report {}", report.getId() );
                return;
            }

            if (CANCELLED.equals( currentReportStatus.getStatus() )) {
                log.warn( "processReport(): Report {} is canceled", report.getId() );
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
            Thread.currentThread().setName(threadName);
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
        reportDAO.partialMerge(report, Report.Columns.STATUS, Report.Columns.MODIFIED);
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
                        new TimeFormatter(),
                        this::isCancel
                );
            case CASE_TIME_ELAPSED:
                return reportCaseTimeElapsed.writeReport(
                        buffer, report,
                        new SimpleDateFormat("dd.MM.yyyy HH:mm"),
                        new TimeFormatter(),
                        this::isCancel
                );
            case CASE_RESOLUTION_TIME:
                log.info( "writeReport(): Start report {}", report.getName() );
                ReportCaseResolutionTime caseCompletionTimeReport = new ReportCaseResolutionTime( report.getCaseQuery(), caseCommentDAO  );
                caseCompletionTimeReport.run();
                Lang.LocalizedLang localizedLang = lang.getFor(Locale.forLanguageTag(report.getLocale()));
                return caseCompletionTimeReport.writeReport(  buffer, localizedLang );
            case PROJECT:
                return reportProject.writeReport(buffer, report, this::isCancel);
        }
        return false;
    }

    private Boolean isCancel(Long reportId) {
        Report report = reportDAO.partialGet( reportId, Report.Columns.STATUS );
        if (report == null || report.isRemoved()) return true;
        if (En_ReportStatus.PROCESS.equals( report.getStatus() )) return false;
        return true;
    }

    // -------------------
    // Process old reports
    // -------------------

    @Override
    public Result processOldReports() {
        List<Report> reports = reportDAO.getReportsByStatuses(
                Arrays.asList(En_ReportStatus.READY, En_ReportStatus.ERROR),
                new Date(System.currentTimeMillis() - config.data().reportConfig().getLiveTime()),
                Arrays.asList(En_ReportScheduledType.NONE)
        );

        log.info("old reports to process : {}", reports.size());
        if (!CollectionUtils.isEmpty(reports)) {
            removeReports(reports);
        }

        return ok();
    }

    private void removeReports(List<Report> reports) {
        List<Long> idsToRemove = new ArrayList<>();
        for (Report report : reports) {
            idsToRemove.add(report.getId());
        }
        reportStorageService.removeContent(idsToRemove);

        Date now = new Date();
        reports.forEach(report -> {
            report.setRemoved(true);
            report.setModified(now);
        });
        reportDAO.mergeBatch(reports);
    }

    // --------------------
    // Process hang reports
    // --------------------

    @Override
    public Result processHangReports() {
        synchronized (reportsInProcess) {
            List<Report> reports = reportDAO.getReportsByStatuses(
                    Collections.singletonList(En_ReportStatus.PROCESS),
                    new Date(System.currentTimeMillis() - config.data().reportConfig().getHangInterval()),
                    Arrays.asList(En_ReportScheduledType.NONE)
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
        CompletableFuture<?>[] futures = reportDAO.getScheduledReports(enReportScheduledType).stream()
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

    @Async(BACKGROUND_TASKS)
    @Override
    public Result<Void> processAbsenceReport(Person initiator, String title, AbsenceQuery query) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            if (reportAbsence.writeReport(buffer, query, dateFormat)) {

                publisherService.publishEvent(new AbsenceReportEvent(
                        this,
                        initiator,
                        title,
                        new ByteArrayInputStream(buffer.toByteArray())));
                return ok();
            }
        } catch (Exception e) {
            log.error("processAbsenceReport(): uncaught exception", e);
        }
        return error(En_ResultStatus.INTERNAL_ERROR);
    }

    private CompletableFuture<MailReportEvent> createScheduledMailReportsTask(Report report) {
        return CompletableFuture.supplyAsync(() -> {
            processReport(report);
            Report processedReport = reportDAO.get(report.getId());
            if (processedReport == null || processedReport.isRemoved() ||
                    !processedReport.getStatus().equals(En_ReportStatus.READY)) {
                log.error("Scheduled Mail Reports failed process, report = {}, status = {}", processedReport,
                        processedReport != null ? processedReport.getStatus() : NOT_FOUND);
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

        if (En_ReportType.CASE_TIME_ELAPSED.equals(report.getReportType())) {
            setCreatedDates(report.getCaseQuery(), new Date(now.getTime() - days * CrmConstants.Time.DAY), now);
        }

        if (En_ReportType.CASE_OBJECTS.equals(report.getReportType())) {
            setCreatedDates(report.getCaseQuery(), null, null);
            setModifiedDates(report.getCaseQuery(), new Date(now.getTime() - days * CrmConstants.Time.DAY), now);
        }
    }

    private void setCreatedDates(CaseQuery query, Date from, Date to) {
        query.setCreatedRange(new DateRange(En_DateIntervalType.FIXED, from, to));
    }

    private void setModifiedDates(CaseQuery query, Date from, Date to) {
        query.setModifiedRange(new DateRange(En_DateIntervalType.FIXED, from, to));
     }
}
