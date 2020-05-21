package ru.protei.portal.core.service.template;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.slf4j.Logger;
import ru.protei.portal.core.event.*;
import ru.protei.portal.core.model.struct.Project;
import ru.protei.portal.core.model.util.CaseTextMarkupUtil;
import ru.protei.portal.core.model.util.DiffCollectionResult;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.core.renderer.HTMLRenderer;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.dict.En_TextMarkup;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.HTMLHelper;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.utils.LinkData;
import ru.protei.portal.core.utils.EnumLangUtil;
import ru.protei.portal.core.utils.WorkTimeFormatter;
import ru.protei.portal.core.model.util.TransliterationUtils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;

/**
 * Реализация сервиса управления проектами
 */
public class TemplateServiceImpl implements TemplateService {
    public static final String BASE_TEMPLATE_PATH = "notification/email/";
    private static Logger log = getLogger(TemplateServiceImpl.class);

    Configuration templateConfiguration;

    @Inject
    HTMLRenderer htmlRenderer;

    @PostConstruct
    public void onInit() {
        try {
            templateConfiguration = new Configuration( Configuration.VERSION_2_3_23 );
            templateConfiguration.setClassForTemplateLoading( TemplateServiceImpl.class, "/" );
            templateConfiguration.setDefaultEncoding( "UTF-8" );
            templateConfiguration.setTemplateExceptionHandler( TemplateExceptionHandler.HTML_DEBUG_HANDLER );
        } catch ( Exception e ) {
            log.error( "Freemarker Configuration init failure", e );
            e.printStackTrace();
        }
    }

    @Override
    public PreparedTemplate getCrmEmailNotificationBody(
            AssembledCaseEvent event, List<CaseComment> caseComments, DiffCollectionResult<LinkData> mergeLinks, String urlTemplate, Collection<String> recipients
    ) {
        CaseObject newState = event.getCaseObject();
        En_TextMarkup textMarkup = CaseTextMarkupUtil.recognizeTextMarkup(newState);

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.putAll(makeTemplateModelUtils());
        templateModel.putAll(makeTemplateModelMeta(event));

//        templateModel.put( "case", newState );
//        templateModel.put( "oldCase", oldState );

        templateModel.put( "linkToIssue", String.format( urlTemplate, newState.getCaseNumber() ) );
        templateModel.put( "isCreated", event.isCreateEvent() );
        templateModel.put( "recipients", recipients );

        templateModel.put( "createdByMe", false );
        templateModel.put( "creator", newState.getCreator().getDisplayShortName() );
        templateModel.put( "created", newState.getCreated() );
        templateModel.put( "caseNumber", newState.getCaseNumber() );

        templateModel.put( "nameChanged", event.getName().hasDifferences() );
        templateModel.put( "infoChanged", event.getInfo().hasDifferences() );
        templateModel.put( "caseName", event.getName().getNewState() );
        templateModel.put( "oldCaseName", event.getName().getInitialState());
        templateModel.put( "caseInfo", escapeTextAndRenderHTML( event.getInfo().getNewState(), textMarkup ) );
        templateModel.put( "oldCaseInfo", event.getInfo().getInitialState() == null ? null : escapeTextAndRenderHTML( event.getInfo().getInitialState(), textMarkup ) );

        templateModel.put( "privacy", newState.isPrivateCase() );

        templateModel.put("hasLinks", hasLinks( mergeLinks) );
        templateModel.put("existingLinks", mergeLinks == null ? null : mergeLinks.getSameEntries());
        templateModel.put("addedLinks", mergeLinks == null ? null : mergeLinks.getAddedEntries());
        templateModel.put("removedLinks", mergeLinks == null ? null : mergeLinks.getRemovedEntries());

        templateModel.putAll(
                buildAttachmentModelKeys(
                        event.getExistingAttachments(),
                        event.getAddedAttachments(),
                        event.getRemovedAttachments())
        );

        templateModel.put( "caseComments",  getCommentsModelKeys(caseComments, event.getAddedCaseComments(), event.getChangedComments(), event.getRemovedComments(), textMarkup));

        PreparedTemplate template = new PreparedTemplate( "notification/email/crm.body.%s.ftl" );
        template.setModel( templateModel );
        template.setTemplateConfiguration( templateConfiguration );
        return template;
    }

