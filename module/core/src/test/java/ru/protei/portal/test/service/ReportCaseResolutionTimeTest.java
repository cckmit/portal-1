package ru.protei.portal.test.service;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.config.DatabaseConfiguration;
import ru.protei.portal.config.MainTestsConfiguration;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ReportType;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.report.caseresolution.ReportCaseResolutionTime;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.report.caseresolution.ReportCaseResolutionTime.*;
import static ru.protei.portal.core.report.caseresolution.ReportCaseResolutionTime.DAY;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfigurationContext.class, JdbcConfigurationContext.class, DatabaseConfiguration.class, MainTestsConfiguration.class})
public class ReportCaseResolutionTimeTest extends BaseServiceTest {

    private void initCaseObjectsQueryTest( ) {
        if (productId == null) {

            DevUnit product = devUnitDAO.getByCondition( "UNIT_NAME=?", PRODUCT_NAME );
            if (product == null)
                productId = makeProduct( PRODUCT_NAME );
            else {
                productId = product.getId();
            }
        }
        Company company = makeCompany( new CompanyCategory( 2L ) );
        person = makePerson( company );

        CaseTag includeTag = makeCaseTag( "tag_for_include_1", En_CaseType.CRM_SUPPORT );
        CaseTag includeTag2 = makeCaseTag( "tag_for_include_2", En_CaseType.CRM_SUPPORT );
        CaseTag excludeTag = makeCaseTag( "tag_for_exclude", En_CaseType.CRM_SUPPORT );
        caseTagIncludedIds = toList( listOf( includeTag, includeTag2 ), CaseTag::getId );

        CaseObjectFactory caseObjectFactory = new CaseObjectFactory( person, productId, this::rememberCaseId );

        //  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 31
        //                               ^x
        Long caseId = caseObjectFactory.make( day( 9 ), includeTag );
        caseObjectIncludedIds.add(caseId);
        CaseComment c1 = createNewComment( person, caseId, "One day" );
        makeComment( c1, CREATED, day( 11 ) );                              //2050-01-11 00:00:00
        makeComment( c1, OPENED, addHours( day( 11 ), 2 ) );                //2050-01-11 02:00:00
        makeComment( c1, DONE, addHours( day( 11 ), 6 ) );                  //2050-01-11 06:00:00

        //  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 31
        //                          ^n------^--------x     ^----------------------------------------
        Long caseId2 = caseObjectFactory.make( day( 9 ), includeTag2 );
        caseObjectIncludedIds.add(caseId2);
        CaseComment c2 = createNewComment( person, caseId2, "Week" );
        makeComment( c2, CREATED, day( 9 ) );                                      //2050-01-09 00:00:00
        makeComment( c2, null, addHours( day( 9 ), 2 ) );            //2050-01-09 02:00:00
        makeComment( c2, OPENED, addHours( day( 12 ), 2 ) );                //2050-01-12 02:00:00
        makeComment( c2, DONE, addHours( day( 15 ), 5 ) );                  //2050-01-15 05:00:00
        makeComment( c2, REOPENED, addHours( day( 17 ), 11 ) );             //2050-01-17 11:00:00

        //  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 31
        //                                     ^--------------^---n------------^
        Long caseId3 = caseObjectFactory.make( day( 9 ), includeTag );
        caseObjectIncludedIds.add(caseId3);
        CaseComment c3 = createNewComment( person, caseId3, "2 Week" );
        makeComment( c3, CREATED, day( 13 ) );                                     //2050-01-13 00:00:00
        makeComment( c3, OPENED, addHours( day( 18 ), 5 ) );                //2050-01-18 05:00:00
        makeComment( c3, null, addHours( day( 19 ), 11 ) );          //2050-01-19 11:00:00
        makeComment( c3, DONE, addHours( day( 24 ), 11 ) );                 //2050-01-24 11:00:00

        //  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 31
        //   excluded                                ^..............................................
        Long excludedCaseId4 = caseObjectFactory.make( day( 9 ), excludeTag );
        CaseComment c4 = createNewComment( person, excludedCaseId4, "excluded comment" );
        makeComment( c4, CREATED, day( 15 ) );                                     //2050-01-15 00:00:00

    }

