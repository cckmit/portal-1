package ru.protei.portal.ui.issue.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.service.ReportService;
import ru.protei.portal.ui.common.server.service.SessionService;
import ru.protei.winter.repo.model.dto.Content;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class ReportDownloadServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger("web");

    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    SessionService sessionService;

    @Autowired
    ReportService reportService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long reportId;
        try {
            reportId = Long.parseLong(req.getParameter("id"));
        } catch (NumberFormatException e) {
            throw new ServletException("Invalid argument 'id' provided");
        }

        log.debug("ReportDownloadServlet.doGet(): reportId={}", reportId);

        UserSessionDescriptor descriptor = sessionService.getUserSessionDescriptor(httpServletRequest);

        CoreResponse<Content> response = reportService.downloadReport(descriptor.makeAuthToken(), reportId);

        if (response.isError()) {
            throw new ServletException(response.getStatus().name());
        }

        String name;
        CoreResponse<Report> responseReport = reportService.getReport(descriptor.makeAuthToken(), reportId);
        if (responseReport.isError() || responseReport.getData().getName() == null) {
            name = response.getData().getName();
        } else {
            name = responseReport.getData().getName();
        }

        BufferedInputStream is = null;
        BufferedOutputStream os = null;
        resp.setContentType("application/octet-stream");
        resp.setHeader("Content-Disposition", "inline; filename=\"" + name + "\"");
        try {
            is = new BufferedInputStream(response.getData().getContent());
            os = new BufferedOutputStream(resp.getOutputStream());
            int count;
            byte[] buffer = new byte[512 * 16];
            while ((count = is.read(buffer)) > 0) {
                os.write(buffer, 0, count);
            }
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {}
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {}
            }
        }
    }
}
