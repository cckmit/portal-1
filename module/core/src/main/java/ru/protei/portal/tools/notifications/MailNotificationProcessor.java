package ru.protei.portal.tools.notifications;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.MimeMessageHelper;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.event.*;
import ru.protei.portal.core.mail.MailMessageFactory;
import ru.protei.portal.core.mail.MailSendChannel;
import ru.protei.portal.core.model.dict.EducationEntryType;
import ru.protei.portal.core.model.dict.En_CaseLink;
import ru.protei.portal.core.model.dto.ReportCaseQuery;
import ru.protei.portal.core.model.dto.ReportDto;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.struct.Interval;
import ru.protei.portal.core.model.struct.NotificationEntry;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.model.struct.ReplaceLoginWithUsernameInfo;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.util.DiffCollectionResult;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.service.CaseCommentService;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.core.service.EmployeeService;
import ru.protei.portal.core.service.ReportService;
import ru.protei.portal.core.service.events.CaseSubscriptionService;
import ru.protei.portal.core.service.template.PreparedTemplate;
import ru.protei.portal.core.service.template.TemplateService;
import ru.protei.portal.core.utils.EnumLangUtil;
import ru.protei.portal.core.utils.LinkData;
import ru.protei.winter.core.utils.services.lock.LockService;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.time.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.partitioningBy;
import static ru.protei.portal.config.PortalConfigData.*;
import static ru.protei.portal.core.event.ReservedIpReleaseRemainingEvent.*;
import static ru.protei.portal.core.model.dict.En_CaseLink.*;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.core.model.helper.DateRangeUtils.makeInterval;
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
    ReportService reportService;
    @Autowired
    PortalConfig config;
    @Autowired
    Lang lang;

    // -----------------------
    // Delivery notifications
    // -----------------------
    @EventListener
    public void onDeliveryChanged(AssembledDeliveryEvent event){
        if (!isSendDeliveryNotification(event)) {
            return;
        }

        Set<NotificationEntry> recipients = subscriptionService.subscribers(event);
        List<ReplaceLoginWithUsernameInfo<CaseComment>> commentReplacementInfoList = caseCommentService.replaceLoginWithUsername(event.getAllComments()).getData();
        recipients.addAll(collectCommentNotifiers(event, commentReplacementInfoList, true));
        Set<String> addresses = recipients.stream().map(NotificationEntry::getAddress).collect(Collectors.toSet());

        PreparedTemplate bodyTemplate = templateService.createEMailDeliveryBody(
                event,
                commentReplacementInfoList.stream().map(ReplaceLoginWithUsernameInfo::getObject).collect(Collectors.toList()),
                addresses,
                makeCrmDeliveryUrl(config.data().getMailNotificationConfig().getCrmUrlInternal(), event.getDeliveryId()),
                new EnumLangUtil(lang)
        );

        if (bodyTemplate == null) {
            log.error("Failed to prepare body template for delivery | delivery.id={}", event.getNewDeliveryState().getId());
            return;
        }

        PreparedTemplate subjectTemplate = templateService.createEmailDeliverySubject(
                event,
                event.getInitiator(),
                new EnumLangUtil(lang));
        if (subjectTemplate == null) {
            log.error("Failed to prepare subject template for delivery | delivery.id={}", event.getNewDeliveryState().getId());
            return;
        }

        sendMailToRecipients(recipients, bodyTemplate, subjectTemplate, true, getFromPortalAddress());
    }

    // ------------------------
    // CaseObject notifications
    // ------------------------
    @EventListener
    public void onCaseChanged(AssembledCaseEvent event){
        Collection<NotificationEntry> notifiers = collectNotifiers(event);

        List<ReplaceLoginWithUsernameInfo<CaseComment>> commentReplacementInfoList = caseCommentService.replaceLoginWithUsername(event.getAllComments()).getData();

        notifiers.addAll(collectCommentNotifiers(event, commentReplacementInfoList, event.getCaseObject().isPrivateCase()));

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

            List<CaseComment> comments = toList(commentReplacementInfoList, ReplaceLoginWithUsernameInfo::getObject);
            Collection<Attachment> attachments = event.getExistingAttachments();

            Long lastMessageId = caseService.getAndIncrementEmailLastId(event.getCaseObjectId() ).orElseGet( r-> Result.ok(0L) ).getData();

            if ( isPrivateNotification(event) ) {
                List<String> recipients = getNotifiersAddresses( privateRecipients );

                performCaseObjectNotification( event, comments, attachments, privateLinks, lastMessageId, recipients, IS_PRIVATE_RECIPIENT, privateCaseUrl, privateRecipients );

            } else {
                List<String> recipients = getNotifiersAddresses(notifiers);

                performCaseObjectNotification( event, comments, attachments, privateLinks, lastMessageId, recipients, IS_PRIVATE_RECIPIENT, privateCaseUrl, privateRecipients );
                performCaseObjectNotification( event, selectPublicComments( comments ), selectPublicAttachments(attachments), publicLinks, lastMessageId, recipients, !IS_PRIVATE_RECIPIENT, publicCaseUrl, publicRecipients );
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
        if (UITS.equals( type )) {
            String remoteId = link.getRemoteId();
            return new LinkData( config.data().getCaseLinkConfig().getLinkUits().replace("%id%", remoteId), remoteId );
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
        String baseUrl = getCrmUrl( isProteiRecipient );
        return baseUrl + config.data().getMailNotificationConfig().getCrmCaseUrl();
    }

    public String getCrmUrl( boolean isProteiRecipient ) {
        return isProteiRecipient ? config.data().getMailNotificationConfig().getCrmUrlInternal()
                                 : config.data().getMailNotificationConfig().getCrmUrlExternal();
    }

    private String getCrmProjectUrl() {
        String baseUrl = config.data().getMailNotificationConfig().getCrmUrlExternal();
        return baseUrl + config.data().getMailNotificationConfig().getCrmProjectUrl();
    }

    public String getIssueCommentHelpUrl(String crmUrl) {
        return crmUrl + "?locale=%" + CrmConstants.IssueCommentHelp.LINK;
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

    private Collection<Attachment> selectPublicAttachments(Collection<Attachment> attachments) {
        return stream(attachments)
                .filter(not(Attachment::isPrivate))
                .collect(Collectors.toList());
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
            AssembledCaseEvent event, List<CaseComment> comments, Collection<Attachment> attachments, DiffCollectionResult<LinkData> linksToTasks, Long lastMessageId, List<String> recipients,
            boolean isProteiRecipients, String crmCaseUrl, Collection<NotificationEntry> notifiers
    ) {

        log.info("performCaseObjectNotification() : isProteiRecipients={}, notifiers={}", isProteiRecipients, join(notifiers, ni->ni.getAddress(), ","));

        if (CollectionUtils.isEmpty(notifiers)) {
            return;
        }

        CaseObject caseObject = event.getCaseObject();

        List<CaseComment> mailComments = stream(comments).filter(comment -> comment.getText() != null).collect(Collectors.toList());

        PreparedTemplate bodyTemplate = templateService.getCrmEmailNotificationBody(event, mailComments, attachments, linksToTasks,
                isProteiRecipients, crmCaseUrl, recipients, new EnumLangUtil(lang),
                getIssueCommentHelpUrl(getCrmUrl(isProteiRecipients)));

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
            log.info("Send message to {}", entry.getAddress());
            MimeMessageHeadersFacade headers =  makeHeaders( caseObject.getCaseNumber(), lastMessageId, entry.hashCode() );

            String body = bodyTemplate.getText(entry.getAddress(), entry.getLangCode(), isProteiRecipients);
            String subject = subjectTemplate.getText(entry.getAddress(), entry.getLangCode(), isProteiRecipients);
            try {
                MimeMessage mimeMessage = messageFactory.createMailMessage(headers.getMessageId(), headers.getInReplyTo(), headers.getReferences());
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, config.data().smtp().getDefaultCharset());
                helper.setSubject(subject);
                helper.setFrom(getFromCrmAddress());
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

        try {
            String body = bodyTemplate.getText(notificationEntry.getAddress(), notificationEntry.getLangCode(), false);

            String subject = subjectTemplate.getText(notificationEntry.getAddress(), notificationEntry.getLangCode(), false);

            sendMail(notificationEntry.getAddress(), subject, body, getFromCrmAddress());
        } catch (Exception e) {
            log.error("Failed to make MimeMessage", e);
        }
    }

    // -----------------------
    // EmployeeRegistration notifications
    // -----------------------

    @EventListener
    public void onEmployeeRegistrationEvent(AssembledEmployeeRegistrationEvent event) {
        if (!isSendEmployeeRegistrationNotification(event)) {
            return;
        }

        EmployeeRegistration employeeRegistration = event.getNewState();

        Set<NotificationEntry> notifiers = subscriptionService.subscribers(event);
        if (CollectionUtils.isEmpty(notifiers)) {
            return;
        }

        List<String> recipients = getNotifiersAddresses(notifiers);

        String urlTemplate = getEmployeeRegistrationUrl();

        PreparedTemplate bodyTemplate = templateService.getEmployeeRegistrationEmailNotificationBody(event, urlTemplate, recipients);
        if (bodyTemplate == null) {
            log.error("Failed to prepare body template for employeeRegistrationId={}", employeeRegistration.getId());
            return;
        }

        PreparedTemplate subjectTemplate = templateService.getEmployeeRegistrationEmailNotificationSubject(employeeRegistration);
        if (subjectTemplate == null) {
            log.error("Failed to prepare subject template for employeeRegistrationId={}", employeeRegistration.getId());
            return;
        }

        sendMailToRecipients(notifiers, bodyTemplate, subjectTemplate, true, getFromPortalAddress());
    }

    @EventListener
    public void onEmployeeRegistrationEmployeeFeedbackEvent( EmployeeRegistrationEmployeeFeedbackEvent event) {
        log.info( "onEmployeeRegistrationEmployeeFeedbackEvent(): {}", event );

        try {
            String subject = templateService.getEmployeeRegistrationEmployeeFeedbackEmailNotificationSubject();

            String body = templateService.getEmployeeRegistrationEmployeeFeedbackEmailNotificationBody(
                    event.getPerson().getDisplayName()
            );

            sendMail( new PlainContactInfoFacade( event.getPerson().getContactInfo() ).getEmail(), subject, body, getFromPortalAddress() );
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

            sendMail( new PlainContactInfoFacade( event.getPerson().getContactInfo() ).getEmail(), subject, body, getFromPortalAddress() );
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

            sendMail( new PlainContactInfoFacade( headOfDepartment.getContactInfo() ).getEmail(), subject, body, getFromPortalAddress() );

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

            sendMail( new PlainContactInfoFacade( curator.getContactInfo() ).getEmail(), subject, body, getFromPortalAddress() );

        } catch (Exception e) {
            log.warn( "Failed to sent employee probation (for curator) notification: employeeId={}", employeeId, e );
        }
    }

    @EventListener
    public void onEmployeeRegistrationProbationAdditionalRecipientsEvent(EmployeeRegistrationProbationAdditionalRecipientsEvent event) {
        log.info( "onEmployeeRegistrationProbationEvent(): {}", event );

        String employeeFullName = event.getEmployeeFullName();
        Long employeeId = event.getEmployeeId();
        List<String> recipients = event.getRecipients();

        recipients.forEach(entry -> {
            try {
                String body = templateService.getEmployeeRegistrationProbationHeadOfDepartmentEmailNotificationBody(
                        employeeId, employeeFullName,
                        getEmployeeRegistrationUrl(), entry );

                String subject = templateService.getEmployeeRegistrationProbationHeadOfDepartmentEmailNotificationSubject(
                        employeeFullName );

                sendMail( entry, subject, body, getFromPortalAddress() );

            } catch (Exception e) {
                log.warn( "Failed to sent employee probation notification: employeeId={}", employeeId, e );
            }
        });
    }

    // -----------------------
    // Pause time notifications
    // -----------------------

    @EventListener
    public void onProjectPauseTimeNotificationEvent( ProjectPauseTimeNotificationEvent event) {
        log.info( "onProjectPauseTimeNotificationEvent(): {}", event );

        try {
            String subject = templateService.getProjectPauseTimeNotificationSubject( event.getProjectId(), event.getProjectName() );

            String body = templateService.getProjectPauseTimeNotificationBody( event.getSubscriber().getDisplayName(),
                    event.getProjectId(), event.getProjectName(),
                    makeCrmProjectUrl( config.data().getMailNotificationConfig().getCrmUrlInternal(), event.getProjectId() ),
                    event.getPauseDate()
            );

            sendMail( new PlainContactInfoFacade( event.getSubscriber().getContactInfo() ).getEmail(), subject, body, getFromPortalAddress() );
        } catch (Exception e) {
            log.warn( "Failed to sent PauseTime notification: {}", event, e );
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

        PreparedTemplate bodyTemplate = templateService.getContractRemainingOneDayNotificationBody(contract, contractDate, urlTemplate, recipients, new EnumLangUtil(lang));
        if (bodyTemplate == null) {
            log.error("Failed to prepare body template for contractId={} and contractDateId={}", contract.getId(), contractDate.getId());
            return;
        }

        PreparedTemplate subjectTemplate = templateService.getContractRemainingOneDayNotificationSubject(contract, contractDate, new EnumLangUtil(lang));
        if (subjectTemplate == null) {
            log.error("Failed to prepare subject template for contractId={} and contractDateId={}", contract.getId(), contractDate.getId());
            return;
        }

        sendMailToRecipients(notifiers, bodyTemplate, subjectTemplate, true, getFromPortalAddress());
    }

    // -----------------------
    // Document notifications
    // -----------------------

    @EventListener
    public void onDocumentMemberAddedEvent(DocumentMemberAddedEvent event) {
        Document document = event.getDocument();
        List<Person> personList = event.getPersonList();
        if (document == null) {
            log.error("Failed to send document member added notification: document is null");
            return;
        }

        if (CollectionUtils.isEmpty(personList)) {
            return;
        }

        List<NotificationEntry> recipients = personList.stream()
                .map(this::fetchNotificationEntryFromPerson)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        String url = String.format(getDocumentPreviewUrl(), document.getId());

        PreparedTemplate bodyTemplate = templateService.getDocumentMemberAddedBody(document.getName(), url);
        if (bodyTemplate == null) {
            log.error("Failed to prepare body template for document added event | document.id={}, person.ids={}",
                    document.getId(), personList.stream().map(Person::getId).collect(Collectors.toList()));
            return;
        }

        PreparedTemplate subjectTemplate = templateService.getDocumentMemberAddedSubject(document.getName());
        if (subjectTemplate == null) {
            log.error("Failed to prepare subject template for document added event | document.id={}, person.ids={}",
                    document.getId(), personList.stream().map(Person::getId).collect(Collectors.toList()));
            return;
        }

        sendMailToRecipients(recipients, bodyTemplate, subjectTemplate, true, getFromPortalAddress());
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
                .collect(Collectors.toList());

        PreparedTemplate bodyTemplate = templateService.getDocumentDocFileUpdatedByMemberBody(document.getName(), initiator.getDisplayShortName(), comment);
        if (bodyTemplate == null) {
            log.error("Failed to prepare body template for document doc file updated by member | document.id={}, person.ids={}, comment={}",
                    document.getId(), personList.stream().map(Person::getId).collect(Collectors.toList()), comment);
            return;
        }

        PreparedTemplate subjectTemplate = templateService.getDocumentDocFileUpdatedByMemberSubject(document.getName());
        if (subjectTemplate == null) {
            log.error("Failed to prepare subject template for document doc file updated by member | document.id={}, person.ids={}, comment={}",
                    document.getId(), personList.stream().map(Person::getId).collect(Collectors.toList()), comment);
            return;
        }

        sendMailToRecipients(recipients, bodyTemplate, subjectTemplate, true, getFromPortalAddress());
    }

    @EventListener
    public void onMailReportEvent(MailReportEvent event) {
        ReportDto reportDto = event.getReport();
        Report report = reportDto.getReport();

        Interval createdInterval = reportDto instanceof ReportCaseQuery
                ? makeInterval(((ReportCaseQuery) reportDto).getQuery().getCreatedRange())
                : null;
        Interval modifiedInterval = reportDto instanceof ReportCaseQuery
                ? makeInterval(((ReportCaseQuery) reportDto).getQuery().getModifiedRange())
                : null;

        PreparedTemplate bodyTemplate = templateService.getMailReportBody(reportDto, createdInterval, modifiedInterval);
        if (bodyTemplate == null) {
            log.error("Failed to prepare body template for reportId={}", report.getId());
            return;
        }

        PreparedTemplate subjectTemplate = templateService.getMailReportSubject(reportDto);
        if (subjectTemplate == null) {
            log.error("Failed to prepare subject template for reportId={}", report.getId());
            return;
        }

        if (event.getContent() != null) {
            String filename = reportService.getReportFilename(report.getId(), reportDto).getData();
            sendMailToRecipientWithAttachment(
                    fetchNotificationEntryFromPerson(report.getCreator()),
                    bodyTemplate, subjectTemplate,
                    true,
                    event.getContent(),
                    filename,
                    getFromReportAddress()
            );
        } else {
            sendMailToRecipients(Collections.singletonList(fetchNotificationEntryFromPerson(report.getCreator())),
                    bodyTemplate, subjectTemplate,
                    true, getFromReportAddress());
        }
    }

    // -----------------------
    // Project notifications
    // -----------------------

    @EventListener
    public void onMailProjectEvent(AssembledProjectEvent event) {
        if (!isSendProjectNotification(event)) {
            return;
        }

        List<PersonProjectMemberView> team = event.getNewProjectState().getTeam();

        List<Long> recipientsIds = CollectionUtils.stream(team).map(PersonShortView::getId).collect(Collectors.toList());
        recipientsIds.add(event.getInitiatorId());
        recipientsIds.add(event.getCreator().getId());

        Set<NotificationEntry> recipients = subscriptionService.subscribers(recipientsIds);

        List<ReplaceLoginWithUsernameInfo<CaseComment>> commentReplacementInfoList = caseCommentService.replaceLoginWithUsername(event.getAllComments()).getData();
        recipients.addAll(collectCommentNotifiers(event, commentReplacementInfoList, true));

        DiffCollectionResult<LinkData> links = convertToLinkData(event.getLinks(), getCrmCaseUrl(true));

        Set<String> addresses = recipients.stream().map(NotificationEntry::getAddress).collect(Collectors.toSet());
        PreparedTemplate bodyTemplate = templateService.getMailProjectBody(
                event,
                commentReplacementInfoList.stream().map(ReplaceLoginWithUsernameInfo::getObject).collect(Collectors.toList()),
                addresses,
                links,
                makeCrmProjectUrl(config.data().getMailNotificationConfig().getCrmUrlInternal(), event.getProjectId()),
                new EnumLangUtil(lang)
        );

        if (bodyTemplate == null) {
            log.error("Failed to prepare body template for project | project.id={}", event.getNewProjectState().getId());
            return;
        }

        PreparedTemplate subjectTemplate = templateService.getMailProjectSubject(event.getNewProjectState(), event.getInitiator());
        if (subjectTemplate == null) {
            log.error("Failed to prepare subject template for project | project.id={}", event.getNewProjectState().getId());
            return;
        }

        sendMailToRecipients(recipients, bodyTemplate, subjectTemplate, true, getFromPortalAddress());
    }

    // -----------------------
    // Room reservation notifications
    // -----------------------

    @EventListener
    public void onRoomReservationNotificationEvent(RoomReservationNotificationEvent event) {
        RoomReservation roomReservation = event.getRoomReservation();
        RoomReservationNotificationEvent.Action action = event.getAction();
        List<NotificationEntry> notificationEntries = event.getNotificationEntryList();
        List<String> recipients = stream(notificationEntries)
                .map(NotificationEntry::getAddress)
                .collect(Collectors.toList());

        if (isEmpty(notificationEntries) || action == null || roomReservation == null) {
            return;
        }

        PreparedTemplate subjectTemplate = templateService.getRoomReservationNotificationSubject(roomReservation, action);
        if (subjectTemplate == null) {
            log.error("Failed to prepare subject template for room reservation notification with id={} and action={}",
                    roomReservation.getId(), action);
            return;
        }

        PreparedTemplate bodyTemplate = templateService.getRoomReservationNotificationBody(roomReservation, action, recipients);
        if (bodyTemplate == null) {
            log.error("Failed to prepare body template for room reservation notification with id={} and action={}",
                    roomReservation.getId(), action);
            return;
        }

        sendMailToRecipients(notificationEntries, bodyTemplate, subjectTemplate, true, getFromPortalAddress());
    }

    // -----------------------------
    // IP reservation notifications
    // -----------------------------

    @EventListener
    public void onSubnetNotificationEvent(SubnetNotificationEvent event) {
        Subnet subnet = event.getSubnet();
        Person initiator = event.getInitiator();
        SubnetNotificationEvent.Action action = event.getAction();
        List<NotificationEntry> notifiers = event.getNotificationEntryList();

        if (isEmpty(notifiers) || action == null || subnet == null) {
            return;
        }

        PreparedTemplate subjectTemplate = templateService.getSubnetNotificationSubject(subnet, initiator, action);
        if (subjectTemplate == null) {
            log.error("Failed to prepare subject template for subnet notification with id={} and action={}",
                    subnet.getId(), action);
            return;
        }

        List<String> recipients = getNotifiersAddresses(notifiers);

        PreparedTemplate bodyTemplate = templateService.getSubnetNotificationBody(subnet, action, recipients);
        if (bodyTemplate == null) {
            log.error("Failed to prepare body template for subnet notification with id={} and action={}",
                    subnet.getId(), action);
            return;
        }

        sendMailToRecipients(notifiers, bodyTemplate, subjectTemplate, true, getFromPortalAddress());
    }

    @EventListener
    public void onReservedIpNotificationEvent(ReservedIpNotificationEvent event) {
        List<ReservedIp> reservedIps = event.getReservedIps();
        Person initiator = event.getInitiator();
        ReservedIpNotificationEvent.Action action = event.getAction();
        List<NotificationEntry> notifiers = event.getNotificationEntryList();

        if (isEmpty(notifiers) || action == null || CollectionUtils.isEmpty(reservedIps)) {
            return;
        }

        PreparedTemplate subjectTemplate = templateService.getReservedIpNotificationSubject(reservedIps, initiator, action);
        if (subjectTemplate == null) {
            log.error("Failed to prepare subject template for reserved IP notification: action={}", action);
            return;
        }

        List<String> recipients = getNotifiersAddresses(notifiers);

        PreparedTemplate bodyTemplate = templateService.getReservedIpNotificationBody(reservedIps, recipients);
        if (bodyTemplate == null) {
            log.error("Failed to prepare body template for reserved IP notification: action={}", action);
            return;
        }

        sendMailToRecipients(notifiers, bodyTemplate, subjectTemplate, true, getFromPortalAddress());
    }

    @EventListener
    public void onReleaseIpRemainingEvent(ReservedIpReleaseRemainingEvent event) {
        List<ReservedIp> reservedIps = event.getReservedIpList();
        Date releaseDateStart = event.getReleaseDateStart();
        Date releaseDateEnd = event.getReleaseDateEnd();
        List<NotificationEntry> notifiers = event.getNotificationEntryList();
        Recipient recipientType = event.getRecipientType();

        if (isEmpty(notifiers)) {
            log.info("Failed to send release reserved IPs remaining notification: empty notifiers set");
            return;
        }

        PreparedTemplate subjectTemplate = templateService.getReservedIpRemainingNotificationSubject(releaseDateStart, releaseDateEnd);
        if (subjectTemplate == null) {
            log.error("Failed to prepare subject template for release reserved IPs notification");
            return;
        }

        if (Recipient.ADMIN.equals(recipientType)) {
            sendReleaseIpRemainingNotificationToAdmins(reservedIps, notifiers, subjectTemplate);
        } else {
            sendReleaseIpRemainingNotificationToOwners(reservedIps, notifiers, subjectTemplate);
        }
    }

    private void sendReleaseIpRemainingNotificationToAdmins(List<ReservedIp> reservedIps, List<NotificationEntry> notifiers, PreparedTemplate subjectTemplate) {
        List<String> recipients = getNotifiersAddresses(notifiers);
        PreparedTemplate bodyTemplate = templateService.getReservedIpNotificationBody(reservedIps, recipients);

        if (bodyTemplate == null) {
            log.error("Failed to prepare body template for release reserved IPs notification");
            return;
        }

        sendMailToRecipients(notifiers, bodyTemplate, subjectTemplate, true, getFromPortalAddress());
    }

    private void sendReleaseIpRemainingNotificationToOwners(List<ReservedIp> reservedIps, List<NotificationEntry> notifiers, PreparedTemplate subjectTemplate) {
        Map<Boolean, List<NotificationEntry>> partitionNotifiers = notifiers.stream().collect(partitioningBy(this::isProteiRecipient));
        final boolean IS_INTERNAL_CRM_RECIPIENT = true;

        List<NotificationEntry> internalCrmNotifiers = partitionNotifiers.get( IS_INTERNAL_CRM_RECIPIENT );
        List<NotificationEntry> externalCrmNotifiers = partitionNotifiers.get( !IS_INTERNAL_CRM_RECIPIENT );

        String internalCrmPortalUrl = getPortalUrl( IS_INTERNAL_CRM_RECIPIENT );
        String externalCrmPortalUrl = getPortalUrl( !IS_INTERNAL_CRM_RECIPIENT );

        List<String> internalCrmRecipients = getNotifiersAddresses(internalCrmNotifiers);
        List<String> externalCrmRecipients = getNotifiersAddresses(externalCrmNotifiers);

        if (!internalCrmNotifiers.isEmpty()) {
            PreparedTemplate bodyTemplate = templateService.getReservedIpNotificationWithInstructionBody(reservedIps, internalCrmRecipients, internalCrmPortalUrl);

            if (bodyTemplate == null) {
                log.error("Failed to prepare body template for release reserved IPs notification to internal crm recipients");
            } else {
                sendMailToRecipients(internalCrmNotifiers, bodyTemplate, subjectTemplate, true, getFromPortalAddress());
            }
        }

        if (!externalCrmNotifiers.isEmpty()) {
            PreparedTemplate bodyTemplate = templateService.getReservedIpNotificationWithInstructionBody(reservedIps, externalCrmRecipients, externalCrmPortalUrl);

            if (bodyTemplate == null) {
                log.error("Failed to prepare body template for release reserved IPs notification to external crm recipients");
            } else {
                sendMailToRecipients(externalCrmNotifiers, bodyTemplate, subjectTemplate, true, getFromPortalAddress());
            }
        }
    }

    @EventListener
    public void onPersonCaseFilterEvent(PersonCaseFilterEvent event) {
        NotificationEntry notifier = fetchNotificationEntryFromPerson(event.getRecipient());

        PreparedTemplate subjectTemplate = templateService.getPersonCaseFilterNotificationSubject();
        if (subjectTemplate == null) {
            log.error("Failed to prepare subject template for PersonCaseFilter notification");
            return;
        }

        PreparedTemplate bodyTemplate = templateService.getPersonCaseFilterNotificationBody( event.getIssues(), getCrmCaseUrl(true));
        if (bodyTemplate == null) {
            log.error("Failed to prepare body template for release PersonCaseFilter notification");
            return;
        }

        sendMailToRecipients(Collections.singletonList(notifier), bodyTemplate, subjectTemplate, true, getFromCrmAddress());
    }


    // -----------------------
    // Absence notifications
    // -----------------------

    @EventListener
    public void onAbsenceNotificationEvent(AbsenceNotificationEvent event) {
        Person initiator = event.getInitiator();
        PersonAbsence absence = event.getNewState();
        EventAction action = event.getAction();
        List<NotificationEntry> notificationEntries = collectNotifiers(event);
        List<String> recipients = getNotifiersAddresses(notificationEntries);

        if (isEmpty(notificationEntries) || action == null || absence == null) {
            return;
        }

        PreparedTemplate subjectTemplate = templateService.getAbsenceNotificationSubject(initiator, absence);
        if (subjectTemplate == null) {
            log.error("Failed to prepare subject template for absence notification with id={} and action={}",
                    absence.getId(), action);
            return;
        }

        PreparedTemplate bodyTemplate = templateService.getAbsenceNotificationBody(event, action, recipients);
        if (bodyTemplate == null) {
            log.error("Failed to prepare body template for absence notification with id={} and action={}",
                    absence.getId(), action);
            return;
        }

        notificationEntries.forEach(entry -> {
            try {
                String body = bodyTemplate.getText(entry.getAddress(), entry.getLangCode(), true);
                String subject = subjectTemplate.getText(entry.getAddress(), entry.getLangCode(), true);
                sendMail(entry.getAddress(), subject, body, getFromAbsenceAddress());
            } catch (Exception e) {
                log.error("Failed to make MimeMessage", e);
            }
        });
    }

    @EventListener
    public void onAbsenceReportEvent(AbsenceReportEvent event) {
        Person initiator = event.getInitiator();
        String title = event.getTitle();

        PreparedTemplate subjectTemplate = templateService.getAbsenceReportSubject(event.getTitle());
        if (subjectTemplate == null) {
            log.error("Failed to prepare subject template for absence report initiator={}", initiator);
            return;
        }

        NotificationEntry notificationEntry = fetchNotificationEntryFromPerson(initiator);

        PreparedTemplate bodyTemplate = templateService.getReportBody(title, event.getCreationDate(),
                initiator.getDisplayShortName(), Arrays.asList(notificationEntry.getAddress()));

        if (bodyTemplate == null) {
            log.error("Failed to prepare body template for absence report notification");
            return;
        }

        try {
            String subject = subjectTemplate.getText(notificationEntry.getAddress(), notificationEntry.getLangCode(), true);
            String body = bodyTemplate.getText(notificationEntry.getAddress(), notificationEntry.getLangCode(), true);
            sendMailWithAttachment(notificationEntry.getAddress(), subject, body, getFromAbsenceAddress(), title + ".xlsx", event.getContent());
        } catch (Exception e) {
            log.error("Failed to make MimeMessage", e);
        }
    }

    @EventListener
    public void onDutyLogReportEvent(DutyLogReportEvent event) {
        Person initiator = event.getInitiator();
        String title = event.getTitle();

        PreparedTemplate subjectTemplate = templateService.getDutyLogReportSubject(event.getTitle());
        if (subjectTemplate == null) {
            log.error("Failed to prepare subject template for duty log report initiator={}", initiator);
            return;
        }

        NotificationEntry notificationEntry = fetchNotificationEntryFromPerson(initiator);

        PreparedTemplate bodyTemplate = templateService.getReportBody(title, event.getCreationDate(),
                initiator.getDisplayShortName(), Arrays.asList(notificationEntry.getAddress()));

        if (bodyTemplate == null) {
            log.error("Failed to prepare body template for duty log report notification");
            return;
        }

        try {
            String subject = subjectTemplate.getText(notificationEntry.getAddress(), notificationEntry.getLangCode(), true);
            String body = bodyTemplate.getText(notificationEntry.getAddress(), notificationEntry.getLangCode(), true);
            sendMailWithAttachment(notificationEntry.getAddress(), subject, body, getFromPortalAddress(), title + ".xlsx", event.getContent());
        } catch (Exception e) {
            log.error("Failed to make MimeMessage", e);
        }
    }

    // -----------------------
    // Birthdays notifications
    // -----------------------

    @EventListener
    public void onBirthdaysNotificationEvent( BirthdaysNotificationEvent event) {
        log.info( "onBirthdaysNotificationEvent(): {}", event );

        if (CollectionUtils.isEmpty(event.getEmployees()) || CollectionUtils.isEmpty(event.getNotifiers())) {
            log.error("Failed to send birthdays notification: empty data or notifiers");
            return;
        }

        PreparedTemplate subjectTemplate = templateService.getBirthdaysNotificationSubject( event.getFromDate(), event.getToDate() );
        if (subjectTemplate == null) {
            log.error("Failed to prepare subject template for PersonCaseFilter notification");
            return;
        }

        List<String> recipients = getNotifiersAddresses(event.getNotifiers());

        LinkedHashMap<Date, TreeSet<EmployeeShortView>> dateToEmployeesMap = CollectionUtils.stream(event.getEmployees())
                .peek(employee -> employee.setBirthday(selectDateThisYear(employee.getBirthday())))
                .sorted(Comparator.comparing(EmployeeShortView::getBirthday))
                .collect(groupingBy(
                        EmployeeShortView::getBirthday,
                        LinkedHashMap::new,
                        Collectors.toCollection(() -> new TreeSet<>(
                                Comparator.comparing(EmployeeShortView::getDisplayName)
                        ))));

        List<DayOfWeek> dayOfWeeks = makeDaysOfWeek(dateToEmployeesMap);

        PreparedTemplate bodyTemplate = templateService.getBirthdaysNotificationBody( dateToEmployeesMap, dayOfWeeks, recipients,
                new EnumLangUtil(lang));
        if (bodyTemplate == null) {
            log.error("Failed to prepare body template for release PersonCaseFilter notification");
            return;
        }

        sendMailToRecipients(event.getNotifiers(), bodyTemplate, subjectTemplate, true, getFromPortalAddress());
    }

    @EventListener
    public void onReservedIpAdminNotificationEvent( ReservedIpAdminNotificationEvent event) {
        log.info( "onReservedIpAdminNotificationEvent(): {}", event );

        List<String> adminEmails = config.data().getNrpeConfig().getAdminMails();
        if (isEmpty(adminEmails)) {
            log.error("No admin mails");
            return;
        }

        PreparedTemplate subjectTemplate = templateService.getNRPENonAvailableIpsNotificationSubject();
        if (subjectTemplate == null) {
            log.error("Failed to prepare subject template for ReservedIpAdminNotification notification");
            return;
        }

        PreparedTemplate bodyTemplate = templateService.getNRPENonAvailableIpsNotificationBody( event.getNonAvailableIps(), adminEmails);
        if (bodyTemplate == null) {
            log.error("Failed to prepare body template for release ReservedIpAdminNotification notification");
            return;
        }

        adminEmails.forEach(adminEmail -> {
            try {
                String body = bodyTemplate.getText(adminEmail, null, false);
                String subject = subjectTemplate.getText(adminEmail, null, false);

                sendMail(adminEmail, subject, body, getFromPortalAddress());
            } catch (Exception e) {
                log.error("Failed to make MimeMessage mail={}, e={}", adminEmail, e);
            }
        });
    }

    @EventListener
    public void onExpiringTechnicalSupportValidityNotificationEvent(ExpiringProjectTSVNotificationEvent event) {
        log.info("onExpiringTechnicalSupportValidityNotificationEvent(): {}", event);

        PreparedTemplate subjectTemplate = templateService.getExpiringTechnicalSupportValidityNotificationSubject();
        if (subjectTemplate == null) {
            log.error("Failed to prepare subject template for ExpiringTechnicalSupportValidityNotificationEvent notification");
            return;
        }

        final NotificationEntry notificationEntry = fetchNotificationEntryFromPerson(event.getHeadManager());

        PreparedTemplate bodyTemplate = templateService.getExpiringTechnicalSupportValidityNotificationBody( event,
                Collections.singletonList(notificationEntry.getAddress()), getCrmProjectUrl());

        if (bodyTemplate == null) {
            log.error("Failed to prepare body template for release ExpiringTechnicalSupportValidityNotificationEvent notification");
            return;
        }

        try {
            String body = bodyTemplate.getText(notificationEntry.getAddress(), null, false);
            String subject = subjectTemplate.getText(notificationEntry.getAddress(), null, false);

            sendMail(notificationEntry.getAddress(), subject, body, getFromPortalAddress());
        } catch (Exception e) {
            log.error("Failed to make MimeMessage mail={}, e={}", notificationEntry.getAddress(), e);
        }
    }

    // ----------------------
    // Education notification
    // ----------------------

    @EventListener
    public void onEducationRequestCreate(EducationRequestCreateEvent event) {
        EducationEntry educationEntry = event.getEducationEntry();
        Set<Person> headsOfDepartments = event.getHeadsOfDepartments();
        List<Person> participants = event.getParticipants();

        Set<String> recipients = getRecipientsEmails(headsOfDepartments, participants);

        Set<String> recipientsFromConfig = getRecipientsFromConfigOnCreateRequest(educationEntry.getType());
        if (isNotEmpty(recipientsFromConfig)) {
            recipients.addAll(recipientsFromConfig);
        }

        if (isEmpty(recipients)) {
            log.warn("Failed to send education request notification: empty recipients set");
            return;
        }

        PreparedTemplate subjectTemplate = templateService.getEducationRequestNotificationSubject(educationEntry);
        if (subjectTemplate == null) {
            log.error("Failed to prepare subject template for create education request={}", educationEntry);
            return;
        }

        PreparedTemplate bodyTemplate = templateService.getEducationRequestCreateNotificationBody(recipients,
                educationEntry, new EnumLangUtil(lang));
        if (bodyTemplate == null) {
            log.error("Failed to prepare body template for create education request={}", educationEntry);
            return;
        }

        recipients.forEach(entry -> {
            try {
                String body = bodyTemplate.getText(entry, null, false);
                String subject = subjectTemplate.getText(entry, null, false);

                sendMail(entry, subject, body, getFromPortalAddress());
            } catch (Exception e) {
                log.error("Failed to make MimeMessage mail={}, e={}", entry, e);
            }
        });
    }

    @EventListener
    public void onEducationRequestApprove(EducationRequestApproveEvent event) {
        EducationEntry educationEntry = event.getEducationEntry();
        Set<Person> headsOfDepartments = event.getHeadsOfDepartments();
        List<Person> approvedParticipants = event.getApprovedParticipants();

        Set<String> recipients = getRecipientsEmails(headsOfDepartments, approvedParticipants);

        Set<String> recipientsFromConfig = getRecipientsFromConfigOnApproveParticipants(educationEntry.getType());
        if (isNotEmpty(recipientsFromConfig)) {
            recipients.addAll(recipientsFromConfig);
        }

        if (isEmpty(recipients)) {
            log.warn("Failed to send education request notification: empty recipients set");
            return;
        }

        PreparedTemplate subjectTemplate = templateService.getEducationRequestNotificationSubject(educationEntry);
        if (subjectTemplate == null) {
            log.error("Failed to prepare subject template for approve education request={}", educationEntry);
            return;
        }

        String approved = approvedParticipants.stream()
                .map(Person::getDisplayShortName)
                .collect(Collectors.joining(", "));

        PreparedTemplate bodyTemplate = templateService.getEducationRequestApproveNotificationBody(recipients,
                educationEntry, approved, new EnumLangUtil(lang));
        if (bodyTemplate == null) {
            log.error("Failed to prepare body template for approve education request={}", educationEntry);
            return;
        }

        recipients.forEach(entry -> {
            try {
                String body = bodyTemplate.getText(entry, null, false);
                String subject = subjectTemplate.getText(entry, null, false);

                sendMail(entry, subject, body, getFromPortalAddress());
            } catch (Exception e) {
                log.error("Failed to make MimeMessage mail={}, e={}", entry, e);
            }
        });
    }

    @EventListener
    public void onEducationRequestDecline(EducationRequestDeclineEvent event) {
        EducationEntry educationEntry = event.getEducationEntry();
        Set<Person> headsOfDepartments = event.getHeadsOfDepartments();
        List<Person> declineParticipants = event.getDeclineParticipants();

        Set<String> recipients = getRecipientsEmails(headsOfDepartments, declineParticipants);

        if (isEmpty(recipients)) {
            log.warn("Failed to send education request notification: empty recipients set");
            return;
        }

        PreparedTemplate subjectTemplate = templateService.getEducationRequestNotificationSubject(educationEntry);
        if (subjectTemplate == null) {
            log.error("Failed to prepare subject template for decline education request={}", educationEntry);
            return;
        }

        String declined = declineParticipants.stream()
                .map(Person::getDisplayShortName)
                .collect(Collectors.joining(", "));

        PreparedTemplate bodyTemplate = templateService.getEducationRequestDeclineNotificationBody(recipients,
                educationEntry, declined, new EnumLangUtil(lang));
        if (bodyTemplate == null) {
            log.error("Failed to prepare body template for decline education request={}", educationEntry);
            return;
        }

        recipients.forEach(entry -> {
            try {
                String body = bodyTemplate.getText(entry, null, false);
                String subject = subjectTemplate.getText(entry, null, false);

                sendMail(entry, subject, body, getFromPortalAddress());
            } catch (Exception e) {
                log.error("Failed to make MimeMessage mail={}, e={}", entry, e);
            }
        });
    }

    private Set<String> getRecipientsFromConfigOnCreateRequest(EducationEntryType type) {
        MailNotificationConfig config = this.config.data().getMailNotificationConfig();
        String[] recipients;
        switch (type) {
            case COURSE: recipients = config.getCrmEducationRequestCourseRecipients(); break;
            case CONFERENCE: recipients = config.getCrmEducationRequestConferenceRecipients(); break;
            case LITERATURE: recipients = config.getCrmEducationRequestLiteratureRecipients(); break;
            default: return new HashSet<>();
        }

        return Arrays.stream(recipients)
                .filter(Strings::isNotEmpty)
                .collect(Collectors.toSet());
    }

    private Set<String> getRecipientsFromConfigOnApproveParticipants(EducationEntryType type) {
        MailNotificationConfig config = this.config.data().getMailNotificationConfig();
        String[] recipients;
        switch (type) {
            case COURSE: recipients = config.getCrmEducationRequestApprovedCourseRecipients(); break;
            case CONFERENCE: recipients = config.getCrmEducationRequestApprovedConferenceRecipients(); break;
            case LITERATURE: recipients = config.getCrmEducationRequestApprovedLiteratureRecipients(); break;
            default: return new HashSet<>();
        }

        return Arrays.stream(recipients)
                .filter(Strings::isNotEmpty)
                .collect(Collectors.toSet());
    }

    private Set<String> getRecipientsEmails(Set<Person> headsOfDepartments, List<Person> participants) {
        Set<String> recipients = new HashSet<>();

        if (isNotEmpty(headsOfDepartments)) {
            CollectionUtils.stream(headsOfDepartments)
                    .map(person -> new PlainContactInfoFacade(person.getContactInfo()).getEmail())
                    .filter(Strings::isNotEmpty)
                    .forEach(recipients::add);
        }

        if (isNotEmpty(participants)) {
            CollectionUtils.stream(participants)
                    .map(person -> new PlainContactInfoFacade(person.getContactInfo()).getEmail())
                    .filter(Strings::isNotEmpty)
                    .forEach(recipients::add);
        }
        return recipients;
    }

    // -----
    // Utils
    // -----

    private List<NotificationEntry> collectNotifiers(AbsenceNotificationEvent event) {
        return stream(new ArrayList<Person>() {{
            addAll(event.getNotifiers());
            add(event.getInitiator());
        }}).map(this::fetchNotificationEntryFromPerson)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    private void sendMailToRecipients(Collection<NotificationEntry> recipients, PreparedTemplate bodyTemplate,
                                      PreparedTemplate subjectTemplate, boolean isShowPrivacy, String from) {
        recipients.forEach(entry -> {
            try {
                String body = bodyTemplate.getText(entry.getAddress(), entry.getLangCode(), isShowPrivacy);
                String subject = subjectTemplate.getText(entry.getAddress(), entry.getLangCode(), isShowPrivacy);
                sendMail(entry.getAddress(), subject, body, from);
            } catch (Exception e) {
                log.error("Failed to make MimeMessage", e);
            }
        });
    }

    private String getPortalUrl(boolean isProteiRecipient) {
        String baseUrl = "";
        if (isProteiRecipient) {
            baseUrl = config.data().getMailNotificationConfig().getCrmUrlInternal();
        } else {
            baseUrl = config.data().getMailNotificationConfig().getCrmUrlExternal();
        }
        return baseUrl + config.data().getMailNotificationConfig().getCrmReservedIpsUrl();
    }

    private void sendMailToRecipientWithAttachment(NotificationEntry recipients, PreparedTemplate bodyTemplate,
                                                   PreparedTemplate subjectTemplate, boolean isShowPrivacy,
                                                   InputStream content, String filename, String from) {
            try {
                String body = bodyTemplate.getText(recipients.getAddress(), recipients.getLangCode(), isShowPrivacy);
                String subject = subjectTemplate.getText(recipients.getAddress(), recipients.getLangCode(), isShowPrivacy);
                sendMailWithAttachment(recipients.getAddress(), subject, body, from, filename, content);
            } catch (Exception e) {
                log.error("Failed to make MimeMessage", e);
            }
    }

    private void sendMail(String address, String subject, String body, String from) throws MessagingException {
        MimeMessageHelper msg = new MimeMessageHelper(messageFactory.createMailMessage(), true, config.data().smtp().getDefaultCharset());
        msg.setSubject(subject);
        msg.setFrom(from);
        msg.setText(HelperFunc.nvlt(body, ""), true);
        msg.setTo(address);
        mailSendChannel.send(msg.getMimeMessage());
    }

    private void sendMailWithAttachment(String address, String subject, String body, String from, String filename, InputStream content) throws MessagingException, IOException {
        MimeMessageHelper msg = new MimeMessageHelper(messageFactory.createMailMessage(), true, config.data().smtp().getDefaultCharset());
        msg.setSubject(subject);
        msg.setFrom(from);
        msg.setText(HelperFunc.nvlt(body, ""), true);
        msg.setTo(address);
        msg.addAttachment(filename, new ByteArrayResource(IOUtils.toByteArray(content)));
        mailSendChannel.send(msg.getMimeMessage());
    }

    private boolean isProteiRecipient(NotificationEntry entry) {
        return CompanySubscription.isProteiRecipient(entry.getAddress());
    }

    private boolean isNotProteiRecipient(NotificationEntry entry) {
        return !isProteiRecipient(entry);
    }

    private String getFromCrmAddress() {
        return config.data().smtp().getFromAddressCrmAlias() + " <" + config.data().smtp().getFromAddressCrm() + ">";
    }

    private String getFromPortalAddress() {
        return config.data().smtp().getFromAddressPortalAlias() + " <" + config.data().smtp().getFromAddressPortal() + ">";
    }

    private String getFromAbsenceAddress() {
        return config.data().smtp().getFromAddressAbsenceAlias() + " <" + config.data().smtp().getFromAddressAbsence() + ">";
    }

    private String getFromReportAddress() {
        return config.data().smtp().getFromAddressReportAlias() + " <" + config.data().smtp().getFromAddressReport() + ">";
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

        if (isPublicChangesExist(assembledCaseEvent)) {
            return false;
        }

        return true;
    }

    private boolean isPublicChangesExist(AssembledCaseEvent assembledCaseEvent) {
        return  assembledCaseEvent.isPublicCommentsChanged()
                || assembledCaseEvent.isPublicAttachmentsChanged()
                || assembledCaseEvent.isCaseImportanceChanged()
                || assembledCaseEvent.isCaseStateChanged()
                || assembledCaseEvent.isPauseDateChanged()
                || assembledCaseEvent.isInitiatorChanged()
                || assembledCaseEvent.isInitiatorCompanyChanged()
                || assembledCaseEvent.isManagerCompanyChanged()
                || assembledCaseEvent.isManagerChanged()
                || assembledCaseEvent.getName().hasDifferences()
                || assembledCaseEvent.getInfo().hasDifferences()
                || assembledCaseEvent.isProductChanged()
                || assembledCaseEvent.isPublicLinksChanged();
    }

    private boolean isSendDeliveryNotification(AssembledDeliveryEvent event) {
        if (!event.isEditEvent()) {
            return true;
        }

        if (event.isDeliveryChanged()) {
            return true;
        }

        return false;
    }

    private boolean isSendProjectNotification(AssembledProjectEvent event) {
        if (!event.isEditEvent()) {
            return true;
        }

        if (isProjectChanged(event)) {
            return true;
        }

        return false;
    }

    private boolean isProjectChanged(AssembledProjectEvent event) {
        return event.isNameChanged()
                || event.isDescriptionChanged()
                || event.isStateChanged()
                || event.isPauseDateChanged()
                || event.isRegionChanged()
                || event.isCompanyChanged()
                || event.isCustomerTypeChanged()
                || event.isProductDirectionChanged()
                || event.isProductChanged()
                || event.isSupportValidityChanged()
                || event.isWorkCompletionDateChanged()
                || event.isPurchaseDateChanged()
                || event.isTeamChanged()
                || event.isSlaChanged()
                || event.isCommentsChanged()
                || event.isAttachmentChanged()
                || event.isLinksChanged();
    }

    private boolean isSendEmployeeRegistrationNotification(AssembledEmployeeRegistrationEvent event) {
        if (!event.isEditEvent()) {
            return true;
        }

        if (isEmployeeRegistrationChanged(event)) {
            return true;
        }

        return false;
    }

    private boolean isEmployeeRegistrationChanged(AssembledEmployeeRegistrationEvent event) {
        if (event.isEmploymentDateChanged()) {
            return true;
        }

        if (event.isCuratorsChanged()) {
            return true;
        }

        return false;
    }

    private String makeCrmProjectUrl( String crmUrl, Long projectId ) {
        String crmProjectUrl = crmUrl + config.data().getMailNotificationConfig().getCrmProjectUrl();
        return String.format( crmProjectUrl, projectId );
    }

    private String makeCrmDeliveryUrl( String crmUrl, Long deliveryId ) {
        String crmProjectUrl = crmUrl + config.data().getMailNotificationConfig().getDeliveryUrl();
        return String.format( crmProjectUrl, deliveryId );
    }

    private Collection<NotificationEntry> collectCommentNotifiers(HasCaseComments hasCaseComments, List<ReplaceLoginWithUsernameInfo<CaseComment>> commentToLoginList, boolean isPrivateCase) {
        List<NotificationEntry> result = new ArrayList<>();

        Set<CaseComment> neededCaseCommentsForNotification = getNeededCaseCommentsForNotification(hasCaseComments);

        if (isPrivateCase) {
            List<Long> personIds = getPersonIdsFromNeededComments(commentToLoginList, neededCaseCommentsForNotification);
            result.addAll(filterNotificationEntries(subscriptionService.subscribers(personIds), this::isProteiRecipient));
        } else {
            List<Long> privateCommentPersonIds = getPersonIdsFromNeededComments(getPrivateCommentsReplacementInfoList(commentToLoginList), neededCaseCommentsForNotification);
            result.addAll(filterNotificationEntries(subscriptionService.subscribers(privateCommentPersonIds), this::isProteiRecipient));

            List<Long> publicCommentPersonIds = getPersonIdsFromNeededComments(getPublicCommentReplacementInfoList(commentToLoginList), neededCaseCommentsForNotification);
            result.addAll(subscriptionService.subscribers(publicCommentPersonIds));
        }

        return result;
    }

    private List<Long> getPersonIdsFromNeededComments(List<ReplaceLoginWithUsernameInfo<CaseComment>> commentToLoginList, Set<CaseComment> neededCaseCommentsForNotification) {
        return commentToLoginList
                .stream()
                .filter(replacementInfo -> neededCaseCommentsForNotification.contains(replacementInfo.getObject()))
                .flatMap(replacementInfo -> replacementInfo.getUserLoginShortViews().stream())
                .map(UserLoginShortView::getPersonId)
                .collect(Collectors.toList());
    }

    private Set<CaseComment> getNeededCaseCommentsForNotification(HasCaseComments hasCaseComments) {
        List<CaseComment> addedCaseComments = emptyIfNull(hasCaseComments.getAddedCaseComments());
        List<CaseComment> changedCaseComments = emptyIfNull(hasCaseComments.getChangedCaseComments());
        List<CaseComment> removeCaseComments = emptyIfNull(hasCaseComments.getRemovedCaseComments());

        addedCaseComments.removeIf(removeCaseComments::contains);
        changedCaseComments.removeIf(removeCaseComments::contains);

        Set<CaseComment> neededComments = new HashSet<>(addedCaseComments);
        neededComments.addAll(changedCaseComments);

        return neededComments;
    }

    private List<ReplaceLoginWithUsernameInfo<CaseComment>> getPrivateCommentsReplacementInfoList(List<ReplaceLoginWithUsernameInfo<CaseComment>> commentToLoginList) {
        return commentToLoginList
                .stream()
                .filter(replacementInfo -> replacementInfo.getObject().isPrivateComment())
                .collect(Collectors.toList());
    }

    private List<ReplaceLoginWithUsernameInfo<CaseComment>> getPublicCommentReplacementInfoList(List<ReplaceLoginWithUsernameInfo<CaseComment>> commentToLoginList) {
        return commentToLoginList
                .stream()
                .filter(replacementInfo -> !replacementInfo.getObject().isPrivateComment())
                .collect(Collectors.toList());
    }

    private List<NotificationEntry> filterNotificationEntries(Collection<NotificationEntry> entries, Predicate<NotificationEntry> notificationEntryPredicate) {
        return stream(entries).filter(notificationEntryPredicate).collect(Collectors.toList());
    }

    private Date selectDateThisYear(Date date) {
        // устанавливаем датам рождения текущие годы, чтобы получить день недели
        // один из частных случаев - если сейчас уже 2021 год, а день рождения был в конце декабря 2020,
        // тогда устанавливаем год на 1 меньше, чтобы получить правильный день недели
        Date now = new Date();
        if (now.getMonth() == Calendar.DECEMBER && date.getMonth() == Calendar.JANUARY) {
            date.setYear(now.getYear() + 1);
        } else if (now.getMonth() == Calendar.JANUARY && date.getMonth() == Calendar.DECEMBER) {
            date.setYear(now.getYear() - 1);
        } else {
            date.setYear(now.getYear());
        }
        return date;
    }

    private List<DayOfWeek> makeDaysOfWeek(LinkedHashMap<Date, TreeSet<EmployeeShortView>> dateToEmployeesMap) {
        List<DayOfWeek> daysOfWeek = new ArrayList<>();

        Set<Date> dates = dateToEmployeesMap.keySet();
        for (Date date : dates) {
            Instant instant = date.toInstant();
            ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
            LocalDate localDate = zdt.toLocalDate();
            daysOfWeek.add(localDate.getDayOfWeek());
        }
        return daysOfWeek;
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
