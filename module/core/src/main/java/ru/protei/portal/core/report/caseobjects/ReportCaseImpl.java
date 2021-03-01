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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.size;
import static ru.protei.portal.core.model.helper.CollectionUtils.stream;

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
    @Autowired
    PlanToCaseObjectDAO planToCaseObjectDAO;

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

            if (isOnlyPlanQuery(query)) {
                List<CaseObjectReportRequest> comments = processByPlanId(query, report);
                writer.write( sheetNumber, comments );
            } else {
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
            }

            writer.collect( buffer );
            return true;
        } catch (Exception ex) {
            log.warn( "writeReport : fail to process chunk [{} - {}] : reportId={} query: {} ",
                                                offset, limit, report.getId(), query, ex );
            return false;
        }
    }

    public List<CaseObjectReportRequest> processByPlanId(CaseQuery query, Report report) {
        return process(() -> planToCaseObjectDAO.getSortedListByPlanId(query.getPlanId()),
                planToCaseObject -> makeRequest(planToCaseObject.getCaseObject(), query, report));
    }

    public List<CaseObjectReportRequest> processChunk(CaseQuery query, Report report) {
        return process(() -> caseObjectDAO.getCases(query),
                caseObject -> makeRequest(caseObject, query, report));
    }

    public <T> List<CaseObjectReportRequest> process(Supplier<List<T>> get, Function<T, CaseObjectReportRequest> map) {
        return stream(get.get()).map(map).collect(Collectors.toList());
    }

    public CaseObjectReportRequest makeRequest(CaseObject caseObject, CaseQuery query, Report report) {
        CaseCommentQuery commentQuery = new CaseCommentQuery();
        commentQuery.addCaseObjectId( caseObject.getId() );
        commentQuery.setTimeElapsed(true);
        List<CaseComment> caseComments = caseCommentDAO.getCaseComments( commentQuery );

        HistoryQuery historyQuery = new HistoryQuery();
        if (Boolean.TRUE.equals(query.isCheckImportanceHistory())) {
            historyQuery.addValueType(En_HistoryType.CASE_IMPORTANCE);
        }
        historyQuery.setCaseObjectId(caseObject.getId());
        historyQuery.addValueType(En_HistoryType.CASE_STATE);
        historyQuery.setHistoryActions(Arrays.asList(En_HistoryAction.ADD, En_HistoryAction.CHANGE));
        List<History> histories = historyDAO.getListByQuery(historyQuery);

        List<CaseTag> caseTags = report.isWithTags() ? caseTagDAO.getListByQuery(new CaseTagQuery(caseObject.getId())) : Collections.emptyList();
        List<CaseLink> caseLinks = report.isWithLinkedIssues() ? caseLinkDAO.getListByQuery(new CaseLinkQuery(caseObject.getId(), report.isRestricted())) : Collections.emptyList();

        return new CaseObjectReportRequest( caseObject, caseComments, histories, caseTags, caseLinks, query.getCreatedRange(), query.getModifiedRange() );
    }

    private boolean isOnlyPlanQuery(CaseQuery query) {
        return query.getPlanId() != null;
    }
}
