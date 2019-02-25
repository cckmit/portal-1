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
import ru.protei.portal.core.event.EmployeeRegistrationProbationNotificaionEvent;
import ru.protei.portal.core.event.UserLoginUpdateEvent;
import ru.protei.portal.core.mail.MailMessageFactory;
import ru.protei.portal.core.mail.MailSendChannel;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.struct.ContactInfo;
import ru.protei.portal.core.model.struct.NotificationEntry;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.service.*;
import ru.protei.portal.core.service.template.PreparedTemplate;
import ru.protei.winter.core.utils.services.lock.LockService;

import javax.mail.MessagingException;
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
    CaseCommentService caseCommentService;

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
        CoreResponse<List<CaseComment>> comments = caseCommentService.getCaseCommentList(
                null,
                En_CaseType.CRM_SUPPORT,
                event.getCaseComment() == null ?
                        new CaseCommentQuery(caseObject.getId()) :
                        new CaseCommentQuery(caseObject.getId(), event.getCaseComment().getCreated())
        );
        if (comments.isError()) {
            log.error("Failed to retrieve comments for caseId={}", caseObject.getId());
            return;
        }

        List<String> recipients = getNotifiersAddresses(notifiers);

        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            log.warn("Semaphore interrupted while waiting for green light, so we are skipping notification with case id = {}. Exception = {}", event.getCaseObject().getId(), e.getMessage());
            return;
        }

        try {
            Long lastMessageId = getEmailLastId(caseObject.getId());

            performCaseObjectNotification(
                    event,
                    comments.getData(),
                    lastMessageId,
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
                    lastMessageId,
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

    private MimeMessageHeadersFacade makeHeaders( Long caseNumber, Long lastMessageId, int recipientAddressHashCode ) {
        return new MimeMessageHeadersFacade()
                .withMessageId(makeCaseObjectMessageId(caseNumber, lastMessageId + 1, recipientAddressHashCode))
                .withInReplyTo(makeCaseObjectMessageId(caseNumber, lastMessageId, recipientAddressHashCode ))
                .withReferences( LongStream.iterate(lastMessageId, id -> id - 1).limit(lastMessageId + 1)
                        .mapToObj(id -> makeCaseObjectMessageId(caseNumber, id, recipientAddressHashCode ))
                        .collect(toList())
                );
    }

    private void performCaseObjectNotification(
            AssembledCaseEvent event, List<CaseComment> comments, Long lastMessageId, List<String> recipients,
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

            MimeMessageHeadersFacade headers =  makeHeaders( caseObject.getCaseNumber(), lastMessageId, entry.hashCode() );

            String body = bodyTemplate.getText(entry.getAddress(), entry.getLangCode(), isProteiRecipients);
            String subject = subjectTemplate.getText(entry.getAddress(), entry.getLangCode(), isProteiRecipients);
            try {
                MimeMessage mimeMessage = messageFactory.createMailMessage(headers.getMessageId(), headers.getInReplyTo(), headers.getReferences());
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, config.data().smtp().getDefaultCharset());
                helper.setSubject(subject);
                helper.setFrom(getFromAddress());
                helper.setText(HelperFunc.nvlt(body, ""), true);
                helper.setTo(entry.getAddress());
                log.info("Send message to {} with headers {}", entry.getAddress(), headers );
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

        NotificationEntry initiatorEmail = fetchNotificationEntryFromPerson(initiator);
        if (initiatorEmail != null) {
            allNotifiers.add(initiatorEmail);
        }

        NotificationEntry creatorEmail = fetchNotificationEntryFromPerson(initiator);
        if (creatorEmail != null) {
            allNotifiers.add(creatorEmail);
        }

        NotificationEntry managerEmail = fetchNotificationEntryFromPerson(initiator);
        if (managerEmail != null) {
            allNotifiers.add(managerEmail);
        }

        return isPrivateCase || config.data().smtp().isBlockExternalRecipients() ?
                allNotifiers.stream().filter(this::isProteiRecipient).collect(Collectors.toList()):
                allNotifiers;
    }

    private String makeCaseObjectMessageId( Long caseNumber, Long id, int recipientAddressHashCode ) {
        return "case." + caseNumber + "." + id + "-" + recipientAddressHashCode;
    }

    // -----------------------
    // UserLogin notifications
    // -----------------------

    @EventListener
    public void onUserLoginCreated(UserLoginUpdateEvent event) {
        if (event.getNotificationEntry() != null) {
            performUserLoginNotification(event, event.getNotificationEntry());
        }
    }

    private void performUserLoginNotification(UserLoginUpdateEvent event, NotificationEntry notificationEntry) {

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
        EmployeeRegistration employeeRegistration = event.getEmployeeRegistration();
        if (employeeRegistration == null) {
            log.info("Failed to send employee registration notification: employee registration is null");
            return;
        }

        Set<NotificationEntry> notifiers = subscriptionService.subscribers(event);
        if (CollectionUtils.isEmpty(notifiers))
            return;

        List<String> recipients = getNotifiersAddresses(notifiers);

        String urlTemplate = config.data().getMailNotificationConfig().getCrmUrlInternal() +
                config.data().getMailNotificationConfig().getCrmEmployeeRegistrationUrl();

        PreparedTemplate bodyTemplate = templateService.getEmployeeRegistrationEmailNotificationBody(employeeRegistration, urlTemplate, recipients);
        if (bodyTemplate == null) {
            log.error("Failed to prepare body template for employeeRegistrationId={}", employeeRegistration.getId());
            return;
        }

        PreparedTemplate subjectTemplate = templateService.getEmployeeRegistrationEmailNotificationSubject(employeeRegistration);
        if (subjectTemplate == null) {
            log.error("Failed to prepare subject template for employeeRegistrationId={}", employeeRegistration.getId());
            return;
        }

        notifiers.forEach(entry -> {
            String body = bodyTemplate.getText(entry.getAddress(), entry.getLangCode(), true);
            String subject = subjectTemplate.getText(entry.getAddress(), entry.getLangCode(), true);

            try {
                MimeMessageHelper msg = new MimeMessageHelper(messageFactory.createMailMessage(), true, config.data().smtp().getDefaultCharset());
                msg.setSubject(subject);
                msg.setFrom(getFromAddress());
                msg.setText(HelperFunc.nvlt(body, ""), true);
                msg.setTo(entry.getAddress());
                mailSendChannel.send(msg.getMimeMessage());
            } catch (Exception e) {
                log.error("Failed to make MimeMessage", e);
            }
        });
    }


    @EventListener
    public void onEmployeeRegistrationProbationEvent( EmployeeRegistrationProbationNotificaionEvent event) {
        log.info( "onEmployeeRegistrationEvent(): {}", event );

        PreparedTemplate bodyTemplate =  templateService.getEmployeeRegistrationProbationEmailNotificationBody(employeeRegistration, urlTemplate, recipients);
        PreparedTemplate subjectTemplate = templateService.getEmployeeRegistrationProbationEmailNotificationSubject(employeeRegistration, urlTemplate, recipients);

        try {
            for (EmployeeRegistration employeeRegistration : event.getProbationExpires()) {

                ContactInfo contactInfo = event.getContactInfoMap().get( employeeRegistration.getHeadOfDepartmentId() );
                if(contactInfo==null) continue;
//
                sentMail( new PlainContactInfoFacade(contactInfo).getEmail(), subject,  body );
            }
        } catch (Exception e) {
            log.error("Failed to sent employee probation notification: ", e);
        }

    }

    private void sentMail( String address, String subject, String body) throws MessagingException {
        MimeMessageHelper msg = new MimeMessageHelper(messageFactory.createMailMessage(), true, config.data().smtp().getDefaultCharset());
        msg.setSubject(subject);
        msg.setFrom(getFromAddress());
        msg.setText(HelperFunc.nvlt(body, ""), true);
        msg.setTo( address);
        mailSendChannel.send(msg.getMimeMessage());
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

    private List<String> getNotifiersAddresses(Collection<NotificationEntry> notifiers) {
        return notifiers.stream().map(NotificationEntry::getAddress).collect(toList());
    }

    private Long getEmailLastId(Long caseId) {
        CoreResponse<Long> lastMessageIdResponse = caseService.getEmailLastId(caseId);
        return lastMessageIdResponse.isOk() ? lastMessageIdResponse.getData() : 0L;
    }

    private NotificationEntry fetchNotificationEntryFromPerson(Person person) {
        if (person == null || person.isFired() || person.isDeleted()) {
            return null;
        }
        String email = new PlainContactInfoFacade(person.getContactInfo()).getEmail();
        if (StringUtils.isBlank(email)) {
            return null;
        }
        String locale = person.getLocale() == null ? "ru" : person.getLocale();
        return NotificationEntry.email(email, locale);
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

        @Override
        public String toString() {
            return "MimeMessageHeadersFacade{" +
                    "messageId='" + messageId + '\'' +
                    ", inReplyTo='" + inReplyTo + '\'' +
                    ", references=" + references +
                    '}';
        }
    }
}
