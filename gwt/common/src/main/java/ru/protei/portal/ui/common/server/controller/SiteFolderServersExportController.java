package ru.protei.portal.ui.common.server.controller;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Server;
import ru.protei.portal.core.model.query.ServerQuery;
import ru.protei.portal.core.service.SiteFolderService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.winter.core.utils.beans.SearchResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.springframework.util.FileCopyUtils.copy;
import static ru.protei.portal.core.model.helper.StringUtils.join;
import static ru.protei.portal.util.EncodeUtils.encodeToRFC2231;

@RestController
public class SiteFolderServersExportController {

    @RequestMapping(value = "/download/siteFolderServers/{platformId:\\d+}", method = RequestMethod.GET)
    @ResponseBody
    public void exportSiteFolderServers(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable("platformId") Long platformId
    ) {
        try {
            AuthToken token = sessionService.getAuthToken(request);
            if (token == null) {
                log.warn("exportSiteFolderServers(): auth token not found");
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return;
            }

            log.info("exportSiteFolderServers(): platformId={}", platformId);

            Result<String> platformResponse = siteFolderService.getPlatformName(token, platformId);
            if (platformResponse.isError()) {
                log.error("get(): failed to get platform name with status = {}", platformResponse.getStatus());
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }

            ServerQuery query = new ServerQuery();
            query.setPlatformId(platformId);
            Result<SearchResult<Server>> serversResponse = siteFolderService.getServersWithAppsNames(token, query);
            if (serversResponse.isError()) {
                log.error("get(): failed to get servers with status = {}", serversResponse.getStatus());
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }

            ByteArrayInputStream serversData = writeToExcelFile(serversResponse.getData().getResults());

            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition",  "attachment; filename*=utf-8''" + encodeToRFC2231(platformResponse.getData() + ".xlsx"));
            copy(serversData, response.getOutputStream());
        } catch (Exception e) {
            log.error(e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private ByteArrayInputStream writeToExcelFile(List<Server> servers) {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            SXSSFWorkbook wb = new SXSSFWorkbook();
            SXSSFSheet sheet = wb.createSheet(SHEET_TITLE);
            sheet.setDefaultColumnWidth(30);

            Row row = sheet.createRow(0);
            row.setRowStyle(createCellStyle(wb, true));

            for (int i = 0; i < COLUMNS_TITLES.length; i++) {
                createCell(row, i, COLUMNS_TITLES[i]);
            }

            CellStyle contentStyle = createCellStyle(wb, false);

            int rowNum = 0;
            for (Server s: servers) {
                row = sheet.createRow(++rowNum);
                row.setRowStyle(contentStyle);
                createCell(row, 0, s.getIp());
                createCell(row, 1, s.getName());
                createCell(row, 2, s.getParams());
                createCell(row, 3, join(s.getAppNames(), ", "));
                createCell(row, 4, s.getComment());
            }

            wb.write(output);
            wb.dispose();
            wb.close();
            return new ByteArrayInputStream(output.toByteArray());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new InternalError("get(): failed to export site folder servers");
        }
    }

    private void createCell(Row row, int i, String value) {
        row.createCell(i).setCellValue(value);
    }

    private CellStyle createCellStyle(SXSSFWorkbook wb, boolean isBold) {
        XSSFFont font = (XSSFFont) wb.createFont();
        font.setFontName("Arial");
        font.setBold(isBold);
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFont(font);
        cellStyle.setWrapText(true);
        return cellStyle;
    }

    @Autowired
    SessionService sessionService;
    @Autowired
    SiteFolderService siteFolderService;

    private static final String SHEET_TITLE = "Список серверов площадки";
    private static final String IP_ADDRESS = "IP-адрес";
    private static final String SERVER_NAME = "Название сервера";
    private static final String ACCESS_PARAMS = "Параметры доступа";
    private static final String APPS_NAMES = "Названия приложений";
    private static final String COMMENT = "Комментарий";
    private static final String[] COLUMNS_TITLES = new String[]{IP_ADDRESS, SERVER_NAME, ACCESS_PARAMS, APPS_NAMES, COMMENT};
    private static final Logger log = LoggerFactory.getLogger(SiteFolderServersExportController.class);
}
