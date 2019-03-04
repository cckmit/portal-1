package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.*;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.EmployeeRegistrationQuery;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import javax.annotation.PostConstruct;
import java.util.*;

import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.helper.StringUtils.*;

public class EmployeeRegistrationServiceImpl implements EmployeeRegistrationService {

    private String EQUIPMENT_PROJECT_NAME, ADMIN_PROJECT_NAME, PHONE_PROJECT_NAME,PORTAL_URL;
    private boolean YOUTRACK_INTEGRATION_ENABLED;

    @Autowired
    EmployeeRegistrationDAO employeeRegistrationDAO;
    @Autowired
    CaseObjectDAO caseObjectDAO;
    @Autowired
    CaseTypeDAO caseTypeDAO;
    @Autowired
    PersonDAO personDAO;
    @Autowired
    YoutrackService youtrackService;
    @Autowired
    CaseLinkDAO caseLinkDAO;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;
    @Autowired
    EventPublisherService publisherService;
    @Autowired
    PortalConfig portalConfig;

    @PostConstruct
    public void setYoutrackProjectNames() {
        YOUTRACK_INTEGRATION_ENABLED = portalConfig.data().integrationConfig().isYoutrackEnabled();
        EQUIPMENT_PROJECT_NAME = portalConfig.data().youtrack().getEquipmentProject();
        ADMIN_PROJECT_NAME = portalConfig.data().youtrack().getAdminProject();
        PHONE_PROJECT_NAME = portalConfig.data().youtrack().getPhoneProject();
        PORTAL_URL = portalConfig.data().getCommonConfig().getCrmUrlInternal();
    }

    @Override
    public CoreResponse<Integer> count(AuthToken token, EmployeeRegistrationQuery query) {
        return new CoreResponse<Integer>().success(employeeRegistrationDAO.countByQuery(query));
    }

    @Override
    public CoreResponse<List<EmployeeRegistration>> employeeRegistrationList(AuthToken token, EmployeeRegistrationQuery query) {
        List<EmployeeRegistration> list = employeeRegistrationDAO.getListByQuery(query);
        if (list == null) {
            return new CoreResponse<List<EmployeeRegistration>>().error(En_ResultStatus.INTERNAL_ERROR);
        }
        return new CoreResponse<List<EmployeeRegistration>>().success(list);
    }

    @Override
    public CoreResponse<EmployeeRegistration> getEmployeeRegistration(AuthToken token, Long id) {
        EmployeeRegistration employeeRegistration = employeeRegistrationDAO.get(id);
        if (employeeRegistration == null)
            return new CoreResponse<EmployeeRegistration>().error(En_ResultStatus.NOT_FOUND);
        jdbcManyRelationsHelper.fillAll(employeeRegistration);
        if(!isEmpty(employeeRegistration.getCuratorsIds())){
            employeeRegistration.setCurators ( personDAO.partialGetListByKeys( employeeRegistration.getCuratorsIds(), "id", "displayShortName" ) );
        }
        return new CoreResponse<EmployeeRegistration>().success(employeeRegistration);
    }

    @Override
    @Transactional
    public CoreResponse<Long> createEmployeeRegistration(AuthToken token, EmployeeRegistration employeeRegistration) {
        if (employeeRegistration == null)
            return new CoreResponse<Long>().error(En_ResultStatus.INCORRECT_PARAMS);

        CaseObject caseObject = createCaseObjectFromEmployeeRegistration(employeeRegistration);
        Long id = caseObjectDAO.persist(caseObject);
        if (id == null)
            return new CoreResponse<Long>().error(En_ResultStatus.NOT_CREATED);

        employeeRegistration.setId(id);
        Long employeeRegistrationId = employeeRegistrationDAO.persist(employeeRegistration);

        if (employeeRegistrationId == null)
            return new CoreResponse<Long>().error(En_ResultStatus.INTERNAL_ERROR);

        // Заполнить связанные поля
        employeeRegistration = employeeRegistrationDAO.get( employeeRegistrationId );

        if(employeeRegistration == null)
            return new CoreResponse<Long>().error(En_ResultStatus.INTERNAL_ERROR);

        publisherService.publishEvent(new EmployeeRegistrationEvent(this, employeeRegistration));

        if (YOUTRACK_INTEGRATION_ENABLED) {
            createPhoneYoutrackIssueIfNeeded(employeeRegistration);
            createAdminYoutrackIssueIfNeeded(employeeRegistration);
            createEquipmentYoutrackIssueIfNeeded(employeeRegistration);
        }

        return new CoreResponse<Long>().success(id);
    }

    @Override
    public CoreResponse<Boolean> notifyAboutEmployeeFeedback() {
        List<EmployeeRegistration> probationComplete = employeeRegistrationDAO.getAfterProbationList( SEND_EMPLOYEE_FEEDBACK_AFTER_PROBATION_END_DAYS );
        log.info( "notifyAboutEmployeeFeedback(): {}", probationComplete );

        for (EmployeeRegistration employeeRegistration : emptyIfNull(probationComplete)) {
            if(employeeRegistration.getPerson() == null ) continue;
            notifyEmployerAboutFeedback( employeeRegistration.getPerson() );
        }

        return new CoreResponse<Boolean>().success(true);
    }

