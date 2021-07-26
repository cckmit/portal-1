package ru.protei.portal.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.event.AbsenceReportEvent;
import ru.protei.portal.core.event.DutyLogReportEvent;
import ru.protei.portal.core.event.MailReportEvent;
import ru.protei.portal.core.event.ProcessNewReportsEvent;
import ru.protei.portal.core.model.dao.HistoryDAO;
import ru.protei.portal.core.model.dao.ReportDAO;
import ru.protei.portal.core.model.dict.En_ReportScheduledType;
import ru.protei.portal.core.model.dict.En_ReportStatus;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.dto.ReportDto;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.*;
import ru.protei.portal.core.model.struct.ReportContent;
import ru.protei.portal.core.report.absence.ReportAbsence;
import ru.protei.portal.core.report.caseobjects.ReportCase;
import ru.protei.portal.core.report.caseresolution.ReportCaseResolutionTime;
import ru.protei.portal.core.report.casetimeelapsed.ReportCaseTimeElapsed;
import ru.protei.portal.core.report.contract.ReportContract;
import ru.protei.portal.core.report.dutylog.ReportDutyLog;
import ru.protei.portal.core.report.nightwork.ReportNightWork;
import ru.protei.portal.core.report.projects.ReportProject;
import ru.protei.portal.core.report.ytwork.ReportYtWork;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.portal.core.utils.TimeFormatter;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.config.MainConfiguration.BACKGROUND_TASKS;
import static ru.protei.portal.config.MainConfiguration.REPORT_TASKS;
import static ru.protei.portal.core.model.dict.En_ResultStatus.NOT_FOUND;
import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;
import static ru.protei.portal.core.model.helper.CollectionUtils.size;

public class ReportControlServiceImpl implements ReportControlService {

    private static Logger log = LoggerFactory.getLogger(ReportControlServiceImpl.class);
    private final Object sync = new Object();
    private final Set<Long> reportsInProcess = new HashSet<>();

    @Autowired
    Lang lang;
    @Autowired
    PortalConfig config;
    @Autowired
    ReportDAO reportDAO;
    @Autowired
    HistoryDAO historyDAO;

    @Autowired
    ReportService reportService;
    @Autowired
    ReportStorageService reportStorageService;
    @Autowired
    ReportCase reportCase;
    @Autowired
    ReportProject reportProject;
    @Autowired
    ReportCaseTimeElapsed reportCaseTimeElapsed;
    @Autowired
    ReportNightWork reportNightWork;
    @Autowired
    ReportYtWork reportYtWork;
    @Autowired
    EventPublisherService publisherService;
    @Autowired
    ReportAbsence reportAbsence;
    @Autowired
    ReportDutyLog reportDutyLog;
    @Autowired
    ReportContract reportContract;
    @Autowired
    @Qualifier(REPORT_TASKS)
    Executor reportExecutorService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @PostConstruct
    public void init() {
        if (isNotConfiguredSystemId()) {
            return;
        }
        processOldReports();
    }

    @Override
    @EventListener
    public void onProcessNewReportsEvent(ProcessNewReportsEvent event) {
        if (isNotConfiguredSystemId()) {
            return;
        }
        processNewReports();
    }

    @Override
    public Result<Void> processNewReports() {
        synchronized (sync) {
            int reportThreadsNumber = config.data().reportConfig().getThreadsNumber();
            int activeThreads = ((ThreadPoolTaskExecutor)reportExecutorService).getActiveCount();
            if (activeThreads >= reportThreadsNumber) {
                log.info("processNewReports(): all threads to process reports are busy");
                return error(En_ResultStatus.NOT_AVAILABLE);
            }
            List<Report> reports = getReportsToProcess(reportThreadsNumber - activeThreads);
            int size = size(reports);
            log.info("processNewReports(): reports to process : {}", size);
            if (!isEmpty(reports)) {
                for (Report report : reports) {
                    ((ThreadPoolTaskExecutor) reportExecutorService).submit(() -> processReport(report));
                }
            }
            return ok();
        }
    }

    private List<Report> getReportsToProcess(int limit) {
        ReportQuery query = new ReportQuery();
        query.setStatuses(Collections.singletonList(En_ReportStatus.CREATED));
        query.setLimit(limit);
        query.setRemoved(false);
        query.setSystemId(config.data().getCommonConfig().getSystemId());
        return reportDAO.getReports(query);
    }

