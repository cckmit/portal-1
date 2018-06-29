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
import ru.protei.portal.core.model.dao.CaseObjectDAO;
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
import java.util.*;
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
    CaseObjectDAO caseObjectDAO;

    @Autowired
    PortalConfig config;

    private Map<Long, Long> caseToLastIdMap = new HashMap<>();

    @EventListener
    public void onCaseChanged(AssembledCaseEvent event){
        Set<NotificationEntry> defaultNotifiers = subscriptionService.subscribers( event );
        Collection<NotificationEntry> notifiers =
                formNotifiers(defaultNotifiers,
                        event.getInitiator(),
                        event.getCaseObject().getCreator(),
                        event.getCaseObject().getManager(),
                        event.getCaseObject().isPrivateCase());

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

        Long currentMessageId = 1 + Math.max(
                newState.getEmailLastId() == null ? 0L : newState.getEmailLastId(),
                caseToLastIdMap.getOrDefault(newState.getId(), 0L)
        );
        String messageId = "case." + String.valueOf(newState.getCaseNumber()) + "." + String.valueOf(currentMessageId);
        String inReplyTo = "case." + String.valueOf(newState.getCaseNumber()) + "." + String.valueOf(currentMessageId - 1);
        List<String> references = new ArrayList<>();
        for (long i = currentMessageId - 1; i >= 0; i--) {
            references.add("case." + String.valueOf(newState.getCaseNumber()) + "." + String.valueOf(i));
        }

        notifiers.forEach( (entry)->{
            if (!isProteiRecipient(entry) && config.data().smtp().isBlockExternalRecipients()) {
                log.debug("block send mail to {} (external recipient)", entry.getAddress());
                return;
            }

            String body = bodyTemplate.getText( entry.getAddress(), entry.getLangCode(), isProteiRecipient(entry) );
            String subject = subjectTemplate.getText( entry.getAddress(), entry.getLangCode(), isProteiRecipient(entry) );

            try {
                MimeMessageHelper msg = prepareMessage(messageId, inReplyTo, references, subject, body);
                msg.setTo( entry.getAddress() );
                mailSendChannel.send( msg.getMimeMessage() );
            }
            catch ( Exception e ) {
                log.error( "Failed to make MimeMessage", e );
            }

        } );

        caseToLastIdMap.put(newState.getId(), currentMessageId);
        newState.setEmailLastId(currentMessageId);
        caseObjectDAO.partialMerge(newState, "email_last_id");
    }

    private MimeMessageHelper prepareMessage( String messageId, String inReplyTo, List<String> references, String subj, String body ) throws MessagingException {
        MimeMessageHelper helper = new MimeMessageHelper( messageFactory.createMailMessage( messageId, inReplyTo, references ), true, config.data().smtp().getDefaultCharset() );
        helper.setSubject( subj );
        helper.setFrom( config.data().smtp().getFromAddress() );
        helper.setText( HelperFunc.nvlt(body, ""), true );
        return helper;
    }

    private boolean isProteiRecipient(NotificationEntry entry){
        return entry.getAddress().endsWith("@protei.ru");
    }

    /**
     * Form case notifiers with initiator, creator and manager
     */
    private Collection<NotificationEntry> formNotifiers(Set<NotificationEntry> allNotifiers, Person initiator, Person creator, Person manager, boolean isPrivateCase){
        String initiatorEmail = new PlainContactInfoFacade( initiator.getContactInfo() ).getEmail();
        String creatorEmail = creator == null? null: new PlainContactInfoFacade( creator.getContactInfo() ).getEmail();
        String managerEmail = manager == null? null: new PlainContactInfoFacade( manager.getContactInfo() ).getEmail();

        if( !(initiatorEmail == null || initiatorEmail.isEmpty()) ){
            allNotifiers.add(
                    new NotificationEntry(initiatorEmail, En_ContactItemType.EMAIL,  "ru")
            );
        }

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
                allNotifiers.stream().filter(this::isProteiRecipient).collect(Collectors.toList()):
                allNotifiers;
    }
}
