package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.EmployeeRegistrationEvent;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.EmployeeRegistration;
import ru.protei.portal.core.model.query.EmployeeRegistrationQuery;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import javax.annotation.PostConstruct;
import java.util.*;

import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.helper.StringUtils.isBlank;
import static ru.protei.portal.core.model.helper.StringUtils.join;
import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

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
    public Result<SearchResult<EmployeeRegistration>> getEmployeeRegistrations( AuthToken token, EmployeeRegistrationQuery query) {
        SearchResult<EmployeeRegistration> sr = employeeRegistrationDAO.getSearchResult(query);
        return ok(sr);
    }

    @Override
    public Result<EmployeeRegistration> getEmployeeRegistration( AuthToken token, Long id) {
        EmployeeRegistration employeeRegistration = employeeRegistrationDAO.get(id);
        if (employeeRegistration == null)
            return error(En_ResultStatus.NOT_FOUND);
        jdbcManyRelationsHelper.fillAll(employeeRegistration);
        if(!isEmpty(employeeRegistration.getCuratorsIds())){
            employeeRegistration.setCurators ( personDAO.partialGetListByKeys( employeeRegistration.getCuratorsIds(), "id", "displayShortName" ) );
        }
        return ok(employeeRegistration);
    }

    @Override
    @Transactional
    public Result<Long> createEmployeeRegistration( AuthToken token, EmployeeRegistration employeeRegistration) {
        if (employeeRegistration == null)
            return error(En_ResultStatus.INCORRECT_PARAMS);

        CaseObject caseObject = createCaseObjectFromEmployeeRegistration(employeeRegistration);
        Long id = caseObjectDAO.persist(caseObject);
        if (id == null)
            return error(En_ResultStatus.NOT_CREATED);

        employeeRegistration.setId(id);
        Long employeeRegistrationId = employeeRegistrationDAO.persist(employeeRegistration);

        if (employeeRegistrationId == null)
            return error(En_ResultStatus.INTERNAL_ERROR);

        // Заполнить связанные поля
        employeeRegistration = employeeRegistrationDAO.get( employeeRegistrationId );

        if(employeeRegistration == null)
            return error(En_ResultStatus.INTERNAL_ERROR);

        publisherService.publishEvent(new EmployeeRegistrationEvent(this, employeeRegistration));

        if (YOUTRACK_INTEGRATION_ENABLED) {
            createPhoneYoutrackIssueIfNeeded(employeeRegistration);
            createAdminYoutrackIssueIfNeeded(employeeRegistration);
            createEquipmentYoutrackIssueIfNeeded(employeeRegistration);
        }

        return ok(id);
    }

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
        boolean needPC = contains(employeeRegistration.getEquipmentList(), En_EmployeeEquipment.COMPUTER);

        String summary = "Регистрация нового сотрудника " + employeeRegistration.getEmployeeFullName();

        String description = join( makeCommonDescriptionString( employeeRegistration ),
                needPC ? "\n Требуется установить новый ПК." : "",
                "\n", "Предоставить доступ к ресурсам: ", join( resourceList, r -> getResourceName( r ), ", " ),
                (isBlank( employeeRegistration.getResourceComment() ) ? "" : "\n   Дополнительно: " + employeeRegistration.getResourceComment()),
                makeWorkplaceConfigurationString( employeeRegistration.getOperatingSystem(), employeeRegistration.getAdditionalSoft() )
        ).toString();

        youtrackService.createIssue( ADMIN_PROJECT_NAME, summary, description ).ifOk( issueId ->
                saveCaseLink( employeeRegistration.getId(), issueId )
        );
    }

    private void createPhoneYoutrackIssueIfNeeded( EmployeeRegistration employeeRegistration) {
        Set<En_PhoneOfficeType> resourceList = employeeRegistration.getPhoneOfficeTypeList();
        if (isEmpty(resourceList)) {
            return;
        }

        String needPhone = contains(employeeRegistration.getEquipmentList(), En_EmployeeEquipment.TELEPHONE) ?
                "\n Требуется установить новый телефон." : "";
        String needConfigure = "\n Необходима " + (contains(employeeRegistration.getEquipmentList(), En_EmployeeEquipment.TELEPHONE) ?
                "настройка" : "перенастройка"
        ) + " офисной телефонии";
        String needCommunication =
                contains(resourceList, En_PhoneOfficeType.INTERNATIONAL) ||
                contains(resourceList, En_PhoneOfficeType.LONG_DISTANCE) ?
                join("\n", "Необходимо включить связь: ",
                        join(removeItem(resourceList, En_PhoneOfficeType.OFFICE), r -> getPhoneOfficeTypeName(r), ", ")
                ).toString() : "";

        String description = join( makeCommonDescriptionString( employeeRegistration ),
                needPhone,
                needConfigure,
                needCommunication
        ).toString();

        String summary = "Настройка офисной телефонии для сотрудника " + employeeRegistration.getEmployeeFullName();

        youtrackService.createIssue( PHONE_PROJECT_NAME, summary, description ).ifOk( issueId ->
                saveCaseLink( employeeRegistration.getId(), issueId )
        );
    }

    private Collection<En_PhoneOfficeType> removeItem(Collection<En_PhoneOfficeType> resourceList, En_PhoneOfficeType removedItem) {
        List<En_PhoneOfficeType> result = new ArrayList<>(resourceList);
        result.remove(removedItem);
        return result;
    }

    private void createEquipmentYoutrackIssueIfNeeded(EmployeeRegistration employeeRegistration) {
        if (isEmpty(employeeRegistration.getEquipmentList())) {
            return;
        }
        Set<En_EmployeeEquipment> equipmentsListFurniture = getEquipmentsListFurniture(employeeRegistration.getEquipmentList());
        if (isEmpty(equipmentsListFurniture)) {
            return;
        }
        String summary = "Оборудование для нового сотрудника " + employeeRegistration.getEmployeeFullName();

        String description = join( makeCommonDescriptionString( employeeRegistration ),
                "\n", "Необходимо: ", join( equipmentsListFurniture, e -> getEquipmentName( e ), ", " )
        ).toString();

        youtrackService.createIssue( EQUIPMENT_PROJECT_NAME, summary, description ).ifOk( issueId ->
                saveCaseLink( employeeRegistration.getId(), issueId )
        );
    }

    private Set<En_EmployeeEquipment> getEquipmentsListFurniture(Set<En_EmployeeEquipment> employeeRegistration) {
        Set<En_EmployeeEquipment> equipmentsListFurniture = new HashSet<>(employeeRegistration);
        equipmentsListFurniture.remove(En_EmployeeEquipment.TELEPHONE);
        equipmentsListFurniture.remove(En_EmployeeEquipment.COMPUTER);
        return equipmentsListFurniture;
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
            case OFFICE:
                return "офисную";

        }
        return "";
    }

}
