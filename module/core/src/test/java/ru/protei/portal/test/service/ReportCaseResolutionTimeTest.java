package ru.protei.portal.test.service;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.protei.portal.core.model.dict.En_CompanyCategory;
import ru.protei.portal.embeddeddb.DatabaseConfiguration;
import ru.protei.portal.config.IntegrationTestsConfiguration;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dto.CaseResolutionTimeReportDto;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.report.caseresolution.ReportCaseResolutionTime;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.report.caseresolution.ReportCaseResolutionTime.*;
import static ru.protei.portal.core.model.util.CrmConstants.Time.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfigurationContext.class, JdbcConfigurationContext.class, DatabaseConfiguration.class, IntegrationTestsConfiguration.class})
public class ReportCaseResolutionTimeTest extends BaseServiceTest {

    @Test
    public void productQueryTest() {
        QueryModel model = new QueryModel( "productQueryTest" );
        try {
            model = initCaseObjectsQueryTestModel( model );

            CaseQuery caseQuery = createCaseQuery( date10, addHours( date10, model.numberOfDays * H_DAY ) );
            caseQuery.setProductIds( model.productIncludedIds );

            ReportCaseResolutionTime report = new ReportCaseResolutionTime( caseQuery, caseCommentDAO );
            report.run();

            checkCases( report.getCases(), model.caseObjectIncludedIds );
            checkIntervals( report.getIntervals(), model.numberOfDays );

        } catch (Exception e) {
            throw e;
        } finally {
            clean( model );
        }
    }

    @Test
    public void tagQueryTest() {
        QueryModel model = new QueryModel( "tagQueryTest" );
        try {
            model = initCaseObjectsQueryTestModel( model );

            CaseQuery caseQuery = createCaseQuery( date10, addHours( date10, model.numberOfDays * H_DAY ) );
            caseQuery.setCaseTagsIds( model.caseTagIncludedIds );

            ReportCaseResolutionTime report = new ReportCaseResolutionTime( caseQuery, caseCommentDAO );
            report.run();

            checkCases( report.getCases(), model.caseObjectIncludedIds );
            checkIntervals( report.getIntervals(), model.numberOfDays );

        } catch (Exception e) {
            throw e;
        } finally {
            clean( model );
        }
    }