    private Map<String, Object> makeTemplateModelUtils() {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("TextUtils", new TextUtils());
        templateModel.put("TimeElapsedFormatter", new WorkTimeFormatter());
        templateModel.put("TranslitUtils", new TransliterationUtils());
        return templateModel;
    }

    private Map<String, Object> makeTemplateModelMeta(AssembledCaseEvent event) {
        Map<String, Object> templateModel = new HashMap<>();
        CaseObjectMeta newMetaState = event.getCaseMeta();
        CaseObjectMeta oldMetaState = event.getInitCaseMeta() == null ? null : newMetaState.equals(event.getInitCaseMeta()) ? null : event.getInitCaseMeta();

        templateModel.put("importanceChanged", event.isCaseImportanceChanged());
        templateModel.put("importanceLevel", newMetaState.getImportance() == null ? null : newMetaState.getImportance().getCode());
        templateModel.put("oldImportanceLevel", oldMetaState == null || oldMetaState.getImportance() == null ? null : oldMetaState.getImportance().getCode());

        templateModel.put("caseChanged", event.isCaseStateChanged());
        templateModel.put("caseState", newMetaState.getState() == null ? null : newMetaState.getState().getName());
        templateModel.put("oldCaseState", oldMetaState == null || oldMetaState.getState() == null ? null : oldMetaState.getState().getName());

        templateModel.put("isPausedState", En_CaseState.PAUSED.equals(newMetaState.getState()));
        templateModel.put("pauseDateChanged", event.isPauseDateChanged());
        templateModel.put("pauseDate", newMetaState.getPauseDate() == null ? null : new Date(newMetaState.getPauseDate()));
        templateModel.put("oldPauseDate", (oldMetaState == null || oldMetaState.getPauseDate() == null) ? null : new Date(oldMetaState.getPauseDate()));

        templateModel.put("timeElapsedChanged", event.isTimeElapsedChanged());
        templateModel.put("elapsed", newMetaState.getTimeElapsed());
        templateModel.put("oldElapsed", !event.isTimeElapsedChanged() ? null : (newMetaState.getTimeElapsed() - event.getTimeElapsedChanging()));

        templateModel.put("customerChanged", event.isInitiatorChanged() || event.isInitiatorCompanyChanged());
        templateModel.put("initiator", newMetaState.getInitiator() == null ? null : newMetaState.getInitiator().getDisplayName());
        templateModel.put("initiatorCompany", newMetaState.getInitiatorCompany() == null ? null : newMetaState.getInitiatorCompany().getCname());
        templateModel.put("oldInitiator", oldMetaState == null || oldMetaState.getInitiator() == null ? null : oldMetaState.getInitiator().getDisplayName());
        templateModel.put("oldInitiatorCompany", oldMetaState == null || oldMetaState.getInitiatorCompany() == null ? null : oldMetaState.getInitiatorCompany().getCname());

        templateModel.put("managerChanged", event.isManagerChanged());
        templateModel.put("manager", newMetaState.getManager() == null ? null : newMetaState.getManager().getDisplayName());
        templateModel.put("oldManager", oldMetaState == null || oldMetaState.getManager() == null ? null : oldMetaState.getManager().getDisplayName());

        templateModel.put("managerCompany", newMetaState.getManagerCompanyName());
        templateModel.put("oldManagerCompany", oldMetaState == null ? null : oldMetaState.getManagerCompanyName());

        templateModel.put("platformChanged", event.isPlatformChanged());
        templateModel.put("platform", newMetaState.getPlatformName());
        templateModel.put("oldPlatform", oldMetaState == null ? null : oldMetaState.getPlatformName());

        templateModel.put("productChanged", event.isProductChanged());
        templateModel.put("product", newMetaState.getProduct() == null ? null : newMetaState.getProduct().getName());
        templateModel.put("oldProduct", oldMetaState == null || oldMetaState.getProduct() == null ? null : oldMetaState.getProduct().getName());

        return templateModel;
    }

