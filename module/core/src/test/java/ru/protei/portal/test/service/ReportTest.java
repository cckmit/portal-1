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

import static org.junit.Assert.assertEquals;
import static ru.protei.portal.core.model.helper.CollectionUtils.size;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfigurationContext.class, JdbcConfigurationContext.class, MainTestsConfiguration.class})
public class ReportTest extends BaseTest {

    Long productId;
    Person person;
    Date date = new GregorianCalendar( 2050, Calendar.JANUARY, 9, 0, 0 ).getTime();
    List<Long> commentsIds = new ArrayList<>();
    List<Long> caseIds = new ArrayList<>();

    @Before
    public void beforeEachTest() {
        init();
    }

    @After
    public void afterEachTest() {
        clean();
    }

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
        Long caseId = makeCaseObject( person, productId, date );
        CaseComment c1 = createNewComment( person, caseId, "One day" );
        makeComment( c1, CREATED, addHours( date, 2 * DAY ) );              //2050-01-11 00:00:00
        makeComment( c1, OPENED, addHours( c1.getCreated(), 2 ) );          //2050-01-11 02:00:00
        makeComment( c1, DONE, addHours( c1.getCreated(), 4 ) );            //2050-01-11 06:00:00

        //  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 31
        //                          ^--------^---------- ^
        Long caseId2 = makeCaseObject( person, productId, date );
        CaseComment c2 = createNewComment( person, caseId2, "Week" );
        makeComment( c2, CREATED, date );                                      //2050-01-09 00:00:00
        makeComment( c2, OPENED, addHours( c2.getCreated(), 3 * DAY + 2 ) );   //2050-01-12 02:00:00
        makeComment( c2, DONE, addHours( c2.getCreated(), 4 * DAY + 3 ) );     //2050-01-16 05:00:00

        //  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 31
        //                                      ^--------------^----------------^
        Long caseId3 = makeCaseObject( person, productId, date );
        CaseComment c3 = createNewComment( person, caseId3, "2 Week" );
        makeComment( c3, CREATED, addHours( date, 4 * DAY ) );                 //2050-01-13 00:00:00
        makeComment( c3, OPENED, addHours( c3.getCreated(), 5 * DAY + 5 ) );   //2050-01-18 05:00:00
        makeComment( c3, DONE, addHours( c3.getCreated(), 6 * DAY + 6 ) );     //2050-01-24 11:00:00

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
        comment1.setCaseStateId( status );
        comment1.setCreated( created );
        comment1.setId( null );
        commentsIds.add( caseCommentDAO.persist( comment1 ) );
    }

    @Test
    public void getCaseObjectsTest() throws Exception {
        Report report = makeReport( productId, date );

        ReportCaseCompletionTime caseComletionTimeReport = new ReportCaseCompletionTime( report, caseCommentDAO );
        caseComletionTimeReport.run();

        List<ReportCaseCompletionTime.Case> cases = caseComletionTimeReport.getCases();
        assertEquals( caseIds.size(), size( cases ) );
        assertEquals( commentsIds.size(), cases.stream().mapToInt( cse -> size( cse.statuses ) ).sum() );

    }

    private Report makeReport( Long productId, Date date ) {

        CaseQuery caseQuery = new CaseQuery();
        caseQuery.setProductIds( Arrays.asList( productId ) );
        caseQuery.setStateIds( Arrays.asList( 3, 5, 7, 8, 9, 10, 17, 32, 33 ) ); //TODO add test product
        caseQuery.setFrom( date );
        caseQuery.setTo( addHours( date, 10 * DAY ) );

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

    public static final String PRODUCT_NAME = "TestProduct";
    private static final Long CREATED = 1L;
    private static final Long OPENED = 2L;
    private static final Long DONE = 17L;
    int DAY = 24;
}
