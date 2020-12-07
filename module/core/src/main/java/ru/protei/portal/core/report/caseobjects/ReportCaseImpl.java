package ru.protei.portal.core.report.caseobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_HistoryAction;
import ru.protei.portal.core.model.dict.En_HistoryType;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.*;
import ru.protei.portal.core.model.struct.CaseObjectReportRequest;
import ru.protei.portal.core.report.ReportWriter;
import ru.protei.portal.core.utils.TimeFormatter;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.*;
import java.util.function.Predicate;

import static ru.protei.portal.core.model.helper.CollectionUtils.emptyIfNull;
import static ru.protei.portal.core.model.helper.CollectionUtils.size;

public class ReportCaseImpl implements ReportCase {

    private static Logger log = LoggerFactory.getLogger(ReportCaseImpl.class);

    @Autowired
    Lang lang;
    @Autowired
    PortalConfig config;
    @Autowired
    CaseObjectDAO caseObjectDAO;
    @Autowired
    CaseCommentDAO caseCommentDAO;
    @Autowired
    HistoryDAO historyDAO;
    @Autowired
    CaseTagDAO caseTagDAO;
    @Autowired
    CaseLinkDAO caseLinkDAO;

    @Override
    public boolean writeReport(OutputStream buffer,
                               Report report,
                               CaseQuery query,
                               DateFormat dateFormat,
                               TimeFormatter timeFormatter,
                               Predicate<Long> isCancel) throws IOException {
        log.info("writeReport : reportId={}", report.getId());
        Lang.LocalizedLang localizedLang = lang.getFor(Locale.forLanguageTag(report.getLocale()));

        final int limit = config.data().reportConfig().getChunkSize();
        int offset = 0;
        try (ReportWriter<CaseObjectReportRequest> writer =
                    new ExcelReportWriter(localizedLang, report.isRestricted(), report.isWithDescription(),
                            report.isWithTags(), report.isWithLinkedIssues(), report.isHumanReadable(), Boolean.TRUE.equals(query.isCheckImportanceHistory()))) {

            int sheetNumber = writer.createSheet();

            while (true) {
                if (isCancel.test(report.getId())) {
                    log.info( "writeReport(): Cancel processing of report {}", report.getId() );
                    return true;
                }
                query.setOffset( offset );
                query.setLimit( limit );
                List<CaseObjectReportRequest> comments = processChunk(query, report);
                writer.write( sheetNumber, comments );
                if (size( comments ) < limit) break;
                offset += limit;
            }

            writer.collect( buffer );
            return true;
        } catch (Exception ex) {
            log.warn( "writeReport : fail to process chunk [{} - {}] : reportId={} query: {} ",
                                                offset, limit, report.getId(), query, ex );
            return false;
        }
    }

    public List<CaseObjectReportRequest> processChunk(CaseQuery query, Report report) {
        List<CaseObjectReportRequest> data = new ArrayList<>();
        List<CaseObject> cases = caseObjectDAO.getCases( query );
        for (CaseObject caseObject : emptyIfNull(cases)) {
            CaseCommentQuery commentQuery = new CaseCommentQuery();
            commentQuery.addCaseObjectId( caseObject.getId() );

            if (Boolean.TRUE.equals(query.isCheckImportanceHistory())) {
                commentQuery.addCommentType(CaseCommentQuery.CommentType.IMPORTANCE);
            }

            commentQuery.addCommentType(CaseCommentQuery.CommentType.CASE_STATE);
            commentQuery.addCommentType(CaseCommentQuery.CommentType.TIME_ELAPSED);

            List<CaseComment> caseComments = caseCommentDAO.getCaseComments( commentQuery );

            HistoryQuery historyQuery = new HistoryQuery();
            historyQuery.setCaseObjectId(caseObject.getId());
            historyQuery.setValueType(En_HistoryType.CASE_STATE);
            historyQuery.setHistoryAction(Arrays.asList(En_HistoryAction.ADD, En_HistoryAction.CHANGE));
            List<History> stateHistories = historyDAO.getListByQuery(historyQuery);

            List<CaseTag> caseTags = report.isWithTags() ? caseTagDAO.getListByQuery(new CaseTagQuery(caseObject.getId())) : Collections.emptyList();
            List<CaseLink> caseLinks = report.isWithLinkedIssues() ? caseLinkDAO.getListByQuery(new CaseLinkQuery(caseObject.getId(), report.isRestricted())) : Collections.emptyList();

            data.add( new CaseObjectReportRequest( caseObject, caseComments, stateHistories, caseTags, caseLinks, query.getCreatedRange(), query.getModifiedRange() ) );
        }
        return data;
    }

}
