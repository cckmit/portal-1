package ru.protei.portal.tools.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.MimeMessageHelper;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.event.UserLoginCreatedEvent;
import ru.protei.portal.core.mail.MailMessageFactory;
import ru.protei.portal.core.mail.MailSendChannel;
import ru.protei.portal.core.model.dict.En_ContactItemType;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.CompanySubscription;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.struct.NotificationEntry;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.service.*;
import ru.protei.portal.core.service.template.PreparedTemplate;
import ru.protei.winter.core.utils.services.lock.LockService;

import javax.mail.MessagingException;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * Created by shagaleev on 30/05/17.
 */
public class MailNotificationProcessor {

    private final static Logger log = LoggerFactory.getLogger( MailNotificationProcessor.class );

    private final static Semaphore semaphore = new Semaphore(1);

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
    LockService lockService;

    @Autowired
    PortalConfig config;

    // ------------------------
    // CaseObject notifications
    // ------------------------

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
            performCaseObjectNotification( event, notifiers );
    }

    private void performCaseObjectNotification(AssembledCaseEvent event, Collection<NotificationEntry> notifiers) {

        CaseObject caseObject = event.getCaseObject();

        List<String> recipients = notifiers.stream().map(NotificationEntry::getAddress).collect(toList());

        CoreResponse<List<CaseComment>> comments = caseService.getCaseCommentList(
                null,
                event.getCaseComment() == null ?
                        new CaseCommentQuery(caseObject.getId()) :
                        new CaseCommentQuery(caseObject.getId(), event.getCaseComment().getCreated())
        );
        if (comments.isError()) {
            log.error("Failed to retrieve comments for caseId={}", caseObject.getId());
            return;
        }

        PreparedTemplate bodyTemplate = templateService.getCrmEmailNotificationBody(
                event, comments.getData(), config.data().getCrmCaseUrl(), recipients
        );
        if (bodyTemplate == null) {
            log.error("Failed to prepare body template");
            return;
        }

        PreparedTemplate subjectTemplate = templateService.getCrmEmailNotificationSubject(caseObject, event.getInitiator());
        if (subjectTemplate == null) {
            log.error("Failed to prepare subject template");
            return;
        }

        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            log.warn("Semaphore interrupted while waiting for green light, so we are skipping notification with case id = {}. Exception = {}", event.getCaseObject().getId(), e.getMessage());
            return;
        }

        CoreResponse<Long> response = caseService.getEmailLastId(caseObject.getId());
        Long lastMessageId = response.isOk() ? response.getData() : 0L;
        String messageId = "case." + String.valueOf(caseObject.getCaseNumber()) + "." + String.valueOf(lastMessageId + 1);
        String inReplyTo = "case." + String.valueOf(caseObject.getCaseNumber()) + "." + String.valueOf(lastMessageId);
        List<String> references = new ArrayList<>();
        for (long i = lastMessageId; i >= 0; i--) {
            references.add("case." + String.valueOf(caseObject.getCaseNumber()) + "." + String.valueOf(i));
        }

        notifiers.forEach((entry) -> {
            if (!isProteiRecipient(entry) && config.data().smtp().isBlockExternalRecipients()) {
                log.debug("block send mail to {} (external recipient)", entry.getAddress());
                return;
            }

            String body = bodyTemplate.getText(entry.getAddress(), entry.getLangCode(), isProteiRecipient(entry));
            String subject = subjectTemplate.getText(entry.getAddress(), entry.getLangCode(), isProteiRecipient(entry));

            try {
                MimeMessageHelper msg = prepareCaseObjectMessage(messageId, inReplyTo, references, subject, body);
                msg.setTo(entry.getAddress());
                mailSendChannel.send(msg.getMimeMessage());
            } catch (Exception e) {
                log.error("Failed to make MimeMessage", e);
            }
        });

        caseObject.setEmailLastId(lastMessageId + 1);
        caseService.updateEmailLastId(caseObject);

        semaphore.release();
    }

    private MimeMessageHelper prepareCaseObjectMessage(String messageId, String inReplyTo, List<String> references, String subj, String body) throws MessagingException {
        MimeMessageHelper helper = new MimeMessageHelper( messageFactory.createMailMessage( messageId, inReplyTo, references ), true, config.data().smtp().getDefaultCharset() );
        helper.setSubject( subj );
        helper.setFrom( config.data().smtp().getFromAddressAlias() + " <" + config.data().smtp().getFromAddress() + ">" );
        helper.setText( HelperFunc.nvlt(body, ""), true );
        return helper;
    }

    private boolean isProteiRecipient(NotificationEntry entry){
        return CompanySubscription.isProteiRecipient(entry.getAddress());
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

    // -----------------------
    // UserLogin notifications
    // -----------------------

    @EventListener
    public void onUserLoginCreated(UserLoginCreatedEvent event) {
        if (event.getNotificationEntry() != null) {
            performUserLoginNotification(event, event.getNotificationEntry());
        }
    }

    private void performUserLoginNotification(UserLoginCreatedEvent event, NotificationEntry notificationEntry) {

        if (event.getLogin() == null || event.getLogin().isEmpty()) {
            log.info("Failed send notification to userLogin with login={}: login is empty", event.getLogin());
            return;
        }

        if (event.getPasswordRaw() == null || event.getPasswordRaw().isEmpty()) {
            log.info("Failed send notification to userLogin with login={}: password is empty", event.getLogin());
            return;
        }

        PreparedTemplate bodyTemplate = templateService.getUserLoginNotificationBody(event, config.data().getCrmUrl());
        if (bodyTemplate == null) {
            log.info("Failed to prepare userLogin body template");
            return;
        }

        PreparedTemplate subjectTemplate = templateService.getUserLoginNotificationSubject(config.data().getCrmUrl());
        if (subjectTemplate == null) {
            log.info("Failed to prepare userLogin subject template");
            return;
        }

        String body = bodyTemplate.getText(notificationEntry.getAddress(), notificationEntry.getLangCode(), false);
        String subject = subjectTemplate.getText(notificationEntry.getAddress(), notificationEntry.getLangCode(), false);

        try {
            MimeMessageHelper msg = new MimeMessageHelper(messageFactory.createMailMessage(), true, config.data().smtp().getDefaultCharset());
            msg.setSubject(subject);
            msg.setFrom(config.data().smtp().getFromAddress());
            msg.setText(HelperFunc.nvlt(body, ""), true);
            msg.setTo(notificationEntry.getAddress());
            mailSendChannel.send(msg.getMimeMessage());
        } catch (Exception e) {
            log.error("Failed to make MimeMessage", e);
        }
    }
}
