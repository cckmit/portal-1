package ru.protei.portal.core.report.projects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.struct.Project;
import ru.protei.portal.core.model.struct.ReportProjectWithLastComment;
import ru.protei.portal.core.report.ReportWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

        log.debug("writeReport : reportId={} has {} case objects to procees", report.getId(), count);

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
            try {
                CaseQuery query = report.getCaseQuery();
                query.setOffset(offset);
                query.setLimit(amount);
                writeReportChunk(writer, sheetNumber, query);
                offset += step;
            } catch (Throwable th) {
                log.warn("writeReport : fail to process chunk [{} - {}] : reportId={} {}", offset, amount, report.getId(), th);
                return false;
            }
        }

        return true;
    }

    private void writeReportChunk(ReportWriter<ReportProjectWithLastComment> writer, int sheetNumber, CaseQuery query) {
        List<ReportProjectWithLastComment> data = new ArrayList<>();
        caseObjectDAO.getCases(query).forEach(caseObject -> {
            CaseCommentQuery commentQuery = new CaseCommentQuery();
            commentQuery.addCaseObjectId(caseObject.getId());
            commentQuery.setCaseStateNotNull(false);
            List<CaseComment> caseComments = caseCommentDAO.getCaseComments(commentQuery);
            data.add(new ReportProjectWithLastComment(
                    Project.fromCaseObject(caseObject),
                    caseComments.isEmpty() ?
                            null :
                            caseComments.get(caseComments.size()-1)));
        });
        writer.write(sheetNumber, data);
    }
}
