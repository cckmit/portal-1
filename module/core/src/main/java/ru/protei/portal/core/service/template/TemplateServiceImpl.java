package ru.protei.portal.core.service.template;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.slf4j.Logger;
import ru.protei.portal.core.model.util.CaseTextMarkupUtil;
import ru.protei.portal.core.model.util.DiffCollectionResult;
import ru.protei.portal.core.renderer.HTMLRenderer;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.event.UserLoginUpdateEvent;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.dict.En_TextMarkup;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.HTMLHelper;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.utils.LinkData;
import ru.protei.portal.core.utils.WorkTimeFormatter;
import ru.protei.portal.core.model.util.TransliterationUtils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;
import static ru.protei.portal.core.model.helper.CollectionUtils.isEmpty;

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
        CaseObject oldState = event.getInitState() == null? null: newState.equals(event.getInitState())? null: event.getInitState();
        En_TextMarkup textMarkup = CaseTextMarkupUtil.recognizeTextMarkup(newState);

        Map<String, Object> templateModel = new HashMap<>();

        templateModel.put( "TextUtils", new TextUtils() );
        templateModel.put( "TimeElapsedFormatter", new WorkTimeFormatter() );
        templateModel.put("TranslitUtils", new TransliterationUtils());
        templateModel.put( "linkToIssue", String.format( urlTemplate, newState.getCaseNumber() ) );
        templateModel.put( "isCreated", event.isCreateEvent() );
        templateModel.put( "createdByMe", false );
        templateModel.put( "case", newState );
        templateModel.put( "oldCase", oldState );
        templateModel.put( "importanceLevel", En_ImportanceLevel.getById( newState.getImpLevel() ).getCode() );
        templateModel.put( "manager", newState.getManager() );
        templateModel.put( "caseState", En_CaseState.getById( newState.getStateId() ).getName() );
        templateModel.put( "recipients", recipients );
        templateModel.put("platform", newState.getPlatformName());

        templateModel.put( "caseInfo", newState == null ? null : escapeTextAndRenderHTML(newState.getInfo(), textMarkup) );
        templateModel.put( "oldCaseInfo", oldState == null ? null : escapeTextAndRenderHTML(oldState.getInfo(), textMarkup) );

        templateModel.put( "productChanged", event.isProductChanged() );
        templateModel.put( "importanceChanged", event.isCaseImportanceChanged() );
        templateModel.put( "oldImportanceLevel", oldState == null ? null : En_ImportanceLevel.getById( oldState.getImpLevel() ).getCode() );

        templateModel.put( "caseChanged", event.isCaseStateChanged() );
        templateModel.put( "oldCaseState", oldState == null ? null : En_CaseState.getById( oldState.getStateId() ).getName() );

        templateModel.put("timeElapsedChanged", event.isTimeElapsedChanged());
        templateModel.put("elapsed", newState.getTimeElapsed());
        templateModel.put("oldElapsed", oldState == null ? null : oldState.getTimeElapsed());

        templateModel.put( "customerChanged", event.isInitiatorChanged() || event.isInitiatorCompanyChanged() );
        templateModel.put( "oldInitiator", oldState == null ? null : oldState.getInitiator() );
        templateModel.put( "oldInitiatorCompany", oldState == null ? null : oldState.getInitiatorCompany() );

        templateModel.put( "managerChanged", event.isManagerChanged() );
        templateModel.put( "oldManager", oldState == null? null: oldState.getManager() );

        templateModel.put( "infoChanged", event.isInfoChanged() );
        templateModel.put( "nameChanged", event.isNameChanged() );
        templateModel.put( "privacyChanged", event.isPrivacyChanged() );

        templateModel.put("platformChanged", event.isPlatformChanged());
        templateModel.put("oldPlatform", oldState == null ? null : oldState.getPlatformName());
        templateModel.put("hasLinks", hasLinks( mergeLinks) );
        templateModel.put("existingLinks", mergeLinks == null ? null : mergeLinks.getSameEntries());
        templateModel.put("addedLinks", mergeLinks == null ? null : mergeLinks.getAddedEntries());
        templateModel.put("removedLinks", mergeLinks == null ? null : mergeLinks.getRemovedEntries());

        Collection<Attachment> existingAttachments = new ArrayList<>((oldState == null? newState.getAttachments(): oldState.getAttachments()));
        existingAttachments.removeIf(a -> event.getRemovedAttachments().contains(a) || event.getAddedAttachments().contains(a));

        templateModel.putAll(
                buildAttachmentModelKeys(
                        existingAttachments,
                        event.getAddedAttachments(),
                        event.getRemovedAttachments())
        );

        templateModel.put( "caseComments",  getCommentsModelKeys(caseComments, event.getCaseComment(), event.getOldComment(), event.getRemovedComment(), textMarkup));

        PreparedTemplate template = new PreparedTemplate( "notification/email/crm.body.%s.ftl" );
        template.setModel( templateModel );
        template.setTemplateConfiguration( templateConfiguration );
        return template;
    }

    @Override
    public PreparedTemplate getCrmEmailNotificationSubject( CaseObject caseObject, Person currentPerson ) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put( "case", caseObject );
        templateModel.put( "author", currentPerson );
        templateModel.put( "caseState", En_CaseState.getById( caseObject.getStateId() ).getName() );
        templateModel.put( "importanceLevel", En_ImportanceLevel.getById( caseObject.getImpLevel() ).getCode() );

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
    public PreparedTemplate getEmployeeRegistrationEmailNotificationBody(EmployeeRegistration employeeRegistration, String urlTemplate, Collection<String> recipients) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put( "linkToEmployeeRegistration", String.format( urlTemplate, employeeRegistration.getId() ) );
        templateModel.put( "er", employeeRegistration );
        templateModel.put( "recipients", recipients );

        PreparedTemplate template = new PreparedTemplate( "notification/email/employee.registration.body.%s.ftl" );
        template.setModel( templateModel );
        template.setTemplateConfiguration( templateConfiguration );
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

    private List<Map<String, Object>> getCommentsModelKeys(List<CaseComment> comments, CaseComment newCaseComment, CaseComment oldCaseComment, CaseComment removedCaseComment, En_TextMarkup textMarkup){
        return comments.stream()
                .sorted(Comparator.comparing(CaseComment::getCreated, Date::compareTo))
                .map( comment -> {

                    boolean isNew = newCaseComment != null && HelperFunc.equals( newCaseComment.getId(), comment.getId() );
                    boolean isChanged = isNew && oldCaseComment != null;

                    Map< String, Object > mailComment = new HashMap<>();
                    mailComment.put( "created", comment.getCreated() );
                    mailComment.put( "author", comment.getAuthor() );
                    if (isNew) {
                        mailComment.put("text", escapeTextAndRenderHTML(newCaseComment.getText(), textMarkup));
                    } else {
                        mailComment.put("text", escapeTextAndRenderHTML(comment.getText(), textMarkup));
                    }
                    mailComment.put( "caseState", En_CaseState.getById( comment.getCaseStateId() ) );
                    mailComment.put( "caseImportance", En_ImportanceLevel.getById( comment.getCaseImpLevel() ) );
                    mailComment.put( "caseManager", comment.getCaseManagerShortName() );
                    mailComment.put( "isPrivateComment", comment.isPrivateComment() );
                    mailComment.put( "changed", isNew);
                    if (isChanged) {
                        mailComment.put( "oldText", escapeTextAndRenderHTML( oldCaseComment.getText(), textMarkup ) );
                    }
                    mailComment.put( "removed", removedCaseComment != null && HelperFunc.equals( removedCaseComment.getId(), comment.getId()));
                    return mailComment;
                } )
                .collect( toList() );
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
        if(existing.isEmpty() && added.isEmpty() && removed.isEmpty())
            return Collections.emptyMap();

        Map<String, Object> model = new HashMap<>(3);
        model.put( "attachments", existing);
        model.put( "removedAttachments", removed);
        model.put( "addedAttachments", added);

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
