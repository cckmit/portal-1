package ru.protei.portal.core.service.template;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.*;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.HtmlUtils;
import ru.protei.portal.core.event.*;
import ru.protei.portal.core.model.dao.CaseStateDAO;
import ru.protei.portal.core.model.dict.En_RegionState;
import ru.protei.portal.core.model.dict.En_TextMarkup;
import ru.protei.portal.core.model.dto.Project;
import ru.protei.portal.core.model.dto.ReportDto;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HTMLHelper;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.struct.Interval;
import ru.protei.portal.core.model.util.CaseTextMarkupUtil;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.util.DiffCollectionResult;
import ru.protei.portal.core.model.util.TransliterationUtils;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.ProductShortView;
import ru.protei.portal.core.renderer.HTMLRenderer;
import ru.protei.portal.core.utils.DateUtils;
import ru.protei.portal.core.utils.EnumLangUtil;
import ru.protei.portal.core.utils.LinkData;
import ru.protei.portal.core.utils.WorkTimeFormatter;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;

/**
 * Реализация сервиса управления проектами
 */
public class TemplateServiceImpl implements TemplateService {
    public static final String BASE_TEMPLATE_PATH = "notification/email/";
    private static Logger log = getLogger(TemplateServiceImpl.class);
    private static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    Configuration templateConfiguration;

    @Inject
    HTMLRenderer htmlRenderer;