    @Override
    public PreparedTemplate getCrmEmailNotificationSubject( AssembledCaseEvent event, Person currentPerson ) {
        CaseObject caseObject = event.getCaseObject();
        CaseObjectMeta caseMeta = event.getCaseMeta();
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put( "TranslitUtils", new TransliterationUtils() );
        templateModel.put( "author", currentPerson );
        templateModel.put( "caseNumber", caseObject.getCaseNumber() );
        templateModel.put( "caseState", caseMeta.getState() == null ? null : caseMeta.getState().getName() );
        templateModel.put( "importanceLevel", caseMeta.getImportance() == null ? null : caseMeta.getImportance().getCode() );
        templateModel.put( "productName", caseMeta.getProduct() == null ? null : caseMeta.getProduct().getName() );

        PreparedTemplate template = new PreparedTemplate( "notification/email/crm.subject.%s.ftl" );
        template.setModel( templateModel );
        template.setTemplateConfiguration( templateConfiguration );
        return template;
    }


    @Override
    public String getEmployeeRegistrationEmployeeFeedbackEmailNotificationBody( String employeeName ) throws IOException, TemplateException {
        Map<String, Object> model = new HashMap<>();
        model.put( "userName", employeeName);

        return getText( model, "employee.registration.employee.feedback.body.%s.ftl" );
    }

    @Override
    public String getEmployeeRegistrationEmployeeFeedbackEmailNotificationSubject() throws IOException, TemplateException {
        return getText( new HashMap<>(), "employee.registration.employee.feedback.subject.%s.ftl" );
    }

    @Override
    public  String getEmployeeRegistrationDevelopmentAgendaEmailNotificationBody( String employeeName ) throws IOException, TemplateException {
        Map<String, Object> model = new HashMap<>();
        model.put( "userName", employeeName);

        return getText( model, "employee.registration.development.agenda.body.%s.ftl" );
    }

    @Override
    public String getEmployeeRegistrationDevelopmentAgendaEmailNotificationSubject() throws IOException, TemplateException {
        return getText( new HashMap<>(), "employee.registration.development.agenda.subject.%s.ftl" );
    }

    @Override
    public String getEmployeeRegistrationProbationHeadOfDepartmentEmailNotificationBody( Long employeeRegistrationId, String employeeFullName, String urlTemplate, String recipientName ) throws IOException, TemplateException {
        Map<String, Object> model = new HashMap<>();
        model.put( "employee_registration_name", employeeFullName );
        model.put( "linkToEmployeeRegistration", String.format( urlTemplate, employeeRegistrationId ) );
        model.put( "userName", recipientName);

        return getText(model, "employee.registration.probation.body.%s.ftl");
    }

    @Override
    public String getEmployeeRegistrationProbationHeadOfDepartmentEmailNotificationSubject( String employeeFullName ) throws IOException, TemplateException {
        Map<String, Object> model = new HashMap<>();
        model.put( "employeeFullName", employeeFullName );

        return getText(model, "employee.registration.probation.subject.%s.ftl");
    }

    @Override
    public String getEmployeeRegistrationProbationCuratorsEmailNotificationBody( Long employeeRegistrationId, String employeeFullName, String urlTemplate, String recipientName ) throws IOException, TemplateException {
        Map<String, Object> model = new HashMap<>();
        model.put( "employee_registration_name", employeeFullName );
        model.put( "linkToEmployeeRegistration", String.format( urlTemplate, employeeRegistrationId ) );
        model.put( "userName", recipientName);

        return getText(model, "employee.registration.probation.curators.body.%s.ftl");
    }

