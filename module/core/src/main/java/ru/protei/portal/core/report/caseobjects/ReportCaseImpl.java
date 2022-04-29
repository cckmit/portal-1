package ru.protei.portal.core.report.caseobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CaseObjectReportWorkType;
import ru.protei.portal.core.model.dict.En_HistoryAction;
import ru.protei.portal.core.model.dict.En_HistoryType;
import ru.protei.portal.core.model.dict.En_TimeElapsedType;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.*;
import ru.protei.portal.core.model.struct.caseobjectreport.CaseObjectReportRequest;
import ru.protei.portal.core.model.struct.caseobjectreport.CaseObjectReportRow;
import ru.protei.portal.core.model.struct.caseobjectreport.CaseObjectReportWork;
import ru.protei.portal.core.report.ReportWriter;
import ru.protei.portal.core.utils.EnumLangUtil;
import ru.protei.portal.core.utils.TimeFormatter;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.dict.En_CaseObjectReportWorkType.*;
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
        try (ReportWriter<CaseObjectReportRow> writer =
                    new ExcelReportWriter(localizedLang, new EnumLangUtil(lang), report.isRestricted(), report.isWithDescription(),
                            report.isWithTags(), report.isWithLinkedIssues(), report.isHumanReadable(),
                            Boolean.TRUE.equals(query.isCheckImportanceHistory()), report.isWithDeadlineAndWorkTrigger(),
                            report.getCaseObjectReportWorkTypes().contains(TYPE),
                            report.getCaseObjectReportWorkTypes().contains(AUTHOR))) {

            int sheetNumber = writer.createSheet();

            while (true) {
                if (isCancel.test(report.getId())) {
                    log.info( "writeReport(): Cancel processing of report {}", report.getId() );
                    return true;
                }
                query.setOffset( offset );
                query.setLimit( limit );
                List<CaseObjectReportRow> comments = processChunk(query, report);
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

    public List<CaseObjectReportRow> processChunk(CaseQuery query, Report report) {
        return stream(caseObjectDAO.getCases(query))
                .map(caseObject -> makeRows(caseObject, query, report))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<CaseObjectReportRow> makeRows(CaseObject caseObject, CaseQuery query, Report report) {
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

        CaseObjectReportRequest caseObjectReportRequest = new CaseObjectReportRequest(caseObject, caseComments, histories, caseTags, caseLinks, query.getCreatedRange(), query.getModifiedRange());

        List<CaseObjectReportRow> rows = new ArrayList<>();
        rows.add(caseObjectReportRequest);
        if (CollectionUtils.isNotEmpty(report.getCaseObjectReportWorkTypes()) ) {
            rows.addAll(makeCaseObjectReportWork(caseComments, report.getCaseObjectReportWorkTypes()));
        }
        return rows;
    }

    private List<CaseObjectReportRow> makeCaseObjectReportWork(List<CaseComment> comments,
                                                               Set<En_CaseObjectReportWorkType> workTypes) {

        Map<Optional<En_TimeElapsedType>, Map<Optional<Person>, Long>> collect = stream(comments)
                .filter(comment -> comment.getTimeElapsedType() != null && comment.getTimeElapsed() != null)
                .collect(Collectors.groupingBy(c -> makeTypeGrouping(c, workTypes),
                            Collectors.groupingBy(c -> makeAuthorGrouping(c, workTypes),
                                Collectors.reducing(0L, CaseComment::getTimeElapsed, Long::sum)
                        ))
                );

        List<CaseObjectReportRow> list = new ArrayList<>();
        collect.forEach((optType, innerMap) ->
                innerMap.forEach((optAuthor, time) -> list.add(new CaseObjectReportWork(
                        time,
                        optType.orElse(null),
                        optAuthor.map(Person::getDisplayName).orElse(null))))
        );
        return list;
    }

    private Optional<En_TimeElapsedType> makeTypeGrouping(CaseComment c, Set<En_CaseObjectReportWorkType> workTypes) {
        return workTypes.contains(TYPE) ? Optional.of(c.getTimeElapsedType()) : Optional.empty();
    }

    private Optional<Person> makeAuthorGrouping(CaseComment c, Set<En_CaseObjectReportWorkType> workTypes) {
        return workTypes.contains(AUTHOR) ? Optional.of(c.getAuthor()) : Optional.empty();
    }
}