    @Override
    public CoreResponse<Boolean> notifyAboutDevelopmentAgenda() {
        List<EmployeeRegistration> probationExpires = employeeRegistrationDAO.getProbationExpireList( SEND_AGENDA_TO_PROBATION_END_DAYS );
        log.info( "notifyAboutDevelopmentAgenda(): {}", probationExpires );

        for (EmployeeRegistration employeeRegistration : emptyIfNull(probationExpires)) {
            if(employeeRegistration.getPerson() == null ) continue;
            notifyEmployerAboutAgenda( employeeRegistration.getPerson() );
        }

        return new CoreResponse<Boolean>().success(true);
    }

    @Override
    public CoreResponse<Boolean> notifyAboutProbationPeriod() {
        List<EmployeeRegistration> probationExpires = employeeRegistrationDAO.getProbationExpireList( SEND_PROBATION_EXPIRES_TO_PROBATION_END_DAYS );
        log.info( "notifyAboutProbationPeriod(): {}", probationExpires );

        Map<Long, Person> idToPerson = collectPersonsForNotification( probationExpires );

        for (EmployeeRegistration employeeRegistration : emptyIfNull(probationExpires)) {
            Person headOfDepartment = idToPerson.get( employeeRegistration.getHeadOfDepartmentId() );
            String employeeFullName = employeeRegistration.getEmployeeFullName();
            Long employeeId = employeeRegistration.getId();

            notifyHeadOfDepartment( headOfDepartment, employeeFullName, employeeId );

            for (Long curatorId : emptyIfNull( employeeRegistration.getCuratorsIds())) {
                Person curator = idToPerson.get( curatorId );
                notifyEmployeeCurator( curator, employeeFullName, employeeId );
            }
        }

        return new CoreResponse<Boolean>().success(true);
    }

    private void notifyEmployerAboutFeedback( Person employee ) {
        publisherService.publishEvent( new EmployeeRegistrationEmployeeFeedbackEvent( this,
                employee ) );
    }

    private void notifyEmployerAboutAgenda( Person employee ) {
        publisherService.publishEvent( new EmployeeRegistrationDevelopmentAgendaEvent( this,
                employee ) );
    }

    private void notifyEmployeeCurator( Person curator, String employeeFullName, Long employeeId ) {
        publisherService.publishEvent( new EmployeeRegistrationProbationCuratorsEvent( this,
                curator, employeeFullName, employeeId  ) );
    }

    private void notifyHeadOfDepartment( Person headOfDepartment, String employeeFullName, Long employeeId ) {
        publisherService.publishEvent( new EmployeeRegistrationProbationHeadOfDepartmentEvent( this,
                headOfDepartment, employeeFullName, employeeId ) );
    }

    private Map<Long, Person> collectPersonsForNotification( List<EmployeeRegistration> probationExpires ) {
        Set<Long> notifyIds = new HashSet<Long>();

        for (EmployeeRegistration er : emptyIfNull( probationExpires )) {
            notifyIds.add( er.getHeadOfDepartmentId() );
            notifyIds.addAll( emptyIfNull( er.getCuratorsIds() ) );
        }

        List<Person> persons = personDAO.partialGetListByKeys( notifyIds, "id", "displayname", "contactInfo" );
        return toMap( persons, Person::getId, person -> person );
    }

    private static final Logger log = LoggerFactory.getLogger( EmployeeRegistrationServiceImpl.class );

    private CaseObject createCaseObjectFromEmployeeRegistration(EmployeeRegistration employeeRegistration) {
        CaseObject caseObject = new CaseObject();
        caseObject.setCaseType(En_CaseType.EMPLOYEE_REGISTRATION);
        caseObject.setState(En_CaseState.CREATED);
        caseObject.setCaseNumber(caseTypeDAO.generateNextId(En_CaseType.EMPLOYEE_REGISTRATION));
        caseObject.setCreated(new Date());

        caseObject.setInfo(employeeRegistration.getComment());
        caseObject.setInitiatorId(employeeRegistration.getHeadOfDepartmentId());
        caseObject.setCreatorId(employeeRegistration.getCreatorId());
        caseObject.setName(employeeRegistration.getEmployeeFullName());
        return caseObject;
    }

    private void createAdminYoutrackIssueIfNeeded(EmployeeRegistration employeeRegistration) {
        Set<En_InternalResource> resourceList = employeeRegistration.getResourceList();
        if (isEmpty(resourceList)) {
            return;
        }
        String summary = "Регистрация нового сотрудника " + employeeRegistration.getEmployeeFullName();

        String description = join( makeCommonDescriptionString( employeeRegistration ),
                "\n", "Предоставить доступ к ресурсам: ", join( resourceList, r -> getResourceName( r ), ", " ),
                (isBlank( employeeRegistration.getResourceComment() ) ? "" : "\n   Дополнительно: " + employeeRegistration.getResourceComment()),
                makeWorkplaceConfigurationString( employeeRegistration.getOperatingSystem(), employeeRegistration.getAdditionalSoft() )
        ).toString();

        String issueId = youtrackService.createIssue(ADMIN_PROJECT_NAME, summary, description);
        saveCaseLink(employeeRegistration.getId(), issueId);
    }

