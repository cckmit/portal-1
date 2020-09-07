package ru.protei.portal.core.report.absence;

import ru.protei.portal.core.model.query.AbsenceQuery;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;

public interface ReportAbsence {
    boolean writeReport(OutputStream buffer, AbsenceQuery query) throws IOException;
}
