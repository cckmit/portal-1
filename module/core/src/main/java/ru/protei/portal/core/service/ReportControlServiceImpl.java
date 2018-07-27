package ru.protei.portal.core.service;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.scheduling.annotation.Scheduled;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.ReportDAO;
import ru.protei.portal.core.model.dict.En_ReportStatus;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.struct.ReportContent;
import ru.protei.portal.core.utils.JXLSHelper;
import ru.protei.portal.core.utils.WorkTimeFormatter;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

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
    CaseObjectDAO caseObjectDAO;

    @Autowired
    CaseCommentDAO caseCommentDAO;

    @Autowired
    ReportStorageService reportStorageService;

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
        try {
            log.debug("start process report : reportId={}", report.getId());
            boolean saved = false;
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            try {

                if (writeIssuesReport(report, buffer)) {

                    ReportContent reportContent = new ReportContent(report.getId(), new ByteArrayInputStream(buffer.toByteArray()));
                    CoreResponse storageResult = reportStorageService.saveContent(reportContent);

                    if (storageResult.isOk()) {
                        saved = true;

                        report.setStatus(En_ReportStatus.READY);
                        report.setModified(new Date());

                        reportDAO.merge(report);

                        log.debug("successful process report : reportId={}", report.getId());

                        return;
                    }
                }

                report.setStatus(En_ReportStatus.ERROR);
                report.setModified(new Date());

                reportDAO.merge(report);

                log.debug("unsuccessful process report : reportId={}", report.getId());

            } catch (Throwable th) {
                if (saved) {
                    reportStorageService.removeContent(report.getId());
                }
                log.warn("fail process report : reportId={} {}", report.getId(), th);
                report.setStatus(En_ReportStatus.ERROR);
                report.setModified(new Date());
                reportDAO.merge(report);
            }
        } finally {
            reportsInProcess.remove(report.getId());
        }
    }

    private boolean writeIssuesReport(final Report report, ByteArrayOutputStream buffer) throws IOException {

        Long count = caseObjectDAO.count(report.getCaseQuery());

        if (count == null || count < 1) {
            log.debug("report : reportId={} has no corresponding case objects", report.getId());
            return true;
        }

        if (count > Integer.MAX_VALUE) {
            log.debug("report : reportId={} has too many corresponding case objects: {}, aborting task", report.getId(), count);
            return false;
        }

        log.debug("report : reportId={} has {} case objects to procees", report.getId(), count);

        JXLSHelper.ReportBook book = new JXLSHelper.ReportBook(
                new SimpleDateFormat("dd.MM.yyyy HH:mm"),
                new WorkTimeFormatter(),
                getLang().getFor(Locale.forLanguageTag(report.getLocale()))
        );

        if (writeIssuesReport(book, report, count)) {
            book.collect(buffer);
            return true;
        } else {
            return false;
        }
    }

    private boolean writeIssuesReport(JXLSHelper.ReportBook book, Report report, Long count) {

        final int step = config.data().reportConfig().getStep();
        final int limit = count.intValue();
        int offset = 0;

        while (offset < limit) {
            int amount = offset + step < limit ? offset + step : limit;
            try {
                CaseQuery query = report.getCaseQuery();
                query.setOffset(offset);
                query.setLimit(amount);
                writeIssuesReportChunk(book, query);
                offset += step;
            } catch (Throwable th) {
                log.warn("fail to process chunk [{} - {}] : reportId={} {}", offset, amount, report.getId(), th);
                return false;
            }
        }

        return true;
    }

    private void writeIssuesReportChunk(JXLSHelper.ReportBook book, CaseQuery query) {
        List<CaseObject> issues = caseObjectDAO.getCases(query);
        List<List<CaseComment>> comments = issues.stream()
                .map(issue -> caseCommentDAO.getCaseComments(issue.getId()))
                .collect(Collectors.toList());

        book.write(issues, comments);
    }

    private Lang getLang() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("Lang");
        messageSource.setDefaultEncoding("UTF-8");
        return new Lang(messageSource);
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
