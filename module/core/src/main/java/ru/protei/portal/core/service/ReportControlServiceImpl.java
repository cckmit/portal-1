package ru.protei.portal.core.service;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.scheduling.annotation.Scheduled;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.ReportDAO;
import ru.protei.portal.core.model.dict.En_ReportStatus;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.utils.JXLSHelper;
import ru.protei.winter.repo.model.dto.Content;
import ru.protei.winter.repo.model.jdbc.OwnerInfo;
import ru.protei.winter.repo.services.RepoService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@ComponentScan("ru.protei.winter.repo")
public class ReportControlServiceImpl implements ReportControlService {

    private static Logger log = LoggerFactory.getLogger(ReportControlServiceImpl.class);
    private final ThreadPoolExecutor reportExecutorService = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    private final Object sync = new Object();
    private final Set<Long> reportsInProcess = new HashSet<>();
    private final DateFormat nameDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");

    @Autowired
    PortalConfig config;

    @Autowired
    ReportDAO reportDAO;

    @Autowired
    CaseObjectDAO caseObjectDAO;

    @Autowired
    RepoService repoService;

    // -------------------
    // Process new reports
    // -------------------

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
            List<Report> reports = reportDAO.getReportsToProcess(limit);
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
            Long contentId = null;
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            try {

                writeIssuesReport(report, buffer);

                Content content = new Content(makeReportFileName(report), new ByteArrayInputStream(buffer.toByteArray()));
                contentId = repoService.loadContentWithBind(content, buildOwnerInfo(report.getId()), true);

                report.setContentId(contentId);
                report.setStatus(En_ReportStatus.READY);
                report.setModified(new Date());

                reportDAO.merge(report);

                log.debug("successful process report : reportId={}", report.getId());
            } catch (Throwable th) {
                if (contentId != null) {
                    repoService.removeContent(contentId, buildOwnerInfo(report.getId()));
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

    private String makeReportFileName(final Report report) {
        return "issue-report-" + nameDateFormat.format(report.getCreated()) + ".xlsx";
    }

    private OwnerInfo buildOwnerInfo(Long reportId) {
        return new OwnerInfo("sb-report", reportId.toString());
    }

    private void writeIssuesReport(final Report report, ByteArrayOutputStream buffer) throws IOException {
        List<CaseObject> issues = caseObjectDAO.getCases(report.getCaseQuery());
        JXLSHelper.writeIssuesReport(issues, buffer, getLang().getFor(Locale.forLanguageTag(report.getLocale())));
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

    @Scheduled(cron = "0 0 5 * * ?") // at 05:00:00 am every day
    public void processOldReportsSchedule() {
        CoreResponse response = processOldReports();
        if (!response.isOk()) {
            log.warn("fail to process reports : status={}", response.getStatus());
        }
    }

    @Override
    public CoreResponse processOldReports() {
        List<Report> reports = reportDAO.getOutdatedReports(config.data().reportConfig().getLiveTime());
        if (CollectionUtils.isEmpty(reports)) {
            log.debug("old reports to process : 0");
            return new CoreResponse().success(null);
        }
        log.info("old reports to process : {}", reports.size());
        removeReports(reports);
        return new CoreResponse().success(null);
    }

    private void removeReports(List<Report> reports) {
        List<Long> contentIdsToRemove = new ArrayList<>();
        List<Long> reportIdsToRemove = new ArrayList<>();
        for (Report report : reports) {
            if (report.getContentId() != null) {
                contentIdsToRemove.add(report.getContentId());
            }
            reportIdsToRemove.add(report.getId());
        }
        repoService.removeContent(contentIdsToRemove, buildOwnerInfo(0L));
        reportDAO.removeByKeys(reportIdsToRemove);
    }

    // --------------------
    // Process hang reports
    // --------------------

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
            List<Report> reports = reportDAO.getHangReports(config.data().reportConfig().getHangInterval());
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
