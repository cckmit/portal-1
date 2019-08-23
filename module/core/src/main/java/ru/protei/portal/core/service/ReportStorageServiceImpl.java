package ru.protei.portal.core.service;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.struct.ReportContent;

import java.io.*;
import java.util.List;

import static ru.protei.portal.api.struct.CoreResponse.error;
import static ru.protei.portal.api.struct.CoreResponse.ok;

public class ReportStorageServiceImpl implements ReportStorageService {

    private static final Logger logger = LoggerFactory.getLogger(ReportStorageServiceImpl.class);

    @Autowired
    PortalConfig config;

    @Override
    public CoreResponse saveContent(ReportContent reportContent) {
        String reportPath = makeReportPath(reportContent.getReportId(), config.data().reportConfig().getStoragePath());
        FileOutputStream outputStream = null;
        try {
            File file = new File(reportPath);
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            if (file.createNewFile() || file.isFile()) {
                outputStream = new FileOutputStream(file, false);
                IOUtils.copy(reportContent.getContent(), outputStream);
            } else {
                throw new IOException("Provided file not created or it is a directory");
            }
        } catch (IOException e) {
            logger.warn("Failed to save content", e);
            return error(En_ResultStatus.NOT_CREATED);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }
        return ok();
    }

    @Override
    public CoreResponse<ReportContent> getContent(Long reportId) {
        String reportPath = makeReportPath(reportId, config.data().reportConfig().getStoragePath());
        try {
            ReportContent content = new ReportContent(reportId);
            content.setContent(new FileInputStream(reportPath));
            return ok(content);
        } catch (FileNotFoundException e) {
            logger.warn("Failed to get content", e);
            return error(En_ResultStatus.NOT_FOUND);
        }
    }

    @Override
    public CoreResponse removeContent(Long reportId) {
        String reportPath = makeReportPath(reportId, config.data().reportConfig().getStoragePath());
        try {
            File file = new File(reportPath);
            if (file.exists() && file.isFile()) {
                file.delete();
            }
            return ok();
        } catch (Throwable t) {
            logger.warn("Failed to remove content", t);
            return error(En_ResultStatus.NOT_REMOVED);
        }
    }

    @Override
    public CoreResponse removeContent(List<Long> reportIds) {
        CoreResponse coreResponse = ok();
        for (Long reportId : reportIds) {
            CoreResponse result = removeContent(reportId);
            if (result.isError()) {
                coreResponse = result;
            }
        }
        return coreResponse;
    }

    @Override
    public CoreResponse<String> getFileName(String reportId) {
        return ok("report-" + reportId + ".xlsx");
    }

    private String makeReportPath(Long reportId, String rootPath) {
        String idStr = String.format("%010d", reportId);
        String fileName = getFileName(idStr).getData();
        StringBuilder sb = new StringBuilder(rootPath.length() + 13 + fileName.length());
        sb.append(rootPath).append('/');
        sb.append(idStr, 0, 2).append('/');
        sb.append(idStr, 2, 4).append('/');
        sb.append(idStr, 4, 6).append('/');
        sb.append(idStr, 6, 8).append('/');
        sb.append(fileName);
        return sb.toString();
    }
}
