package ru.protei.portal.core.report.casetimeelapsed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.helper.CollectionUtils;
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
        return false;
    }

    public void run() {

        intervals = makeIntervals( caseQuery.getFrom(), caseQuery.getTo(), DAY );

        if (CollectionUtils.size( intervals ) > 100) {
            //TODO splitе requests
        }

        List<CaseComment> comments = caseCommentDAO.reportCaseCompletionTime(
                caseQuery.getProductIds().get( 0 ),
                caseQuery.getFrom(),
                caseQuery.getTo(),
                caseQuery.getStateIds()
        );

        cases = groupBayIssues( comments );

        Set<Integer> ignoredStates = new HashSet<Integer>( caseQuery.getStateIds() );
        for (Interval interval : intervals) {
            interval.fill( cases, ignoredStates );
        }


        int stop = 0;
    }

    public List<Case> getCases() {
        return cases;
    }

    public static List<Interval> makeIntervals( Date fromdate, Date toDate, long step ) {
//        long from = fromdate.getTime() - fromdate.getTime() % step; //Timezone
//        long deltaTo = toDate.getTime() % step;
//        long to = toDate.getTime() - deltaTo + step;

        long from = fromdate.getTime();
        long to = toDate.getTime();
        ArrayList<Interval> intervals = new ArrayList<Interval>();
        for (; from < to; from = from + step) {
            intervals.add( new Interval( from, from + step ) );
        }

        return intervals;
    }

    public static List<Case> groupBayIssues( List<CaseComment> comments ) {
        List<Case> cases = new ArrayList<>();
        Map<Long, Case> map = new HashMap<>();
        for (CaseComment comment : comments) {
            Case aCase = map.get( comment.getCaseId() );
            if (aCase == null) {
                aCase = new Case();
                map.put( comment.getCaseId(), aCase );
                cases.add( aCase );
            }
            mapCase( aCase, comment );
        }
        return cases;
    }

    public List<Interval> getIntervals() {
        return intervals;

    }

    private static Case mapCase( Case aCase, CaseComment comment ) {
        aCase.add( comment.getCreated(), comment.getCaseStateId().intValue() );
        aCase.caseId = comment.getCaseId();//TODO DEBUG
        return aCase;
    }


    List<Case> cases = new ArrayList<>();//TODO DEBUG
    List<Interval> intervals;
    public static final long SEC = 1000L;
    public static final long MINUTE = 60 * SEC;
    public static final long HOUR = 60 * MINUTE;
    public static final long DAY = 24 * HOUR;
    private Report report;
    private CaseCommentDAO caseCommentDAO;
    private CaseQuery caseQuery;
    private static Logger log = LoggerFactory.getLogger( ReportCaseCompletionTime.class );

    public static class Interval {

        public Interval( long from, long to ) {
            this.from = from;
            this.to = to;
        }

        public void fill( List<Case> cases, Set<Integer> ignoredStates ) {
            for (Case aCase : cases) {
                long time = aCase.getTime( this, ignoredStates );
                if (time <= 0) {
                    continue;
                }
                casesCount++;
                summTime += time;
                if (time < minTime || minTime == 0) minTime = time;
                if (time > maxTime || maxTime == 0) maxTime = time;
            }
        }

        public long from;

        public long to;

        public int casesCount;
        public long summTime;
        public long maxTime;
        public long minTime;

        @Override
        public String toString() {
            return "Interval{" +
                    "from=" + new Date(from) +
                    ", to=" + new Date(to) +
                    ", casesCount=" + casesCount +
                    ", summTime=" + summTime +
                    ", maxTime=" + maxTime +
                    ", minTime=" + minTime +
                    '}';
        }
    }

    public static class Case {
        public long getTime( Interval interval, Set<Integer> ignoredStateIds ) {
            long time = 0;
            for (Status status : statuses) {
                if (ignoredStateIds.contains( status.caseStateId )) continue;
                if (status.to != null && status.to < interval.from) continue;
                if (interval.to <= status.from) continue;//исключить пересечение по концу интервала

                long delta = interval.to - interval.from;
                if (status.from > interval.from) delta = delta - (interval.from - status.from);
                if (status.to != null && status.to < interval.to) delta = delta - (interval.to - status.to);

                time = time + delta;
            }

            log.warn( "getTime(): " + time );//TODO NotImplemented
            return time;
        }

        public void add( Date created, int caseStateId ) {
            if (lastStatus != null) {
                lastStatus.setStop( created.getTime() );
            }
            lastStatus = new Status( created.getTime(), caseStateId );
            statuses.add( lastStatus );
        }

        public Long caseId;//TODO DEBUG
        public List<Status> statuses = new ArrayList<>();
        Status lastStatus;
    }

    static class Status {

        public Status( Long created, int caseStateId ) {
            this.from = created;
            this.caseStateId = caseStateId;
        }

        public void setStop( Long stop ) {
            this.to = stop;
        }

        Long to;
        long from;
        int caseStateId;

        @Override
        public String toString() {
            return "Status{" +
                    "from=" + new Date(from) +
                    ", to=" + (to==null?"null":new Date(to)) +
                    ", caseStateId=" + caseStateId +
                    '}';
        }
    }
}
