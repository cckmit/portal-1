package ru.protei.portal.tools.notifications;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.MimeMessageHelper;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.event.*;
import ru.protei.portal.core.mail.MailMessageFactory;
import ru.protei.portal.core.mail.MailSendChannel;
import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.struct.NotificationEntry;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.model.util.DiffCollectionResult;
import ru.protei.portal.core.service.CaseCommentService;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.core.service.EmployeeService;
import ru.protei.portal.core.service.events.CaseSubscriptionService;
import ru.protei.portal.core.service.template.PreparedTemplate;
import ru.protei.portal.core.service.template.TemplateService;
import ru.protei.portal.core.utils.LinkData;
import ru.protei.winter.core.utils.services.lock.LockService;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.toList;
import static ru.protei.portal.core.model.dict.En_CaseLink.CRM;
import static ru.protei.portal.core.model.dict.En_CaseLink.YT;
import static ru.protei.portal.core.model.helper.CollectionUtils.filterToList;
import static ru.protei.portal.core.model.helper.CollectionUtils.toList;
import static ru.protei.portal.core.model.helper.StringUtils.join;

/**
 * Created by shagaleev on 30/05/17.
 */
public class MailNotificationProcessor {

    private final static Logger log = LoggerFactory.getLogger( MailNotificationProcessor.class );

    public static final LinkData EMPTY_LINK = new LinkData( "#", "" );

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
    @EventListener
    public void onCaseChanged(AssembledCaseEvent event){
        Collection<NotificationEntry> notifiers = collectNotifiers(event);

        if(CollectionUtils.isEmpty(notifiers)) {
            log.info( "Case notification :: subscribers not found, break notification" );
            return;
        }

        log.info( "Case notification :: subscribers: {}", join( notifiers, ni->ni.getAddress(), ",") );

        try {
            Map<Boolean, List<NotificationEntry>> partitionNotifiers = notifiers.stream().collect(partitioningBy(this::isProteiRecipient));
            final boolean IS_PRIVATE_RECIPIENT = true;

            List<NotificationEntry> privateRecipients = partitionNotifiers.get( IS_PRIVATE_RECIPIENT );
            List<NotificationEntry> publicRecipients = partitionNotifiers.get( !IS_PRIVATE_RECIPIENT );

            String privateCaseUrl = getCrmCaseUrl( IS_PRIVATE_RECIPIENT );
            String publicCaseUrl = getCrmCaseUrl( !IS_PRIVATE_RECIPIENT );

            DiffCollectionResult<LinkData> privateLinks = convertToLinkData( event.getLinks(), privateCaseUrl );
            DiffCollectionResult<LinkData> publicLinks = convertToLinkData(selectPublicLinks(event.getLinks()), publicCaseUrl );

            List<CaseComment> comments =  event.getAllComments();
            Long lastMessageId = caseService.getAndIncrementEmailLastId(event.getCaseObjectId() ).orElseGet( r-> Result.ok(0L) ).getData();

            if ( isPrivateNotification(event) ) {
                List<String> recipients = getNotifiersAddresses( privateRecipients );

                performCaseObjectNotification( event, comments, privateLinks, lastMessageId, recipients, IS_PRIVATE_RECIPIENT, privateCaseUrl, privateRecipients );

            } else {
                List<String> recipients = getNotifiersAddresses(notifiers);

                performCaseObjectNotification( event, comments, privateLinks, lastMessageId, recipients, IS_PRIVATE_RECIPIENT, privateCaseUrl, privateRecipients );
                performCaseObjectNotification( event, selectPublicComments( comments ), publicLinks, lastMessageId, recipients, !IS_PRIVATE_RECIPIENT, publicCaseUrl, publicRecipients );
            }
        } catch (Exception e) {
            log.error( "Can't sent mail notification with case id = {}. Exception: ", event.getCaseObjectId(), e );
        }
    }

