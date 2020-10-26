package ru.protei.portal.test.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.core.service.ProjectService;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.mock.AuthServiceMock;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BaseServiceTest {

    public UserLogin createUserLogin( Person person ) {
        UserLogin userLogin = userLoginDAO.createNewUserLogin( person );
        userLogin.setUlogin("user" + person.getId());
        userLogin.setCreated(new Date());
        userLogin.setAdminStateId(En_AdminState.UNLOCKED.getId());
        userLogin.setAuthType(En_AuthType.LOCAL);
        userLogin.setPersonId(person.getId());
        return userLogin;
    }

    public static UserRole createUserRole(String roleCode, Set<En_Privilege> privileges) {
        UserRole role = new UserRole();
        role.setCode(roleCode);
        role.setPrivileges(privileges);
        role.setScope(En_Scope.SYSTEM);
        return role;
    }

    public static DevUnit createProduct( String productName ) {
        DevUnit product = new DevUnit();
        product.setName( productName );
        product.setCreated( new Date() );
        product.setType( En_DevUnitType.PRODUCT );
        product.setStateId( En_DevUnitState.ACTIVE.getId() );
        product.setInfo( "info" );
        product.setHistoryVersion( "historyVersion" );
        product.setCdrDescription( "cdrDescription" );
        product.setConfiguration( "configuration" );
        product.setWikiLink( "https://newportal.protei.ru/" );
        return product;
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
        caseObject.setCaseNumber( caseNo );
        caseObject.setStateId( CrmConstants.State.CREATED );
        caseObject.setStateName( "created" );
        caseObject.setManagerCompanyId( person.getCompanyId() );
        caseObject.setType( caseType );
        caseObject.setCreator( person );
        caseObject.setCreated( new Date() );
        caseObject.setModified( new Date() );
        caseObject.setImpLevel( En_ImportanceLevel.BASIC.getId() );
        return caseObject;
    }

    public static Person createNewPerson( Company company ) {
        return createNewPerson(company, null);
    }

    public static Person createNewPerson(Company company, Long personId) {
        Person person = new Person();
        person.setCreated( new Date() );
        person.setId(personId);
        person.setCreator( "TEST" );
        person.setCompanyId( company.getId() );
        person.setCompany( company );
        person.setDisplayName( "Test_Person" );
        person.setGender( En_Gender.MALE );
        return person;
    }

    public static Company createNewCustomerCompany() {
        return createNewCompany(En_CompanyCategory.CUSTOMER);
    }

    public static Company createNewCompany( En_CompanyCategory category ) {
        return createNewCompany( "Test_Company", category );
    }

    public static Company createNewCompany( String companyName, En_CompanyCategory category ) {
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

    protected CaseTag createCaseTag (String name, En_CaseType type, Long companyId){
        CaseTag caseTag = new CaseTag();
        caseTag.setCaseType(type);
        caseTag.setName(name);
        caseTag.setCompanyId(companyId);
        return caseTag;
    }

    protected Plan createPlan() {
        return createPlan("");
    }

    protected Plan createPlan(String name) {
        Plan plan = new Plan();
        plan.setName(name + "test" + new Date().getTime());
        plan.setStartDate(new Date());
        plan.setFinishDate(new Date());
        return plan;
    }

    protected PersonAbsence createAbsence(Long personId) {
        PersonAbsence absence = new PersonAbsence();
        absence.setCreated(new Date());
        absence.setCreatorId(personId);
        absence.setPersonId(personId);
        absence.setFromTime(new Date());
        absence.setTillTime(new Date());
        absence.setReason(En_AbsenceReason.PERSONAL_AFFAIR);
        return absence;
    }

    public static void checkResult( Result result ) {
        assertNotNull( "Expected result", result );
        assertTrue( "Expected ok result", result.isOk() );
    }

    public static <T> T checkResultAndGetData( Result<T> result ) {
        checkResult( result );
        return result.getData();
    }

    protected AuthToken getAuthToken() {
        return (authService instanceof AuthServiceMock) ? ((AuthServiceMock) authService).getAuthToken() : null;
    }


    // Create and persist



    protected UserRole makeUserRole(UserRole userRole) {
        userRole.setId(  userRoleDAO.persist(userRole)  );
        return userRole;
    }

    protected UserLogin makeUserLogin( Person person ) {
        return makeUserLogin(createUserLogin( person ));
    }

    protected UserLogin makeUserLogin( UserLogin userLogin ) {
        userLogin.setId( userLoginDAO.persist( userLogin ) );
        return userLogin;
    }

    protected CaseObject makeCaseObject( Person person ) {
        return makeCaseObject(En_CaseType.CRM_SUPPORT, person);
    }

    protected CaseObject makeCaseObject( En_CaseType caseType, Person person) {
        Company company = makeCompany( createNewCustomerCompany() );
        CaseObject newCaseObject = createNewCaseObject( caseType, person );
        newCaseObject.setInitiatorCompany( company );
        return checkResultAndGetData(
                caseService.createCaseObject( getAuthToken(), new CaseObjectCreateRequest(newCaseObject) )
        );
    }

    protected CaseObject makeCaseObject( Person person, Long productId, Date date, Long initiatorCompanyId ) {
        CaseObject caseObject = createNewCaseObject( person );
        caseObject.setProductId( productId );
        caseObject.setCreated( date );
        caseObject.setInitiatorCompanyId( initiatorCompanyId );
        return makeCaseObject(caseObject);
    }

    protected CaseObject makeCaseObject( CaseObject caseObject ) {
        Long caseId = caseObjectDAO.insertCase( caseObject );
        caseObject.setId( caseId );
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
        return makePerson(company, null);
    }

    protected Person makePerson(Company company, Long personId) {
        return makePerson(createNewPerson( company, personId ));
    }

    protected Person makePerson( Person person ) {
        person.setId( personDAO.persist( person ) );
        return person;
    }

    protected Company makeCustomerCompany() {
        return makeCompany( En_CompanyCategory.CUSTOMER );
    }

    protected Company makeCompany( En_CompanyCategory category ) {
        Company company = createNewCompany( category );
        company.setId( companyDAO.persist( company ) );
        return company;
    }

    protected Company makeCompany( String companyName,  En_CompanyCategory category ) {
        return makeCompany( createNewCompany( companyName, category ) );
    }

    protected Company makeCompany( Company company ) {
        company.setId( companyDAO.persist( company ) );
        return company;
    }

    protected CaseTag makeCaseTag( String name, En_CaseType type, Long companyId ) {
        CaseTag caseTag = createCaseTag( name, type, companyId );
        caseTag.setId( caseTagDAO.persist( caseTag ) );
        return caseTag;
    }

    public DevUnit makeProduct(  ) {
        return makeProduct( "Test product_" + uniqueIndex.incrementAndGet() );
    }

    public DevUnit makeProduct( String productName ) {
        return makeProduct( createProduct( productName ) );
    }

    public DevUnit makeProduct( DevUnit product ) {
        product.setId( devUnitDAO.persist( product ) );
        return product;
    }

    protected PersonAbsence makeAbsence(Long personId) {
        PersonAbsence absence = createAbsence(personId);
        absence.setId(personAbsenceDAO.persist(absence));
        return absence;
    }

    protected String serializeAsJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected <T> T deserializeFromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Remove

    protected boolean removeCaseObjectAndComments(CaseObject caseObject) {
        caseCommentDAO.getCaseComments(new CaseCommentQuery(caseObject.getId()))
                .forEach(caseComment -> caseCommentDAO.remove(caseComment));
        return caseObjectDAO.remove(caseObject);
    }

    protected static Long generateNextCaseNumber( En_CaseType caseType ) {
        return caseNumberRepo.get( caseType ).incrementAndGet();
    }

    static ConcurrentHashMap<En_CaseType, AtomicLong> caseNumberRepo = new ConcurrentHashMap<>();

    static {
        for (En_CaseType type : En_CaseType.values()) {
            caseNumberRepo.put( type, new AtomicLong( 0L ) );
        }
    }

    private AtomicInteger uniqueIndex = new AtomicInteger( 0 );

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
    protected ContactItemDAO contactItemDAO;
    @Autowired
    protected DevUnitDAO devUnitDAO;
    @Autowired
    protected CaseObjectDAO caseObjectDAO;
    @Autowired
    protected ProjectDAO projectDAO;
    @Autowired
    protected CaseLinkDAO caseLinkDAO;
    @Autowired
    protected PlatformDAO platformDAO;
    @Autowired
    protected ProjectToProductDAO projectToProductDAO;
    @Autowired
    protected CaseObjectMetaDAO caseObjectMetaDAO;
    @Autowired
    protected CaseObjectMetaNotifiersDAO caseObjectMetaNotifiersDAO;
    @Autowired
    protected CaseCommentDAO caseCommentDAO;
    @Autowired
    protected UserLoginDAO userLoginDAO;
    @Autowired
    protected JdbcManyRelationsHelper jdbcManyRelationsHelper;
    @Autowired
    protected UserRoleDAO userRoleDAO;
    @Autowired
    protected ProjectService projectService;
    @Autowired
    protected PersonAbsenceDAO personAbsenceDAO;
    @Autowired
    protected PortalConfig config;
    @Autowired
    protected ObjectMapper objectMapper;
}
