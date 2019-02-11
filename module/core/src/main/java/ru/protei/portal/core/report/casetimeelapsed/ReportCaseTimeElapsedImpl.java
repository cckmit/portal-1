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
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.report.ReportWriter;
import ru.protei.portal.core.utils.TimeFormatter;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.*;
import java.util.stream.Collectors;

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

        List<CaseShortView> caseIds = caseShortViewDAO.partialGetCases(caseQuery, "id");

        if (CollectionUtils.isEmpty(caseIds)) {
            log.debug("writeReport : reportId={} has no corresponding case objects", report.getId());
            ReportWriter<CaseCommentTimeElapsedSum> writer = new ExcelReportWriter(localizedLang, dateFormat, timeFormatter);
            writer.setSheetName(writer.createSheet(), localizedLang.get("no_data"));
            writer.collect(buffer);
            return true;
        }

        CaseCommentQuery caseCommentQuery = new CaseCommentQuery();
        caseCommentQuery.useSort(En_SortField.author_id, En_SortDir.DESC);
        caseCommentQuery.setTimeElapsedNotNull(true);
        caseCommentQuery.setCaseObjectIds(caseIds.stream()
                .map(CaseShortView::getId)
                .collect(Collectors.toList())
        );
        caseCommentQuery.setAuthorIds(caseQuery.getCommentAuthorIds());

        ReportWriter<CaseCommentTimeElapsedSum> writer = new ExcelReportWriter(localizedLang, dateFormat, timeFormatter);

        if (writeReport(writer, report, caseCommentQuery)) {
            writer.collect(buffer);
            return true;
        } else {
            writer.close();
            return false;
        }
    }

    private boolean writeReport(ReportWriter<CaseCommentTimeElapsedSum> writer, Report report, CaseCommentQuery query) {

        final Processor processor = new Processor();
        final int step = config.data().reportConfig().getChunkSize();
        int offset = 0;

        while (true) {
            try {
                query.setOffset(offset);
                query.setLimit(step);
                List<CaseCommentTimeElapsedSum> comments = caseCommentTimeElapsedSumDAO.getListByQuery(query);
                boolean isThisTheEnd = comments.size() < step;
                processor.writeChunk(writer, comments, isThisTheEnd);
                offset += step;
                if (isThisTheEnd) {
                    // hold ur breath
                    break;
                }
            } catch (Throwable th) {
                log.warn("writeReport : fail to process chunk [{} - {}] : reportId={}", offset, offset + step, report.getId());
                log.warn("writeReport : fail to process chunk", th);
                return false;
            }
        }

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