    private DiffCollectionResult<CaseLink> selectPublicLinks( DiffCollectionResult<CaseLink> mergeLinks ) {
        DiffCollectionResult result = new DiffCollectionResult();
        if (mergeLinks == null) return result;
        result.putAddedEntries( filterToList( mergeLinks.getAddedEntries(), this::isPublic ) );
        result.putRemovedEntries( filterToList( mergeLinks.getRemovedEntries(), this::isPublic ) );
        result.putSameEntries( filterToList( mergeLinks.getSameEntries(), this::isPublic ) );
        return result;
    }

    private DiffCollectionResult<LinkData> convertToLinkData( DiffCollectionResult<CaseLink> mergeLinks, String crmCaseUrl ) {
        DiffCollectionResult<LinkData> result = new DiffCollectionResult<LinkData>();
        if (mergeLinks == null) return result;
        result.putAddedEntries( toList( mergeLinks.getAddedEntries(), link -> makeLinkData( link, crmCaseUrl ) ) );
        result.putRemovedEntries( toList( mergeLinks.getRemovedEntries(), link -> makeLinkData( link, crmCaseUrl ) ) );
        result.putSameEntries( toList( mergeLinks.getSameEntries(), link -> makeLinkData( link, crmCaseUrl ) ) );
        return result;
    }

    private LinkData makeLinkData( CaseLink link, String crmCaseUrl ) {
        En_CaseLink type = link.getType();

        if (YT.equals( type )) {
            String remoteId = link.getRemoteId();
            return new LinkData( config.data().getCaseLinkConfig().getLinkYouTrack().replace("%id%", remoteId), remoteId );
        }
        if (CRM.equals( type )) {
            CaseInfo caseInfo = link.getCaseInfo();
            if (caseInfo == null) return EMPTY_LINK;
            Long crmNumber = link.getCaseInfo().getCaseNumber();
            if (crmNumber == null) return EMPTY_LINK;
            return new LinkData( String.format( crmCaseUrl, crmNumber ), String.valueOf( crmNumber ) );
        }

        return EMPTY_LINK;
    }

    private boolean isPublic( CaseLink caseLink){
        if(caseLink.isPrivate()) {
            return false;
        }
        if(!CRM.equals( caseLink.getType() )) {
            return false;
        }
        return true;
    }

    private String getCrmCaseUrl( boolean isProteiRecipient ) {
        String baseUrl = "";
        if (isProteiRecipient) {
            baseUrl = config.data().getMailNotificationConfig().getCrmUrlInternal();
        } else {
            baseUrl = config.data().getMailNotificationConfig().getCrmUrlExternal();
        }
        return baseUrl + config.data().getMailNotificationConfig().getCrmCaseUrl();
    }

    private boolean isPrivateNotification(AssembledCaseEvent event) {
        return event.getCaseObject().isPrivateCase()
                || isPrivateSend(event)
                || config.data().smtp().isBlockExternalRecipients();
    }

    private List<CaseComment> selectPublicComments(List<CaseComment> comments) {
        return comments.stream()
                .filter(comment -> !comment.isPrivateComment())
                .collect( Collectors.toList());
    }

    private MimeMessageHeadersFacade makeHeaders( Long caseNumber, Long lastMessageId, int recipientAddressHashCode ) {
        return new MimeMessageHeadersFacade()
                .withMessageId(makeCaseObjectMessageId(caseNumber, lastMessageId + 1, recipientAddressHashCode))
                .withInReplyTo(makeCaseObjectMessageId(caseNumber, lastMessageId, recipientAddressHashCode ))
                .withReferences( LongStream.iterate(lastMessageId, id -> id - 1).limit(lastMessageId + 1)
                        .mapToObj(id -> makeCaseObjectMessageId(caseNumber, id, recipientAddressHashCode ))
                        .collect( Collectors.toList())
                );
    }

