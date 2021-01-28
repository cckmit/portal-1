package ru.protei.portal.core.report.projects;

import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.struct.ReportProjectWithComments;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.function.Predicate;

public interface ReportProject {
    boolean writeReport(OutputStream buffer,
                        Report report,
                        ProjectQuery query,
                        Predicate<Long> isCancel) throws IOException;

    List<ReportProjectWithComments> createData(ProjectQuery query);
}
