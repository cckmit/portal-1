package ru.protei.portal.core.report.caseresolution;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.dto.CaseResolutionTimeReportDto;
import ru.protei.portal.core.model.query.CaseQuery;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static ru.protei.portal.core.model.helper.CollectionUtils.size;
import static ru.protei.portal.core.model.helper.StringUtils.join;
import static ru.protei.portal.core.model.util.CrmConstants.Time.*;

public class ReportCaseResolutionTime {

    public ReportCaseResolutionTime( CaseQuery caseQuery, CaseCommentDAO caseCommentDAO ) {
        this.caseCommentDAO = caseCommentDAO;
        this.caseQuery = caseQuery;
    }

    public boolean writeReport( ByteArrayOutputStream out, Lang.LocalizedLang localizedLang ) throws IOException {
        List<String> columnNames = new ArrayList<>();
        if (localizedLang == null) {
            columnNames.addAll( DEFAULT_COLUMN_NAMES );
        } else {
            columnNames.add( localizedLang.get( "dateColumn" ) );
            columnNames.add( localizedLang.get( "averageColumn" ) );
            columnNames.add( localizedLang.get( "maximumColumn" ) );
            columnNames.add( localizedLang.get( "minimumColumn" ) );
            columnNames.add( localizedLang.get( "numberUncompletedCases" ) );
            columnNames.add( localizedLang.get( "uncompletedCasesNumbers" ) );
        }

        XSSFWorkbook workbook = createWorkBook( intervals, columnNames );

        workbook.write( out );
        workbook.close();

        return true;
    }

    public void run() {
        log.info( "run(): Start report. caseQuery: {}", caseQuery );
        intervals = makeIntervals( caseQuery.getCreatedFrom(), caseQuery.getCreatedTo(), DAY );

        long startQuery = System.currentTimeMillis();
        List<CaseResolutionTimeReportDto> comments = caseCommentDAO.reportCaseResolutionTime(
                caseQuery.getCreatedFrom(),
                caseQuery.getCreatedTo(),
                caseQuery.getStateIds(),
                caseQuery.getCompanyIds(),
                caseQuery.getProductIds(),
                caseQuery.getManagerIds(),
                caseQuery.getImportanceIds(),
                caseQuery.getCaseTagsIds()
        );
        log.info( "run(): Case comments request time: {} ms", System.currentTimeMillis() - startQuery );
        long startProcessing = System.currentTimeMillis();

        cases = groupBayIssues( comments );

        Set<Long> acceptableStates = new HashSet<>( caseQuery.getStateIds() );
        for (Interval interval : intervals) {
            interval.fill( cases, acceptableStates );
        }

        log.info( "run(): Case comments processing time: {} ms", System.currentTimeMillis() - startProcessing );
    }

    public static XSSFWorkbook createWorkBook( List<Interval> intervals, List<String> columnNames ) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();
        XSSFCellStyle dateStyle = workbook.createCellStyle();
        dateStyle.setDataFormat( HSSFDataFormat.getBuiltinFormat( "yy-m-d" ) );
        int rowid = 0;
        int cellIndex = 0;

        XSSFRow headersRow = sheet.createRow( rowid++ );
        for (String columnName : columnNames) {
            headersRow.createCell( cellIndex++ ).setCellValue( columnName );
        }

