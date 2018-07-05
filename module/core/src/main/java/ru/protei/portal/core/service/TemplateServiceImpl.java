package ru.protei.portal.core.service;


import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.event.UserLoginCreatedEvent;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.HTMLHelper;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.service.template.PreparedTemplate;
import ru.protei.portal.core.service.template.TextUtils;

import javax.annotation.PostConstruct;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * Реализация сервиса управления проектами
 */
public class TemplateServiceImpl implements TemplateService {
    private static Logger log = LoggerFactory.getLogger(TemplateServiceImpl.class);

    Configuration templateConfiguration;

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
            AssembledCaseEvent event, List<CaseComment> caseComments, String urlTemplate, Collection< String > recipients
    ) {
        CaseObject newState = event.getCaseObject();
        CaseObject oldState = event.getInitState() == null? null: newState.equals(event.getInitState())? null: event.getInitState();


        Map<String, Object> templateModel = new HashMap<>();

        templateModel.put( "TextUtils", new TextUtils() );
        templateModel.put( "linkToIssue", String.format( urlTemplate, newState.getCaseNumber() ) );
        templateModel.put( "isCreated", event.isCreateEvent() );
        templateModel.put( "createdByMe", false );
        templateModel.put( "case", newState );
        templateModel.put( "oldCase", oldState );
        templateModel.put( "importanceLevel", En_ImportanceLevel.getById( newState.getImpLevel() ).getCode() );
        templateModel.put( "manager", newState.getManager() );
        templateModel.put( "caseState", En_CaseState.getById( newState.getStateId() ).getName() );
        templateModel.put( "recipients", recipients );

        templateModel.put( "productChanged", event.isProductChanged() );
        templateModel.put( "importanceChanged", event.isCaseImportanceChanged() );
        templateModel.put( "oldImportanceLevel", oldState == null ? null : En_ImportanceLevel.getById( oldState.getImpLevel() ).getCode() );

        templateModel.put( "caseChanged", event.isCaseStateChanged() );
        templateModel.put( "oldCaseState", oldState == null ? null : En_CaseState.getById( oldState.getStateId() ).getName() );

        templateModel.put( "customerChanged", event.isInitiatorChanged() || event.isInitiatorCompanyChanged() );
        templateModel.put( "oldInitiator", oldState == null ? null : oldState.getInitiator() );
        templateModel.put( "oldInitiatorCompany", oldState == null ? null : oldState.getInitiatorCompany() );

        templateModel.put( "managerChanged", event.isManagerChanged() );
        templateModel.put( "oldManager", oldState == null? null: oldState.getManager() );

        templateModel.put( "infoChanged", event.isInfoChanged() );
        templateModel.put( "nameChanged", event.isNameChanged() );
        templateModel.put( "privacyChanged", event.isPrivacyChanged() );

        Collection<Attachment> existingAttachments = new ArrayList<>((oldState == null? newState.getAttachments(): oldState.getAttachments()));
        existingAttachments.removeIf(a -> event.getRemovedAttachments().contains(a) || event.getAddedAttachments().contains(a));

        templateModel.putAll(
                buildAttachmentModelKeys(
                        existingAttachments,
                        event.getAddedAttachments(),
                        event.getRemovedAttachments())
        );

        templateModel.put( "caseComments",  getCommentsModelKeys(caseComments, event.getCaseComment(), event.getOldComment()));

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
    public PreparedTemplate getUserLoginNotificationBody(UserLoginCreatedEvent event, String url) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("url", url);
        templateModel.put("hasDisplayName", HelperFunc.isNotEmpty(event.getDisplayName()));
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

    private List<Map<String, Object>> getCommentsModelKeys(List<CaseComment> comments, CaseComment newCaseComment, CaseComment oldCaseComment){
        return comments
                .stream()
                .sorted(Comparator.comparing(CaseComment::getCreated, Date::compareTo))
                .map( comment -> {
                    Map< String, Object > caseComment = new HashMap<>();
                    caseComment.put( "created", comment.getCreated() );
                    caseComment.put( "author", comment.getAuthor() );
                    caseComment.put( "text", escapeTextComment( comment.getText() ) );
                    caseComment.put( "caseState", En_CaseState.getById( comment.getCaseStateId() ) );
                    caseComment.put( "caseImportance", En_ImportanceLevel.getById( comment.getCaseImpLevel() ) );

                    boolean isChanged = newCaseComment != null && HelperFunc.equals( newCaseComment.getId(), comment.getId() );
                    caseComment.put( "changed",  isChanged);
                    if(isChanged && oldCaseComment != null){
                        caseComment.put( "oldText", escapeTextComment( oldCaseComment.getText() ) );
                    }

                    return caseComment;
                } )
                .collect( toList() );
    }

    private String escapeTextComment(String text) {
        if (text == null) {
            return null;
        }
        text = HTMLHelper.htmlEscape( text );
        text = prewrapBlockquote( text ); // HTMLHelper.prewrapBlockquote( text );
        text = replaceLineBreaks( text );
        return text;
    }

    private String replaceLineBreaks(String text) {
        if (text == null) {
            return null;
        }
        return text.replaceAll("(\r\n|\n|\r)", "<br/>");
    }

    private String prewrapBlockquote(String text) {
        if (text == null) {
            return null;
        }
        return text.replaceAll("\\[quote\\]", "<blockquote style=\"margin-left: 0;border-left: 2px solid #015d5d;padding-left: 5px;color: #015d5d;\">")
                .replaceAll("\\[/quote\\]", "</blockquote>");
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
}
