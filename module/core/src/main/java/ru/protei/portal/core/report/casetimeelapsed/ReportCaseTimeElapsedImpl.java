package ru.protei.portal.core.report.casetimeelapsed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.dao.CaseCommentTimeElapsedSumDAO;
import ru.protei.portal.core.model.dao.CaseShortViewDAO;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.CaseCommentTimeElapsedSum;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.report.ReportWriter;
import ru.protei.portal.core.utils.TimeFormatter;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.*;

import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;

public class ReportCaseTimeElapsedImpl implements ReportCaseTimeElapsed {

    private static Logger log = LoggerFactory.getLogger(ReportCaseTimeElapsedImpl.class);

    @Autowired
    Lang lang;
    @Autowired
    PortalConfig config;
    @Autowired
    CaseShortViewDAO caseShortViewDAO;
    @Autowired
    CaseCommentTimeElapsedSumDAO caseCommentTimeElapsedSumDAO;

    @Override
    public boolean writeReport(OutputStream buffer, Report report, DateFormat dateFormat, TimeFormatter timeFormatter) throws IOException {

        CaseQuery caseQuery = report.getCaseQuery();
        if (caseQuery == null) {
            log.debug("writeReport : reportId={} has invalid queries: caseQuery={}, aborting task",
                    report.getId(), caseQuery);
            return false;
        }

        Lang.LocalizedLang localizedLang = lang.getFor(Locale.forLanguageTag(report.getLocale()));
        ReportWriter<CaseCommentTimeElapsedSum> writer = new ExcelReportWriter(localizedLang, dateFormat, timeFormatter);

        caseQuery.useSort(En_SortField.author_id, En_SortDir.DESC);

        final Processor processor = new Processor();
        final int step = config.data().reportConfig().getChunkSize();
        int offset = 0;

        log.info( "writeReport(): Start report {}", report );
        try {
            while (true) {
                caseQuery.setOffset( offset );
                caseQuery.setLimit( step );
                List<CaseCommentTimeElapsedSum> comments = caseCommentTimeElapsedSumDAO.getListByQuery( caseQuery );
                boolean isThisTheEnd = comments.size() < step;
                processor.writeChunk(writer, comments, isThisTheEnd);
                offset += step;
                if (isThisTheEnd) {
                    if(offset==step && isEmpty(comments)){
                        writer.setSheetName(writer.createSheet(), localizedLang.get("no_data"));
                    }
                    // hold ur breath
                    break;
                }
            }
        } catch (Throwable th) {
            writer.close();
            log.warn( "writeReport : fail to process chunk [{} - {}] : reportId={} e: ", offset, offset + step, report.getId(), th );
            return false;
        }

        writer.collect( buffer );
        return true;
    }

    private class Processor {

        private Map<Long, Integer> author2sheet = new HashMap<>();
        private List<CaseCommentTimeElapsedSum> data = new ArrayList<>();
        private Integer sheetNumberForData = null;
        private long summaryForSheetNumber = 0L;

        public void writeChunk(ReportWriter<CaseCommentTimeElapsedSum> writer, List<CaseCommentTimeElapsedSum> comments, boolean isEndOfChain) {
            for (CaseCommentTimeElapsedSum comment : comments) {
                Long authorId = comment.getAuthorId();
                Integer sheetNumberForAuthor = author2sheet.get(authorId);
                if (sheetNumberForAuthor == null) {
                    writeDataIfNeeded(writer);
                    sheetNumberForAuthor = writer.createSheet();
                    writer.setSheetName(sheetNumberForAuthor, comment.getAuthorDisplayName());
                    author2sheet.put(authorId, sheetNumberForAuthor);
                }
                if (sheetNumberForData != null && !Objects.equals(sheetNumberForData, sheetNumberForAuthor)) {
                    addSummaryIfNeeded();
                    writeDataIfNeeded(writer);
                }
                sheetNumberForData = sheetNumberForAuthor;
                data.add(comment);
                summaryForSheetNumber += comment.getTimeElapsedSum();
            }
            if (isEndOfChain) {
                addSummaryIfNeeded();
            }
            writeDataIfNeeded(writer);
        }

        private void writeDataIfNeeded(ReportWriter<CaseCommentTimeElapsedSum> writer) {
            if (sheetNumberForData != null && data.size() > 0) {
                writer.write(sheetNumberForData, data);
                data.clear();
            }
        }

        private void addSummaryIfNeeded() {
            if (summaryForSheetNumber > 0) {
                CaseCommentTimeElapsedSum summary = new CaseCommentTimeElapsedSum();
                summary.setTimeElapsedSum(summaryForSheetNumber);
                data.add(summary);
                summaryForSheetNumber = 0;
            }
        }
    }
}
