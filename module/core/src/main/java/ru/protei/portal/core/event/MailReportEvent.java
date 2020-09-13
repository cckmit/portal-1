package ru.protei.portal.core.event;

import org.springframework.context.ApplicationEvent;
import ru.protei.portal.core.model.dto.ReportDto;
import ru.protei.portal.core.model.ent.Report;

import java.io.InputStream;

public class MailReportEvent extends ApplicationEvent {

    public MailReportEvent(Object source, ReportDto reportDto, InputStream content) {
        super(source);
        this.reportDto = reportDto;
        this.content = content;
    }

    public InputStream getContent() {
        return content;
    }

    public ReportDto getReport() {
        return reportDto;
    }

    private ReportDto reportDto;
    private InputStream content;
}
