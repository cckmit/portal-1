package ru.protei.portal.core.report.dutylog;

import ru.protei.portal.core.model.query.DutyLogQuery;

import java.io.IOException;
import java.io.OutputStream;

public interface ReportDutyLog {
    boolean writeReport(OutputStream buffer, DutyLogQuery query) throws IOException;
}
