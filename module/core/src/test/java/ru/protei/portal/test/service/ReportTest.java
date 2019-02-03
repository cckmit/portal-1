package ru.protei.portal.test.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.config.MainTestsConfiguration;
import ru.protei.portal.core.model.dict.En_ReportType;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.report.casetimeelapsed.ReportCaseCompletionTime;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static ru.protei.portal.core.model.helper.CollectionUtils.size;
import static ru.protei.portal.core.report.casetimeelapsed.ReportCaseCompletionTime.*;
import static ru.protei.portal.core.report.casetimeelapsed.ReportCaseCompletionTime.DAY;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfigurationContext.class, JdbcConfigurationContext.class, MainTestsConfiguration.class})
public class ReportTest extends BaseTest {

    Long productId;
    static Person person;
    Date date9 = new GregorianCalendar( 2050, Calendar.JANUARY, 9, 0, 0 ).getTime();
    Date date1 = new GregorianCalendar( 2050, Calendar.JANUARY, 1, 0, 0 ).getTime();
    List<Long> commentsIds = new ArrayList<>();
    List<Long> caseIds = new ArrayList<>();

//    @Before
//    public void beforeEachTest() {
//        init();
//    }
//
//    @After
//    public void afterEachTest() {
//        clean();
//    }

    private void init() {
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

        //  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 31
        //                                ^
        Long caseId = makeCaseObject( person, productId, date9 );
        CaseComment c1 = createNewComment( person, caseId, "One day" );
        makeComment( c1, CREATED, addHours( date9, 2 * H_DAY ) );              //2050-01-11 00:00:00
        makeComment( c1, OPENED, addHours( c1.getCreated(), 2 ) );          //2050-01-11 02:00:00
        makeComment( c1, DONE, addHours( c1.getCreated(), 4 ) );            //2050-01-11 06:00:00

        //  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 31
        //                          ^--------^---------- ^
        Long caseId2 = makeCaseObject( person, productId, date9 );
        CaseComment c2 = createNewComment( person, caseId2, "Week" );
        makeComment( c2, CREATED, date9 );                                      //2050-01-09 00:00:00
        makeComment( c2, OPENED, addHours( c2.getCreated(), 3 * H_DAY + 2 ) );   //2050-01-12 02:00:00
        makeComment( c2, DONE, addHours( c2.getCreated(), 4 * H_DAY + 3 ) );     //2050-01-16 05:00:00

        //  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 31
        //                                      ^--------------^----------------^
        Long caseId3 = makeCaseObject( person, productId, date9 );
        CaseComment c3 = createNewComment( person, caseId3, "2 Week" );
        makeComment( c3, CREATED, addHours( date9, 4 * H_DAY ) );                 //2050-01-13 00:00:00
        makeComment( c3, OPENED, addHours( c3.getCreated(), 5 * H_DAY + 5 ) );   //2050-01-18 05:00:00
        makeComment( c3, DONE, addHours( c3.getCreated(), 6 * H_DAY + 6 ) );     //2050-01-24 11:00:00

    }

    @Test
    public void getCaseObjectsTest() throws Exception {
        init();

        int numberOfDays = 12;
        //  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 31
        //                          ^----------------------------------^
        Report report = createReport( productId, date9, addHours( date9, numberOfDays * H_DAY ) );

        ReportCaseCompletionTime caseCompletionTimeReport = new ReportCaseCompletionTime( report, caseCommentDAO );
        caseCompletionTimeReport.run();

        List<Case> cases = caseCompletionTimeReport.getCases();
        assertEquals( caseIds.size(), size( cases ) );
        assertEquals( commentsIds.size(), cases.stream().mapToInt( cse -> size( cse.statuses ) ).sum() );

        List<Interval> intervals = caseCompletionTimeReport.getIntervals();
        assertEquals( numberOfDays, intervals.size() );

        int dayNumber = 0;
        assertEquals( 1, intervals.get( dayNumber ).casesCount );
        assertEquals( DAY, intervals.get( dayNumber ).minTime );
        assertEquals( DAY, intervals.get( dayNumber ).maxTime );
        assertEquals( DAY, intervals.get( dayNumber ).summTime );

        dayNumber = 1;
        assertEquals( 1, intervals.get( dayNumber ).casesCount );
        assertEquals( dayNumber * DAY, intervals.get( dayNumber ).minTime );
        assertEquals( dayNumber * DAY, intervals.get( dayNumber ).maxTime );
        assertEquals( dayNumber * DAY, intervals.get( dayNumber ).summTime );

        clean();
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
        //                            ^--^--x
        //                               ^-----^-----x
        comments.add( fillComment( createNewComment( person, 1L, "1 case 1 comment" ), CREATED, day( 10 ) ) );
        comments.add( fillComment( createNewComment( person, 1L, "1 case 2 comment" ), OPENED, day( 11 ) ) );
        comments.add( fillComment( createNewComment( person, 1L, "1 case 3 comment" ), DONE, day( 12 ) ) );

        comments.add( fillComment( createNewComment( person, 2L, "2 case 1 comment" ), CREATED, day( 11 ) ) );
        comments.add( fillComment( createNewComment( person, 2L, "2 case 2 comment" ), OPENED, day( 13 ) ) );
        comments.add( fillComment( createNewComment( person, 2L, "2 case 3 comment" ), DONE, day( 15 ) ) );

        List<Case> cases = groupBayIssues( comments );

        for (Interval interval : intervals) {
            interval.fill( cases, new HashSet<>( ignoredStates ) );
        }

        assertEquals( 0, intervals.get( 0 ).summTime );
        assertEquals( 1 * DAY, intervals.get( 1 ).maxTime );
        assertEquals( 1 * DAY, intervals.get( 1 ).minTime );
        assertEquals( 1 * DAY, intervals.get( 1 ).summTime );

        assertEquals( 3 * DAY, intervals.get( 2 ).summTime );
        assertEquals( 1 * DAY, intervals.get( 2 ).minTime );
        assertEquals( 2 * DAY, intervals.get( 2 ).maxTime );

        assertEquals( 2 * DAY, intervals.get( 3 ).maxTime );
        assertEquals( 2 * DAY, intervals.get( 3 ).summTime );
        assertEquals( 2 * DAY, intervals.get( 3 ).minTime );

        assertEquals( 1 * DAY, intervals.get( 4 ).summTime );

        assertEquals( 1 * DAY, intervals.get( 5 ).summTime );
        assertEquals( 0, intervals.get( 6 ).summTime );

        int stop = 0;

    }

