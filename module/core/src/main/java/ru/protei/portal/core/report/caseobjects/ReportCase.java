package ru.protei.portal.core.report.caseobjects;

import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.utils.TimeFormatter;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;

public interface ReportCase {
    boolean writeReport(OutputStream buffer,
                        Report report, DateFormat dateFormat,
                        TimeFormatter timeFormatter) throws IOException;
}