    @Override
    public String getEmployeeRegistrationProbationCuratorsEmailNotificationSubject( String employeeFullName ) throws IOException, TemplateException {
        Map<String, Object> model = new HashMap<>();
        model.put( "employeeFullName", employeeFullName );

        return getText(model, "employee.registration.probation.curators.subject.%s.ftl");
    }

    @Override
    public PreparedTemplate getEmployeeRegistrationEmailNotificationBody(AssembledEmployeeRegistrationEvent event, String urlTemplate, Collection<String> recipients) {
        Map<String, Object> templateModel = new HashMap<>();
        EmployeeRegistration newState = event.getNewState();
        EmployeeRegistration oldState = event.getOldState();

        templateModel.put("TransliterationUtils", new TransliterationUtils());

        templateModel.put("linkToEmployeeRegistration", String.format(urlTemplate, newState.getId()));
        templateModel.put("employeeFullName", newState.getEmployeeFullName());
        templateModel.put("headOfDepartmentShortName", newState.getHeadOfDepartmentShortName());
        templateModel.put("employmentType", newState.getEmploymentType().name());
        templateModel.put("withRegistration", newState.isWithRegistration());
        templateModel.put("position", newState.getPosition());
        templateModel.put("state", newState.getState().getName());
        templateModel.put("employmentDateChanged", event.isEmploymentDateChanged());
        templateModel.put("oldEmploymentDate", oldState == null ? null : oldState.getEmploymentDate());
        templateModel.put("newEmploymentDate", newState.getEmploymentDate());
        templateModel.put("created", newState.getCreated());
        templateModel.put("workplace", newState.getWorkplace());
        templateModel.put("equipmentList", newState.getEquipmentList());
        templateModel.put("operatingSystem", newState.getOperatingSystem());
        templateModel.put("additionalSoft", newState.getAdditionalSoft());
        templateModel.put("resourceList", newState.getResourceList());
        templateModel.put("resourceComment", newState.getResourceComment());
        templateModel.put("phoneOfficeTypeList", newState.getPhoneOfficeTypeList());
        templateModel.put("comment", newState.getComment());
        templateModel.put("recipients", recipients);
        templateModel.put("curatorsDiff", event.getCuratorsDiff());

        PreparedTemplate template = new PreparedTemplate("notification/email/employee.registration.body.%s.ftl");
        template.setModel(templateModel);
        template.setTemplateConfiguration(templateConfiguration);
        return template;
    }

    @Override
    public PreparedTemplate getEmployeeRegistrationEmailNotificationSubject(EmployeeRegistration employeeRegistration) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put( "employeeFullName", employeeRegistration.getEmployeeFullName() );

