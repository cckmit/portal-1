package ru.protei.portal.core.service.report.managertime;

import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.utils.TimeFormatter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;

public interface ReportCrmManagerTimeService {
    boolean writeExport(ByteArrayOutputStream buffer, Report report,
                        DateFormat dateFormat,
                        TimeFormatter timeFormatter) throws IOException;
}