    @Test
    public void caseObjectsQueryTest() throws Exception {
        try {
            initCaseObjectsQueryTest();

            int numberOfDays = 12;
            //                            | 0| 1| 2| 3| 4| 5| 6| 7| 8| 9|10|11|
            //  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 31
            //                            |-----------------------------------|
            //                               ^x
            //                          ^n------^--------x     ^----------------------------------------
            //                                     ^--------------^---n-------------х
            //   excluded                                ^..............................................
            Report report = createReport( productId, date10, addHours( date10, numberOfDays * H_DAY ), caseTagIncludedIds );
//            Report report = createReport( productId, date10, addHours( date10, numberOfDays * H_DAY ), listOf( 2L ));

            ReportCaseResolutionTime caseCompletionTimeReport = new ReportCaseResolutionTime( report, caseCommentDAO );
            caseCompletionTimeReport.run();

            List<Case> cases = caseCompletionTimeReport.getCases();
            assertEquals( "grouping comments not worked", size( caseObjectIncludedIds ), size( cases ) );
            assertEquals( "expected only included cases", caseObjectIncludedIds, stream( cases ).map( cse -> cse.caseId ).sorted().collect( Collectors.toList() ) );
            assertEquals( 9, cases.stream().mapToInt( cse -> size( cse.statuses ) ).sum() );

            List<Interval> intervals = caseCompletionTimeReport.getIntervals();
            assertEquals( numberOfDays, intervals.size() );

            assertEquals( 1, intervals.get( 0 ).casesCount );
            assertEquals( 2 * DAY, intervals.get( 0 ).minTime );
            assertEquals( 2 * DAY, intervals.get( 0 ).maxTime );
            assertEquals( 2 * DAY, intervals.get( 0 ).summTime );

            assertEquals( 2, intervals.get( 1 ).casesCount );
            assertEquals( 6 * HOUR, intervals.get( 1 ).minTime );
            assertEquals( 3 * DAY, intervals.get( 1 ).maxTime );
            assertEquals( 3 * DAY + 6 * HOUR, intervals.get( 1 ).summTime );

            assertEquals( 1, intervals.get( 2 ).casesCount );
            assertEquals( 4 * DAY, intervals.get( 2 ).minTime );
            assertEquals( 4 * DAY, intervals.get( 2 ).maxTime );
            assertEquals( 4 * DAY, intervals.get( 2 ).summTime );

            assertEquals( 2, intervals.get( 3 ).casesCount );
            assertEquals( 1 * DAY, intervals.get( 3 ).minTime );
            assertEquals( 5 * DAY, intervals.get( 3 ).maxTime );
            assertEquals( 6 * DAY, intervals.get( 3 ).summTime );

            assertEquals( 1, intervals.get( 6 ).casesCount );
            assertEquals( 4 * DAY, intervals.get( 6 ).minTime );
            assertEquals( 4 * DAY, intervals.get( 6 ).maxTime );
            assertEquals( 4 * DAY, intervals.get( 6 ).summTime );

            assertEquals( 2, intervals.get( 7 ).casesCount );
            long case3Time = 5 * DAY;
            assertEquals( case3Time, intervals.get( 7 ).minTime );
            long case2Time = 6 * DAY + 5 * HOUR + DAY - 11 * HOUR;
            assertEquals( case2Time, intervals.get( 7 ).maxTime );
            assertEquals( case2Time + case3Time, intervals.get( 7 ).summTime );

            assertEquals( 2, intervals.get( 11 ).casesCount );
            long case11minTime = 9 * DAY;
            assertEquals( case11minTime, intervals.get( 11 ).minTime );
            long case11maxTime = 7 * DAY + 5 * HOUR + 4 * DAY - 11 * HOUR;
            assertEquals( case11maxTime, intervals.get( 11 ).maxTime );
            assertEquals( case11minTime + case11maxTime, intervals.get( 11 ).summTime );

        } catch (Exception e) {
            throw e;
        } finally {
            clean();
        }
    }

