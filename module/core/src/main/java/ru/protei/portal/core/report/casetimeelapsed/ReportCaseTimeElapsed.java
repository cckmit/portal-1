package ru.protei.portal.core.report.casetimeelapsed;

import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.utils.TimeFormatter;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.function.Supplier;

public interface ReportCaseTimeElapsed {
    boolean writeReport(OutputStream buffer,
                        Report report, DateFormat dateFormat,
                        TimeFormatter timeFormatter,
                        Supplier<Boolean> cancelTest) throws IOException;
}