        PreparedTemplate template = new PreparedTemplate( "notification/email/employee.registration.subject.%s.ftl" );
        template.setModel( templateModel );
        template.setTemplateConfiguration( templateConfiguration );
        return template;
    }

    @Override
    public PreparedTemplate getUserLoginNotificationBody(UserLoginUpdateEvent event, String url) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("url", url);
        templateModel.put("hasDisplayName", HelperFunc.isNotEmpty(event.getDisplayName()));
        templateModel.put("isNewAccount", event.isNewAccount());
        templateModel.put("displayName", event.getDisplayName());
        templateModel.put("login", event.getLogin());
        templateModel.put("password", event.getPasswordRaw());

        PreparedTemplate template = new PreparedTemplate("notification/email/user.login.body.%s.ftl");
        template.setModel(templateModel);
        template.setTemplateConfiguration(templateConfiguration);
        return template;
    }

    @Override
    public PreparedTemplate getUserLoginNotificationSubject(String url) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("url", url);

        PreparedTemplate template = new PreparedTemplate("notification/email/user.login.subject.%s.ftl");
        template.setModel(templateModel);
        template.setTemplateConfiguration(templateConfiguration);
        return template;
    }

    @Override
    public PreparedTemplate getContractRemainingOneDayNotificationBody(Contract contract, ContractDate contractDate, String urlTemplate, Collection<String> recipients) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("contractNumber", contract.getNumber());
        templateModel.put("contractDateType", contractDate.getType());
        templateModel.put("contractDateDate", contractDate.getDate());
        templateModel.put("contractDateComment", escapeTextAndReplaceLineBreaks(contractDate.getComment()));
        templateModel.put("contractDateCommentExists", StringUtils.isNotBlank(contractDate.getComment()));
        templateModel.put("linkToContract", String.format(urlTemplate, contract.getId()));
        templateModel.put("recipients", recipients);

        PreparedTemplate template = new PreparedTemplate("notification/email/contract.remaining.one.day.body.%s.ftl");
        template.setModel(templateModel);
        template.setTemplateConfiguration(templateConfiguration);
        return template;
    }

    @Override
    public PreparedTemplate getContractRemainingOneDayNotificationSubject(Contract contract, ContractDate contractDate) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("contractNumber", contract.getNumber());

        PreparedTemplate template = new PreparedTemplate("notification/email/contract.remaining.one.day.subject.%s.ftl");
        template.setModel(templateModel);
        template.setTemplateConfiguration(templateConfiguration);
        return template;
    }

    @Override
    public PreparedTemplate getDocumentMemberAddedBody(String documentName, String url) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("url", url);
        templateModel.put("documentName", documentName);

        PreparedTemplate template = new PreparedTemplate("notification/email/document.member.added.body.%s.ftl");
        template.setModel(templateModel);
        template.setTemplateConfiguration(templateConfiguration);
        return template;
    }

    @Override
    public PreparedTemplate getDocumentMemberAddedSubject(String documentName) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("documentName", documentName);

        PreparedTemplate template = new PreparedTemplate("notification/email/document.member.added.subject.%s.ftl");
        template.setModel(templateModel);
        template.setTemplateConfiguration(templateConfiguration);
        return template;
    }

    @Override
    public PreparedTemplate getDocumentDocFileUpdatedByMemberBody(String documentName, String initiatorName, String comment) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("documentName", documentName);
        templateModel.put("initiatorName", initiatorName);
        templateModel.put("comment", comment);

        PreparedTemplate template = new PreparedTemplate("notification/email/document.doc.file.updated.by.member.body.%s.ftl");
        template.setModel(templateModel);
        template.setTemplateConfiguration(templateConfiguration);
        return template;
    }

    @Override
    public PreparedTemplate getDocumentDocFileUpdatedByMemberSubject(String documentName) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("documentName", documentName);

        PreparedTemplate template = new PreparedTemplate("notification/email/document.doc.file.updated.by.member.subject.%s.ftl");
        template.setModel(templateModel);
        template.setTemplateConfiguration(templateConfiguration);
        return template;
    }

    @Override
    public PreparedTemplate getMailReportBody(Report report) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("reportId", report.getId());
        templateModel.put("name", report.getName());
        templateModel.put("created", report.getCreated());
        templateModel.put("creator", report.getCreator().getDisplayShortName());
        templateModel.put("type", report.getReportType());
        templateModel.put("status", report.getStatus());
        templateModel.put("filter", report.getCaseQuery());

        PreparedTemplate template = new PreparedTemplate("notification/email/report.body.%s.ftl");
        template.setModel(templateModel);
        template.setTemplateConfiguration(templateConfiguration);
        return template;
    }

    @Override
    public PreparedTemplate getMailReportSubject(Report report) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("reportTitle", report.getName());
        templateModel.put("scheduledType", report.getScheduledType());

        PreparedTemplate template = new PreparedTemplate("notification/email/report.subject.%s.ftl");
        template.setModel(templateModel);
        template.setTemplateConfiguration(templateConfiguration);
        return template;
    }

    @Override
    public PreparedTemplate getMailProjectSubject(Project project, Person initiator) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("projectNumber", String.valueOf(project.getId()));
        templateModel.put("initiator", initiator.getDisplayName());
        templateModel.put("TransliterationUtils", new TransliterationUtils());

        PreparedTemplate template = new PreparedTemplate("notification/email/project.subject.%s.ftl");
        template.setModel(templateModel);
        template.setTemplateConfiguration(templateConfiguration);

        return template;
    }

    @Override
    public PreparedTemplate getMailProjectBody(AssembledProjectEvent event, Collection<String> recipients, DiffCollectionResult<LinkData> links, String crmProjectUrl, EnumLangUtil enumLangUtil) {
        Project oldProjectState = event.getOldProjectState();
        Project newProjectState = event.getNewProjectState();

        Map<String, Object> templateModel = new HashMap<>();

        templateModel.put("TransliterationUtils", new TransliterationUtils());
        templateModel.put("EnumLangUtil", enumLangUtil);
        templateModel.put("TimeFormatter", new WorkTimeFormatter(true));
        templateModel.put("TextUtils", new TextUtils());

        templateModel.put("creator", newProjectState.getCreator().getDisplayShortName());
        templateModel.put("created", newProjectState.getCreated());
        templateModel.put("isCreated", event.isCreateEvent());
        templateModel.put("recipients", recipients);

        templateModel.put("linkToProject", String.format(crmProjectUrl, event.getProjectId()));
        templateModel.put("projectNumber", String.valueOf(event.getProjectId()));
        templateModel.put("nameChanged", event.isNameChanged());
        templateModel.put("oldName", getNullOrElse(oldProjectState, Project::getName));
        templateModel.put("newName", newProjectState.getName());

        templateModel.put("descriptionChanged", event.isDescriptionChanged());
        templateModel.put("oldDescription", getNullOrElse(oldProjectState, Project::getDescription));
        templateModel.put("newDescription", newProjectState.getDescription());

        templateModel.put("stateChanged", event.isStateChanged());
        templateModel.put("oldState", getNullOrElse(oldProjectState, Project::getState));
        templateModel.put("newState", newProjectState.getState());

        templateModel.put("regionChanged", event.isRegionChanged());
        templateModel.put("oldRegion", getNullOrElse(getNullOrElse(oldProjectState, Project::getRegion), EntityOption::getDisplayText));
        templateModel.put("newRegion", getNullOrElse(newProjectState.getRegion(), EntityOption::getDisplayText));

        templateModel.put("companyChanged", event.isCompanyChanged());
        templateModel.put("oldCompany", getNullOrElse(getNullOrElse(oldProjectState, Project::getCustomer), Company::getCname));
        templateModel.put("newCompany", newProjectState.getCustomer().getCname());

        templateModel.put("customerTypeChanged", event.isCustomerTypeChanged());
        templateModel.put("oldCustomerType", getNullOrElse(oldProjectState, Project::getCustomerType));
        templateModel.put("newCustomerType", newProjectState.getCustomerType());

        templateModel.put("productDirectionChanged", event.isProductDirectionChanged());
        templateModel.put("oldProductDirection", getNullOrElse(getNullOrElse(oldProjectState, Project::getProductDirection), EntityOption::getDisplayText));
        templateModel.put("newProductDirection", newProjectState.getProductDirection().getDisplayText());

        templateModel.put("productChanged", event.isProductChanged());
        templateModel.put("oldProduct", getNullOrElse(getNullOrElse(oldProjectState, Project::getSingleProduct), ProductShortView::getName));
        templateModel.put("newProduct", getNullOrElse(newProjectState.getSingleProduct(), ProductShortView::getName));

        templateModel.put("supportValidityChanged", event.isSupportValidityChanged());
        templateModel.put("oldSupportValidity", getNullOrElse(oldProjectState, Project::getTechnicalSupportValidity));
        templateModel.put("newSupportValidity", newProjectState.getTechnicalSupportValidity());

        templateModel.put("team", event.getTeamDiffs());
        templateModel.put("sla", event.getSlaDiffs());

        templateModel.put( "caseComments",  getProjectCommentsModelKeys(event.getAllComments(), event.getAddedComments(), event.getChangedComments(), event.getRemovedComments(), En_TextMarkup.MARKDOWN));

        templateModel.put("hasLinks", hasLinks(links));
        templateModel.put("existingLinks", links == null ? null : links.getSameEntries());
        templateModel.put("addedLinks", links == null ? null : links.getAddedEntries());
        templateModel.put("removedLinks", links == null ? null : links.getRemovedEntries());

        PreparedTemplate template = new PreparedTemplate("notification/email/project.body.%s.ftl");
        template.setModel(templateModel);
        template.setTemplateConfiguration(templateConfiguration);

        return template;
    }

    @Override
    public PreparedTemplate getRoomReservationNotificationSubject(RoomReservation roomReservation, RoomReservationNotificationEvent.Action action) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("is_created", action == RoomReservationNotificationEvent.Action.CREATED);
        templateModel.put("is_updated", action == RoomReservationNotificationEvent.Action.UPDATED);
        templateModel.put("is_removed", action == RoomReservationNotificationEvent.Action.REMOVED);
        templateModel.put("room", roomReservation.getRoom() != null
                ? roomReservation.getRoom().getName()
                : "?");

        PreparedTemplate template = new PreparedTemplate("notification/email/reservation.room.subject.%s.ftl");
        template.setModel(templateModel);
        template.setTemplateConfiguration(templateConfiguration);
        return template;
    }

    @Override
    public PreparedTemplate getRoomReservationNotificationBody(RoomReservation roomReservation, RoomReservationNotificationEvent.Action action, Collection<String> recipients) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("is_created", action == RoomReservationNotificationEvent.Action.CREATED);
        templateModel.put("is_updated", action == RoomReservationNotificationEvent.Action.UPDATED);
        templateModel.put("is_removed", action == RoomReservationNotificationEvent.Action.REMOVED);
        templateModel.put("person_responsible", roomReservation.getPersonResponsible() != null
                ? roomReservation.getPersonResponsible().getDisplayName()
                : "?");
        templateModel.put("room", roomReservation.getRoom() != null
                ? roomReservation.getRoom().getName()
                : "?");
        templateModel.put("date", roomReservation.getDateFrom() != null
                ? new SimpleDateFormat("dd.MM.yyyy").format(roomReservation.getDateFrom())
                : "?");
        templateModel.put("time",
                (roomReservation.getDateFrom() != null
                        ? new SimpleDateFormat("HH:mm").format(roomReservation.getDateFrom())
                        : "?") +
                " - " +
                (roomReservation.getDateUntil() != null
                        ? new SimpleDateFormat("HH:mm").format(roomReservation.getDateUntil())
                        : "?"));
        templateModel.put("reason", roomReservation.getReason() != null
                ? roomReservation.getReason().getId()
                : "?");
        templateModel.put("coffee_break_count", roomReservation.getCoffeeBreakCount());
        templateModel.put("comment", roomReservation.getComment());
        templateModel.put("recipients", recipients);

        PreparedTemplate template = new PreparedTemplate("notification/email/reservation.room.body.%s.ftl");
        template.setModel(templateModel);
        template.setTemplateConfiguration(templateConfiguration);
        return template;
    }

    private <T, R> R getNullOrElse(T value, Function<T, R> orElseFunction) {
        return value == null ? null : orElseFunction.apply(value);
    }

    private List<Map<String, Object>> getCommentsModelKeys(List<CaseComment> comments, List<CaseComment> added, List<CaseComment> changed, List<CaseComment> removed, En_TextMarkup textMarkup){
        return comments.stream()
                .sorted(Comparator.comparing(CaseComment::getCreated, Date::compareTo))
                .map( comment -> {

                    boolean isNew = contains( added, comment );
                    boolean isChanged = contains( changed, comment );

                    Map< String, Object > mailComment = new HashMap<>();
                    mailComment.put( "created", comment.getCreated() );
                    mailComment.put( "author", comment.getAuthor() );
                    mailComment.put("text", escapeTextAndRenderHTML(comment.getText(), textMarkup));
                    mailComment.put( "caseState", En_CaseState.getById( comment.getCaseStateId() ) );
                    mailComment.put( "caseImportance", En_ImportanceLevel.getById( comment.getCaseImpLevel() ) );
                    mailComment.put( "caseManager", comment.getCaseManagerId());
                    mailComment.put( "caseManagerAndCompany", comment.getCaseManagerShortName() + " (" + comment.getManagerCompanyName() + ")");
                    mailComment.put( "isPrivateComment", comment.isPrivateComment() );
                    mailComment.put( "added", isNew);
                    if (isChanged) {
                        CaseComment oldComment = changed.get( changed.indexOf( comment ) );
                        mailComment.put( "oldText", escapeTextAndRenderHTML( oldComment.getText(), textMarkup ) );
                    }
                    mailComment.put( "removed", contains( removed, comment ));
                    return mailComment;
                } )
                .collect( toList() );
    }

    private List<Map<String, Object>> getProjectCommentsModelKeys(List<CaseComment> comments, List<CaseComment> added, List<CaseComment> changed, List<CaseComment> removed, En_TextMarkup textMarkup){
        return comments.stream()
                .sorted(Comparator.comparing(CaseComment::getCreated, Date::compareTo))
                .map(comment -> {

                    boolean isNew = contains(added, comment);
                    boolean isChanged = contains(changed, comment);

                    Map<String, Object> mailComment = new HashMap<>();
                    mailComment.put("created", comment.getCreated());
                    mailComment.put("author", comment.getAuthor());
                    mailComment.put("text", escapeTextAndRenderHTML(comment.getText(), textMarkup));
                    mailComment.put("added", isNew);
                    if (isChanged) {
                        CaseComment oldComment = changed.get(changed.indexOf(comment));
                        mailComment.put("oldText", escapeTextAndRenderHTML(oldComment.getText(), textMarkup));
                    }
                    mailComment.put("removed", contains(removed, comment));
                    return mailComment;
                })
                .collect(toList());
    }

    String escapeTextAndRenderHTML(String text, En_TextMarkup textMarkup) {
        if (text == null) {
            return null;
        }
        text = htmlRenderer.plain2html(text, textMarkup, false);
        return text;
    }

    private String escapeTextAndReplaceLineBreaks(String text) {
        if (text == null) {
            return null;
        }
        text = HTMLHelper.htmlEscape( text );
        text = replaceLineBreaks( text );
        return text;
    }

    private String replaceLineBreaks(String text) {
        if (text == null) {
            return null;
        }
        return text.replaceAll("((?<!<br \\/>)[\r\n|\n|\r])", "<br/>");
    }

    private Map<String, Object> buildAttachmentModelKeys(Collection<Attachment> existing, Collection<Attachment> added, Collection<Attachment> removed){
        if(isEmpty(existing) && isEmpty(added) && isEmpty(removed))
            return Collections.emptyMap();

        Map<String, Object> model = new HashMap<>(3);
        model.put( "attachments", emptyIfNull( existing ));
        model.put( "removedAttachments", emptyIfNull(removed));
        model.put( "addedAttachments", emptyIfNull(added));

        return model;
    }

    private String getText( Map<String, Object> model, String nameTemplate ) throws IOException, TemplateException  {
        return getText( model, nameTemplate, null );
    }

    private String getText( Map<String, Object> model, String nameTemplate, Locale lang  ) throws IOException, TemplateException {
        Writer writer = new StringWriter();

        if (lang == null) {
            lang = new Locale( "ru" );
        }

        nameTemplate = String.format( nameTemplate, lang.getLanguage() );

        Template template = templateConfiguration.getTemplate( BASE_TEMPLATE_PATH + nameTemplate, lang );
        template.process( model, writer );
        return writer.toString();

    }

    private boolean hasLinks( DiffCollectionResult<LinkData> mergeLinks ) {
        if (mergeLinks == null) return false;
        if (!isEmpty( mergeLinks.getSameEntries() )) return true;
        if (!isEmpty( mergeLinks.getAddedEntries() )) return true;
        if (!isEmpty( mergeLinks.getRemovedEntries() )) return true;
        return false;
    }


}
