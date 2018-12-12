package ru.protei.portal.core.service.report.managertime;

import ru.protei.portal.core.model.ent.Report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;

public interface ReportCrmManagerTimeService {
    boolean writeExport(ByteArrayOutputStream buffer, Report report, DateFormat dateFormat) throws IOException;
}
