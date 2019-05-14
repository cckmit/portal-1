package ru.protei.portal.ui.issuereport.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import protei.utils.common.CollectionUtils;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.model.query.ReportQuery;
import ru.protei.portal.core.service.ReportService;
import ru.protei.portal.ui.common.client.service.ReportController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.server.service.SessionService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

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
        log.debug("createReport(): locale={} | caseQuery={}", report.getLocale(), report.getCaseQuery());

        UserSessionDescriptor descriptor = sessionService.getUserSessionDescriptor(httpServletRequest);

        CoreResponse<Long> response = reportService.createReport(descriptor.makeAuthToken(), report);

        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }

        return response.getData();
    }

    @Override
    public Report getReport(Long id) throws RequestFailedException {
        log.debug("getReport(): id={}", id);

        UserSessionDescriptor descriptor = sessionService.getUserSessionDescriptor(httpServletRequest);

        CoreResponse<Report> response = reportService.getReport(descriptor.makeAuthToken(), id);

        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }

        return response.getData();
    }

    @Override
    public SearchResult<Report> getReportsByQuery(ReportQuery query) throws RequestFailedException {
        log.debug("getReportsByQuery(): query={}", query);
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return ServiceUtils.checkResultAndGetData(reportService.getReports(token, query));
    }

    @Override
    public void removeReports(Set<Long> include, Set<Long> exclude) throws RequestFailedException {
        log.debug("removeReports(): include={} | exclude={}",
                include == null ? "" : CollectionUtils.joinIter(include, ","),
                exclude == null ? "" : CollectionUtils.joinIter(exclude, ",")
        );

        UserSessionDescriptor descriptor = sessionService.getUserSessionDescriptor(httpServletRequest);

        CoreResponse response = reportService.removeReports(descriptor.makeAuthToken(), include, exclude);

        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
    }

    @Override
    public void recreateReport(Long id) throws RequestFailedException {
        log.debug("createReport(): id={}", id);

        UserSessionDescriptor descriptor = sessionService.getUserSessionDescriptor(httpServletRequest);

        CoreResponse response = reportService.recreateReport(descriptor.makeAuthToken(), id);

        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
    }
}
