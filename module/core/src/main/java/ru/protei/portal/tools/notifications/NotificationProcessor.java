package ru.protei.portal.tools.notifications;

import oracle.ucp.common.Core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.MimeMessageHelper;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.CaseCommentEvent;
import ru.protei.portal.core.event.CaseObjectEvent;
import ru.protei.portal.core.mail.MailMessageFactory;
import ru.protei.portal.core.mail.MailSendChannel;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.struct.NotificationEntry;
import ru.protei.portal.core.service.*;
import ru.protei.portal.core.service.template.PreparedTemplate;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Set;

/**
 * Created by shagaleev on 30/05/17.
 */
public class NotificationProcessor {

    private final static Logger log = LoggerFactory.getLogger( NotificationProcessor.class );

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

        performNotification( event.getCaseObject(), oldManager, event, null, notificationEntries, event.getPerson() );

    }

    @EventListener
    public void onCaseCommentAdded( CaseCommentEvent event ) {
        Set<NotificationEntry> notificationEntries = subscriptionService.subscribers( event );
        if ( notificationEntries.isEmpty() ) {
            return;
        }

        performNotification( event.getCaseObject(), null, null, event, notificationEntries, event.getPerson() );
    }

    private void performNotification(
        CaseObject caseObject, Person oldManager, CaseObjectEvent caseEvent, CaseCommentEvent commentEvent,
        Set<NotificationEntry> notificationEntries, Person currentPerson
    ) {
        CoreResponse<List<CaseComment> > comments = caseService.getCaseCommentList( caseObject.getId() );
        if ( comments.isError() ) {
            log.error( "Failed to retrieve comments for caseId={}", caseObject.getId() );
            return;
        }

        Person manager = getManager( caseObject.getManagerId() );
        if ( oldManager == null ) {
            oldManager = manager;
        }

        PreparedTemplate bodyTemplate = templateService.getCrmEmailNotificationBody(
            caseEvent, comments.getData(), manager, oldManager, commentEvent,
            config.data().getCrmCaseUrl()
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

        notificationEntries.stream().forEach( (entry)->{
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
        helper.setText( body, true );
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
}
