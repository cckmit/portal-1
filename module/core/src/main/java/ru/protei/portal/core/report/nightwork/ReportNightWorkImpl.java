package ru.protei.portal.core.report.nightwork;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.dao.CaseCommentNightWorkDAO;
import ru.protei.portal.core.model.dao.ReportDAO;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseCommentNightWork;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.report.ReportWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.size;

public class ReportNightWorkImpl implements ReportNightWork {

    private static Logger log = LoggerFactory.getLogger(ReportNightWorkImpl.class);

    @Autowired
    Lang lang;
    @Autowired
    PortalConfig config;
    @Autowired
    CaseCommentDAO caseCommentDAO;
    @Autowired
    CaseCommentNightWorkDAO caseCommentNightWorkDAO;
    @Autowired
    ReportDAO reportDAO;

    @Override
    public boolean writeReport(OutputStream buffer,
                               Report report,
                               CaseQuery caseQuery,
                               Predicate<Long> isCancel) throws IOException {
        log.info("writeReport : reportId={}", report.getId());
        if (caseQuery == null) {
            log.debug("writeReport : reportId={} has invalid queries: caseQuery={}, aborting task",
                    report.getId(), caseQuery);
            return false;
        }

        Lang.LocalizedLang localizedLang = lang.getFor(Locale.forLanguageTag(report.getLocale()));

        final int limit = config.data().reportConfig().getChunkSize();
        int offset = 0;

        caseQuery.setSortDir(En_SortDir.ASC);
        caseQuery.setSortField(En_SortField.day);

        log.info( "writeReport(): Start report {}", report );
        try (ReportWriter<CaseCommentNightWork> writer = new ExcelReportWriter(localizedLang)) {

            int sheetNumber = writer.createSheet();

            while (true) {
                if (isCancel.test(report.getId())) {
                    log.info( "writeReport(): Cancel processing of report {}", report.getId() );
                    return true;
                }
                caseQuery.setOffset( offset );
                caseQuery.setLimit( limit );
                List<CaseCommentNightWork> comments = caseCommentNightWorkDAO.getListByQuery( caseQuery );
                fillLastCaseComment(comments);
                writer.write( sheetNumber, comments );
                if (size( comments ) < limit) break;
                offset += limit;
            }

            writer.collect( buffer );
            return true;
        } catch (Throwable th) {
            log.warn( "writeReport : fail to process chunk [{} - {}] : reportId={} e: ", offset, offset + limit, report.getId(), th );
            return false;
        }
    }

    private void fillLastCaseComment(List<CaseCommentNightWork> comments) {
        Map<Long, CaseComment> caseCommentsMap = caseCommentDAO.getListByKeys(
                comments.stream().map(CaseCommentNightWork::getLastCommentId).collect(Collectors.toList())
        ).stream().collect(Collectors.toMap(CaseComment::getId, Function.identity()));
        comments.forEach(
                caseCommentNightWork -> caseCommentNightWork.setLastCaseComment(caseCommentsMap.get(caseCommentNightWork.getLastCommentId())));
    }
}