    @Test
    public void intervalsTest() {
        int numberOfDays = 12;
        List<Interval> intervals = makeIntervals( date9, addHours( date9, numberOfDays * H_DAY ), DAY );
        assertEquals( numberOfDays, intervals.size() );
        assertEquals( date9.getTime(), intervals.get( 0 ).from );
        assertEquals( addHours( date9, 1 * H_DAY ).getTime(), intervals.get( 0 ).to );
    }

    @Test
    public void casesGroupingTest() {
        Person person = new Person( 1L );

        List<CaseComment> comments = new ArrayList<>();
        comments.add( createNewComment( person, 1L, "1 case 1 comment", CREATED ) );
        comments.add( createNewComment( person, 1L, "1 case 2 comment", OPENED ) );
        comments.add( createNewComment( person, 1L, "1 case 3 comment", DONE ) );
        comments.add( createNewComment( person, 2L, "2 case 1 comment", CREATED ) );
        comments.add( createNewComment( person, 2L, "2 case 2 comment", OPENED ) );
        comments.add( createNewComment( person, 2L, "2 case 3 comment", DONE ) );
        comments.add( createNewComment( person, 3L, "3 case 1 comment", CREATED ) );
        comments.add( createNewComment( person, 3L, "3 case 2 comment", OPENED ) );
        comments.add( createNewComment( person, 3L, "3 case 3 comment", DONE ) );

        List<Case> cases = groupBayIssues( comments );

        assertEquals( 3, cases.size() );
    }

    @Test
    public void caseInIntervalTest() {
        Person person = new Person( 1L );

        int numberOfDays = 12;
        List<Interval> intervals = makeIntervals( date9, addHours( date9, numberOfDays * H_DAY ), DAY );

        List<CaseComment> comments = new ArrayList<>();
        //                         | 0| 1| 2| 3| 4| 5| 6| 7| 8| 9|10|11|
        //  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 31
        //                            ^--^--x           ^--------x
        //                               ^-----^-----x     ^--x
        comments.add( fillComment( createNewComment( person, 1L, "1 case CREATED" ), CREATED, day( 10 ) ) );
        comments.add( fillComment( createNewComment( person, 1L, "1 case OPENED" ), OPENED, day( 11 ) ) );
        comments.add( fillComment( createNewComment( person, 1L, "1 case DONE" ), DONE, day( 12 ) ) );
        comments.add( fillComment( createNewComment( person, 1L, "1 case REOPENED" ), REOPENED, day( 16 ) ) );
        comments.add( fillComment( createNewComment( person, 1L, "1 case VERIFIED" ), VERIFIED, day( 19 ) ) );

        comments.add( fillComment( createNewComment( person, 2L, "2 case CREATED" ), CREATED, day( 11 ) ) );
        comments.add( fillComment( createNewComment( person, 2L, "2 case OPENED" ), OPENED, day( 13 ) ) );
        comments.add( fillComment( createNewComment( person, 2L, "2 case DONE" ), DONE, day( 15 ) ) );
        comments.add( fillComment( createNewComment( person, 2L, "2 case REOPENED" ), REOPENED, day( 17 ) ) );
        comments.add( fillComment( createNewComment( person, 2L, "2 case VERIFIED" ), VERIFIED, day( 18 ) ) );

        List<Case> cases = groupBayIssues( comments );

        for (Interval interval : intervals) {
            log.info( "\n" );
            log.info( "caseInIntervalTest(): interval {} - {}", new Date( interval.from ), new Date( interval.to ) );
            interval.fill( cases, new HashSet<>( activeStatesShort ) );
            log.info( "caseInIntervalTest(): {}", interval );
        }

        assertEquals( 0, intervals.get( 0 ).summTime );
        assertEquals( 1 * DAY, intervals.get( 1 ).summTime );
        assertEquals( 1 * DAY, intervals.get( 1 ).minTime );
        assertEquals( 1 * DAY, intervals.get( 1 ).maxTime );

        assertEquals( 2, intervals.get( 2 ).casesCount );
        assertEquals( 3 * DAY, intervals.get( 2 ).summTime );
        assertEquals( 1 * DAY, intervals.get( 2 ).minTime );
        assertEquals( 2 * DAY, intervals.get( 2 ).maxTime );

        assertEquals( 2 * DAY, intervals.get( 3 ).summTime );
        assertEquals( 2 * DAY, intervals.get( 3 ).minTime );
        assertEquals( 2 * DAY, intervals.get( 3 ).maxTime );

        assertEquals( 3 * DAY, intervals.get( 4 ).summTime );
        assertEquals( 3 * DAY, intervals.get( 4 ).minTime );
        assertEquals( 3 * DAY, intervals.get( 4 ).maxTime );

        assertEquals( 4 * DAY, intervals.get( 5 ).summTime );
        assertEquals( 4 * DAY, intervals.get( 5 ).minTime );
        assertEquals( 4 * DAY, intervals.get( 5 ).maxTime );

        assertEquals( 0, intervals.get( 6 ).summTime );
        assertEquals( 0, intervals.get( 6 ).minTime );
        assertEquals( 0, intervals.get( 6 ).maxTime );

        assertEquals( 3 * DAY, intervals.get( 7 ).summTime );
        assertEquals( 3 * DAY, intervals.get( 7 ).minTime );
        assertEquals( 3 * DAY, intervals.get( 7 ).maxTime );

        assertEquals( 2, intervals.get( 8 ).casesCount );
        assertEquals( 9 * DAY, intervals.get( 8 ).summTime );
        assertEquals( 4 * DAY, intervals.get( 8 ).minTime );
        assertEquals( 5 * DAY, intervals.get( 8 ).maxTime );

        assertEquals( 5 * DAY, intervals.get( 9 ).summTime );
        assertEquals( 5 * DAY, intervals.get( 9 ).minTime );
        assertEquals( 5 * DAY, intervals.get( 9 ).maxTime );

        assertEquals( 0, intervals.get( 10 ).summTime );
        assertEquals( 0, intervals.get( 10 ).minTime );
        assertEquals( 0, intervals.get( 10 ).maxTime );

    }

