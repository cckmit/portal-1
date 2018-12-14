package ru.protei.portal.core.service.report.caseobjects;

import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.utils.TimeFormatter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;

public interface ReportCrmCaseObjectsService {
    boolean writeReport(ByteArrayOutputStream buffer,
                        Report report, DateFormat dateFormat,
                        TimeFormatter timeFormatter) throws IOException;
}