        for (Interval interval : intervals) {
            XSSFRow row = sheet.createRow( rowid++ );
            cellIndex = 0;
            XSSFCell dateCell = row.createCell( cellIndex );
            dateCell.setCellValue( makeDateFormat().format( interval.from ) );
            dateCell.setCellStyle( dateStyle );
            row.createCell( ++cellIndex ).setCellValue( calcAverage( interval ) );
            row.createCell( ++cellIndex ).setCellValue( calcHours( interval.maxTime ) );
            row.createCell( ++cellIndex ).setCellValue( calcHours( interval.minTime ) );
            row.createCell( ++cellIndex ).setCellValue( interval.caseNumbers.size() );
            row.createCell( ++cellIndex ).setCellValue( join(interval.caseNumbers, ", ") );
        }
        log.info( "createWorkBook() intervals: {}", intervals );
        return workbook;
    }

    public List<Case> getCases() {
        return cases;
    }

    public static List<Interval> makeIntervals( Date fromdate, Date toDate, long step ) {
        long from = fromdate.getTime();
        long to = toDate.getTime();
        ArrayList<Interval> intervals = new ArrayList<Interval>();
        for (; from < to; from = from + step) {
            intervals.add( new Interval( from, from + step ) );
        }

        return intervals;
    }

    public static List<Case> groupBayIssues( List<CaseResolutionTimeReportDto> comments ) {
        List<Case> cases = new ArrayList<>();
        Map<Long, Case> map = new HashMap<>();
        for (CaseResolutionTimeReportDto comment : comments) {
            Case aCase = map.get( comment.getCaseId() );
            if (aCase == null) {
                aCase = new Case();
                map.put( comment.getCaseId(), aCase );
                cases.add( aCase );
            }
            if (comment.getCaseStateId() != null){
                mapCase( aCase, comment);
            }
        }
        return cases;
    }

    public List<Interval> getIntervals() {
        return intervals;
    }

    private static Integer calcHours( Long value ) {
        return (int) (value / HOUR);
    }

    private static Integer calcAverage( Interval interval ) {
        if (interval == null || size(interval.caseNumbers ) == 0) return 0;
        return (int) ((interval.summTime / size(interval.caseNumbers )) / HOUR);
    }

    private static Case mapCase( Case aCase, CaseResolutionTimeReportDto comment ) {
        aCase.add( comment.getCreated(), comment.getCaseStateId().intValue() );
        aCase.caseId = comment.getCaseId();
        aCase.caseNumber = comment.getCaseNumber();
        return aCase;
    }

    private static DateFormat makeDateFormat() {
        return new SimpleDateFormat( DATE_FORMAT );
    }

    public static final String DATE_FORMAT = "yyyy-MM-dd" ;
    public static final List<String> DEFAULT_COLUMN_NAMES = Arrays.asList( "Date", "Average", "Maximum", "Minimum", "Case count", "Active cases" );

    private List<Case> cases = new ArrayList<>();
    private List<Interval> intervals = new ArrayList<>();
    private CaseCommentDAO caseCommentDAO;
    private CaseQuery caseQuery;

    private static Logger log = LoggerFactory.getLogger( ReportCaseResolutionTime.class );

    public static class Interval {

        public Interval( long from, long to ) {
            this.from = from;
            this.to = to;
        }

        public void fill( List<Case> cases, Set<Long> acceptableStates ) {
            for (Case aCase : cases) {
                long time = aCase.getTime( this, acceptableStates );
                if (time <= 0) {
                    continue;
                }

                caseNumbers.add( aCase.caseNumber );
                summTime += time;
                if (time < minTime || minTime == 0) minTime = time;
                if (time > maxTime || maxTime == 0) maxTime = time;
            }
        }

        @Override
        public String toString() {
            return "Interval{" +
                    "from=" + new Date( from ) +
                    ", to=" + new Date( to ) +
                    ", summTime=" + summTime +
                    ", maxTime=" + maxTime +
                    ", minTime=" + minTime +
                    ", caseNumbers: " + caseNumbers +
                    '}';
        }

        public long from;
        public long to;
        public List<Long> caseNumbers = new ArrayList<>( );
        public long summTime;
        public long maxTime;
        public long minTime;
    }

    public static class Case {
        public long getTime( Interval interval, Set<Long> acceptableStates ) {

            boolean hasIntersectionOnActiveInterval = false;
            long activeTime = 0;

            for (Status status : statuses) {
                // статусы после интервала не подходят
                if (interval.to <= status.from) {
                    continue;// (=) исключить пересечение по концу интервала
                }

                // Если статус не активный
                if (!acceptableStates.contains( status.caseStateId )) {
                    continue;
                }

                if (hasStatusIntersection( interval, status )) {// учитывает null - когда статус длится
                    hasIntersectionOnActiveInterval = true;
                }

                activeTime += calcStatusTime( interval, status );
            }

            // Задача в интервале была не активна - время задачи не учитывается
            if (!hasIntersectionOnActiveInterval) {
                return 0;
            }

            return activeTime;
        }

        public static long calcStatusTime( long iTo, long sFrom, Long sTo ) {
            if (sTo == null || iTo < sTo)
                return iTo - sFrom;
            else
                return sTo - sFrom;
        }

        public static boolean hasIntersection( long iFrom, long iTo, long sFrom, Long sTo ) {
            if (iTo <= sFrom
                    || (sTo != null && sTo <= iFrom)) return false;
            return true;
        }

        public void add( Date created, int caseStateId ) {
            if (previousStatus != null) {
                previousStatus.setStop( created.getTime() );
            }
            previousStatus = new Status( created.getTime(), caseStateId );
            statuses.add( previousStatus );
        }

        @Override
        public String toString() {
            return "Case{" +
                    "caseId=" + caseId +
                    "caseNumber=" + caseNumber +
                    '}';
        }

        private long calcStatusTime( Interval interval, Status status ) {
            return calcStatusTime( interval.to, status.from, status.to );
        }

        private boolean hasStatusIntersection( Interval interval, Status status ) {
            if (interval == null || status == null)
                return false;
            return hasIntersection( interval.from, interval.to, status.from, status.to );
        }

        public Long caseId;
        public Long caseNumber;
        public List<Status> statuses = new ArrayList<>();
        Status previousStatus;
    }

    static class Status {

        public Status( Long created, long caseStateId ) {
            this.from = created;
            this.caseStateId = caseStateId;
        }

        public void setStop( Long stop ) {
            this.to = stop;
        }

        @Override
        public String toString() {
            return "Status{" +
                    "from=" + new Date( from ) +
                    ", to=" + (to == null ? "null" : new Date( to )) +
                    ", caseStateId=" + caseStateId +
                    '}';
        }

        Long to; // null - значит статус ещё длится (время завершения статуса окончательное или изменится в будущем)
        long from;
        long caseStateId;
    }
}
