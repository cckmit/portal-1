package ru.protei.portal.ui.common.server.controller;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.query.AbsenceQuery;
import ru.protei.portal.core.report.absence.ReportAbsence;
import ru.protei.portal.core.service.session.SessionService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import static ru.protei.portal.util.EncodeUtils.encodeToRFC2231;

@RestController
public class AbsenceReportController {

    @RequestMapping(value = "/download/report", method = RequestMethod.GET)
    @ResponseBody
    public void downloadReport(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam("from_time") Long fromTime,
            @RequestParam("till_time") Long tillTime,
            @RequestParam("employees") Set<Long> employees,
            @RequestParam("reasons") Set<Integer> reasons,
            @RequestParam("sort_field") En_SortField sortField,
            @RequestParam("sort_dir") En_SortDir sortDir)
    {
        log.debug("downloadReport(): from_time = {}, till_time = {}, employees = {}, reasons = {}, sort_field = {}, sort_dir = {}",
                new Date(fromTime), new Date(tillTime), employees, reasons, sortField, sortDir);

        AuthToken token = sessionService.getAuthToken(request);
        if (token == null) {
            log.warn("downloadReport(): auth token not found");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

            AbsenceQuery query = new AbsenceQuery(new Date(fromTime), new Date(tillTime), employees, reasons, sortField, sortDir);
            boolean result = reportAbsence.writeReport(buffer, query, dateFormat);

            if (result) {
                try (ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer.toByteArray())) {
                    response.setStatus(HttpStatus.OK.value());
                    response.setContentType("application/octet-stream");
                    response.setHeader("Content-Disposition", "attachment; filename*=utf-8''" + encodeToRFC2231("absence_report.xlsx"));
                    IOUtils.copy(inputStream, response.getOutputStream());
                    response.flushBuffer();
                }
            }
        } catch (Exception e) {
            log.error("downloadReport(): uncaught exception", e);
        }

        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @Autowired
    ReportAbsence reportAbsence;

    @Autowired
    SessionService sessionService;

    private final static DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    private static final Logger log = LoggerFactory.getLogger(AbsenceReportController.class);
}