    @Test
    public void companiesQueryTest() {
        QueryModel model = new QueryModel( "companiesQueryTest" );
        try {
            model = initCaseObjectsQueryTestModel( model );

            CaseQuery caseQuery = createCaseQuery( date10, addHours( date10, model.numberOfDays * H_DAY ) );
            caseQuery.setCompanyIds( model.companysIncludedIds );

            ReportCaseResolutionTime report = new ReportCaseResolutionTime( caseQuery , caseCommentDAO );
            report.run();

            checkCases( report.getCases(), model.caseObjectIncludedIds );
            checkIntervals( report.getIntervals(), model.numberOfDays );

        } catch (Exception e) {
            throw e;
        } finally {
            clean( model );
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

        List<Case> cases = groupBayIssues( convert( comments ) );

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

        List<Case> cases = groupBayIssues( convert( comments ) );

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

        assertEquals( 2, size( intervals.get( 2 ).caseNumbers ));
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

        assertEquals( 2, size( intervals.get( 8 ).caseNumbers ));
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


        List<Case> cases = groupBayIssues( convert(comments) );

        for (Interval interval : intervals) {
            interval.fill( cases, new HashSet<>( activeStatesShort ) );
        }

        XSSFWorkbook workBook = createWorkBook( intervals, DEFAULT_COLUMN_NAMES );

        assertNotNull( workBook );
        assertEquals( 1, workBook.getNumberOfSheets() );
        assertNotNull( workBook.getSheetAt( 0 ) );
        assertEquals( numberOfDays, workBook.getSheetAt( 0 ).getLastRowNum() ); // учтена строка с заголовком
    }

    private List<CaseResolutionTimeReportDto> convert( List<CaseComment> comments ) {
        return toList( comments, comment -> {
            CaseResolutionTimeReportDto reportDto = new CaseResolutionTimeReportDto();
            reportDto.setCaseId( comment.getCaseId() );
            reportDto.setCaseStateId( comment.getCaseStateId() );
            reportDto.setCreated( comment.getCreated() );
            return reportDto;
        } );
    }

    private QueryModel initCaseObjectsQueryTestModel( QueryModel model) {
        CaseObjectFactory factory = new CaseObjectFactory( model );

        model.companyCategory = En_CompanyCategory.PARTNER;
        Company company = factory.makeCompany( "some_company" );
        Person person = factory.makePerson( company );
        model.person = person;

        model.caseTagType = En_CaseType.CRM_SUPPORT;
        CaseTag includeTag = factory.makeTag( "tag_for_include_1", company.getId() );
        CaseTag includeTag2 = factory.makeTag( "tag_for_include_2", company.getId() );
        CaseTag excludeTag = factory.makeTag( "tag_for_exclude", company.getId() );
        model.caseTagIncludedIds = toList( listOf( includeTag, includeTag2 ), CaseTag::getId );

        Long includedProduct = factory.makeProduct( "product_included_1" );
        Long includedProduct2 = factory.makeProduct( "product_included_2" );
        Long excludedProduct = factory.makeProduct( "product_excluded" );
        model.productsIncludedIds( setOf( includedProduct, includedProduct2 ) );

        Company includedCompany = factory.makeCompany( "company_included_1" );
        Company includedCompany2 = factory.makeCompany( "company_included_2" );
        Company excludedCompany = factory.makeCompany( "company_excluded" );
        model.companysIncludedIds( toList( listOf( includedCompany, includedCompany2 ), Company::getId ) );

        model.numberOfDays = 12;
        //  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 31
        //                               ^x
        Long caseId = factory.makeCase( day( 9 ), includeTag, includedProduct, includedCompany );
        model.caseObjectIncludedIds.add(caseId);
        CaseComment c1 = createNewComment( person, caseId, "One day" );
        makeComment( c1, CREATED, day( 11 ) );                              //2050-01-11 00:00:00
        makeComment( c1, OPENED, addHours( day( 11 ), 2 ) );                //2050-01-11 02:00:00
        makeComment( c1, DONE, addHours( day( 11 ), 6 ) );                  //2050-01-11 06:00:00

        //  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 31
        //                          ^n------^--------x     ^----------------------------------------
        Long caseId2 = factory.makeCase( day( 9 ), includeTag2, includedProduct2, includedCompany2 );
        model.caseObjectIncludedIds.add(caseId2);
        CaseComment c2 = createNewComment( person, caseId2, "Week" );
        makeComment( c2, CREATED, day( 9 ) );                                      //2050-01-09 00:00:00
        makeComment( c2, null, addHours( day( 9 ), 2 ) );            //2050-01-09 02:00:00
        makeComment( c2, OPENED, addHours( day( 12 ), 2 ) );                //2050-01-12 02:00:00
        makeComment( c2, DONE, addHours( day( 15 ), 5 ) );                  //2050-01-15 05:00:00
        makeComment( c2, REOPENED, addHours( day( 17 ), 11 ) );             //2050-01-17 11:00:00

        //  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 31
        //                                     ^--------------^---n------------^
        Long caseId3 = factory.makeCase( day( 9 ), includeTag, includedProduct, includedCompany );
        model.caseObjectIncludedIds.add(caseId3);
        CaseComment c3 = createNewComment( person, caseId3, "2 Week" );
        makeComment( c3, CREATED, day( 13 ) );                                     //2050-01-13 00:00:00
        makeComment( c3, OPENED, addHours( day( 18 ), 5 ) );                //2050-01-18 05:00:00
        makeComment( c3, null, addHours( day( 19 ), 11 ) );          //2050-01-19 11:00:00
        makeComment( c3, DONE, addHours( day( 24 ), 11 ) );                 //2050-01-24 11:00:00

        //  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 31
        //   excluded                                ^..............................................
        Long excludedCaseId4 = factory.makeCase( day( 9 ), excludeTag, excludedProduct, excludedCompany );
        CaseComment c4 = createNewComment( person, excludedCaseId4, "excluded comment" );
        makeComment( c4, CREATED, day( 15 ) );                                     //2050-01-15 00:00:00

        return model;
    }

    private void checkIntervals( List<Interval> intervals, int numberOfDays  ) {

        //                            | 0| 1| 2| 3| 4| 5| 6| 7| 8| 9|10|11|
        //  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 31
        //                            |-----------------------------------|
        //                               ^x
        //                          ^n------^--------x     ^----------------------------------------
        //                                     ^--------------^---n-------------х
        //   excluded                                ^..............................................

        assertEquals( numberOfDays, intervals.size() );

        assertEquals( 1, size( intervals.get( 0 ).caseNumbers ));
        assertEquals( 2 * DAY, intervals.get( 0 ).minTime );
        assertEquals( 2 * DAY, intervals.get( 0 ).maxTime );
        assertEquals( 2 * DAY, intervals.get( 0 ).summTime );

        assertEquals( 2, size( intervals.get( 1 ).caseNumbers ));
        assertEquals( 6 * HOUR, intervals.get( 1 ).minTime );
        assertEquals( 3 * DAY, intervals.get( 1 ).maxTime );
        assertEquals( 3 * DAY + 6 * HOUR, intervals.get( 1 ).summTime );

        assertEquals( 1, size( intervals.get( 2 ).caseNumbers ));
        assertEquals( 4 * DAY, intervals.get( 2 ).minTime );
        assertEquals( 4 * DAY, intervals.get( 2 ).maxTime );
        assertEquals( 4 * DAY, intervals.get( 2 ).summTime );

        assertEquals( 2, size( intervals.get( 3 ).caseNumbers ));
        assertEquals( 1 * DAY, intervals.get( 3 ).minTime );
        assertEquals( 5 * DAY, intervals.get( 3 ).maxTime );
        assertEquals( 6 * DAY, intervals.get( 3 ).summTime );

        assertEquals( 1, size( intervals.get( 6 ).caseNumbers ));
        assertEquals( 4 * DAY, intervals.get( 6 ).minTime );
        assertEquals( 4 * DAY, intervals.get( 6 ).maxTime );
        assertEquals( 4 * DAY, intervals.get( 6 ).summTime );

        assertEquals( 2, size( intervals.get( 7 ).caseNumbers ));
        long case3Time = 5 * DAY;
        assertEquals( case3Time, intervals.get( 7 ).minTime );
        long case2Time = 6 * DAY + 5 * HOUR + DAY - 11 * HOUR;
        assertEquals( case2Time, intervals.get( 7 ).maxTime );
        assertEquals( case2Time + case3Time, intervals.get( 7 ).summTime );

        assertEquals( 2, size( intervals.get( 11 ).caseNumbers ));
        long case11minTime = 9 * DAY;
        assertEquals( case11minTime, intervals.get( 11 ).minTime );
        long case11maxTime = 7 * DAY + 5 * HOUR + 4 * DAY - 11 * HOUR;
        assertEquals( case11maxTime, intervals.get( 11 ).maxTime );
        assertEquals( case11minTime + case11maxTime, intervals.get( 11 ).summTime );
    }

    private void checkCases( List<Case> cases, List<Long> caseObjectIncludedIds ) {
        assertEquals( "grouping comments not worked", size( caseObjectIncludedIds ), size( cases ) );
        assertEquals( "expected only included cases", caseObjectIncludedIds, stream( cases ).map( cse -> cse.caseId ).sorted().collect( Collectors.toList() ) );
        assertEquals( 9, cases.stream().mapToInt( cse -> size( cse.statuses ) ).sum() );
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

    private CaseQuery createCaseQuery(  Date from, Date to ) {
        CaseQuery caseQuery = new CaseQuery();
        caseQuery.setStateIds( activeStatesShort );
        caseQuery.setCreatedFrom( from );
        caseQuery.setCreatedTo( to );
        return caseQuery;
    }

    private Date addHours( Date date, int hours ) {
        Calendar cal = Calendar.getInstance();
        cal.setTime( date );
        cal.add( Calendar.HOUR_OF_DAY, hours );
        return cal.getTime();
    }

    private QueryModel clean(QueryModel model) {
        if (model == null) return model;

        if (!commentsIds.isEmpty() && model.caseIds != null) {
            String caseIdsString = model.caseIds.stream().map( String::valueOf ).collect( Collectors.joining( "," ) );
            caseCommentDAO.removeByCondition( "CASE_ID in (" + caseIdsString + ")" );
            commentsIds.clear();
        }

        caseObjectDAO.removeByKeys( model.caseIds );
        personDAO.removeByKeys( model.personIds );
        caseTagDAO.removeByKeys( model.caseTagIds );
        companyDAO.removeByKeys( model.companyIds );

        return model;
    }

    private Date day( int day_of_month ) {
        return addHours( date1, (day_of_month - 1) * H_DAY );
    }


    private static Date date1 = new GregorianCalendar( 2050, Calendar.JANUARY, 1, 0, 0 ).getTime();
    private static Date date10 = new GregorianCalendar( 2050, Calendar.JANUARY, 10, 0, 0 ).getTime();
    private static Date date9 = new GregorianCalendar( 2050, Calendar.JANUARY, 9, 0, 0 ).getTime();

    private static List<Integer> activeStatesShort = Arrays.asList( 1, 2, 6, 16, 19, 30 );
    private static List<Long> commentsIds = new ArrayList<>();

    private static final Long CREATED = 1L;
    private static final Long OPENED = 2L;
    private static final Long REOPENED = 6L;
    private static final Long VERIFIED = 5L;
    private static final Long DONE = 17L;
    private static final int H_DAY = 24;
    private static final Logger log = LoggerFactory.getLogger( ReportCaseResolutionTimeTest.class );

    class QueryModel {

        public QueryModel( String testDataPrefix ) {
            this.prefix = testDataPrefix + "_";
        }

        public void productsIncludedIds( Set<Long> productIncludedIds ) {
            this.productIncludedIds = productIncludedIds;
        }

        public void companysIncludedIds( List<Long> companysIncludedIds ) {
            this.companysIncludedIds = companysIncludedIds;

        }
        public void rememberProductId( Long id ) {
            productIds.add( id );
        }

        public void rememberCompanyId( Long id ) {
            companyIds.add( id );
        }

        public void rememberPersonId( Long id ) {
            personIds.add(id);
        }


        private void rememberCaseId( Long caseId ) {
            caseIds.add( caseId );
        }

        private void rememberTagId( Long caseId ) {
            caseTagIds.add( caseId );
        }

        public String prefix;
        public int numberOfDays;
        public En_CompanyCategory companyCategory;
        public En_CaseType caseTagType;

        List<Long> caseObjectIncludedIds = new ArrayList<>();
        List<Long> caseTagIncludedIds = new ArrayList<>();
        List<Long> caseIds = new ArrayList<>();
        List<Long> caseTagIds = new ArrayList<>();
        Set<Long> productIncludedIds;
        List<Long> companysIncludedIds;

        private Person person;
        private List<Long> productIds = new ArrayList<>();
        private List<Long> companyIds = new ArrayList<>();
        private List<Long> personIds = new ArrayList<>();
    }

    class CaseObjectFactory {

        public CaseObjectFactory( QueryModel model ) {
            this.model = model;
        }

        public Long makeProduct( String name ) {
            Long id = ReportCaseResolutionTimeTest.this.makeProduct( model.prefix + name ).getId();
            model.rememberProductId( id );
            return id;
        }

        public Company makeCompany( String companyName ) {
            Company company = ReportCaseResolutionTimeTest.this.makeCompany( model.prefix + companyName, model.companyCategory );
            model.rememberCompanyId( company.getId() );
            return company;
        }

        public Person makePerson( Company company ) {
            Person person = ReportCaseResolutionTimeTest.this.makePerson( company );
            model.rememberPersonId( person.getId() );
            return person;
        }

        protected CaseTag makeTag( String tag1, Long companyId ) {
            CaseTag caseTag = ReportCaseResolutionTimeTest.this.makeCaseTag(model.prefix + tag1, model.caseTagType, companyId );
            model.rememberTagId( caseTag.getId() );
            return caseTag;
        }

        public Long makeCase( Date day, CaseTag caseTag, Long productId, Company initiatorCompany ) {
            Long id = ReportCaseResolutionTimeTest.this.makeCaseObject(
                    model.person, productId, day, initiatorCompany.getId()
            ).getId();
            model.rememberCaseId( id );
            caseObjectTagDAO.persist(new CaseObjectTag(id, caseTag.getId()));

            return id;
        }

        private QueryModel model;
    }
}
