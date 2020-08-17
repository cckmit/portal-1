package ru.protei.portal.core.report.caseobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.CaseTagDAO;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.CaseTagQuery;
import ru.protei.portal.core.model.struct.CaseObjectReportRequest;
import ru.protei.portal.core.report.ReportWriter;
import ru.protei.portal.core.utils.TimeFormatter;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
    CaseTagDAO caseTagDAO;

    @Override
    public boolean writeReport(OutputStream buffer, Report report, DateFormat dateFormat, TimeFormatter timeFormatter,
                                    Predicate<Long> isCancel) throws IOException {
        log.info("writeReport : reportId={}", report.getId());
        Lang.LocalizedLang localizedLang = lang.getFor(Locale.forLanguageTag(report.getLocale()));

        final int limit = config.data().reportConfig().getChunkSize();
        int offset = 0;
        try (ReportWriter<CaseObjectReportRequest> writer =
                    new ExcelReportWriter(localizedLang, dateFormat, timeFormatter, report.isRestricted(), report.isWithDescription())) {

            int sheetNumber = writer.createSheet();

            while (true) {
                if (isCancel.test(report.getId())) {
                    log.info( "writeReport(): Cancel processing of report {}", report.getId() );
                    return true;
                }
                CaseQuery query = report.getCaseQuery();
                query.setOffset( offset );
                query.setLimit( limit );
                List<CaseObjectReportRequest> comments = processChunk(query);
                writer.write( sheetNumber, comments );
                if (size( comments ) < limit) break;
                offset += limit;
            }

            writer.collect( buffer );
            return true;
        } catch (Exception ex) {
            log.warn( "writeReport : fail to process chunk [{} - {}] : reportId={} query: {} ",
                                                offset, limit, report.getId(), report.getCaseQuery(), ex );
            return false;
        }
    }

    public List<CaseObjectReportRequest> processChunk(CaseQuery query ) {
        List<CaseObjectReportRequest> data = new ArrayList<>();
        List<CaseObject> cases = caseObjectDAO.getCases( query );
        for (CaseObject caseObject : emptyIfNull(cases)) {
            CaseCommentQuery commentQuery = new CaseCommentQuery();
            commentQuery.addCaseObjectId( caseObject.getId() );
            commentQuery.setCaseStateNotNull( query.isCheckImportanceHistory() == null || !query.isCheckImportanceHistory() );
            List<CaseComment> caseComments = caseCommentDAO.getCaseComments( commentQuery );

            CaseTagQuery caseTagQuery = new CaseTagQuery(caseObject.getId());
            List<CaseTag> caseTags = caseTagDAO.getListByQuery(caseTagQuery);

            data.add( new CaseObjectReportRequest( caseObject, caseComments, caseTags ) );
        }
        return data;
    }

}
