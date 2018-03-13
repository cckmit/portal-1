package ru.protei.portal.tools.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.MimeMessageHelper;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.AssembledCaseEvent;
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
    public void onCaseChanged(AssembledCaseEvent event){
        Set<NotificationEntry> defaultNotifiers = subscriptionService.subscribers( event );
        Collection<NotificationEntry> notifiers =
                formNotifiers(defaultNotifiers, event.getInitiator(), event.getCaseObject().getManager(), event.getCaseObject().isPrivateCase());

        if(!notifiers.isEmpty())
            performNotification( event, notifiers );
    }

    private void performNotification(
        AssembledCaseEvent event, Collection<NotificationEntry> notifiers
    ) {

        CaseObject newState = event.getCaseObject();
        List<String> recipients = notifiers.stream().map( NotificationEntry::getAddress ).collect( toList() );

        CoreResponse<List<CaseComment> > comments = caseService.getCaseCommentList( null, newState.getId() );
        if ( comments.isError() ) {
            log.error( "Failed to retrieve comments for caseId={}", newState.getId() );
            return;
        }

        PreparedTemplate bodyTemplate = templateService.getCrmEmailNotificationBody(
            event, comments.getData(), config.data().getCrmCaseUrl(), recipients
        );
        if ( bodyTemplate == null ) {
            log.error( "Failed to prepare body template" );
            return;
        }

        PreparedTemplate subjectTemplate = templateService.getCrmEmailNotificationSubject( newState, event.getInitiator() );
        if ( subjectTemplate == null ) {
            log.error( "Failed to prepare subject template" );
            return;
        }

        notifiers.forEach( (entry)->{
            String body = bodyTemplate.getText( entry.getAddress(), entry.getLangCode(), isProteiNotifier(entry) );
            String subject = subjectTemplate.getText( entry.getAddress(), entry.getLangCode(), isProteiNotifier(entry) );

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

    private boolean isProteiNotifier(NotificationEntry entry){
        return entry.getAddress().endsWith("@protei.ru");
    }

    /**
     * Form case notifiers with creator and manager
     */
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

        return isPrivateCase?
                allNotifiers.stream().filter(this::isProteiNotifier).collect(Collectors.toList()):
                allNotifiers;
    }
}
