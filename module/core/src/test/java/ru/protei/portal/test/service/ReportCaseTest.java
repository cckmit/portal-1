package ru.protei.portal.test.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.config.IntegrationTestsConfiguration;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.struct.CaseObjectComments;
import ru.protei.portal.core.report.caseobjects.ReportCase;
import ru.protei.portal.core.report.caseobjects.ReportCaseImpl;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.utils.TimeFormatter;
import ru.protei.portal.embeddeddb.DatabaseConfiguration;
import ru.protei.portal.mock.AuthServiceMock;
import ru.protei.winter.core.CoreConfigurationContext;
import ru.protei.winter.jdbc.JdbcConfigurationContext;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertTrue;
import static ru.protei.portal.core.model.dict.En_ImportanceLevel.*;
import static ru.protei.portal.core.model.helper.CollectionUtils.find;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfigurationContext.class,
        JdbcConfigurationContext.class,
        DatabaseConfiguration.class, IntegrationTestsConfiguration.class})
@Transactional
public class ReportCaseTest extends BaseServiceTest {

    @Autowired
    ReportCase reportCase;

    private AuthServiceMock authService;

    @Autowired
    private void authService( AuthService authService ) {
        this.authService = (AuthServiceMock) authService;
    }

    @Test
    public void writeReport() throws Exception {
        initData();

        CaseQuery caseQuery = makeCaseQuery(BASIC, IMPORTANT, CRITICAL);
        Report report = makeReport( caseQuery );

        assertTrue( "Expected not empty report", writeReport( report ) );
    }

    @Test
    public void withoutImportanceHistory() throws Exception {
        List<En_ImportanceLevel> importances = listOf( BASIC, IMPORTANT, CRITICAL );

        List<CaseObject> initCases = initData();
        List<CaseObject> cases = filterToList( initCases, caseObject -> importances.contains( caseObject.importanceLevel() ));

        CaseQuery caseQuery = makeCaseQuery();
        caseQuery.setCheckImportanceHistory(false);

        List<CaseObjectComments> caseObjectComments = ((ReportCaseImpl) reportCase).processChunk( caseQuery );

        assertTrue(  "Expected not empty report data", !isEmpty(caseObjectComments)  );
        List<CaseObject> reportCases = toList( caseObjectComments, CaseObjectComments::getCaseObject );
        for (CaseObject aCase : cases) {
            assertTrue(  "Missing case: " + aCase, find(  reportCases, caseObject -> Objects.equals( caseObject.getId(), aCase.getId() ) ).isPresent() );
        }
    }

    @Test
    public void withImportanceHistory() throws Exception {
        List<CaseObject> cases = initData();

        CaseQuery caseQuery = makeCaseQuery();
        caseQuery.setCheckImportanceHistory(true);

        List<CaseObjectComments> caseObjectComments = ((ReportCaseImpl) reportCase).processChunk( caseQuery );

        assertTrue(  "Expected not empty report data", !isEmpty(caseObjectComments)  );
        List<CaseObject> reportCases = toList( caseObjectComments, CaseObjectComments::getCaseObject );
        for (CaseObject aCase : cases) {
            assertTrue(  "Missing case: " + aCase, find(  reportCases, caseObject -> Objects.equals( caseObject.getId(), aCase.getId() ) ).isPresent() );
        }
    }

    private List<CaseObject> initData() {
        Person person = makePerson( makeCustomerCompany() );
        List<CaseObject> cases = new ArrayList<>();
        authService.makeThreadAuthToken( makeUserLogin( person ) );
        CaseObject caseObject1 = makeCaseObject( person );
        cases.add( caseObject1 );
        caseObject1.setImpLevel( BASIC.getId() );
        caseService.updateCaseObjectMeta( getAuthToken(), new CaseObjectMeta(caseObject1) );
        caseObject1.setImpLevel( IMPORTANT.getId() );
        caseService.updateCaseObjectMeta( getAuthToken(), new CaseObjectMeta(caseObject1) );
        caseObject1.setImpLevel( COSMETIC.getId() );
        caseService.updateCaseObjectMeta( getAuthToken(), new CaseObjectMeta(caseObject1) );

        CaseObject caseObject2 = makeCaseObject( person );
        cases.add( caseObject2 );
        caseObject2.setImpLevel( IMPORTANT.getId() );
        caseService.updateCaseObjectMeta( getAuthToken(), new CaseObjectMeta(caseObject2) );

        CaseObject caseObject3 = makeCaseObject( person );
        cases.add( caseObject3 );
        caseObject3.setImpLevel( CRITICAL.getId() );
        caseService.updateCaseObjectMeta( getAuthToken(), new CaseObjectMeta(caseObject3) );

        return cases;
    }

    private Report makeReport( CaseQuery caseQuery ) {
        Report report = new Report();
        report.setLocale( "Ru" );
        report.setCaseQuery( caseQuery );
        return report;
    }

    private boolean writeReport( Report report ) throws IOException {
        MockStream mockStream = new MockStream();
        reportCase.writeReport( mockStream, report, new SimpleDateFormat( "dd.MM.yyyy HH:mm" ), new TimeFormatter() );
        return !mockStream.isEmpty();
    }

    private class MockStream extends OutputStream {
        @Override
        public void write( int b ) throws IOException {
            bytes++;
        }

        int bytes = 0;

        public boolean isEmpty() {
            return bytes < 1;
        }
    }

    private CaseComment makeComment( Person person, CaseObject caseObject, En_ImportanceLevel importance ) {
        CaseComment caseComment = createNewComment( person, caseObject.getId(), "Test comment" );
        caseComment.setCaseImportance( importance );
        caseCommentDAO.persist( caseComment );
        return caseComment;
    }

    private CaseQuery makeCaseQuery( En_ImportanceLevel... importances ) {
        CaseQuery caseQuery = new CaseQuery();

        Date from = new Date();
        Calendar cal = new GregorianCalendar();
        cal.setTime( from );
        cal.set( Calendar.HOUR_OF_DAY, 0 );
        cal.set( Calendar.MINUTE, 0 );
        cal.set( Calendar.SECOND, 0 );
        cal.set( Calendar.MILLISECOND, 0 );
        from = cal.getTime();

        Date to = new Date();
        cal = new GregorianCalendar();
        cal.setTime( to );
        cal.set( Calendar.HOUR_OF_DAY, 23 );
        cal.set( Calendar.MINUTE, 59 );
        cal.set( Calendar.SECOND, 59 );
        cal.set( Calendar.MILLISECOND, 0 );
        to = cal.getTime();

        caseQuery.setCreatedFrom( from );
        caseQuery.setCreatedTo( to );
        caseQuery.setImportanceIds( toList( importances, En_ImportanceLevel::getId ) );

        return caseQuery;
    }


    @Test
    public void intervalsTest() {
        int numberOfDays = 12;
//        List<Interval> intervals = makeIntervals( date9, addHours( date9, numberOfDays * H_DAY ), DAY );
//        assertEquals( numberOfDays, intervals.size() );
//        assertEquals( date9.getTime(), intervals.get( 0 ).from );
//        assertEquals( addHours( date9, 1 * H_DAY ).getTime(), intervals.get( 0 ).to );
    }

}