    private void processReport(Report report) {
        synchronized (reportsInProcess) {
            if (!reportsInProcess.add(report.getId())) {
                log.error("processReport(): report is already processed in another thread : reportId={}", report.getId());
                return;
            }
        }
        Result storageResult = null;
        String threadName = Thread.currentThread().getName();
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            Thread.currentThread().setName("T-" + Thread.currentThread().getId() + " reportId=" + report.getId());
            log.info("processReport(): start process report : reportId={} {}", report.getId(), report.getReportType());

            mergeReportStatus(report, En_ReportStatus.PROCESS);

            if (!writeReport(report, buffer)) {
                mergeReportStatus(report, En_ReportStatus.ERROR);
                return;
            }

            En_ReportStatus currentReportStatus = getReportStatus(report.getId());
            if (!En_ReportStatus.PROCESS.equals(currentReportStatus)) {
                log.warn("processReport(): Report {} is canceled by status = {}", report.getId(), currentReportStatus);
                return;
            }

            ReportContent reportContent = new ReportContent(report.getId(), new ByteArrayInputStream(buffer.toByteArray()));
            storageResult = reportStorageService.saveContent(reportContent);

            if (!storageResult.isOk()) {
                mergeReportStatus(report, En_ReportStatus.ERROR);
                return;
            }

            mergeReportStatus(report, En_ReportStatus.READY);
        } catch (Throwable th) {
            log.error("processReport(): reportId={}, throwable={}", report.getId(), th.getMessage());
            th.printStackTrace();
            if (storageResult != null) {
                reportStorageService.removeContent(report.getId());
            }
            mergeReportStatus(report, En_ReportStatus.ERROR);
        } finally {
            reportsInProcess.remove(report.getId());
            Thread.currentThread().setName(threadName);
        }
    }

    private void mergeReportStatus(Report report, En_ReportStatus status) {
        report.setStatus(status);
        report.setModified(new Date());
        reportDAO.partialMerge(report, Report.Columns.STATUS, Report.Columns.MODIFIED);
        log.info("mergeReportStatus() : reportId={}, status={}", report.getId(), status);
    }

    private En_ReportStatus getReportStatus(Long reportId) {
        Report report = reportDAO.partialGet(reportId, Report.Columns.STATUS);
        if (report == null) {
            return null;
        }
        return report.getStatus();
    }

    private boolean writeReport(Report report, ByteArrayOutputStream buffer) throws IOException {
        if (report.getReportType() == null) {
            log.warn("writeReport(): reportId={} - report type is null", report.getId());
            return false;
        }
        switch (report.getReportType()) {
            case CASE_OBJECTS:
                return reportCase.writeReport(
                        buffer,
                        report,
                        getQuery(report, CaseQuery.class),
                        new SimpleDateFormat("dd.MM.yyyy HH:mm"),
                        new TimeFormatter(),
                        this::isCancel
                );
            case CASE_TIME_ELAPSED:
                return reportCaseTimeElapsed.writeReport(
                        buffer,
                        report,
                        getQuery(report, CaseQuery.class),
                        this::isCancel
                );
            case CASE_RESOLUTION_TIME:
                log.info( "writeReport(): Start report {}", report.getName() );
                ReportCaseResolutionTime caseCompletionTimeReport = new ReportCaseResolutionTime(
                        getQuery(report, CaseQuery.class),
                        historyDAO
                );
                caseCompletionTimeReport.run();
                Lang.LocalizedLang localizedLang = lang.getFor(Locale.forLanguageTag(report.getLocale()));
                return caseCompletionTimeReport.writeReport(  buffer, localizedLang );
            case PROJECT:
                return reportProject.writeReport(
                        buffer,
                        report,
                        getQuery(report, ProjectQuery.class),
                        this::isCancel
                );
            case CONTRACT:
                return reportContract.writeReport(
                        buffer,
                        report,
                        getQuery(report, ContractQuery.class),
                        new SimpleDateFormat("dd.MM.yyyy"),
                        this::isCancel
                );
            case NIGHT_WORK:
                return reportNightWork.writeReport(
                        buffer,
                        report,
                        getQuery(report, CaseQuery.class),
                        this::isCancel
                );
            case YT_WORK:
                return reportYtWork.writeReport(
                        buffer,
                        report,
                        getQuery(report, YtWorkQuery.class),
                        this::isCancel
                );
        }
        return false;
    }

    private Boolean isCancel(Long reportId) {
        Report report = reportDAO.partialGet(reportId, Report.Columns.STATUS, Report.Columns.REMOVED);
        if (report == null || report.isRemoved()) return true;
        if (!En_ReportStatus.PROCESS.equals(report.getStatus())) return true;
        return false;
    }


    @Override
    public Result processOldReports() {
        List<Report> reports = getReportsToRemove();
        log.info("processOldReports(): old reports to process : {}", reports.size());
        if (!isEmpty(reports)) {
            reportService.removeReports(reports);
        }
        return ok();
    }

    private List<Report> getReportsToRemove() {
        ReportQuery query = new ReportQuery();
        query.setStatuses(Arrays.asList(En_ReportStatus.READY, En_ReportStatus.ERROR));
        query.setToModified(new Date(System.currentTimeMillis() - config.data().reportConfig().getLiveTime()));
        query.setScheduledTypes(Arrays.asList(En_ReportScheduledType.NONE));
        query.setRemoved(false);
        query.setSystemId(config.data().getCommonConfig().getSystemId());
        return reportDAO.getReports(query);
    }


    @Override
    public Result<Void> processScheduledMailReports(En_ReportScheduledType enReportScheduledType) {
        log.info("processScheduledMailReports(): start");
        final String systemId = config.data().getCommonConfig().getSystemId();
        CompletableFuture<?>[] futures = reportDAO.getScheduledReports(enReportScheduledType, systemId).stream()
                .map(this::createScheduledMailReportsTask)
                .map(f -> f.thenAccept(mailReportEvent -> publisherService.publishEvent(mailReportEvent)))
                .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(futures).join();
        log.info("processScheduledMailReports(): end");
        return ok();
    }


    @Async(BACKGROUND_TASKS)
    @Override
    public Result<Void> processAbsenceReport(Person initiator, String title, AbsenceQuery query) {
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            if (reportAbsence.writeReport(buffer, query)) {
                publisherService.publishEvent(new AbsenceReportEvent(
                        this,
                        initiator,
                        title,
                        new Date(),
                        new ByteArrayInputStream(buffer.toByteArray())));
                return ok();
            }
        } catch (Exception e) {
            log.error("processAbsenceReport(): uncaught exception", e);
        }
        return error(En_ResultStatus.INTERNAL_ERROR);
    }

    @Async(BACKGROUND_TASKS)
    @Override
    public Result<Void> processDutyLogReport(Person initiator, String title, DutyLogQuery query) {
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            if (reportDutyLog.writeReport(buffer, query)) {
                publisherService.publishEvent(new DutyLogReportEvent(
                        this,
                        initiator,
                        title,
                        new Date(),
                        new ByteArrayInputStream(buffer.toByteArray())));
                return ok();
            }
        } catch (Exception e) {
            log.error("processDutyLogReport(): uncaught exception", e);
        }
        return error(En_ResultStatus.INTERNAL_ERROR);
    }

    private CompletableFuture<MailReportEvent> createScheduledMailReportsTask(Report report) {
        log.info("processScheduledMailReports(): Scheduled Mail Reports = {}", report);
        return CompletableFuture.supplyAsync(() -> {
            processReport(report);
            Report processedReport = reportDAO.get(report.getId());
            ReportDto reportDto = reportService.convertReportToDto(processedReport).getData();
            if (reportDto.getReport().getCreator() != null) jdbcManyRelationsHelper.fill(reportDto.getReport().getCreator(), Person.Fields.CONTACT_ITEMS);
            boolean isFailedOrRemoved = processedReport == null || processedReport.isRemoved();
            boolean isNotReady = isFailedOrRemoved || !En_ReportStatus.READY.equals(processedReport.getStatus());
            if (isNotReady) {
                log.error("Scheduled Mail Reports failed process, report = {}, status = {}", processedReport,
                        processedReport != null ? processedReport.getStatus() : NOT_FOUND);
                return new MailReportEvent(this, reportDto, null);
            }
            Result<ReportContent> reportContentResult = reportStorageService.getContent(processedReport.getId());
            if (reportContentResult.isOk()) {
                return new MailReportEvent(this, reportDto, reportContentResult.getData().getContent());
            } else {
                log.error("Scheduled Mail Reports failed get content, report = {}, error = {}", processedReport, reportContentResult.getStatus());
                return new MailReportEvent(this, reportDto, null);
            }}, reportExecutorService);
    }

    private <T extends BaseQuery> T getQuery(Report report, Class<T> clazz) {
        try {
            return objectMapper.readValue(report.getQuery(), clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isNotConfiguredSystemId() {
        if (HelperFunc.isEmpty(config.data().getCommonConfig().getSystemId())) {
            log.warn("reports is not started because system.id not set in configuration");
            return true;
        }
        return false;
    }
}