    @Autowired
    CaseStateDAO caseStateDAO;

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
            AssembledCaseEvent event, List<CaseComment> caseComments, Collection<Attachment> attachments,
            DiffCollectionResult<LinkData> mergeLinks, String urlTemplate, Collection<String> recipients, EnumLangUtil enumLangUtil
    ) {
        CaseObject newState = event.getCaseObject();
        En_TextMarkup textMarkup = CaseTextMarkupUtil.recognizeTextMarkup(newState);

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.putAll(makeTemplateModelUtils(enumLangUtil));
        templateModel.putAll(makeTemplateModelMeta(event));

//        templateModel.put( "case", newState );
//        templateModel.put( "oldCase", oldState );

        templateModel.put( "linkToIssue", String.format( urlTemplate, newState.getCaseNumber() ) );
        templateModel.put( "isCreated", event.isCreateEvent() );
        templateModel.put( "recipients", recipients );

        templateModel.put( "createdByMe", false );
        templateModel.put( "creator", newState.getCreator() == null ? null : newState.getCreator().getDisplayShortName() );
        templateModel.put( "created", newState.getCreated() );
        templateModel.put( "caseNumber", newState.getCaseNumber() );

        templateModel.put( "nameChanged", event.getName().hasDifferences() );
        templateModel.put( "infoChanged", event.getInfo().hasDifferences() );
        templateModel.put( "caseName", HtmlUtils.htmlEscape(event.getName().getNewState() ));
        templateModel.put( "oldCaseName", HtmlUtils.htmlEscape(event.getName().getInitialState()));
        templateModel.put( "caseInfo", escapeTextAndRenderHTML( event.getInfo().getNewState(), textMarkup ) );
        templateModel.put( "oldCaseInfo", event.getInfo().getInitialState() == null ? null : escapeTextAndRenderHTML( event.getInfo().getInitialState(), textMarkup ) );

        templateModel.put( "privacy", newState.isPrivateCase() );

        templateModel.put("hasLinks", hasLinks( mergeLinks) );
        templateModel.put("existingLinks", mergeLinks == null ? null : mergeLinks.getSameEntries());
        templateModel.put("addedLinks", mergeLinks == null ? null : mergeLinks.getAddedEntries());
        templateModel.put("removedLinks", mergeLinks == null ? null : mergeLinks.getRemovedEntries());

        templateModel.putAll(
                buildAttachmentModelKeys(
                        attachments,
                        event.getAddedAttachments(),
                        event.getRemovedAttachments())
        );

        templateModel.put( "caseComments",  getCommentsModelKeys(caseComments, event.getAddedCaseComments(), event.getChangedCaseComments(), event.getRemovedCaseComments(), textMarkup));

        PreparedTemplate template = new PreparedTemplate( "notification/email/crm.body.%s.ftl" );
        template.setModel( templateModel );
        template.setTemplateConfiguration( templateConfiguration );
        return template;
    }

    private Map<String, Object> makeTemplateModelUtils(EnumLangUtil enumLangUtil) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("TextUtils", new TextUtils());
        templateModel.put("TimeElapsedFormatter", new WorkTimeFormatter());
        templateModel.put("TranslitUtils", new TransliterationUtils());
        templateModel.put("EnumLangUtil", enumLangUtil);
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
        templateModel.put("caseState", newMetaState.getStateName());
        templateModel.put("oldCaseState", oldMetaState == null ? null : oldMetaState.getStateName());

        templateModel.put("isPausedState", CrmConstants.State.PAUSED == newMetaState.getStateId());
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

        templateModel.put("deadlineChanged", event.isDeadlineChanged());
        templateModel.put("deadline", newMetaState.getDeadline() == null ? null : new Date(newMetaState.getDeadline()));
        templateModel.put("oldDeadline", (oldMetaState == null || oldMetaState.getDeadline() == null) ? null : new Date(oldMetaState.getDeadline()));

        templateModel.put("workTriggerChanged", event.isWorkTriggerChanged());
        templateModel.put("workTrigger", newMetaState.getWorkTrigger() == null ? null : newMetaState.getWorkTrigger());
        templateModel.put("oldWorkTrigger", oldMetaState == null || oldMetaState.getWorkTrigger() == null ? null : oldMetaState.getWorkTrigger());

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
        templateModel.put( "caseState", caseMeta.getStateName());
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
        templateModel.put("employeeFullName", HtmlUtils.htmlEscape(newState.getEmployeeFullName()));
        templateModel.put("headOfDepartmentShortName", newState.getHeadOfDepartmentShortName());
        templateModel.put("employmentType", newState.getEmploymentType().name());
        templateModel.put("withRegistration", newState.isWithRegistration());
        templateModel.put("position", HtmlUtils.htmlEscape(newState.getPosition()));
        templateModel.put("state", newState.getStateName());
        templateModel.put("employmentDateChanged", event.isEmploymentDateChanged());
        templateModel.put("oldEmploymentDate", oldState == null ? null : oldState.getEmploymentDate());
        templateModel.put("newEmploymentDate", newState.getEmploymentDate());
        templateModel.put("created", newState.getCreated());
        templateModel.put("workplace", HtmlUtils.htmlEscape(newState.getWorkplace()));
        templateModel.put("equipmentList", newState.getEquipmentList());
        templateModel.put("operatingSystem", HtmlUtils.htmlEscape(newState.getOperatingSystem()));
        templateModel.put("additionalSoft", HtmlUtils.htmlEscape(newState.getAdditionalSoft()));
        templateModel.put("resourceList", newState.getResourceList());
        templateModel.put("resourceComment", HtmlUtils.htmlEscape(newState.getResourceComment()));
        templateModel.put("phoneOfficeTypeList", newState.getPhoneOfficeTypeList());
        templateModel.put("comment", HtmlUtils.htmlEscape(newState.getComment()));
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
    public PreparedTemplate getContractRemainingOneDayNotificationBody(Contract contract, ContractDate contractDate, String urlTemplate, Collection<String> recipients, EnumLangUtil enumLangUtil) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("EnumLangUtil", enumLangUtil);

        templateModel.put("contractType", contract.getContractType());
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
    public PreparedTemplate getContractRemainingOneDayNotificationSubject(Contract contract, ContractDate contractDate, EnumLangUtil enumLangUtil) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("EnumLangUtil", enumLangUtil);

        templateModel.put("contractType", contract.getContractType());
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
    public PreparedTemplate getMailReportBody(ReportDto reportDto, Interval createdInterval, Interval modifiedInterval) {
        Report report = reportDto.getReport();

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("reportId", report.getId());
        templateModel.put("name", report.getName());
        templateModel.put("created", report.getCreated());
        templateModel.put("creator", report.getCreator().getDisplayShortName());
        templateModel.put("type", report.getReportType());
        templateModel.put("status", report.getStatus());
        templateModel.put("createdInterval", createdInterval);
        templateModel.put("modifiedInterval", modifiedInterval);

        PreparedTemplate template = new PreparedTemplate("notification/email/report.body.%s.ftl");
        template.setModel(templateModel);
        template.setTemplateConfiguration(templateConfiguration);
        return template;
    }

    @Override
    public PreparedTemplate getMailReportSubject(ReportDto reportDto) {
        Report report = reportDto.getReport();
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
    public PreparedTemplate getMailProjectBody(AssembledProjectEvent event, List<CaseComment> comments, Collection<String> recipients, DiffCollectionResult<LinkData> links, String crmProjectUrl, EnumLangUtil enumLangUtil) {
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

        templateModel.put("linkToProject", crmProjectUrl);
        templateModel.put("projectNumber", String.valueOf(event.getProjectId()));
        templateModel.put("nameChanged", event.isNameChanged());
        templateModel.put("oldName", HtmlUtils.htmlEscape(getNullOrElse(oldProjectState, Project::getName)));
        templateModel.put("newName", HtmlUtils.htmlEscape(newProjectState.getName()));

        templateModel.put("descriptionChanged", event.isDescriptionChanged());
        templateModel.put("oldDescription", HtmlUtils.htmlEscape(getNullOrElse(oldProjectState, Project::getDescription)));
        templateModel.put("newDescription", HtmlUtils.htmlEscape(newProjectState.getDescription()));

        templateModel.put("stateChanged", event.isStateChanged());
        templateModel.put("oldState", getNullOrElse(oldProjectState, Project::getState));
        templateModel.put("newState", newProjectState.getState());

        templateModel.put("showPauseDate", newProjectState.getState() == En_RegionState.PAUSED);
        templateModel.put("pauseDateChanged", event.isPauseDateChanged());
        templateModel.put("oldPauseDate", getNullOrElse(getNullOrElse(oldProjectState, Project::getPauseDate), new SimpleDateFormat("dd.MM.yyyy")::format));
        templateModel.put("newPauseDate", getNullOrElse(newProjectState.getPauseDate(), new SimpleDateFormat("dd.MM.yyyy")::format));

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

        templateModel.put( "caseComments",
                getProjectCommentsModelKeys(
                        comments, event.getAddedCaseComments(), event.getChangedCaseComments(),
                        event.getRemovedCaseComments(), event.getCommentToAttachmentDiffs(), event.getExistingAttachments(), En_TextMarkup.MARKDOWN)
        );

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
                ? roomReservation.getPersonResponsible().getName()
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

    @Override
    public PreparedTemplate getAbsenceNotificationSubject(Person initiator, PersonAbsence absence) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("is_leave", absence.getReason().isLeave());
        templateModel.put("absentEmployee", absence.getPerson().getName());
        templateModel.put("initiator", initiator.getDisplayName());

        PreparedTemplate template = new PreparedTemplate("notification/email/absence.subject.%s.ftl");
        template.setModel(templateModel);
        template.setTemplateConfiguration(templateConfiguration);
        return template;
    }

    @Override
    public PreparedTemplate getAbsenceNotificationBody(AbsenceNotificationEvent event, EventAction action, Collection<String> recipients) {
        PersonAbsence oldState = event.getOldState();
        PersonAbsence newState = event.getNewState();
        List<PersonAbsence> multiAddAbsenceList = event.getMultiAddAbsenceList();

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("is_created", action == EventAction.CREATED);
        templateModel.put("is_updated", action == EventAction.UPDATED);
        templateModel.put("is_removed", action == EventAction.REMOVED);

        templateModel.put("absentEmployee", newState.getPerson().getName());

        templateModel.put("fromTimeChanged", event.isFromTimeChanged());
        templateModel.put("oldFromTime", oldState == null ? null : dateTimeFormat.format(oldState.getFromTime()));
        templateModel.put("fromTime", dateTimeFormat.format(newState.getFromTime()));

        templateModel.put("tillTimeChanged", event.isTillTimeChanged());
        templateModel.put("oldTillTime", oldState == null ? null : dateTimeFormat.format(oldState.getTillTime()));
        templateModel.put("tillTime", dateTimeFormat.format(newState.getTillTime()));

        templateModel.put("multiAddAbsenceList", multiAddAbsenceList);

        templateModel.put("reason", newState.getReason().getId());

        templateModel.put("commentChanged", event.isUserCommentChanged());
        templateModel.put("oldComment", getNullOrElse(oldState, PersonAbsence::getUserComment));
        templateModel.put("comment", newState.getUserComment());

        templateModel.put("recipients", recipients);

        PreparedTemplate template = new PreparedTemplate("notification/email/absence.body.%s.ftl");
        template.setModel(templateModel);
        template.setTemplateConfiguration(templateConfiguration);
        return template;
    }

    @Override
    public PreparedTemplate getAbsenceReportSubject(String title) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("reportTitle", title);

        PreparedTemplate template = new PreparedTemplate("notification/email/absence.report.subject.%s.ftl");
        template.setModel(templateModel);
        template.setTemplateConfiguration(templateConfiguration);
        return template;
    }

    @Override
    public PreparedTemplate getSubnetNotificationSubject(Subnet subnet, Person initiator, SubnetNotificationEvent.Action action) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("is_created", action == SubnetNotificationEvent.Action.CREATED);
        templateModel.put("is_updated", action == SubnetNotificationEvent.Action.UPDATED);
        templateModel.put("is_removed", action == SubnetNotificationEvent.Action.REMOVED);
        templateModel.put("ipAddress", subnet.getAddress() + "." + subnet.getMask());
        templateModel.put("initiator", initiator.getDisplayName());

        PreparedTemplate template = new PreparedTemplate("notification/email/subnet.subject.%s.ftl");
        template.setModel(templateModel);
        template.setTemplateConfiguration(templateConfiguration);
        return template;
    }

    @Override
    public PreparedTemplate getSubnetNotificationBody(Subnet subnet, SubnetNotificationEvent.Action action, Collection<String> recipients) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("is_created", action == SubnetNotificationEvent.Action.CREATED);
        templateModel.put("is_updated", action == SubnetNotificationEvent.Action.UPDATED);
        templateModel.put("is_removed", action == SubnetNotificationEvent.Action.REMOVED);
        templateModel.put("ipAddress", subnet.getAddress() + "." + subnet.getMask());
        templateModel.put("comment", HtmlUtils.htmlEscape(subnet.getComment()));
        templateModel.put("recipients", recipients);

        PreparedTemplate template = new PreparedTemplate("notification/email/subnet.body.%s.ftl");
        template.setModel(templateModel);
        template.setTemplateConfiguration(templateConfiguration);
        return template;
    }

    @Override
    public PreparedTemplate getReservedIpNotificationSubject(List<ReservedIp> reservedIps, Person initiator,
                                                             ReservedIpNotificationEvent.Action action) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("is_created", action == ReservedIpNotificationEvent.Action.CREATED);
        templateModel.put("is_updated", action == ReservedIpNotificationEvent.Action.UPDATED);
        templateModel.put("is_removed", action == ReservedIpNotificationEvent.Action.REMOVED);
        templateModel.put("initiator", initiator.getDisplayName());

        PreparedTemplate template = new PreparedTemplate("notification/email/reserved.ip.subject.%s.ftl");
        template.setModel(templateModel);
        template.setTemplateConfiguration(templateConfiguration);
        return template;
    }

    @Override
    public PreparedTemplate getReservedIpNotificationBody(List<ReservedIp> reservedIps, Collection<String> recipients) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("reservedIps", reservedIps);
        templateModel.put("recipients", recipients);

        BeansWrapper wrapper = BeansWrapper.getDefaultInstance();
        TemplateHashModel staticModels = wrapper.getStaticModels();
        try {
            TemplateHashModel htmlUtils =
                    (TemplateHashModel) staticModels.get("org.springframework.web.util.HtmlUtils");
            templateModel.put("HtmlUtils", htmlUtils);
        } catch (Exception ex) {
            log.error("getReservedIpNotificationBody: error at 'staticModels.get(org.springframework.web.util.HtmlUtils)'");
        }

        PreparedTemplate template = new PreparedTemplate("notification/email/reserved.ip.body.%s.ftl");
        template.setModel(templateModel);
        template.setTemplateConfiguration(templateConfiguration);
        return template;
    }

    @Override
    public PreparedTemplate getReservedIpRemainingNotificationSubject(Date releaseDateStart, Date releaseDateEnd) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put( "releaseDateStart", releaseDateStart != null ?
                new SimpleDateFormat("dd.MM.yyyy").format(releaseDateStart) : null);
        templateModel.put( "releaseDateEnd", releaseDateEnd != null ?
                        new SimpleDateFormat("dd.MM.yyyy").format(releaseDateEnd) : "?" );

        PreparedTemplate template = new PreparedTemplate( "notification/email/reserved.ip.remaining.subject.%s.ftl" );
        template.setModel( templateModel );
        template.setTemplateConfiguration( templateConfiguration );
        return template;
    }

    @Override
    public PreparedTemplate getPersonCaseFilterNotificationSubject() {
        Map<String, Object> templateModel = new HashMap<>();

        PreparedTemplate template = new PreparedTemplate("notification/email/person.case.filter.subject.%s.ftl");
        template.setModel(templateModel);
        template.setTemplateConfiguration(templateConfiguration);
        return template;

    }

    @Override
    public PreparedTemplate getPersonCaseFilterNotificationBody(List<CaseObject> issue, String urlTemplate) {
        Map<String, List<CaseObject>> stateToIssues = issue
                .stream().collect(groupingBy(CaseObject::getStateName));

        List<String> stateOrder = stateToIssues.values().stream()
                .map(list -> list.get(0))
                .sorted(Comparator.comparing(CaseObject::getStateId))
                .map(CaseObject::getStateName)
                .collect(Collectors.toList());

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("stateToIssues", stateToIssues);
        templateModel.put("urlTemplate", urlTemplate);
        templateModel.put("stateOrder", stateOrder);

        PreparedTemplate template = new PreparedTemplate("notification/email/person.case.filter.body.%s.ftl");
        template.setModel(templateModel);
        template.setTemplateConfiguration(templateConfiguration);
        return template;
    }


    @Override
    public String getProjectPauseTimeNotificationSubject( Long projectNumber, String projectName ) throws IOException, TemplateException{
        Map<String, Object> model = new HashMap<>();
        model.put( "projectNumber", String.valueOf( projectNumber ));
        model.put( "projectName", projectName );

        return getText(model, "project.pausetime.subject.%s.ftl");
    }

    @Override
    public String getProjectPauseTimeNotificationBody( String subscriberName, Long projectNumber, String projectName, String projectUrl, Date pauseTimeDate) throws IOException, TemplateException {
        Map<String, Object> model = new HashMap<>();
        model.put( "projectNumber", projectNumber );
        model.put( "projectName", projectName );
        model.put( "projectUrl", projectUrl );
        model.put( "userName", subscriberName );
        model.put( "pauseTimeDate",  new SimpleDateFormat("dd.MM.yyyy").format(pauseTimeDate) );

        return getText(model, "project.pausetime.body.%s.ftl");
    }

    @Override
    public PreparedTemplate getBirthdaysNotificationSubject( Date from, Date to ) {
        Map<String, Object> model = new HashMap<>();
        model.put( "fromDate", dateFormat.format(from));
        model.put( "toDate", dateFormat.format(to));

        PreparedTemplate template = new PreparedTemplate("notification/email/birthdays.subject.%s.ftl");
        template.setModel(model);
        template.setTemplateConfiguration(templateConfiguration);
        return template;
    }

    @Override
    public PreparedTemplate getBirthdaysNotificationBody(List<EmployeeShortView> employees, Collection<String> recipients) {
        Map<String, Object> model = new HashMap<>();
        model.put("employees", employees.stream().collect(Collectors.groupingBy(
                employee -> DateUtils.resetYear(employee.getBirthday()),
                LinkedHashMap::new,
                Collectors.toCollection(() -> new TreeSet<>(
                        Comparator.comparing(EmployeeShortView::getDisplayName)
                )))));
        model.put("recipients", recipients);

        PreparedTemplate template = new PreparedTemplate("notification/email/birthdays.body.%s.ftl");
        template.setModel(model);
        template.setTemplateConfiguration(templateConfiguration);
        return template;
    }

    @Override
    public PreparedTemplate getNRPENonAvailableIpsNotificationSubject() {
        Map<String, Object> model = new HashMap<>();
        PreparedTemplate template = new PreparedTemplate("notification/email/nrpe.ips.subject.%s.ftl");
        template.setModel(model);
        template.setTemplateConfiguration(templateConfiguration);
        return template;
    }

    @Override
    public PreparedTemplate getNRPENonAvailableIpsNotificationBody(List<String> nonAvailableIps, Collection<String> recipients) {
        Map<String, Object> model = new HashMap<>();
        model.put("nonAvailableIps", nonAvailableIps);
        model.put("recipients", recipients);

        PreparedTemplate template = new PreparedTemplate("notification/email/nrpe.ips.body.%s.ftl");
        template.setModel(model);
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
                    mailComment.put( "caseState", comment.getCaseStateName() );
                    mailComment.put( "caseImportance", comment.getCaseImportance() == null ? null : comment.getCaseImportance().getCode() );
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

    private List<Map<String, Object>> getProjectCommentsModelKeys(List<CaseComment> comments, List<CaseComment> added, List<CaseComment> changed, List<CaseComment> removed,
                                                                  Map<Long, DiffCollectionResult<Attachment>> commentToAttachmentDiffs, List<Attachment> existingAttachments, En_TextMarkup textMarkup) {
        return comments.stream()
                .sorted(Comparator.comparing(CaseComment::getCreated, Date::compareTo))
                .map(comment -> {

                    boolean isNewComment = contains(added, comment);
                    boolean isChangedComment = contains(changed, comment);
                    boolean isRemovedComment = contains(removed, comment);

                    Map<String, Object> mailComment = new HashMap<>();
                    mailComment.put("created", comment.getCreated());
                    mailComment.put("author", renameAuthorIfRemoteComment(comment).getAuthor());
                    mailComment.put("text", escapeTextAndRenderHTML(comment.getText(), textMarkup));

                    List<CaseAttachment> caseAttachments = emptyIfNull(comment.getCaseAttachments());

                    Set<Attachment> commentAddedAttachments = Optional.ofNullable(commentToAttachmentDiffs.get(comment.getId()))
                            .map(DiffCollectionResult::getAddedEntries)
                            .map(HashSet::new)
                            .orElse(new HashSet<>());

                    Set<Attachment> commentRemovedAttachments = Optional.ofNullable(commentToAttachmentDiffs.get(comment.getId()))
                            .map(DiffCollectionResult::getRemovedEntries)
                            .map(HashSet::new)
                            .orElse(new HashSet<>());

                    Set<Attachment> commentSameAttachments = emptyIfNull(existingAttachments)
                            .stream()
                            .filter(existingAttachment -> CollectionUtils.toList(caseAttachments, CaseAttachment::getAttachmentId).contains(existingAttachment.getId()))
                            .filter(existingAttachment -> !commentAddedAttachments.contains(existingAttachment))
                            .collect(Collectors.toSet());

                    mailComment.put("added", isNewComment);
                    mailComment.put("removed", isRemovedComment);

                    if (isNewComment || isRemovedComment) {
                        commentSameAttachments.addAll(commentAddedAttachments);
                        commentSameAttachments.addAll(commentRemovedAttachments);

                        commentAddedAttachments.clear();
                        commentRemovedAttachments.clear();
                    }

                    if (isChangedComment) {
                        CaseComment oldComment = changed.get(changed.indexOf(comment));
                        mailComment.put("oldText", escapeTextAndRenderHTML(oldComment.getText(), textMarkup));
                    }

                    mailComment.put("addedAttachments", commentAddedAttachments);
                    mailComment.put("sameAttachments", commentSameAttachments);
                    mailComment.put("removedAttachments", commentRemovedAttachments);
                    mailComment.put("hasAttachments", isNotEmpty(commentAddedAttachments) || isNotEmpty(commentSameAttachments) || isNotEmpty(commentRemovedAttachments));
                    mailComment.put("isUpdated", isChangedComment || isNotEmpty(commentAddedAttachments) || isNotEmpty(commentRemovedAttachments));

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

    private CaseComment renameAuthorIfRemoteComment (CaseComment comment){
        if (StringUtils.isNotBlank(comment.getRemoteId())){
            comment.getAuthor().setDisplayName(StringUtils.isNotBlank(comment.getOriginalAuthorFullName())
                    ? comment.getOriginalAuthorFullName()
                    : comment.getOriginalAuthorName());
        }

        return comment;
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