    @Test
    public void hasIntersection() {

        assertFalse( "Unexpected intersection", Case.hasIntersection( 10L, 20L, -1L, -2L ) );
        assertFalse( "Unexpected intersection", Case.hasIntersection( 10L, 20L, 0L, 0L ) );
        assertFalse( "Unexpected intersection", Case.hasIntersection( 10L, 20L, 1L, 9L ) );
        assertFalse( "Unexpected intersection", Case.hasIntersection( 10L, 20L, 9L, 1L ) );
        assertFalse( "Unexpected intersection", Case.hasIntersection( 10L, 20L, 10L, 0L ) );
        assertFalse( "Unexpected intersection", Case.hasIntersection( 10L, 20L, 21L, 30L ) );
        assertFalse( "Unexpected intersection by continuing status", Case.hasIntersection( 10L, 20L, 20L, null ) );

        assertFalse( "Unexpected intersection by boundary", Case.hasIntersection( 10L, 20L, 0L, 10L ) );
        assertFalse( "Unexpected intersection by boundary", Case.hasIntersection( 10L, 20L, 20L, 30L ) );

        assertTrue( "Expected an intersection by continuing status", Case.hasIntersection( 10L, 20L, 0L, null ) );
        assertTrue( "Expected an intersection", Case.hasIntersection( 10L, 20L, 10L, 20L ) );
        assertTrue( "Expected an intersection", Case.hasIntersection( 10L, 20L, 11L, 19L ) );
        assertTrue( "Expected an intersection", Case.hasIntersection( 10L, 20L, 4L, 14L ) );
        assertTrue( "Expected an intersection", Case.hasIntersection( 10L, 20L, 16L, 26L ) );

    }

