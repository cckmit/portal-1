package ru.protei.portal.ui.report.server.servlet;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dto.ReportDto;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.struct.ReportContent;
import ru.protei.portal.core.service.ReportService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static ru.protei.portal.util.EncodeUtils.encodeToRFC2231;

public class ReportDownloadServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(ReportDownloadServlet.class);

    @Autowired
    SessionService sessionService;
    @Autowired
    ReportService reportService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Long reportId = fetchReportId(req);
        if (reportId == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        log.info("get(): reportId={}", reportId);

        AuthToken token = fetchAuthToken(req);
        if (token == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Result<ReportContent> response = reportService.downloadReport(token, reportId);
        if (response.isError()) {
            log.error("get(): failed to get report content with status = {}", response.getStatus());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        ReportDto reportDto = reportService.getReport(token, reportId).getData();
        String filename = reportService.getReportFilename(reportId, reportDto).getData();

        resp.setContentType("application/octet-stream");
        resp.setHeader("Content-Disposition", "attachment; filename*=utf-8''" + encodeToRFC2231(filename));
        IOUtils.copy(response.getData().getContent(), resp.getOutputStream());
    }

    private Long fetchReportId(HttpServletRequest req) {
        try {
            return Long.parseLong(req.getParameter("id"));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private AuthToken fetchAuthToken(HttpServletRequest req) {
        try {
            return ServiceUtils.getAuthToken(sessionService, req);
        } catch (RequestFailedException e) {
            return null;
        }
    }
}
