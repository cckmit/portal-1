package ru.protei.portal.tools.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.MimeMessageHelper;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.CaseAttachmentEvent;
import ru.protei.portal.core.event.CaseCommentEvent;
import ru.protei.portal.core.event.CaseObjectEvent;
import ru.protei.portal.core.mail.MailMessageFactory;
import ru.protei.portal.core.mail.MailSendChannel;
import ru.protei.portal.core.model.dict.En_ContactItemType;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.struct.NotificationEntry;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.service.*;
import ru.protei.portal.core.service.template.PreparedTemplate;

import javax.mail.MessagingException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * Created by shagaleev on 30/05/17.
 */
public class MailNotificationProcessor {

    private final static Logger log = LoggerFactory.getLogger( MailNotificationProcessor.class );

    @Autowired
    CaseSubscriptionService subscriptionService;

    @Autowired
    TemplateService templateService;

    @Autowired
    CaseService caseService;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    MailSendChannel mailSendChannel;

    @Autowired
    MailMessageFactory messageFactory;

    @Autowired
    PortalConfig config;


    @EventListener
    public void onCaseObjectChanged( CaseObjectEvent event ) {
        Set<NotificationEntry> notificationEntries = subscriptionService.subscribers( event );
        if ( notificationEntries.isEmpty() ) {
            return;
        }

        Person oldManager = null;
        if ( event.isManagerChanged() ) {
            oldManager = getManager( event.getOldState().getManagerId() );
        }

        performNotification( event.getCaseObject(), oldManager, event, null, null, notificationEntries, event.getPerson() );
    }

    @EventListener
    public void onCaseAttachmentsChanged( CaseAttachmentEvent event ) {
        Set<NotificationEntry> notificationEntries = subscriptionService.subscribers( event );
        if ( notificationEntries.isEmpty() ) {
            return;
        }

        performNotification( event.getCaseObject(), null, null, null, event, notificationEntries, event.getPerson() );
    }

    @EventListener
    public void onCaseCommentChanged(CaseCommentEvent event ) {
        Set<NotificationEntry> notificationEntries = subscriptionService.subscribers( event );
        if ( notificationEntries.isEmpty() ) {
            return;
        }

        performNotification( event.getCaseObject(), null, null, event, null, notificationEntries, event.getPerson() );
    }

    private void performNotification(
        CaseObject caseObject, Person oldManager, CaseObjectEvent caseEvent, CaseCommentEvent commentEvent, CaseAttachmentEvent attachmentEvent,
        Set<NotificationEntry> notificationEntries, Person currentPerson
    ) {

        Person manager = getManager( caseObject.getManagerId() );
        Collection<NotificationEntry> notifiers =
                formNotifiers(notificationEntries, currentPerson, caseObject.getManager(), caseObject.isPrivateCase());

        if(notifiers.isEmpty())
            return;

        CoreResponse<List<CaseComment> > comments = caseService.getCaseCommentList( null, caseObject.getId() );
        if ( comments.isError() ) {
            log.error( "Failed to retrieve comments for caseId={}", caseObject.getId() );
            return;
        }

        if ( oldManager == null ) {
            oldManager = manager;
        }

        List<String> recipients = notifiers.stream().map( NotificationEntry::getAddress ).collect( toList() );

        PreparedTemplate bodyTemplate = templateService.getCrmEmailNotificationBody(
            caseEvent, comments.getData(), manager, oldManager, commentEvent, attachmentEvent,
            config.data().getCrmCaseUrl(), recipients
        );
        if ( bodyTemplate == null ) {
            log.error( "Failed to prepare body template" );
            return;
        }

        PreparedTemplate subjectTemplate = templateService.getCrmEmailNotificationSubject( caseObject, currentPerson );
        if ( subjectTemplate == null ) {
            log.error( "Failed to prepare subject template" );
            return;
        }

        notifiers.stream().forEach( (entry)->{
            String body = bodyTemplate.getText( entry.getAddress(), entry.getLangCode() );
            String subject = subjectTemplate.getText( entry.getAddress(), entry.getLangCode() );

            try {
                MimeMessageHelper msg = prepareMessage( subject, body );
                msg.setTo( entry.getAddress() );
                mailSendChannel.send( msg.getMimeMessage() );
            }
            catch ( Exception e ) {
                log.error( "Failed to make MimeMessage", e );
            }

        } );
    }

    private MimeMessageHelper prepareMessage( String subj, String body ) throws MessagingException {
        MimeMessageHelper helper = new MimeMessageHelper( messageFactory.createMailMessage(), true );
        helper.setSubject( subj );
        helper.setFrom( config.data().smtp().getFromAddress() );
        helper.setText( HelperFunc.nvlt(body, ""), true );
        return helper;
    }

    private Person getManager( Long managerId ) {
        if ( managerId == null ) {
            return null;
        }
        CoreResponse<Person> managerResponse = employeeService.getEmployee( managerId );
        if ( managerResponse.isError() ) {
            log.error( "Failed to retrieve manager {}", managerId );
            return null;
        }

        return managerResponse.getData();
    }

    private List<NotificationEntry> filterProteiNotifiers(Collection<NotificationEntry> notifiers){
        return notifiers.stream().filter(entry -> entry.getAddress().endsWith("@protei.ru")).collect(Collectors.toList());
    }

    private Collection<NotificationEntry> formNotifiers(Set<NotificationEntry> allNotifiers, Person creator, Person manager, boolean isPrivateCase){
        String creatorEmail = new PlainContactInfoFacade( creator.getContactInfo() ).getEmail();
        String managerEmail = manager == null? null: new PlainContactInfoFacade( manager.getContactInfo() ).getEmail();

        if( !(creatorEmail == null || creatorEmail.isEmpty()) ){
            allNotifiers.add(
                new NotificationEntry(creatorEmail, En_ContactItemType.EMAIL,  "ru")
            );
        }

        if ( !(managerEmail == null || managerEmail.isEmpty()) ) {
            allNotifiers.add(
                new NotificationEntry(managerEmail, En_ContactItemType.EMAIL, "ru")
            );
        }

        return isPrivateCase? filterProteiNotifiers(allNotifiers): allNotifiers;
    }
}