    @Test
    public void calcIntersectionTime() {

        assertEquals( "Expected an intersection by continuing status", 20L, Case.calcStatusTime( 20L, 0L, null ) );
        assertEquals( "Expected an intersection", 0L, Case.calcStatusTime( 20L, 20L, 20L ) );
        assertEquals( "Expected an intersection", 0L, Case.calcStatusTime( 20L, 10L, 10L ) );
        assertEquals( "Expected an intersection", 20L, Case.calcStatusTime( 20L, 0L, 20L ) );
        assertEquals( "Expected an intersection", 10L, Case.calcStatusTime( 20L, 10L, 21L ) );
        assertEquals( "Expected an intersection", 11L, Case.calcStatusTime( 20L, 9L, 20L ) );
        assertEquals( "Expected an intersection", 1L, Case.calcStatusTime( 20L, 19L, 20L ) );
        assertEquals( "Expected an intersection", 1L, Case.calcStatusTime( 20L, 10L, 11L ) );
        assertEquals( "Expected an intersection", 9L, Case.calcStatusTime( 20L, 10L, 19L ) );
        assertEquals( "Expected an intersection", 9L, Case.calcStatusTime( 20L, 11L, 20L ) );
        assertEquals( "Expected an intersection", 8L, Case.calcStatusTime( 20L, 11L, 19L ) );
        assertEquals( "Expected an intersection", 4l, Case.calcStatusTime( 20L, 16L, 26L ) );

    }

    @Test
    public void makeWorkBook() {
        Person person = new Person( 1L );
        int numberOfDays = 12;
        List<Interval> intervals = makeIntervals( date9, addHours( date9, numberOfDays * H_DAY ), DAY );

        List<CaseComment> comments = new ArrayList<>();
        //                         | 0| 1| 2| 3| 4| 5| 6| 7| 8| 9|10|11|
        //  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 31
        //                            ^--^--x           ^--------x
        //                               ^-----^-----x     ^--x
        comments.add( fillComment( createNewComment( person, 1L, "1 case CREATED" ), CREATED, day( 10 ) ) );
        comments.add( fillComment( createNewComment( person, 1L, "1 case OPENED" ), OPENED, day( 11 ) ) );
        comments.add( fillComment( createNewComment( person, 1L, "1 case DONE" ), DONE, day( 12 ) ) );
        comments.add( fillComment( createNewComment( person, 1L, "1 case REOPENED" ), REOPENED, day( 16 ) ) );
        comments.add( fillComment( createNewComment( person, 1L, "1 case VERIFIED" ), VERIFIED, day( 19 ) ) );

        comments.add( fillComment( createNewComment( person, 2L, "2 case CREATED" ), CREATED, day( 11 ) ) );
        comments.add( fillComment( createNewComment( person, 2L, "2 case OPENED" ), OPENED, day( 13 ) ) );
        comments.add( fillComment( createNewComment( person, 2L, "2 case DONE" ), DONE, day( 15 ) ) );
        comments.add( fillComment( createNewComment( person, 2L, "2 case REOPENED" ), REOPENED, day( 17 ) ) );
        comments.add( fillComment( createNewComment( person, 2L, "2 case VERIFIED" ), VERIFIED, day( 18 ) ) );


        List<Case> cases = groupBayIssues( comments );

        for (Interval interval : intervals) {
            interval.fill( cases, new HashSet<>( activeStatesShort ) );
        }

        XSSFWorkbook workBook = createWorkBook( intervals, DEFAULT_COLUMN_NAMES );

        assertNotNull( workBook );
        assertEquals( 1, workBook.getNumberOfSheets() );
        assertNotNull( workBook.getSheetAt( 0 ) );
        assertEquals( numberOfDays, workBook.getSheetAt( 0 ).getLastRowNum() ); // учтена строка с заголовком
    }

    protected CaseTag makeCaseTag( String tag1, En_CaseType type ) {
        CaseTag caseTag = super.makeCaseTag(tag1, type);
        caseTagIds.add( caseTag.getId() );
        return caseTag;
    }

