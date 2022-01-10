package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.AssembledEmployeeRegistrationEvent;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CaseLinkQuery;
import ru.protei.portal.core.model.query.EmployeeRegistrationQuery;
import ru.protei.portal.core.model.struct.ContactItem;
import ru.protei.portal.core.model.struct.NotificationEntry;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.helper.StringUtils.isBlank;
import static ru.protei.portal.core.model.helper.StringUtils.join;

public class EmployeeRegistrationServiceImpl implements EmployeeRegistrationService {

    private static Logger log = LoggerFactory.getLogger(EmployeeRegistrationServiceImpl.class);

    @Autowired
    EmployeeRegistrationDAO employeeRegistrationDAO;
    @Autowired
    CaseObjectDAO caseObjectDAO;
    @Autowired
    CaseTypeDAO caseTypeDAO;
    @Autowired
    PersonDAO personDAO;
    @Autowired
    PersonShortViewDAO personShortDAO;
    @Autowired
    CompanyDAO companyDAO;
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
    @Autowired
    EmployeeRegistrationReminderService employeeRegistrationReminderService;

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
        if(!isEmpty(employeeRegistration.getCuratorsIds())){
            employeeRegistration.setCurators ( personShortDAO.getListByKeys( employeeRegistration.getCuratorsIds() ));
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
        setProbationPeriodEndDate(employeeRegistration);
        Long employeeRegistrationId = employeeRegistrationDAO.persist(employeeRegistration);

        if (employeeRegistrationId == null)
            return error(En_ResultStatus.INTERNAL_ERROR);

        // Заполнить связанные поля
        employeeRegistration = employeeRegistrationDAO.get( employeeRegistrationId );

        employeeRegistration.setCurators(personShortDAO.getListByKeys(employeeRegistration.getCuratorsIds()));

        List <NotificationEntry> registrationSubscribers = getEmployeeRegistrationSubscribersEmails(employeeRegistration.getHeadOfDepartment());

        final boolean YOUTRACK_INTEGRATION_ENABLED = portalConfig.data().integrationConfig().isYoutrackEnabled();

        if (YOUTRACK_INTEGRATION_ENABLED) {
            String youTrackIssueId = createAdminYoutrackIssueIfNeeded( employeeRegistration );
            createPhoneYoutrackIssueIfNeeded(employeeRegistration, youTrackIssueId);
            createEquipmentYoutrackIssueIfNeeded(employeeRegistration);
        }

        return ok(id).publishEvent(new AssembledEmployeeRegistrationEvent(this, null, employeeRegistration, registrationSubscribers));
    }

