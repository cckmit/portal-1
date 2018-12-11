package ru.protei.portal.core.service.report.managertime;

import ru.protei.portal.core.model.ent.Report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public interface ReportCrmManagerTimeService {
    boolean writeExport(ByteArrayOutputStream buffer, Report report) throws IOException;
}
