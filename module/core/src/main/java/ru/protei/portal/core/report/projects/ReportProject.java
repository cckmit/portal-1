package ru.protei.portal.core.report.projects;

import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.struct.ReportProjectWithLastComment;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.List;
import java.util.function.Predicate;

public interface ReportProject {
    boolean writeReport(OutputStream buffer, Report report, Predicate<Long> isCancel) throws IOException;
    List<ReportProjectWithLastComment> createData(CaseQuery query);
}
