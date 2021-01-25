package ru.protei.portal.core.report.projects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.dao.DevUnitDAO;
import ru.protei.portal.core.model.dao.ProjectDAO;
import ru.protei.portal.core.model.dao.ReportDAO;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.query.ProjectQuery;
import ru.protei.portal.core.model.struct.ReportProjectWithComments;
import ru.protei.portal.core.report.ReportWriter;
import ru.protei.portal.core.utils.EnumLangUtil;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ReportProjectImpl implements ReportProject {

    private static Logger log = LoggerFactory.getLogger(ReportProjectImpl.class);

    @Autowired
    Lang lang;
    @Autowired
    PortalConfig config;
    @Autowired
    ProjectDAO projectDAO;
    @Autowired
    DevUnitDAO devUnitDAO;
    @Autowired
    CaseCommentDAO caseCommentDAO;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;
    @Autowired
    ReportDAO reportDAO;

    @Override
    public boolean writeReport(OutputStream buffer,
                               Report report,
                               ProjectQuery query,
                               Predicate<Long> isCancel) throws IOException {

        int count = projectDAO.countByQuery(query);

        Lang.LocalizedLang localizedLang = lang.getFor(Locale.forLanguageTag(report.getLocale()));

        if (count < 1) {
            log.debug("writeReport : reportId={} has no corresponding projects", report.getId());
            ReportWriter<ReportProjectWithComments> writer = new ExcelReportWriter(localizedLang, new EnumLangUtil(lang),
                    query.getCommentCreationRange() != null, config.data().reportConfig().getProjectLimitComments());
            writer.createSheet();
            writer.collect(buffer);
            return true;
        }

        log.debug("writeReport : reportId={} has {} projects to process", report.getId(), count);

        try (ReportWriter<ReportProjectWithComments> writer = new ExcelReportWriter(localizedLang, new EnumLangUtil(lang),
                query.getCommentCreationRange() != null, config.data().reportConfig().getProjectLimitComments())) {
            int sheetNumber = writer.createSheet();
            if (writeReport(writer, sheetNumber, report.getId(), query, count, isCancel)) {
                writer.collect(buffer);
                return true;
            }
            return false;
        } catch (Exception ex) {
            log.error("writeReport : fail to process reportId={} query: {} ",
                    report.getId(), query, ex);
            return false;
        }
    }

    private boolean writeReport(ReportWriter<ReportProjectWithComments> writer,
                                int sheetNumber,
                                Long reportId,
                                ProjectQuery query,
                                int count,
                                Predicate<Long> isCancel) {

        final int step = config.data().reportConfig().getChunkSize();
        final int limit = count;
        int offset = 0;

        while (offset < limit) {
            if (isCancel.test(reportId)) {
                log.info("writeReport(): Cancel processing of report {}", reportId);
                return true;
            }
            int amount = offset + step < limit ? step : limit - offset;
            query.setOffset(offset);
            query.setLimit(amount);
            List<ReportProjectWithComments> data = createData(query);
            try {
                writer.write(sheetNumber, data);
            } catch (Throwable th) {
                log.error("writeReport : fail to process chunk [{} - {}] : reportId={}", offset, amount, reportId, th);
                return false;
            }
            offset += step;
        }

        return true;
    }

    public List<ReportProjectWithComments> createData(ProjectQuery query) {
        List<Project> projects = projectDAO.getProjects(query);
        if (projects.isEmpty()) {
            return new ArrayList<>();
        }

        jdbcManyRelationsHelper.fill(projects, "locations");
        projects.forEach(project -> project.setProductDirections(new HashSet<>(devUnitDAO.getProjectDirections(project.getId()))));

        List<Long> ids = projects.stream().map(Project::getId).collect(Collectors.toList());
        List<CaseComment> lastNotNullTextCommentsForReport = caseCommentDAO
                .getLastNotNullTextPartialCommentsForReport(ids);
        Map<Long, CaseComment> caseIdToLastCaseComment = lastNotNullTextCommentsForReport
                .stream().collect(Collectors.toMap(CaseComment::getCaseId, Function.identity()));

        Map<Long, List<CaseComment>> caseIdToCaseComment;
        if (query.getCommentCreationRange() != null) {
            CaseCommentQuery caseCommentQuery = new CaseCommentQuery();
            caseCommentQuery.setCaseObjectIds(ids);
            caseCommentQuery.setCreationRange(query.getCommentCreationRange());
            caseCommentQuery.setSortDir(En_SortDir.DESC);
            caseCommentQuery.setSortField(En_SortField.creation_date);
            List<CaseComment> caseComments = caseCommentDAO.getPartialCommentsForReport(caseCommentQuery);
            caseIdToCaseComment = caseComments.stream().collect(Collectors.groupingBy(CaseComment::getCaseId));
        } else {
            caseIdToCaseComment = null;
        }

        return projects.stream().map(project ->
                new ReportProjectWithComments(project,
                        caseIdToLastCaseComment.get(project.getId()),
                        caseIdToCaseComment != null ? caseIdToCaseComment.get(project.getId()) : null)
                ).collect(Collectors.toList());
    }
}
