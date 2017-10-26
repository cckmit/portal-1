package ru.protei.portal.core.service;


import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.protei.portal.core.event.CaseAttachmentEvent;
import ru.protei.portal.core.event.CaseCommentEvent;
import ru.protei.portal.core.event.CaseObjectEvent;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.*;
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
            CaseObjectEvent caseEvent, List< CaseComment > caseComments, Person manager, Person oldManager,
            CaseCommentEvent commentEvent, CaseAttachmentEvent attachmentEvent, String urlTemplate, List< String > recipients
    ) {
        CaseObject newState;
        if(caseEvent != null){
            newState = caseEvent.getNewState();
        }else if(commentEvent != null){
            newState = commentEvent.getCaseObject();
        }else if (attachmentEvent != null){
            newState = attachmentEvent.getCaseObject();
        }else
            return null;

        CaseObject oldState = caseEvent == null ? null : caseEvent.getOldState();

        Map<String, Object> templateModel = new HashMap<>();

        templateModel.put( "TextUtils", new TextUtils() );
        templateModel.put( "linkToIssue", String.format( urlTemplate, newState.getId() ) );
        templateModel.put( "isCreated", caseEvent == null ? false : caseEvent.isCreateEvent() );
        templateModel.put( "createdByMe", false );
        templateModel.put( "case", newState );
        templateModel.put( "oldCase", oldState );
        templateModel.put( "importanceLevel", En_ImportanceLevel.getById( newState.getImpLevel() ).getCode() );
        templateModel.put( "manager", manager );
        templateModel.put( "caseState", En_CaseState.getById( newState.getStateId() ).getName() );
        templateModel.put( "recipients", recipients );

        templateModel.put( "productChanged", caseEvent == null ? false : caseEvent.isProductChanged() );
        templateModel.put( "importanceChanged", caseEvent == null ? false : caseEvent.isCaseImportanceChanged() );
        templateModel.put( "oldImportanceLevel", oldState == null ? null : En_ImportanceLevel.getById( oldState.getImpLevel() ).getCode() );
        templateModel.put( "caseChanged", caseEvent == null ? false : caseEvent.isCaseStateChanged() );
        templateModel.put( "oldCaseState", oldState == null ? null : En_CaseState.getById( oldState.getStateId() ).getName() );
        templateModel.put( "customerChanged", caseEvent == null ? false : (caseEvent.isInitiatorChanged() || caseEvent.isInitiatorCompanyChanged() ) );
        templateModel.put( "oldInitiator", oldState == null ? null : oldState.getInitiator() );
        templateModel.put( "oldInitiatorCompany", oldState == null ? null : oldState.getInitiatorCompany() );
        templateModel.put( "managerChanged", caseEvent == null ? false : caseEvent.isManagerChanged() );
        templateModel.put( "oldManager", oldManager );
        templateModel.put( "infoChanged", caseEvent == null ? false : caseEvent.isInfoChanged() );
        templateModel.put( "nameChanged", caseEvent == null ? false : caseEvent.isNameChanged() );
        templateModel.put( "privacyChanged", caseEvent == null ? false : caseEvent.isPrivacyChanged() );


        if(oldState == null){
            if(!(commentEvent == null && attachmentEvent == null)){
                Collection<Attachment> added = commentEvent == null?attachmentEvent.getAddedAttachments():commentEvent.getAddedAttachments();
                Collection<Attachment> removed = commentEvent == null?attachmentEvent.getRemovedAttachments():commentEvent.getRemovedAttachments();
                newState.getAttachments().removeAll(added);
                templateModel.putAll(
                        buildAttachmentModelKeys(newState.getAttachments(), added, removed)
                );

            }else if(CollectionUtils.isNotEmpty(newState.getAttachments())){
                templateModel.put( "attachments", newState.getAttachments() );
            }
        }else{
            templateModel.putAll(
                    getAttachmentModelKeys(getAttachmentsFromCase(oldState), getAttachmentsFromCase(newState))
            );
        }

        templateModel.put( "caseComments",  getCommentsModelKeys(caseComments, commentEvent));

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

    private Map<String, Object> getAttachmentModelKeys(Collection<Attachment> oldAttachs, Collection<Attachment> newAttachs){
        if(oldAttachs.isEmpty() && newAttachs.isEmpty())
            return Collections.emptyMap();
        else{
            Collection<Attachment> removed = newAttachs.isEmpty()?oldAttachs:HelperFunc.subtract(oldAttachs, newAttachs);
            Collection<Attachment> added = oldAttachs.isEmpty()?newAttachs:HelperFunc.subtract(newAttachs, oldAttachs);

            Collection<Attachment> existing;
            if(added.isEmpty() && removed.isEmpty())
                existing = newAttachs;
            else{
                oldAttachs.retainAll(newAttachs);
                existing = oldAttachs;
            }

            return buildAttachmentModelKeys(existing, added, removed);
        }
    }

    private List<Map<String, Object>> getCommentsModelKeys(List<CaseComment> comments, CaseCommentEvent event){
        return comments
                .stream()
                .sorted(Comparator.comparing(CaseComment::getCreated, Date::compareTo))
                .map( comment -> {
                    Map< String, Object > caseComment = new HashMap<>();
                    caseComment.put( "created", comment.getCreated() );
                    caseComment.put( "author", comment.getAuthor() );
                    caseComment.put( "text", comment.getText() );
                    caseComment.put( "caseState", En_CaseState.getById( comment.getCaseStateId() ) );

                    boolean isChanged = event == null ? false : HelperFunc.equals( event.getCaseComment().getId(), comment.getId() );
                    caseComment.put( "changed",  isChanged);
                    if(isChanged && event.getOldCaseComment() != null){
                        caseComment.put( "oldText", event.getOldCaseComment().getText() );
                    }

                    return caseComment;
                } )
                .collect( toList() );
    }

    private Map<String, Object> buildAttachmentModelKeys(Collection<Attachment> existing, Collection<Attachment> added, Collection<Attachment> removed){
        Map<String, Object> model = new HashMap<>(3);
        model.put( "attachments", existing);
        model.put( "removedAttachments", removed);
        model.put( "addedAttachments", added);

        return model;
    }

    private Collection<Attachment> getAttachmentsFromCase(CaseObject object){
        return object == null?
                Collections.emptyList()
                :
                (object.getAttachments() == null?
                        Collections.emptyList()
                        :
                        object.getAttachments());
    }
}