    private void performCaseObjectNotification(
            AssembledCaseEvent event, List<CaseComment> comments, DiffCollectionResult<LinkData> linksToTasks, Long lastMessageId, List<String> recipients,
            boolean isProteiRecipients, String crmCaseUrl, Collection<NotificationEntry> notifiers
    ) {

        if (CollectionUtils.isEmpty(notifiers)) {
            return;
        }

        CaseObject caseObject = event.getCaseObject();

        PreparedTemplate bodyTemplate = templateService.getCrmEmailNotificationBody(event, comments, linksToTasks, crmCaseUrl, recipients);
        if (bodyTemplate == null) {
            log.error("Failed to prepare body template for caseId={}", caseObject.getId());
            return;
        }

        PreparedTemplate subjectTemplate = templateService.getCrmEmailNotificationSubject(event, event.getInitiator());
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


    private Collection<NotificationEntry> collectNotifiers(AssembledCaseEvent event) {
        Set<NotificationEntry> defaultNotifiers = subscriptionService.subscribers(event.getCaseMeta());
        return formNotifiers(defaultNotifiers,
                event.getInitiator(),
                event.getCreator(),
                event.getManager());
    }

    /**
     * Form case notifiers with initiator, creator and manager
     */
    private Collection<NotificationEntry> formNotifiers(Set<NotificationEntry> allNotifiers, Person initiator, Person creator, Person manager){

        NotificationEntry initiatorEmail = fetchNotificationEntryFromPerson(initiator);
        if (initiatorEmail != null) {
            allNotifiers.add(initiatorEmail);
        }

        NotificationEntry creatorEmail = fetchNotificationEntryFromPerson(creator);
        if (creatorEmail != null) {
            allNotifiers.add(creatorEmail);
        }

        NotificationEntry managerEmail = fetchNotificationEntryFromPerson(manager);
        if (managerEmail != null) {
            allNotifiers.add(managerEmail);
        }

        return allNotifiers;
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
            sendMail(notificationEntry.getAddress(), subject, body);
        } catch (Exception e) {
            log.error("Failed to make MimeMessage", e);
        }
    }

    // -----------------------
    // EmployeeRegistration notifications
    // -----------------------

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

        String urlTemplate = getEmployeeRegistrationUrl();

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

