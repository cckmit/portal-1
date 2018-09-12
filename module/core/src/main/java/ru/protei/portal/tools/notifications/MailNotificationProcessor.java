package ru.protei.portal.tools.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.MimeMessageHelper;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.AssembledCaseEvent;
import ru.protei.portal.core.event.EmployeeRegistrationEvent;
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
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.core.service.CaseSubscriptionService;
import ru.protei.portal.core.service.EmployeeService;
import ru.protei.portal.core.service.TemplateService;
import ru.protei.portal.core.service.template.PreparedTemplate;
import ru.protei.winter.core.utils.services.lock.LockService;

import javax.mail.internet.MimeMessage;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

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
    LockService lockService;

    @Autowired
    PortalConfig config;

    // ------------------------
    // CaseObject notifications
    // ------------------------

    private final static Semaphore semaphore = new Semaphore(1);

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

        if (notifiers == null || notifiers.size() == 0) {
            return;
        }

        CaseObject caseObject = event.getCaseObject();
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

        List<String> recipients = notifiers.stream().map(NotificationEntry::getAddress).collect(toList());

        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            log.warn("Semaphore interrupted while waiting for green light, so we are skipping notification with case id = {}. Exception = {}", event.getCaseObject().getId(), e.getMessage());
            return;
        }

        try {
            CoreResponse<Long> lastMessageIdResponse = caseService.getEmailLastId(caseObject.getId());
            Long lastMessageId = lastMessageIdResponse.isOk() ? lastMessageIdResponse.getData() : 0L;
            MimeMessageHeadersFacade headersFacade = new MimeMessageHeadersFacade()
                    .withMessageId(makeCaseObjectMessageId(caseObject, lastMessageId + 1))
                    .withInReplyTo(makeCaseObjectMessageId(caseObject, lastMessageId))
                    .withReferences(LongStream.iterate(lastMessageId, id -> id - 1).limit(lastMessageId + 1)
                            .mapToObj(id -> makeCaseObjectMessageId(caseObject, id))
                            .collect(toList())
                    );

            performCaseObjectNotification(
                    event,
                    comments.getData(),
                    headersFacade,
                    recipients,
                    true,
                    config.data().getMailNotificationConfig().getCrmUrlInternal() + config.data().getMailNotificationConfig().getCrmCaseUrl(),
                    notifiers.stream()
                            .filter(this::isProteiRecipient)
                            .collect(toList())
            );

            performCaseObjectNotification(
                    event,
                    comments.getData(),
                    headersFacade,
                    recipients,
                    false,
                    config.data().getMailNotificationConfig().getCrmUrlExternal() + config.data().getMailNotificationConfig().getCrmCaseUrl(),
                    notifiers.stream()
                            .filter(this::isNotProteiRecipient)
                            .collect(toList())
            );

            caseObject.setEmailLastId(lastMessageId + 1);
            caseService.updateEmailLastId(caseObject);

        } finally {
            semaphore.release();
        }
    }

    private void performCaseObjectNotification(
            AssembledCaseEvent event, List<CaseComment> comments, MimeMessageHeadersFacade headers, List<String> recipients,
            boolean isProteiRecipients, String crmCaseUrl, Collection<NotificationEntry> notifiers
    ) {

        if (notifiers == null || notifiers.size() == 0) {
            return;
        }

        CaseObject caseObject = event.getCaseObject();

        PreparedTemplate bodyTemplate = templateService.getCrmEmailNotificationBody(event, comments, crmCaseUrl, recipients);
        if (bodyTemplate == null) {
            log.error("Failed to prepare body template for caseId={}", caseObject.getId());
            return;
        }

        PreparedTemplate subjectTemplate = templateService.getCrmEmailNotificationSubject(caseObject, event.getInitiator());
        if (subjectTemplate == null) {
            log.error("Failed to prepare subject template for caseId={}", caseObject.getId());
            return;
        }

        notifiers.forEach((entry) -> {
            String body = bodyTemplate.getText(entry.getAddress(), entry.getLangCode(), isProteiRecipients);
            String subject = subjectTemplate.getText(entry.getAddress(), entry.getLangCode(), isProteiRecipients);
            try {
                MimeMessage mimeMessage = messageFactory.createMailMessage(headers.getMessageId(), headers.getInReplyTo(), headers.getReferences());
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, config.data().smtp().getDefaultCharset());
                helper.setSubject(subject);
                helper.setFrom(getFromAddress());
                helper.setText(HelperFunc.nvlt(body, ""), true);
                helper.setTo(entry.getAddress());
                mailSendChannel.send(helper.getMimeMessage());
            } catch (Exception e) {
                log.error("Failed to make MimeMessage", e);
            }
        });
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

        return isPrivateCase || config.data().smtp().isBlockExternalRecipients() ?
                allNotifiers.stream().filter(this::isProteiRecipient).collect(Collectors.toList()):
                allNotifiers;
    }

    private String makeCaseObjectMessageId(CaseObject caseObject, Long id) {
        return "case." + String.valueOf(caseObject.getCaseNumber()) + "." + String.valueOf(id);
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

        String crmUrl = isProteiRecipient(notificationEntry) ?
                config.data().getMailNotificationConfig().getCrmUrlInternal() :
                config.data().getMailNotificationConfig().getCrmUrlExternal();

        PreparedTemplate bodyTemplate = templateService.getUserLoginNotificationBody(event, crmUrl);
        if (bodyTemplate == null) {
            log.info("Failed to prepare userLogin body template for login={}", event.getLogin());
            return;
        }

        PreparedTemplate subjectTemplate = templateService.getUserLoginNotificationSubject(crmUrl);
        if (subjectTemplate == null) {
            log.info("Failed to prepare userLogin subject template for login={}", event.getLogin());
            return;
        }

        String body = bodyTemplate.getText(notificationEntry.getAddress(), notificationEntry.getLangCode(), false);
        String subject = subjectTemplate.getText(notificationEntry.getAddress(), notificationEntry.getLangCode(), false);

        try {
            MimeMessageHelper msg = new MimeMessageHelper(messageFactory.createMailMessage(), true, config.data().smtp().getDefaultCharset());
            msg.setSubject(subject);
            msg.setFrom(getFromAddress());
            msg.setText(HelperFunc.nvlt(body, ""), true);
            msg.setTo(notificationEntry.getAddress());
            mailSendChannel.send(msg.getMimeMessage());
        } catch (Exception e) {
            log.error("Failed to make MimeMessage", e);
        }
    }

    @EventListener
    public void onEmployeeRegistrationEvent(EmployeeRegistrationEvent event) {
        if (event.getEmployeeRegistration() == null) {
            log.info("Failed to send employee registration notification: employee registration is null");
            return;
        }

        System.out.println("sending emp reg notification");
    }


    // -----
    // Utils
    // -----

    private boolean isProteiRecipient(NotificationEntry entry) {
        return CompanySubscription.isProteiRecipient(entry.getAddress());
    }

    private boolean isNotProteiRecipient(NotificationEntry entry) {
        return !isProteiRecipient(entry);
    }

    private String getFromAddress() {
        return config.data().smtp().getFromAddressAlias() + " <" + config.data().smtp().getFromAddress() + ">";
    }

    private class MimeMessageHeadersFacade {

        public MimeMessageHeadersFacade() {}

        private String messageId;
        private String inReplyTo;
        private List<String> references;

        public MimeMessageHeadersFacade withMessageId(String messageId) {
            this.messageId = messageId;
            return this;
        }

        public MimeMessageHeadersFacade withInReplyTo(String inReplyTo) {
            this.inReplyTo = inReplyTo;
            return this;
        }

        public MimeMessageHeadersFacade withReferences(List<String> references) {
            this.references = references;
            return this;
        }

        public String getMessageId() {
            return messageId;
        }

        public String getInReplyTo() {
            return inReplyTo;
        }

        public List<String> getReferences() {
            return references;
        }
    }
}
