package ru.protei.portal.test.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.Collections;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static ru.protei.portal.core.model.helper.CollectionUtils.setOf;

public class BaseServiceTest {

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

    public static CaseObject createNewCaseObject( Person person, CaseTag caseTag ) {
        CaseObject caseObject = createNewCaseObject( En_CaseType.CRM_SUPPORT, person );
        caseObject.setTags( setOf( caseTag ) );
        return caseObject;
    }

    public static CaseObject createNewCaseObject( Person person ) {
        return createNewCaseObject( En_CaseType.CRM_SUPPORT, person );
    }

    public static CaseObject createNewCaseObject(  En_CaseType caseType, Person person ) {
        return createNewCaseObject(En_CaseType.CRM_SUPPORT, generateNextCaseNumber( caseType ), person);
    }

    public static CaseObject createNewCaseObject( En_CaseType caseType, Long caseNo, Person person ) {
        CaseObject caseObject = new CaseObject();
        caseObject.setName( "Test_Case_Name" );
        caseObject.setCaseNumber(caseNo);
        caseObject.setState( En_CaseState.CREATED );
        caseObject.setCaseType( caseType );
        caseObject.setCreator( person );
        caseObject.setCreated( new Date() );
        caseObject.setModified( new Date() );
        caseObject.setInitiatorCompanyId(1L);
        caseObject.setImpLevel(En_ImportanceLevel.BASIC.getId());
        return caseObject;
    }

    public static Person createNewPerson( Company company ) {
        Person person = new Person();
        person.setCreated( new Date() );
        person.setCreator( "TEST" );
        person.setCompanyId( company.getId() );
        person.setCompany( company );
        person.setDisplayName( "Test_Person" );
        person.setGender( En_Gender.MALE );
        return person;
    }

    public static Company createNewCustomerCompany() {
        return createNewCompany(new CompanyCategory(En_CompanyCategory.CUSTOMER.getId()));
    }

    public static Company createNewCompany( CompanyCategory category ) {
        return createNewCompany( "Test_Company", category );
    }

    public static Company createNewCompany( String companyName, CompanyCategory category ) {
        Company company = new Company();
        company.setCname( companyName );
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

    protected CaseTag createCaseTag (String name, En_CaseType type){
        CaseTag caseTag = new CaseTag();
        caseTag.setCaseType(type);
        caseTag.setName( name );
        return caseTag;
    }

    public static void checkResult( Result result ) {
        assertNotNull( "Expected result", result );
        assertTrue( "Expected ok result", result.isOk() );
    }

    public static <T> T checkResultAndGetData( Result<T> result ) {
        checkResult( result );
        return result.getData();
    }

    protected UserSessionDescriptor getDescriptor() {
        return authService.findSession(null);
    }

    protected AuthToken getAuthToken() {
        return getDescriptor().makeAuthToken();
    }


    // Create and persist

    protected CaseObject makeCaseObject( Person person ) {
        return makeCaseObject(En_CaseType.CRM_SUPPORT, person);
    }

    protected CaseObject makeCaseObject( En_CaseType caseType, Person person) {
        return checkResultAndGetData(
                caseService.createCaseObject( getAuthToken(), createNewCaseObject( caseType, person ), person )
        );
    }

    protected CaseObject makeCaseObject( Person person, Long productId, Date date, CaseTag caseTag, Long initiatorCompanyId ) {
        CaseObject caseObject = createNewCaseObject( person, caseTag );
        caseObject.setProductId( productId );
        caseObject.setCreated( date );
        caseObject.setInitiatorCompanyId( initiatorCompanyId );
        return makeCaseObject(caseObject);
    }

    protected CaseObject makeCaseObject( Person person, Long productId, Date date, CaseTag caseTag ) {
        CaseObject caseObject = createNewCaseObject( person, caseTag );
        caseObject.setProductId( productId );
        caseObject.setCreated( date );
        return makeCaseObject(caseObject);
    }

    protected CaseObject makeCaseObject( CaseObject caseObject ) {
        Long caseId = caseObjectDAO.insertCase( caseObject );
        caseObject.setId( caseId );
        caseObjectTagDAO.persistBatch(
                caseObject.getTags()
                        .stream()
                        .map(tag -> new CaseObjectTag(caseId, tag.getId()))
                        .collect( Collectors.toList())
        );
        return caseObject;
    }

    protected CaseComment makeCaseComment(Person person, Long caseObjectId, String text) {
        return makeCaseComment(person, caseObjectId, text, null);
    }

    protected CaseComment makeCaseComment(Person person, Long caseObjectId, String text, Long caseStateId) {
        CaseComment caseComment = createNewComment(person, caseObjectId, text, caseStateId);
        caseComment.setId(caseCommentDAO.persist(caseComment));
        return caseComment;
    }

    protected Person makePerson( Company company ) {
        Person person = createNewPerson( company );
        person.setId( personDAO.persist( person ) );
        return person;
    }

    protected Company makeCustomerCompany() {
        return makeCompany(new CompanyCategory(En_CompanyCategory.CUSTOMER.getId()));
    }

    protected Company makeCompany( CompanyCategory category ) {
        Company company = createNewCompany( category );
        company.setId( companyDAO.persist( company ) );
        return company;
    }

    protected Company makeCompany( String companyName,  CompanyCategory category ) {
        return makeCompany( createNewCompany( companyName, category ) );
    }

    protected Company makeCompany( Company company ) {
        company.setId( companyDAO.persist( company ) );
        return company;
    }

    protected CaseTag makeCaseTag( String tag1, En_CaseType type ) {
        CaseTag caseTag = createCaseTag(tag1, type);
        caseTag.setId( caseTagDAO.persist( caseTag ) );
        return caseTag;
    }

    // Remove

    protected boolean removeCaseObjectAndComments(CaseObject caseObject) {
        caseCommentDAO.getCaseComments(new CaseCommentQuery(caseObject.getId()))
                .forEach(caseComment -> caseCommentDAO.remove(caseComment));
        return caseObjectDAO.remove(caseObject);
    }

    private static Long generateNextCaseNumber( En_CaseType caseType ) {
        return caseNumberRepo.get( caseType ).incrementAndGet();
    }

    static ConcurrentHashMap<En_CaseType, AtomicLong> caseNumberRepo = new ConcurrentHashMap<>();

    static {
        for (En_CaseType type : En_CaseType.values()) {
            caseNumberRepo.put( type, new AtomicLong( 0L ) );
        }
    }

    @Autowired
    protected CaseService caseService;
    @Autowired
    protected AuthService authService;

    @Autowired
    protected CompanyDAO companyDAO;
    @Autowired
    protected CaseTagDAO caseTagDAO;
    @Autowired
    protected CaseObjectTagDAO caseObjectTagDAO;
    @Autowired
    protected CaseTypeDAO caseTypeDAO;
    @Autowired
    protected PersonDAO personDAO;
    @Autowired
    protected DevUnitDAO devUnitDAO;
    @Autowired
    protected CaseObjectDAO caseObjectDAO;
    @Autowired
    protected CaseCommentDAO caseCommentDAO;
    @Autowired
    protected JdbcManyRelationsHelper jdbcManyRelationsHelper;
}
