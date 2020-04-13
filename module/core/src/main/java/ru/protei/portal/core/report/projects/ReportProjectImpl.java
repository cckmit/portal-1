package ru.protei.portal.core.report.projects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.struct.Project;
import ru.protei.portal.core.model.struct.ReportProjectWithLastComment;
import ru.protei.portal.core.report.ReportWriter;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReportProjectImpl implements ReportProject {

    private static Logger log = LoggerFactory.getLogger(ReportProjectImpl.class);

    @Autowired
    Lang lang;
    @Autowired
    PortalConfig config;
    @Autowired
    CaseObjectDAO caseObjectDAO;
    @Autowired
    CaseCommentDAO caseCommentDAO;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @Override
    public boolean writeReport(OutputStream buffer, Report report) throws IOException {

        int count = caseObjectDAO.countByQuery(report.getCaseQuery());

        Lang.LocalizedLang localizedLang = lang.getFor(Locale.forLanguageTag(report.getLocale()));

        if (count < 1) {
            log.debug("writeReport : reportId={} has no corresponding case objects", report.getId());
            ReportWriter<ReportProjectWithLastComment> writer = new ExcelReportWriter(localizedLang);
            writer.createSheet();
            writer.collect(buffer);
            return true;
        }

        log.debug("writeReport : reportId={} has {} case objects to process", report.getId(), count);

        ReportWriter<ReportProjectWithLastComment> writer = new ExcelReportWriter(localizedLang);

        int sheetNumber = writer.createSheet();

        if (writeReport(writer, sheetNumber, report, count)) {
            writer.collect(buffer);
            return true;
        } else {
            writer.close();
            return false;
        }
    }

    private boolean writeReport(ReportWriter<ReportProjectWithLastComment> writer, int sheetNumber, Report report, int count) {

        final int step = config.data().reportConfig().getChunkSize();
        final int limit = count;
        int offset = 0;

        while (offset < limit) {
            int amount = offset + step < limit ? step : limit - offset;
            CaseQuery query = report.getCaseQuery();
            query.setOffset(offset);
            query.setLimit(amount);
            List<ReportProjectWithLastComment> data = createData(query);
            try {
                writer.write(sheetNumber, data);
            } catch (Throwable th) {
                log.warn("writeReport : fail to process chunk [{} - {}] : reportId={} {}", offset, amount, report.getId(), th);
                return false;
            }
            offset += step;
        }

        return true;
    }

    public List<ReportProjectWithLastComment> createData(CaseQuery query) {
        List<CaseObject> cases = caseObjectDAO.getCases(query);

        jdbcManyRelationsHelper.fill(cases, "locations");

        List<CaseComment> lastNotNullTextCommentsForReport = caseCommentDAO
                .getLastNotNullTextCommentsForReport(
                        cases.stream().map(CaseObject::getId).collect(Collectors.toList()));
        Map<Long, CaseComment> CaseIdToCaseComment = lastNotNullTextCommentsForReport
                .stream().collect(Collectors.toMap(CaseComment::getCaseId, Function.identity()));

        return cases.stream().map(caseObject ->
                new ReportProjectWithLastComment(
                        Project.fromCaseObject(caseObject),
                        CaseIdToCaseComment.get(caseObject.getId())))
                .collect(Collectors.toList());
    }
}
