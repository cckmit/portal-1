package ru.protei.portal.core.service.report.managertime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.dao.CaseCommentCaseObjectDAO;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseCommentCaseObject;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.service.report.ReportWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.*;

public class ReportCrmManagerTimeServiceImpl implements ReportCrmManagerTimeService {

    private static Logger log = LoggerFactory.getLogger(ReportCrmManagerTimeServiceImpl.class);

    @Autowired
    Lang lang;
    @Autowired
    PortalConfig config;
    @Autowired
    CaseCommentCaseObjectDAO caseCommentCaseObjectDAO;

    @Override
    public boolean writeExport(ByteArrayOutputStream buffer, Report report, DateFormat dateFormat) throws IOException {

        CaseQuery caseQuery = report.getCaseQuery();
        CaseCommentQuery caseCommentQuery = report.getCaseCommentQuery();
        if (caseQuery == null || caseCommentQuery == null) {
            log.debug("writeReport : reportId={} has invalid queries: caseQuery={}, caseCommentQuery={}, aborting task",
                    report.getId(), caseQuery, caseCommentQuery);
            return false;
        }
        caseCommentQuery.useSort(En_SortField.person_id, En_SortDir.DESC);
        caseCommentQuery.setTimeElapsedNotNull(true);

        Long count = caseCommentCaseObjectDAO.count(caseQuery, caseCommentQuery);

        if (count == null || count < 1) {
            log.debug("writeReport : reportId={} has no corresponding case comments", report.getId());
            return true;
        }

        if (count > Integer.MAX_VALUE) {
            log.debug("writeReport : reportId={} has too many corresponding case comments: {}, aborting task", report.getId(), count);
            return false;
        }

        log.debug("writeReport : reportId={} has {} case comments to procees", report.getId(), count);

        Lang.LocalizedLang localizedLang = lang.getFor(Locale.forLanguageTag(report.getLocale()));

        ReportWriter<CaseCommentCaseObject> writer = new ExcelReportWriter(localizedLang, dateFormat);

        if (writeReport(writer, report, count)) {
            writer.collect(buffer);
            return true;
        } else {
            writer.close();
            return false;
        }
    }

    private boolean writeReport(ReportWriter<CaseCommentCaseObject> writer, Report report, Long count) {

        final Processor processor = new Processor();
        final int step = config.data().reportConfig().getChunkSize();
        final int limit = count.intValue();
        int offset = 0;

        while (offset < limit) {
            int amount = offset + step < limit ? step : limit - offset;
            try {
                CaseCommentQuery query = report.getCaseCommentQuery();
                query.setOffset(offset);
                query.setLimit(amount);
                processor.writeChunk(writer, report.getCaseQuery(), query);
                offset += step;
            } catch (Throwable th) {
                log.warn("writeReport : fail to process chunk [{} - {}] : reportId={} {}", offset, amount, report.getId(), th);
                return false;
            }
        }

        return true;
    }

    private class Processor {

        private Map<Long, Integer> author2sheet = new HashMap<>();
        private List<CaseCommentCaseObject> data = new ArrayList<>();
        private Integer sheetNumberForData = null;

        public void writeChunk(ReportWriter<CaseCommentCaseObject> writer, CaseQuery caseQuery, CaseCommentQuery caseCommentQuery) {
            List<CaseCommentCaseObject> comments = caseCommentCaseObjectDAO.getListByQueries(caseQuery, caseCommentQuery);
            for (CaseCommentCaseObject comment : comments) {
                CaseComment caseComment = comment.getCaseComment();
                Long authorId = caseComment.getAuthorId();
                Integer sheetNumberForAuthor = author2sheet.get(authorId);
                if (sheetNumberForAuthor == null) {
                    writeDataIfNeeded(writer);
                    sheetNumberForAuthor = writer.createSheet();
                    String name = caseComment.getAuthor() == null ?
                            String.valueOf(authorId) :
                            caseComment.getAuthor().getDisplayName();
                    writer.setSheetName(sheetNumberForAuthor, name);
                    author2sheet.put(authorId, sheetNumberForAuthor);
                }
                if (!Objects.equals(sheetNumberForData, sheetNumberForAuthor)) {
                    writeDataIfNeeded(writer);
                }
                sheetNumberForData = sheetNumberForAuthor;
                data.add(comment);
            }
            writeDataIfNeeded(writer);
        }

        private void writeDataIfNeeded(ReportWriter<CaseCommentCaseObject> writer) {
            if (sheetNumberForData != null && data.size() > 0) {
                writer.write(sheetNumberForData, data);
                data.clear();
            }
        }
    }
}
