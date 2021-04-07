package ru.protei.portal.ui.sitefolder.server.servlet;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Platform;
import ru.protei.portal.core.model.ent.Server;
import ru.protei.portal.core.model.query.ServerQuery;
import ru.protei.portal.core.service.SiteFolderService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static ru.protei.portal.core.model.helper.StringUtils.join;
import static ru.protei.portal.core.model.util.CrmConstants.PlatformServerParameters.*;
import static ru.protei.portal.util.EncodeUtils.encodeToRFC2231;

public class SiteFolderServersDownloadServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(SiteFolderServersDownloadServlet.class);
    private static final String SHEET_TITLE = "Серверы площадки";

    @Autowired
    SessionService sessionService;
    @Autowired
    SiteFolderService siteFolderService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long platformId = Long.parseLong(req.getParameter("platformId"));

        log.info("get(): platformId={}", platformId);

        AuthToken token = fetchAuthToken(req);
        if (token == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Result<Platform> platformResponse = siteFolderService.getPlatform(token, platformId);
        if (platformResponse.isError()) {
            log.error("get(): failed to get platform with status = {}", platformResponse.getStatus());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        ServerQuery query = new ServerQuery();
        query.setPlatformId(platformId);
        Result<SearchResult<Server>> serversResponse = siteFolderService.getServers(token, query);
        if (serversResponse.isError()) {
            log.error("get(): failed to get servers with status = {}", serversResponse.getStatus());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        export(resp, platformResponse.getData(), serversResponse.getData().getResults());
    }

    private void export(HttpServletResponse resp, Platform platform, List<Server> servers) {
        try {
            ByteArrayInputStream exportData = writeToExcelFile(servers);
            if (exportData == null) {
                log.error("get(): failed to get servers report");
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }

            resp.setContentType("application/vnd.ms-excel");
            resp.setHeader("Content-Disposition",  "attachment; filename*=utf-8''" + encodeToRFC2231(platform.getName() + ".xlsx"));
            IOUtils.copy(exportData, resp.getOutputStream());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private ByteArrayInputStream writeToExcelFile(List<Server> servers) {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            SXSSFWorkbook wb = new SXSSFWorkbook();
            wb.setCompressTempFiles(true);

            Sheet sheet = wb.createSheet(SHEET_TITLE);
            Row row = sheet.createRow(0);
            row.createCell(0).setCellValue(IP_ADDRESS);
            row.createCell(1).setCellValue(SERVER_NAME);
            row.createCell(2).setCellValue(ACCESS_PARAMS);
            row.createCell(3).setCellValue(APPS_NAMES);
            row.createCell(4).setCellValue(COMMENT);

            int rowNum = 0;
            for (Server server: servers) {
                row = sheet.createRow(++rowNum);
                row.createCell(0).setCellValue(server.getIp());
                row.createCell(1).setCellValue(server.getName());
                row.createCell(2).setCellValue(server.getAuditType());
                row.createCell(3).setCellValue(join(server.getAppNames(), ", "));
            }

            wb.write(output);
            wb.dispose();
            wb.close();
            return new ByteArrayInputStream(output.toByteArray());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
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
