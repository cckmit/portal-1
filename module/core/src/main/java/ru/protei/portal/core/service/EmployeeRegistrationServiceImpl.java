package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.EmployeeRegistrationEvent;
import ru.protei.portal.core.model.dao.CaseLinkDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dao.CaseTypeDAO;
import ru.protei.portal.core.model.dao.EmployeeRegistrationDAO;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseLink;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.EmployeeRegistration;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.EmployeeRegistrationQuery;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class EmployeeRegistrationServiceImpl implements EmployeeRegistrationService {

    private String EQUIPMENT_PROJECT_NAME, ADMIN_PROJECT_NAME,ACRM_PROJECT_NAME,PORTAL_URL;
    private boolean YOUTRACK_INTEGRATION_ENABLED;

    @Autowired
    EmployeeRegistrationDAO employeeRegistrationDAO;
    @Autowired
    CaseObjectDAO caseObjectDAO;
    @Autowired
    CaseTypeDAO caseTypeDAO;
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
        ACRM_PROJECT_NAME = portalConfig.data().youtrack().getAcrmProject();
        PORTAL_URL = portalConfig.data().getMailNotificationConfig().getCrmUrlInternal();
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

        if(!sendNotifyEvent(employeeRegistrationId))
            return new CoreResponse<Long>().error(En_ResultStatus.INTERNAL_ERROR);

        // Заполнить связанные поля
        employeeRegistration = employeeRegistrationDAO.get( employeeRegistrationId );

        if (YOUTRACK_INTEGRATION_ENABLED) {
            createAcrmYoutrackIssueIfNeeded(employeeRegistration);
            createAdminYoutrackIssueIfNeeded(employeeRegistration);
            createEquipmentYoutrackIssueIfNeeded(employeeRegistration);
        }

        return new CoreResponse<Long>().success(id);
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

    private boolean sendNotifyEvent(Long employeeRegistrationId) {
        EmployeeRegistration employeeRegistration = employeeRegistrationDAO.get(employeeRegistrationId);
        if (employeeRegistration == null)
            return false;

        publisherService.publishEvent(new EmployeeRegistrationEvent(this, employeeRegistration));
        return true;
    }
    
    
    private void createAdminYoutrackIssueIfNeeded(EmployeeRegistration employeeRegistration) {
        Set<En_InternalResource> resourceList = employeeRegistration.getResourceList();
        if (CollectionUtils.isEmpty(resourceList)) {
            return;
        }
        String summary = "Открытие доступа к внутренним ресурсам для нового сотрудника " + employeeRegistration.getEmployeeFullName();
        String description = "Необходимо открыть доступ к: " +
                StringUtils.join(resourceList, r -> getResourceName(r),  ", ");
        String issueId = youtrackService.createIssue(ADMIN_PROJECT_NAME, summary, description);
        saveCaseLink(employeeRegistration.getId(), issueId);
    }

    private void createAcrmYoutrackIssueIfNeeded(EmployeeRegistration employeeRegistration) {
        Set<En_PhoneOfficeType> resourceList = employeeRegistration.getPhoneOfficeTypeList();
        if (CollectionUtils.isEmpty(resourceList)) {
            return;
        }

        String summary = "Настройка офисной телефонии для сотрудника " + employeeRegistration.getEmployeeFullName();
        String description = "Необходимо включить связь: " +
                StringUtils.join(resourceList, r -> getPhoneOfficeTypeName(r),  ", ");

        description = StringUtils.join( description,
                "\n", "Руководитель: ", employeeRegistration.getHeadOfDepartmentShortName(),
                "\n", "Расположение рабочего места: ", employeeRegistration.getWorkplace(),
                "\n", "Анкета: ", PORTAL_URL, HASH_SYMBOL, "employee_registration_preview:id="+employeeRegistration.getId()//TODO ссылка на анкету
        ).toString();

        String issueId = youtrackService.createIssue(ACRM_PROJECT_NAME, summary, description);
        saveCaseLink(employeeRegistration.getId(), issueId);
    }

    private void createEquipmentYoutrackIssueIfNeeded(EmployeeRegistration employeeRegistration) {
        Set<En_EmployeeEquipment> equipmentList = employeeRegistration.getEquipmentList();
        if (CollectionUtils.isEmpty(equipmentList)) {
            return;
        }
        String summary = "Оборудование для нового сотрудника " + employeeRegistration.getEmployeeFullName();
        String description = "Необходимо: " +
                StringUtils.join(equipmentList, e -> getEquipmentName(e), ", ");
        String issueId = youtrackService.createIssue(EQUIPMENT_PROJECT_NAME, summary, description);
        saveCaseLink(employeeRegistration.getId(), issueId);
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

    private static final String HASH_SYMBOL = "#";
//    private static final String HASH_SYMBOL = "%23";
}