    private void createPhoneYoutrackIssueIfNeeded( EmployeeRegistration employeeRegistration) {
        Set<En_PhoneOfficeType> resourceList = employeeRegistration.getPhoneOfficeTypeList();
        if (isEmpty(resourceList)) {
            return;
        }

        String summary = "Настройка офисной телефонии для сотрудника " + employeeRegistration.getEmployeeFullName();

        String configure = contains( employeeRegistration.getEquipmentList(), En_EmployeeEquipment.TELEPHONE )
                ? "перенастройка": "настройка";

        String description = join( makeCommonDescriptionString( employeeRegistration ),
                "\n", "Необходима ", configure, " офисной телефонии",
                "\n", "Необходимо включить связь: ", join( resourceList, r -> getPhoneOfficeTypeName( r ), ", " )
        ).toString();

        String issueId = youtrackService.createIssue( PHONE_PROJECT_NAME, summary, description);
        saveCaseLink(employeeRegistration.getId(), issueId);
    }

    private void createEquipmentYoutrackIssueIfNeeded(EmployeeRegistration employeeRegistration) {
        Set<En_EmployeeEquipment> equipmentList = employeeRegistration.getEquipmentList();

        String summary = "Оборудование для нового сотрудника " + employeeRegistration.getEmployeeFullName();

        String description = join( makeCommonDescriptionString( employeeRegistration ),
                "\n", "Необходимо: ", join( equipmentList, e -> getEquipmentName( e ), ", " )
        ).toString();

        String issueId = youtrackService.createIssue(EQUIPMENT_PROJECT_NAME, summary, description);
        saveCaseLink(employeeRegistration.getId(), issueId);
    }

    private CharSequence makeCommonDescriptionString( EmployeeRegistration er ) {
        return join( "Анкета: ", makeYtLinkToCrmRegistration( er.getId(), er.getEmployeeFullName() ),
                "\n", "Руководитель: ", er.getHeadOfDepartmentShortName(),
                "\n", "Расположение рабочего места: ", er.getWorkplace()
        );
    }

    private CharSequence makeWorkplaceConfigurationString( String operatingSystem, String additionalSoft ) {
        if (isBlank( operatingSystem ) && isBlank( additionalSoft )) {
            return "";
        }

        StringBuilder sb = new StringBuilder( "\nНастройка рабочего места: " );
        if (!isBlank( operatingSystem )) {
            sb.append( "\n   Тип ОС: " ).append( operatingSystem );
        }

        if (!isBlank( additionalSoft )) {
            sb.append( "\n   Дополнительное ПО: " ).append( additionalSoft );
        }
        return sb;
    }

    private CharSequence makeYtLinkToCrmRegistration( Long employeeRegistrationId, String employeeFullName ) {
        return join( "[", PORTAL_URL, "#employee_registration_preview:id=" + employeeRegistrationId, " ", employeeFullName, "]" );
    }

    private void saveCaseLink(Long employeeRegistrationId, String issueId) {
        CaseLink caseLink = new CaseLink();
        caseLink.setCaseId(employeeRegistrationId);
        caseLink.setType(En_CaseLink.YT);
        caseLink.setRemoteId(issueId);
        caseLinkDAO.persist(caseLink);
    }

    private static String getResourceName(En_InternalResource internalResource) {
        if (internalResource == null)
            return "";
        switch (internalResource) {
            case YOUTRACK:
                return "YouTrack";
            case CVS:
                return "CVS";
            case SVN:
                return "SVN";
            case MERCURIAL:
                return "Mercurial";
            case GIT:
                return "Git";
            case CRM:
                return "CRM";
            case STORE_DELIVERY:
                return "Store Delivery";
            case EMAIL:
                return "почта";
        }
        return "";
    }

    private static String getEquipmentName(En_EmployeeEquipment employeeEquipment) {
        if (employeeEquipment == null)
            return "";
        switch (employeeEquipment) {
            case TABLE:
                return "стол";
            case CHAIR:
                return "стул";
            case COMPUTER:
                return "компьютер";
            case MONITOR:
                return "монитор";
            case TELEPHONE:
                return "телефон";
        }
        return "";
    }

    private static String getPhoneOfficeTypeName(En_PhoneOfficeType phoneOfficeType) {
        if (phoneOfficeType == null)
            return "";
        switch (phoneOfficeType) {
            case INTERNATIONAL:
                return "международную";
            case LONG_DISTANCE:
                return "междугороднюю";

        }
        return "";
    }

    public static final int SEND_EMPLOYEE_FEEDBACK_AFTER_PROBATION_END_DAYS = 1;
    public static final int SEND_AGENDA_TO_PROBATION_END_DAYS = 7;
    public static final int SEND_PROBATION_EXPIRES_TO_PROBATION_END_DAYS = 9;
}