    @Override
    @Transactional
    public Result<Long> updateEmployeeRegistration(AuthToken token, EmployeeRegistrationShortView employeeRegistrationShortView) {
        if (employeeRegistrationShortView == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        EmployeeRegistration oldEmployeeRegistration = employeeRegistrationDAO.get(employeeRegistrationShortView.getId());
        oldEmployeeRegistration.setCurators(personShortDAO.getListByKeys(oldEmployeeRegistration.getCuratorsIds()));

        if (!isEmployeeRegistrationChanged(oldEmployeeRegistration, employeeRegistrationShortView)) {
            return ok(oldEmployeeRegistration.getId());
        }

        EmployeeRegistration newEmployeeRegistration = employeeRegistrationDAO.get(employeeRegistrationShortView.getId());
        newEmployeeRegistration.setCuratorsIds(employeeRegistrationShortView.getCuratorIds());
        newEmployeeRegistration.setEmploymentDate(employeeRegistrationShortView.getEmploymentDate());
        setProbationPeriodEndDate(newEmployeeRegistration);

        if (!employeeRegistrationDAO.partialMerge(newEmployeeRegistration, "employment_date", "curators", "probation_period_end_date")) {
            return error(En_ResultStatus.NOT_UPDATED);
        }

        newEmployeeRegistration.setCurators(personShortDAO.partialGetListByKeys(newEmployeeRegistration.getCuratorsIds(), "id", "displayname"));

        boolean isEmploymentDateChanged = !Objects.equals(oldEmployeeRegistration.getEmploymentDate().getTime(), newEmployeeRegistration.getEmploymentDate().getTime());

        final boolean YOUTRACK_INTEGRATION_ENABLED = portalConfig.data().integrationConfig().isYoutrackEnabled();

        if (YOUTRACK_INTEGRATION_ENABLED && isEmploymentDateChanged) {
            updateYouTrackEmploymentDate(oldEmployeeRegistration.getId(), oldEmployeeRegistration.getEmploymentDate());
        }

        return ok(oldEmployeeRegistration.getId()).publishEvent(new AssembledEmployeeRegistrationEvent(this, oldEmployeeRegistration, newEmployeeRegistration));
    }

    @Override
    @Transactional
    public Result<EmployeeRegistration> completeProbationPeriod(AuthToken token, Long employeeRegistrationId) {
        if (employeeRegistrationId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        EmployeeRegistration employeeRegistration = employeeRegistrationDAO.get(employeeRegistrationId);
        employeeRegistration.setProbationPeriodEndDate(new Date());

        if (!employeeRegistrationDAO.partialMerge(employeeRegistration, "probation_period_end_date")) {
            return error(En_ResultStatus.NOT_UPDATED);
        }

        employeeRegistrationReminderService.notifyAboutEmployeeProbationPeriod(employeeRegistration);
        employeeRegistrationReminderService.notifyEmployeeAboutDevelopmentAgenda(employeeRegistration);

        return ok(employeeRegistration);
    }

    private void updateYouTrackEmploymentDate(Long employeeRegistrationId, Date employmentDate) {
        CaseLinkQuery query = new CaseLinkQuery();
        query.setCaseId(employeeRegistrationId);

        List<CaseLink> listByQuery = caseLinkDAO.getListByQuery(query);

        for (CaseLink nextLink : CollectionUtils.emptyIfNull(listByQuery)) {
            youtrackService.addIssueSystemComment(
                    nextLink.getRemoteId(),
                    "Дата приема на работу была изменена: " + new SimpleDateFormat("dd.MM.yyyy").format(employmentDate)
            );
        }
    }

    private boolean isEmployeeRegistrationChanged(EmployeeRegistration employeeRegistration, EmployeeRegistrationShortView employeeRegistrationShortView) {
        if (!Objects.equals(employeeRegistration.getEmploymentDate(), employeeRegistrationShortView.getEmploymentDate())) {
            return true;
        }

        if (!Objects.equals(employeeRegistration.getCuratorsIds().size(), employeeRegistrationShortView.getCuratorIds().size())) {
            return true;
        }

        if (!employeeRegistration.getCuratorsIds().containsAll(employeeRegistrationShortView.getCuratorIds())) {
            return true;
        }

        return false;
    }

    private CaseObject createCaseObjectFromEmployeeRegistration(EmployeeRegistration employeeRegistration) {
        CaseObject caseObject = new CaseObject();
        caseObject.setType(En_CaseType.EMPLOYEE_REGISTRATION);
        caseObject.setStateId(CrmConstants.State.CREATED);
        caseObject.setCaseNumber(caseTypeDAO.generateNextId(En_CaseType.EMPLOYEE_REGISTRATION));
        caseObject.setCreated(new Date());

        caseObject.setInfo(employeeRegistration.getComment());
        caseObject.setInitiatorId(employeeRegistration.getHeadOfDepartmentId());
        caseObject.setCreatorId(employeeRegistration.getCreatorId());
        caseObject.setName(employeeRegistration.getEmployeeFullName());
        return caseObject;
    }

    private String createAdminYoutrackIssueIfNeeded(EmployeeRegistration employeeRegistration) {
        Set<En_InternalResource> resourceList = employeeRegistration.getResourceList();
        if (isEmpty(resourceList)) {
            return null;
        }
        boolean needPC = contains(employeeRegistration.getEquipmentList(), En_EmployeeEquipment.COMPUTER);
        boolean needMonitor = contains(employeeRegistration.getEquipmentList(), En_EmployeeEquipment.MONITOR);

        String summary = "Регистрация нового сотрудника " + employeeRegistration.getEmployeeFullName();

        String description = join( makeCommonDescriptionString( employeeRegistration ),
                needPC ? "\n Требуется установить новый ПК." : null,
                needMonitor ? "\n Требуется установить новый Монитор." : null,
                "\n Предоставить доступ к ресурсам: ", join( resourceList, r -> getResourceName( r ), ", " ),
                isBlank( employeeRegistration.getResourceComment() ) ? null :
                        "\n   Дополнительно: " + employeeRegistration.getResourceComment(),
                makeWorkplaceConfigurationString( employeeRegistration.getOperatingSystem(), employeeRegistration.getAdditionalSoft() ),
                employeeRegistration.getEmploymentDate() == null ? null :
                        "\n Дата приёма на работу: " +  new SimpleDateFormat("dd.MM.yyyy").format(employeeRegistration.getEmploymentDate()),
                isBlank( employeeRegistration.getComment() ) ? null :
                        "\n Дополнительный комментарий: " + employeeRegistration.getComment()
        ).toString();

        final String USER_SUPPORT_PROJECT_NAME = portalConfig.data().youtrack().getSupportProject();
        if (StringUtils.isEmpty(USER_SUPPORT_PROJECT_NAME)){
            log.error("createAdminYoutrackIssueIfNeeded(): no Youtrack support project specified, YT issue will not be created!");
        }

        return youtrackService.createIssue( USER_SUPPORT_PROJECT_NAME, summary, description ).ifOk( issueId ->
                saveCaseLink( employeeRegistration.getId(), issueId )
        ).getData();
    }

    private void createPhoneYoutrackIssueIfNeeded( EmployeeRegistration employeeRegistration, String youTrackIssueId ) {
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

        String employmentDate = null;
        if( employeeRegistration.getEmploymentDate()!=null){
            employmentDate = "Дата приёма на работу: " +  new SimpleDateFormat("dd.MM.yyyy").format(
                    employeeRegistration.getEmploymentDate() );
        }

        String youtrackIssue = null;
        if(!isBlank( youTrackIssueId )){
            youtrackIssue = "Регистрация нового сотрудника: " + youTrackIssueId;
        }

        String description = join(
                youtrackIssue,
                "\n", makeCommonDescriptionString( employeeRegistration ),
                "\n", employmentDate,
                needPhone,
                needConfigure,
                needCommunication
        ).toString();

        String summary = "Настройка офисной телефонии для сотрудника " + employeeRegistration.getEmployeeFullName();

        final String PHONE_PROJECT_NAME = portalConfig.data().youtrack().getPhoneProject();

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

        List<En_EmployeeEquipment> equipmentsListFurniture = filterToList( employeeRegistration.getEquipmentList(),
                eq -> En_EmployeeEquipment.CHAIR == eq || En_EmployeeEquipment.TABLE == eq );

        if (isEmpty(equipmentsListFurniture)) {
            return;
        }
        String summary = "Оборудование для нового сотрудника " + employeeRegistration.getEmployeeFullName();

        String description = join( makeCommonDescriptionString( employeeRegistration ),
                "\n", employeeRegistration.getEmploymentDate() == null ? "" :
                        "Дата приёма на работу: " +  new SimpleDateFormat("dd.MM.yyyy").format(employeeRegistration.getEmploymentDate()),
                "\n", "Необходимо: ", join( equipmentsListFurniture, e -> getEquipmentName( e ), ", " )
        ).toString();

        final String EQUIPMENT_PROJECT_NAME = portalConfig.data().youtrack().getEquipmentProject();

        youtrackService.createIssue( EQUIPMENT_PROJECT_NAME, summary, description ).ifOk( issueId ->
                saveCaseLink( employeeRegistration.getId(), issueId )
        );
    }

    private CharSequence makeCommonDescriptionString( EmployeeRegistration er ) {
        return join( "Анкета: ", makeYtLinkToCrmRegistration( er.getId(), er.getEmployeeFullName() ),
                "\n", "Компания: ", er.getCompanyName(),
                "\n", "Отдел: ", er.getDepartment(),
                "\n", "Руководитель: ", er.getHeadOfDepartmentShortName(),
                "\n", "Кураторы: ", join( er.getCurators(), PersonShortView::getDisplayShortName, "," ),
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
        final String PORTAL_URL = portalConfig.data().getCommonConfig().getCrmUrlInternal();
        return join( "[", employeeFullName, "](", PORTAL_URL, "#employee_registration_preview:id=" + employeeRegistrationId, ")");
    }

    private void saveCaseLink(Long employeeRegistrationId, String issueId) {
        CaseLink caseLink = new CaseLink();
        caseLink.setCaseId(employeeRegistrationId);
        caseLink.setType(En_CaseLink.YT);
        caseLink.setBundleType(En_BundleType.LINKED_WITH);
        caseLink.setRemoteId(issueId);
        caseLink.setWithCrosslink(false);
        caseLinkDAO.persist(caseLink);
    }

    private void setProbationPeriodEndDate(EmployeeRegistration employeeRegistration) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(employeeRegistration.getEmploymentDate());
        calendar.add(Calendar.MONTH, employeeRegistration.getProbationPeriodMonth());
        Date probationPeriodEndDate = calendar.getTime();
        employeeRegistration.setProbationPeriodEndDate(probationPeriodEndDate);
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
            case VPN:
                return "OpenVPN";
        }
        return "";
    }

    private List<NotificationEntry> getEmployeeRegistrationSubscribersEmails (PersonShortView headOfDepartment) {
        Person head = personDAO.get(headOfDepartment.getId());
        if (head == null) return new ArrayList<>();
        Company company = companyDAO.get(head.getCompanyId());
        if (company == null) return new ArrayList<>();
        jdbcManyRelationsHelper.fill(company, Company.Fields.CONTACT_ITEMS);

        return stream(company.getContactInfo().getItems(En_ContactEmailSubscriptionType.SUBSCRIPTION_TO_EMPLOYEE_REGISTRATION))
                .map(ContactItem::value)
                .filter(Strings::isNotEmpty)
                .map(email -> NotificationEntry.email(email, head.getLocale()))
                .collect(Collectors.toList());
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
