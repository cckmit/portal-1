package ru.protei.portal.core.report.casetimeelapsed;

import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.utils.TimeFormatter;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.function.Predicate;

public interface ReportCaseTimeElapsed {
    boolean writeReport(OutputStream buffer,
                        Report report,
                        Predicate<Long> isCancel) throws IOException;
}