    @Test
    public void hasIntersection() {

        assertFalse( "Unexpected intersection", Case.hasIntersection( 10L, 20L, -1L, -2L ) );
        assertFalse( "Unexpected intersection", Case.hasIntersection( 10L, 20L, 0L, 0L ) );
        assertFalse( "Unexpected intersection", Case.hasIntersection( 10L, 20L, 1L, 9L ) );
        assertFalse( "Unexpected intersection", Case.hasIntersection( 10L, 20L, 9L, 1L ) );
        assertFalse( "Unexpected intersection", Case.hasIntersection( 10L, 20L, 10L, 0L ) );
        assertFalse( "Unexpected intersection", Case.hasIntersection( 10L, 20L, 21L, 30L ) );

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

        assertEquals( "Expected an intersection by continuing status", 10L, Case.calcIntersectionTime( 10L, 20L, 0L, null ) );
        assertEquals( "Expected an intersection", 0L, Case.calcIntersectionTime( 10L, 20L, 20L, 20L ) );
        assertEquals( "Expected an intersection", 0L, Case.calcIntersectionTime( 10L, 20L, 10L, 10L ) );
        assertEquals( "Expected an intersection", 10L, Case.calcIntersectionTime( 10L, 20L, 10L, 20L ) );
        assertEquals( "Expected an intersection", 10L, Case.calcIntersectionTime( 10L, 20L, 10L, 21L ) );
        assertEquals( "Expected an intersection", 10L, Case.calcIntersectionTime( 10L, 20L, 9L, 20L ) );
        assertEquals( "Expected an intersection", 1L, Case.calcIntersectionTime( 10L, 20L, 19L, 20L ) );
        assertEquals( "Expected an intersection", 1L, Case.calcIntersectionTime( 10L, 20L, 10L, 11L ) );
        assertEquals( "Expected an intersection", 9L, Case.calcIntersectionTime( 10L, 20L, 10L, 19L ) );
        assertEquals( "Expected an intersection", 9L, Case.calcIntersectionTime( 10L, 20L, 11L, 20L ) );
        assertEquals( "Expected an intersection", 8L, Case.calcIntersectionTime( 10L, 20L, 11L, 19L ) );
        assertEquals( "Expected an intersection", 4L, Case.calcIntersectionTime( 10L, 20L, 4L, 14L ) );
        assertEquals( "Expected an intersection", 4l, Case.calcIntersectionTime( 10L, 20L, 16L, 26L ) );

    }

    private Long makeCaseObject( Person person, Long productId, Date date ) {
        CaseObject caseObject = createNewCaseObject( person );
        caseObject.setProductId( productId );
        caseObject.setCreated( date );
        Long caseId = caseObjectDAO.insertCase( caseObject );
        caseIds.add( caseId );
        return caseId;
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

    private Report createReport( Long productId, Date from, Date to ) {
        CaseQuery caseQuery = new CaseQuery();
        caseQuery.setProductIds( Arrays.asList( productId ) );
        caseQuery.setStateIds( ignoredStates ); //TODO add test product


        caseQuery.setFrom( from );
        caseQuery.setTo( to );

        Report report = new Report();
        report.setReportType( En_ReportType.CASE_COMPLETION_TIME );
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
//        if (true) return; //TODO TURN ON
        if (caseIds == null) return;//
        if (!commentsIds.isEmpty()) {
            String caseIdsString = caseIds.stream().map( String::valueOf ).collect( Collectors.joining( "," ) );
            caseCommentDAO.removeByCondition( "CASE_ID in (" + caseIdsString + ")" );
            commentsIds.clear();
        }
        caseObjectDAO.removeByKeys( caseIds );
        personDAO.remove( person );
        companyDAO.removeByKey( person.getCompanyId() );

        caseIds = null;
    }

    private Date day( int day_of_month ) {
        return addHours( date1, (day_of_month - 1) * H_DAY );
    }


    public static final String PRODUCT_NAME = "TestProduct";
    private static final Long CREATED = 1L;
    private static final Long OPENED = 2L;
    private static final Long DONE = 17L;
    int H_DAY = 24;
    List<Integer> ignoredStates = Arrays.asList( 3, 5, 7, 8, 9, 10, 17, 32, 33 );
}
