package ru.protei.portal.test.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.service.CaseService;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BaseServiceTest {
    @Inject
    CompanyDAO companyDAO;
    @Inject
    PersonDAO personDAO;
    @Inject
    CaseService caseService;
    @Autowired
    DevUnitDAO devUnitDAO;
    @Autowired
    CaseObjectDAO caseObjectDAO;

    @Inject
    CaseCommentDAO caseCommentDAO;

    @Inject
    JdbcManyRelationsHelper jdbcManyRelationsHelper;


    public static final AuthToken TEST_AUTH_TOKEN = new AuthToken( "TEST_SID", "127.0.0.1" );

    public Long makeProduct( String productName ) {
        DevUnit product = createProduct( productName );

        Long productId = devUnitDAO.persist( product );
        return productId;
    }

    private static DevUnit createProduct( String productName ) {
        DevUnit product = new DevUnit();
        product.setName( productName );
        product.setCreated( new Date() );
        product.setTypeId( En_DevUnitType.PRODUCT.getId() );
        product.setStateId( En_DevUnitState.ACTIVE.getId() );
        return product;
    }


    public static CaseObject createNewCaseObject( Person person ) {
        CaseObject caseObject = new CaseObject();
        caseObject.setCaseType( En_CaseType.TASK );
        caseObject.setName( "Test_Case_Name" );
        caseObject.setState( En_CaseState.CREATED );
        caseObject.setCaseType( En_CaseType.CRM_SUPPORT );
        return caseObject;
    }

    public static Person createNewPerson( Company company ) {
        Person person = new Person();
        person.setCreated( new Date() );
        person.setCreator( "TEST" );
        person.setCompanyId( company.getId() );
        person.setDisplayName( "Test_Person" );
        person.setGender( En_Gender.MALE );
        return person;
    }

    public static Company createNewCompany( CompanyCategory category ) {
        Company company = new Company();
        company.setCname( "Test_Company" );
        company.setCategory( category );
        return company;
    }

    protected static CaseComment createNewComment( Person person, Long caseObjectId, String text ) {
        return createNewComment( person, caseObjectId, text, null );
    }

    protected static CaseComment createNewComment( Person person, Long caseObjectId, String text, Long caseStateId ) {
        CaseComment comment = new CaseComment( text );
        comment.setCreated( new Date() );
        comment.setCaseId( caseObjectId );
        comment.setAuthorId( person.getId() );
        comment.setText( text );
        if (caseStateId != null) comment.setCaseStateId( caseStateId );
        comment.setCaseAttachments( Collections.emptyList() );
        return comment;
    }

    public static void checkResult( CoreResponse result ) {
        assertNotNull( "Expected result", result );
        assertTrue( "Expected ok result", result.isOk() );
    }

    public static <T> T checkResultAndGetData( CoreResponse<T> result ) {
        checkResult( result );
        return result.getData();
    }

    protected CaseObject makeCaseObject( Person person ) {
        return checkResultAndGetData(
                caseService.saveCaseObject( TEST_AUTH_TOKEN, createNewCaseObject( person ), person )
        );
    }

    protected Person makePerson( Company company ) {
        Person person = createNewPerson( company );
        person.setId( personDAO.persist( person ) );
        return person;
    }

    protected Company makeCompany( CompanyCategory category ) {
        Company company = createNewCompany( category );
        company.setId( companyDAO.persist( company ) );
        return company;
    }


}
