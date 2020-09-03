package ru.protei.portal.core.report.caseobjects;

import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.utils.TimeFormatter;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.function.Predicate;

public interface ReportCase {
    boolean writeReport(OutputStream buffer,
                        Report report,
                        CaseQuery query,
                        DateFormat dateFormat,
                        TimeFormatter timeFormatter,
                        Predicate<Long> isCancel) throws IOException;
}
