package ru.protei.portal.core.report.projects;

import ru.protei.portal.core.model.ent.Report;

import java.io.IOException;
import java.io.OutputStream;

public interface ReportProject {
    boolean writeReport(OutputStream buffer, Report report) throws IOException;
}
