package ru.protei.portal.ui.issuereport.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import protei.utils.common.CollectionUtils;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.ReportQuery;
import ru.protei.portal.core.service.ReportService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.ReportController;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

import static ru.protei.portal.ui.common.server.ServiceUtils.*;

/**
 * Реализация сервиса по работе с отчетами
 */
@Service("ReportController")
public class ReportControllerImpl implements ReportController {

    private static final Logger log = LoggerFactory.getLogger(ReportControllerImpl.class);

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    SessionService sessionService;

    @Autowired
    ReportService reportService;

    @Override
    public Long createReport(Report report) throws RequestFailedException {
        log.info("createReport(): locale={} | caseQuery={}", report.getLocale(), report.getCaseQuery());

        AuthToken token = getAuthToken(sessionService, httpServletRequest);

        Result<Long> response = reportService.createReport(token, report);

        return checkResultAndGetData(response);
    }

    @Override
    public Report getReport(Long id) throws RequestFailedException {
        log.info("getReport(): id={}", id);

        AuthToken token = getAuthToken(sessionService, httpServletRequest);

        Result<Report> response = reportService.getReport(token, id);

        return checkResultAndGetData(response);
    }

    @Override
    public SearchResult<Report> getReportsByQuery(ReportQuery query) throws RequestFailedException {
        log.info("getReportsByQuery(): query={}", query);
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(reportService.getReports(token, query));
    }

    @Override
    public void removeReports(Set<Long> include, Set<Long> exclude) throws RequestFailedException {
        log.info("removeReports(): include={} | exclude={}",
                include == null ? "" : CollectionUtils.joinIter(include, ","),
                exclude == null ? "" : CollectionUtils.joinIter(exclude, ",")
        );

        AuthToken token = getAuthToken(sessionService, httpServletRequest);

        Result response = reportService.removeReports(token, include, exclude);

        checkResult(response);
    }

    @Override
    public void recreateReport(Long id) throws RequestFailedException {
        log.info("createReport(): id={}", id);

        AuthToken token = getAuthToken(sessionService, httpServletRequest);

        Result response = reportService.recreateReport(token, id);

        checkResult(response);
    }

    @Override
    public Long cancelReport(Long id) throws RequestFailedException {
        log.info("cancelReport(): id={}", id);

        AuthToken token = getAuthToken(sessionService, httpServletRequest);

        Result<Long> response = reportService.cancelReport(token, id);
        return checkResultAndGetData(response);
    }
}