        sendMailToRecipients(notifiers, bodyTemplate, subjectTemplate, true);
    }

    @EventListener
    public void onEmployeeRegistrationEmployeeFeedbackEvent( EmployeeRegistrationEmployeeFeedbackEvent event) {
        log.info( "onEmployeeRegistrationEmployeeFeedbackEvent(): {}", event );

        try {
            String subject = templateService.getEmployeeRegistrationEmployeeFeedbackEmailNotificationSubject();

            String body = templateService.getEmployeeRegistrationEmployeeFeedbackEmailNotificationBody(
                    event.getPerson().getDisplayName()
            );

            sendMail( new PlainContactInfoFacade( event.getPerson().getContactInfo() ).getEmail(), subject, body );
        } catch (Exception e) {
            log.warn( "Failed to sent employee feedback notification: {}", event.getPerson().getDisplayName(), e );
        }
    }

    @EventListener
    public void onEmployeeRegistrationDevelopmentAgendaEvent( EmployeeRegistrationDevelopmentAgendaEvent event) {
        log.info( "onEmployeeRegistrationDevelopmentAgendaEvent(): {}", event );

        try {
            String subject = templateService.getEmployeeRegistrationDevelopmentAgendaEmailNotificationSubject();

            String body = templateService.getEmployeeRegistrationDevelopmentAgendaEmailNotificationBody(
                    event.getPerson().getDisplayName()
            );

            sendMail( new PlainContactInfoFacade( event.getPerson().getContactInfo() ).getEmail(), subject, body );
        } catch (Exception e) {
            log.warn( "Failed to sent development agenda notification: {}", event.getPerson().getDisplayName(), e );
        }
    }

    @EventListener
    public void onEmployeeRegistrationProbationEvent( EmployeeRegistrationProbationHeadOfDepartmentEvent event) {
        log.info( "onEmployeeRegistrationProbationEvent(): {}", event );

        String employeeFullName = event.getEmployeeFullName();
        Long employeeId = event.getEmployeeId();
        Person headOfDepartment = event.getHeadOfDepartment();

        try {
            String body = templateService.getEmployeeRegistrationProbationHeadOfDepartmentEmailNotificationBody(
                    employeeId, employeeFullName,
                    getEmployeeRegistrationUrl(), headOfDepartment.getDisplayName() );

            String subject = templateService.getEmployeeRegistrationProbationHeadOfDepartmentEmailNotificationSubject(
                    employeeFullName );

            sendMail( new PlainContactInfoFacade( headOfDepartment.getContactInfo() ).getEmail(), subject, body );

        } catch (Exception e) {
            log.warn( "Failed to sent employee probation notification: employeeId={}", employeeId, e );
        }
    }

    @EventListener
    public void onEmployeeRegistrationProbationCuratorsEvent( EmployeeRegistrationProbationCuratorsEvent event) {
        log.info( "onEmployeeRegistrationProbationCuratorsEvent(): {}", event );

        String employeeFullName = event.getEmployeeFullName();
        Long employeeId = event.getEmployeeId();
        Person curator = event.getCurator();

        try {
            String body = templateService.getEmployeeRegistrationProbationCuratorsEmailNotificationBody(
                    employeeId, employeeFullName,
                    getEmployeeRegistrationUrl(), curator.getDisplayName() );

            String subject = templateService.getEmployeeRegistrationProbationCuratorsEmailNotificationSubject(
                    employeeFullName );

            sendMail( new PlainContactInfoFacade( curator.getContactInfo() ).getEmail(), subject, body );

        } catch (Exception e) {
            log.warn( "Failed to sent employee probation (for curator) notification: employeeId={}", employeeId, e );
        }
    }

    // -----------------------
    // Contract notifications
    // -----------------------

    @EventListener
    public void onContractDateOneDayRemainingEvent(ContractDateOneDayRemainingEvent event) {
        Contract contract = event.getContract();
        ContractDate contractDate = event.getContractDate();
        Set<NotificationEntry> notifiers = event.getNotificationEntrySet();
        if (contract == null || contractDate == null || notifiers == null) {
            log.error("Failed to send contract notification: incomplete data provided: " +
                    "contract={}, contractDate={}, notifiers={}", contract, contractDate, notifiers);
            return;
        }

        if (CollectionUtils.isEmpty(notifiers)) {
            log.info("Failed to send contract notification: empty notifiers set: contract={}, contractDate={}", contract, contractDate);
            return;
        }

        List<String> recipients = getNotifiersAddresses(notifiers);

        performContractDateOneDayRemainingNotification(
                contract,
                contractDate,
                config.data().getMailNotificationConfig().getCrmUrlInternal() +
                        config.data().getMailNotificationConfig().getContractUrl(),
                recipients,
                notifiers.stream()
                        .filter(this::isProteiRecipient)
                        .collect(Collectors.toSet())
        );

        performContractDateOneDayRemainingNotification(
                contract,
                contractDate,
                config.data().getMailNotificationConfig().getCrmUrlExternal() +
                        config.data().getMailNotificationConfig().getContractUrl(),
                recipients,
                notifiers.stream()
                        .filter(this::isNotProteiRecipient)
                        .collect(Collectors.toSet())
        );
    }

    private void performContractDateOneDayRemainingNotification(Contract contract, ContractDate contractDate,
                                        String urlTemplate, List<String> recipients, Set<NotificationEntry> notifiers) {

        if (CollectionUtils.isEmpty(notifiers)) {
            return;
        }

        PreparedTemplate bodyTemplate = templateService.getContractRemainingOneDayNotificationBody(contract, contractDate, urlTemplate, recipients);
        if (bodyTemplate == null) {
            log.error("Failed to prepare body template for contractId={} and contractDateId={}", contract.getId(), contractDate.getId());
            return;
        }

        PreparedTemplate subjectTemplate = templateService.getContractRemainingOneDayNotificationSubject(contract, contractDate);
        if (subjectTemplate == null) {
            log.error("Failed to prepare subject template for contractId={} and contractDateId={}", contract.getId(), contractDate.getId());
            return;
        }

        sendMailToRecipients(notifiers, bodyTemplate, subjectTemplate, true);
    }

    // -----------------------
    // Document notifications
    // -----------------------

    @EventListener
    public void onDocumentMemberAddedEvent(DocumentMemberAddedEvent event) {
        Document document = event.getDocument();
        List<Person> personList = event.getPersonList();
        if (document == null || CollectionUtils.isEmpty(personList)) {
            log.error("Failed to send document member added notification: incomplete data provided: " +
                    "document={}, personList={}", document, personList);
            return;
        }
        List<NotificationEntry> recipients = personList.stream()
                .map(this::fetchNotificationEntryFromPerson)
                .filter(Objects::nonNull)
                .distinct()
                .collect(toList());

        String url = String.format(getDocumentPreviewUrl(), document.getId());

        PreparedTemplate bodyTemplate = templateService.getDocumentMemberAddedBody(document.getName(), url);
        if (bodyTemplate == null) {
            log.error("Failed to prepare body template for document added event | document.id={}, person.ids={}",
                    document.getId(), personList.stream().map(Person::getId).collect(toList()));
            return;
        }

        PreparedTemplate subjectTemplate = templateService.getDocumentMemberAddedSubject(document.getName());
        if (subjectTemplate == null) {
            log.error("Failed to prepare subject template for document added event | document.id={}, person.ids={}",
                    document.getId(), personList.stream().map(Person::getId).collect(toList()));
            return;
        }

        sendMailToRecipients(recipients, bodyTemplate, subjectTemplate, true);
    }

    @EventListener
    public void onDocumentDocFileUpdatedByMemberEvent(DocumentDocFileUpdatedByMemberEvent event) {
        Person initiator = event.getInitiator();
        Document document = event.getDocument();
        List<Person> personList = event.getPersonList();
        String comment = event.getComment();
        if (initiator == null || document == null || CollectionUtils.isEmpty(personList)) {
            log.error("Failed to send document doc file updated by member notification: incomplete data provided: " +
                    "document={}, personList={}, comment={}, initiator={}", document, personList, comment, initiator);
            return;
        }
        List<NotificationEntry> recipients = personList.stream()
                .map(this::fetchNotificationEntryFromPerson)
                .filter(Objects::nonNull)
                .distinct()
                .collect(toList());

        PreparedTemplate bodyTemplate = templateService.getDocumentDocFileUpdatedByMemberBody(document.getName(), initiator.getDisplayShortName(), comment);
        if (bodyTemplate == null) {
            log.error("Failed to prepare body template for document doc file updated by member | document.id={}, person.ids={}, comment={}",
                    document.getId(), personList.stream().map(Person::getId).collect(toList()), comment);
            return;
        }

        PreparedTemplate subjectTemplate = templateService.getDocumentDocFileUpdatedByMemberSubject(document.getName());
        if (subjectTemplate == null) {
            log.error("Failed to prepare subject template for document doc file updated by member | document.id={}, person.ids={}, comment={}",
                    document.getId(), personList.stream().map(Person::getId).collect(toList()), comment);
            return;
        }

        sendMailToRecipients(recipients, bodyTemplate, subjectTemplate, true);
    }

    @EventListener
    public void onMailReportEvent(MailReportEvent event) {
        Report report = event.getReport();

        PreparedTemplate bodyTemplate = templateService.getMailReportBody(report);
        if (bodyTemplate == null) {
            log.error("Failed to prepare body template for reporId={}", report.getId());
            return;
        }

        PreparedTemplate subjectTemplate = templateService.getMailReportSubject(report);
        if (subjectTemplate == null) {
            log.error("Failed to prepare subject template for reporId={}", report.getId());
            return;
        }

        if ( event.getContent() != null) {
            sendMailToRecipientWithAttachment(
                    fetchNotificationEntryFromPerson(report.getCreator()),
                    bodyTemplate, subjectTemplate,
                    true,
                    event.getContent(), report.getName() + ".xls");
        } else {
            sendMailToRecipients(Collections.singletonList(fetchNotificationEntryFromPerson(report.getCreator())),
                    bodyTemplate, subjectTemplate,
                    true);
        }

    }

    // -----
    // Utils
    // -----

    private void sendMailToRecipients(Collection<NotificationEntry> recipients, PreparedTemplate bodyTemplate, PreparedTemplate subjectTemplate, boolean isShowPrivacy) {
        recipients.forEach(entry -> {
            try {
                String body = bodyTemplate.getText(entry.getAddress(), entry.getLangCode(), isShowPrivacy);
                String subject = subjectTemplate.getText(entry.getAddress(), entry.getLangCode(), isShowPrivacy);
                sendMail(entry.getAddress(), subject, body);
            } catch (Exception e) {
                log.error("Failed to make MimeMessage", e);
            }
        });
    }

    private void sendMailToRecipientWithAttachment(NotificationEntry recipients, PreparedTemplate bodyTemplate,
                                                   PreparedTemplate subjectTemplate, boolean isShowPrivacy,
                                                   InputStream content, String filename) {
            try {
                String body = bodyTemplate.getText(recipients.getAddress(), recipients.getLangCode(), isShowPrivacy);
                String subject = subjectTemplate.getText(recipients.getAddress(), recipients.getLangCode(), isShowPrivacy);
                MimeMessageHelper msg = new MimeMessageHelper(messageFactory.createMailMessage(), true, config.data().smtp().getDefaultCharset());
                msg.setSubject(subject);
                msg.setFrom(getFromAddress());
                msg.setText(HelperFunc.nvlt(body, ""), true);
                msg.setTo(recipients.getAddress());
                msg.addAttachment(filename, new ByteArrayResource(IOUtils.toByteArray(content)));
                mailSendChannel.send(msg.getMimeMessage());
            } catch (Exception e) {
                log.error("Failed to make MimeMessage", e);
            }
    }

    private void sendMail(String address, String subject, String body) throws MessagingException {
        MimeMessageHelper msg = new MimeMessageHelper(messageFactory.createMailMessage(), true, config.data().smtp().getDefaultCharset());
        msg.setSubject(subject);
        msg.setFrom(getFromAddress());
        msg.setText(HelperFunc.nvlt(body, ""), true);
        msg.setTo(address);
        mailSendChannel.send(msg.getMimeMessage());
    }

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
        return notifiers.stream().map(NotificationEntry::getAddress).collect( Collectors.toList());
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

    private String getEmployeeRegistrationUrl() {
        return config.data().getMailNotificationConfig().getCrmUrlInternal() +
                config.data().getMailNotificationConfig().getCrmEmployeeRegistrationUrl();
    }

    private String getDocumentPreviewUrl() {
        return config.data().getMailNotificationConfig().getCrmUrlInternal() +
                config.data().getMailNotificationConfig().getCrmDocumentPreviewUrl();
    }

    private boolean isPrivateSend(AssembledCaseEvent assembledCaseEvent) {
        if (assembledCaseEvent.isCreateEvent()) {
            return false;
        }

        if (assembledCaseEvent.isPublicCommentsChanged()) {
            return false;
        }

        if (publicChangesExistWithoutComments(assembledCaseEvent)) {
            return false;
        }

        return true;
    }

    private boolean publicChangesExistWithoutComments(AssembledCaseEvent assembledCaseEvent) {
        return  assembledCaseEvent.isCaseImportanceChanged()
                || assembledCaseEvent.isCaseStateChanged()
                || assembledCaseEvent.isInitiatorChanged()
                || assembledCaseEvent.isInitiatorCompanyChanged()
                || assembledCaseEvent.isManagerChanged()
                || assembledCaseEvent.getName().hasDifferences()
                || assembledCaseEvent.getInfo().hasDifferences()
                || assembledCaseEvent.isProductChanged()
                || assembledCaseEvent.isPublicLinksChanged();
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
