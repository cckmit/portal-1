package ru.protei.portal.core.report.ytwork;

import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.YoutrackWorkQuery;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Predicate;

public interface ReportYoutrackWork {
    boolean writeReport(OutputStream buffer,
                        Report report,
                        YoutrackWorkQuery query,
                        Predicate<Long> isCancel) throws IOException;
}
