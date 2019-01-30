package ru.protei.portal.core.report.casetimeelapsed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.CaseQuery;

import java.io.ByteArrayOutputStream;
import java.util.*;

public class ReportCaseCompletionTime {


    public ReportCaseCompletionTime( Report report, CaseCommentDAO caseCommentDAO ) {
        this.report = report;
        this.caseCommentDAO = caseCommentDAO;
        caseQuery = report.getCaseQuery();

    }

    public boolean writeReport( ByteArrayOutputStream buffer ) {

        List<CaseComment> list = caseCommentDAO.reportCaseCompletionTime( caseQuery.getProductIds().get( 0 ) );

        for (CaseComment comment : list) {
            Case aCase = map.get( comment.getCaseId() );
            if (aCase == null) {
                aCase = new Case();
                map.put( comment.getCaseId(), aCase );
                cases.add( aCase);
            }
            mapCase( aCase, comment );
        }




        return false;
    }

    private Case mapCase( Case aCase, CaseComment comment ) {
        aCase.add( comment.getCreated(), comment.getCaseStateId() );
        aCase.caseId = comment.getCaseId();//TODO DEBUG
        return aCase;
    }

    Map<Long, Case> map = new HashMap<>();
    List<Case> cases = new ArrayList<>();//TODO DEBUG

    private Report report;
    private CaseCommentDAO caseCommentDAO;
    private CaseQuery caseQuery;
    private static Logger log = LoggerFactory.getLogger( ReportCaseCompletionTime.class );

    class Case {
        public Long caseId;//TODO DEBUG
        Status lastStatus;

        public void add( Date created, Long caseStateId ) {
            if (lastStatus != null) {
                lastStatus.setStop( created );
            }
            lastStatus = new Status( created, caseStateId );
            statuses.add( lastStatus );
        }

        List<Status> statuses = new ArrayList<>();
    }

    class Status {

        public Status( Date created, Long caseStateId ) {
            this.created = created;
            this.caseStateId = caseStateId;
        }

        public void setStop( Date stop ) {
            this.stop = stop;
        }

        private Date stop;
        private Date created;
        private Long caseStateId;
    }

}
