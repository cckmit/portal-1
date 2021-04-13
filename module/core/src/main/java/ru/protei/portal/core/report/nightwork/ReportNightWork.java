package ru.protei.portal.core.report.nightwork;

import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.CaseQuery;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Predicate;

public interface ReportNightWork {
    boolean writeReport(OutputStream buffer,
                        Report report,
                        CaseQuery query,
                        Predicate<Long> isCancel) throws IOException;
}
