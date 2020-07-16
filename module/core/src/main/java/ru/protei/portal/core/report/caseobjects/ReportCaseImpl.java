package ru.protei.portal.core.report.caseobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.ReportDAO;
import ru.protei.portal.core.model.dict.En_ReportStatus;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.struct.CaseObjectComments;
import ru.protei.portal.core.report.ReportWriter;
import ru.protei.portal.core.utils.TimeFormatter;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static ru.protei.portal.core.model.helper.CollectionUtils.*;

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
    ReportDAO reportDAO;

    @Override
    public boolean writeReport(OutputStream buffer, Report report, DateFormat dateFormat, TimeFormatter timeFormatter) throws IOException {
        log.info("writeReport : reportId={}", report.getId());
        Lang.LocalizedLang localizedLang = lang.getFor(Locale.forLanguageTag(report.getLocale()));
        ReportWriter<CaseObjectComments> writer = new ExcelReportWriter(localizedLang, dateFormat, timeFormatter, report.isRestricted(), report.isWithDescription());

        int sheetNumber = writer.createSheet();

        final int limit = config.data().reportConfig().getChunkSize();
        int offset = 0;
        try {
            while (true) {
                if (!isProcessed( report.getId() )) {
                    log.info( "writeReport(): Stop processing of report {}", report.getId() );
                    break;
                }
                CaseQuery query = report.getCaseQuery();
                query.setOffset( offset );
                query.setLimit( limit );
                List<CaseObjectComments> comments = processChunk(query);
                writer.write( sheetNumber, comments );
                if (size( comments ) < limit) break;
                offset += limit;
            }
        } catch (Exception ex) {
            log.warn( "writeReport : fail to process chunk [{} - {}] : reportId={} query: {} ", offset, limit, report.getId(), report.getCaseQuery(), ex );
            writer.close();
            return false;
        }

        writer.collect( buffer );
        return true;
    }

    private boolean isProcessed( Long id ) {
        Report report = reportDAO.partialGet( id, Report.Columns.STATUS );
        if(report == null) return false;
        if (!En_ReportStatus.PROCESS.equals( report.getStatus() )) return false;
        return true;
    }

    public List<CaseObjectComments> processChunk( CaseQuery query ) {
        List<CaseObjectComments> data = new ArrayList<>();
        List<CaseObject> cases = caseObjectDAO.getCases( query );
        for (CaseObject caseObject : emptyIfNull(cases)) {
            CaseCommentQuery commentQuery = new CaseCommentQuery();
            commentQuery.addCaseObjectId( caseObject.getId() );
            commentQuery.setCaseStateNotNull( query.isCheckImportanceHistory() == null || !query.isCheckImportanceHistory() );
            List<CaseComment> caseComments = caseCommentDAO.getCaseComments( commentQuery );
            data.add( new CaseObjectComments( caseObject, caseComments ) );
        }
        return data;
    }

}
