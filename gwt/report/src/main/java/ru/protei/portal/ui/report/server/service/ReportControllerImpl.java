package ru.protei.portal.ui.report.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import protei.utils.common.CollectionUtils;
import ru.protei.portal.core.model.dto.ReportDto;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.query.ReportQuery;
import ru.protei.portal.core.service.ReportService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.ReportController;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

import static ru.protei.portal.ui.common.server.ServiceUtils.checkResultAndGetData;
import static ru.protei.portal.ui.common.server.ServiceUtils.getAuthToken;

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
    public Long createReport(ReportDto reportDto) throws RequestFailedException {
        log.info("createReport(): reportDto={}", reportDto);
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(reportService.createReport(token, reportDto));
    }

    @Override
    public ReportDto getReport(Long id) throws RequestFailedException {
        log.info("getReport(): id={}", id);
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(reportService.getReport(token, id));
    }

    @Override
    public SearchResult<ReportDto> getReportsByQuery(ReportQuery query) throws RequestFailedException {
        log.info("getReportsByQuery(): query={}", query);
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(reportService.getReports(token, query));
    }

    @Override
    public List<Long> removeReports(Set<Long> include, Set<Long> exclude) throws RequestFailedException {
        log.info("removeReports(): include={} | exclude={}",
                include == null ? "" : CollectionUtils.joinIter(include, ","),
                exclude == null ? "" : CollectionUtils.joinIter(exclude, ",")
        );
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(reportService.removeReports(token, include, exclude));
    }

    @Override
    public Long recreateReport(Long id) throws RequestFailedException {
        log.info("recreateReport(): id={}", id);
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(reportService.recreateReport(token, id));
    }

    @Override
    public Long cancelReport(Long id) throws RequestFailedException {
        log.info("cancelReport(): id={}", id);
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(reportService.cancelReport(token, id));
    }
}
