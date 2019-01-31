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

import java.io.ByteArrayOutputStream;
import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfigurationContext.class, JdbcConfigurationContext.class, MainTestsConfiguration.class})
public class ReportTest extends BaseTest {

    Long productId;
    Long caseId;
    Person person;
    Date date = new GregorianCalendar( 2050, Calendar.JANUARY, 9, 0, 0 ).getTime();
    List<Long> commentsIds = new ArrayList<>();

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

        CaseObject caseObject = createNewCaseObject( person );
        caseObject.setProductId( productId );
        caseObject.setCreated( date );
        caseId =  caseObjectDAO.insertCase( caseObject );

        CaseComment comment1 = createNewComment( person, caseId, "Test_Comment" );
        commentsIds.add( comment1.getId() );

        comment1.setCaseStateId( CREATED );
        comment1.setCreated( date );
        comment1.setId( null );
        commentsIds.add( caseCommentDAO.persist( comment1 ) );

        comment1.setCaseStateId( OPENED );
        comment1.setCreated( addHours( date, 1 ) );//Date.from( date.plus( 1, ChronoUnit.HOURS ) ) );
        comment1.setId( null );
        commentsIds.add( caseCommentDAO.persist( comment1 ) );

        comment1.setCaseStateId( DONE );
        comment1.setCreated( addHours( date, 2 ) );
        comment1.setId( null );
        commentsIds.add( caseCommentDAO.persist( comment1 ) );


    }

    @Test
    public void getCaseObjectsTest() throws Exception {
        Report report = makeReport( productId ); //TODO add test product 18572L
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        ReportCaseCompletionTime caseComletionTimeReport = new ReportCaseCompletionTime( report, caseCommentDAO );
        caseComletionTimeReport.writeReport( buffer );
    }

    private Report makeReport( Long productId ) {

        CaseQuery caseQuery = new CaseQuery();
        caseQuery.setProductIds( Arrays.asList( productId ) );
        caseQuery.setStateIds( Arrays.asList( 3, 5, 7, 8, 9, 10, 17, 32, 33 ) ); //TODO add test product
        caseQuery.setFrom( date );
        caseQuery.setTo( addHours( date, 12 ) );

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
        if(true) return; //TODO TURN ON
        if (caseId == null) return;//
        if (!commentsIds.isEmpty()) {
            caseCommentDAO.removeByCondition( "CASE_ID=?", caseId );
            commentsIds.clear();
        }
        caseObjectDAO.removeByKey( caseId );
        personDAO.remove( person );
        companyDAO.removeByKey( person.getCompanyId() );

        caseId = null;
    }

    public static final String PRODUCT_NAME = "TestProduct";
    private static final Long CREATED = 1L;
    private static final Long OPENED = 2L;
    private static final Long DONE = 17L;
}
