package ru.protei.portal.core.model.struct;

import java.io.IOException;
import java.io.InputStream;

public class ReportContent {

    private Long reportId;

    private InputStream content;

    public ReportContent(Long reportId) {
        this.reportId = reportId;
    }

    public ReportContent(Long reportId, InputStream content) {
        this.reportId = reportId;
        this.content = content;
    }

    /**
     * Закрывает стрим content, если он существует
     *
     * @throws IOException
     */
    public void close() throws IOException {
        if (content != null) {
            content.close();
        }
    }

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public InputStream getContent() {
        return content;
    }

    public void setContent(InputStream content) {
        this.content = content;
    }
}