    private void makeComment( CaseComment comment1, Long status, Date created ) {
        comment1 = fillComment( comment1, status, created );
        comment1.setId( null );
        commentsIds.add( caseCommentDAO.persist( comment1 ) );
    }

    private CaseComment fillComment( CaseComment comment1, Long status, Date created ) {
        comment1.setCreated( created );
        comment1.setCaseStateId( status );
        return comment1;
    }

    private Report createReport( Long productId, Date from, Date to, List<Long> caseTagsIds ) {
        CaseQuery caseQuery = new CaseQuery();
        caseQuery.setProductIds( Arrays.asList( productId ) );
        caseQuery.setStateIds( activeStatesShort );
        caseQuery.setCaseTagsIds( caseTagsIds );


        caseQuery.setCreatedFrom( from );
        caseQuery.setCreatedTo( to );

        Report report = new Report();
        report.setReportType( En_ReportType.CASE_RESOLUTION_TIME );
        report.setCaseQuery( caseQuery );
        return report;
    }

    private Date addHours( Date date, int hours ) {
        Calendar cal = Calendar.getInstance();
        cal.setTime( date );
        cal.add( Calendar.HOUR_OF_DAY, hours );
        return cal.getTime();
    }

    private void clean() {
        if (!commentsIds.isEmpty() && caseIds != null) {
            String caseIdsString = caseIds.stream().map( String::valueOf ).collect( Collectors.joining( "," ) );
            caseCommentDAO.removeByCondition( "CASE_ID in (" + caseIdsString + ")" );
            commentsIds.clear();
        }
        if (caseIds != null) {
            caseObjectDAO.removeByKeys( caseIds );
            caseIds = null;
        }

        if (person != null) {
            personDAO.remove( person );
            companyDAO.removeByKey( person.getCompanyId() );
        }

        if(!isEmpty( caseTagIds )){
            caseTagDAO.removeByKeys( caseTagIds );
            caseTagIds.clear();
        }
    }

    private Date day( int day_of_month ) {
        return addHours( date1, (day_of_month - 1) * H_DAY );
    }

    private void rememberCaseId( Long caseId ) {
        caseIds.add( caseId);
    }



    private static Date date1 = new GregorianCalendar( 2050, Calendar.JANUARY, 1, 0, 0 ).getTime();
    private static Date date10 = new GregorianCalendar( 2050, Calendar.JANUARY, 10, 0, 0 ).getTime();
    private static Date date9 = new GregorianCalendar( 2050, Calendar.JANUARY, 9, 0, 0 ).getTime();

    private static List<Integer> activeStatesShort = Arrays.asList( 1, 2, 6, 16, 19, 30 );
    private static Long productId;
    private static Person person;
    private static List<Long> commentsIds = new ArrayList<>();
    private static List<Long> caseIds = new ArrayList<>();
    private List<Long> caseObjectIncludedIds = new ArrayList<>();
    private List<Long> caseTagIds = new ArrayList<>();
    private List<Long> caseTagIncludedIds = new ArrayList<>();
    private static final String PRODUCT_NAME = "TestProduct";
    private static final Long CREATED = 1L;
    private static final Long OPENED = 2L;
    private static final Long REOPENED = 6L;
    private static final Long VERIFIED = 5L;
    private static final Long DONE = 17L;
    private static final int H_DAY = 24;
    private static final Logger log = LoggerFactory.getLogger( ReportCaseResolutionTimeTest.class );

    class CaseObjectFactory {

        public CaseObjectFactory( Person person, Long productId, Consumer<Long> createdIds ) {
            this.createdIds = createdIds;
            this.person = person;
            this.productId = productId;
        }

        public Long make( Date day ) {
            return make(day, null);
        }

        public Long make( Date day, CaseTag caseTag ) {
            Long id = makeCaseObject( person, productId, day, caseTag ).getId();
            createdIds.accept(  id   );
            return id;
        }

        private Consumer<Long> createdIds;
        private final Person person;
        private final Long productId;
    }
}
