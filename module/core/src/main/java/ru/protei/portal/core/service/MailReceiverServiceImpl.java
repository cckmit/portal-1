package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.config.PortalConfigData;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.struct.receivedmail.ReceivedMail;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ru.protei.portal.config.MainConfiguration.BACKGROUND_TASKS;
import static ru.protei.portal.core.model.helper.CollectionUtils.stream;
import static ru.protei.portal.util.MailReceiverUtils.*;

public class MailReceiverServiceImpl implements MailReceiverService {

    private static final Logger log = LoggerFactory.getLogger(MailReceiverServiceImpl.class);
    private Store store;
    private Properties smtpProperties;

    @Autowired
    PortalConfig portalConfig;

    @Autowired
    CaseCommentService caseCommentService;

    @Autowired
    public void onInit() {
        try {
            log.info("onInit(): init store");
            store = Session.getInstance(createImapProperties()).getStore();
            smtpProperties = createSmtpProperties(portalConfig);
        } catch (NoSuchProviderException e) {
            log.error("onInit(): fail to get store");
        }
    }

    @Override
    @Async(BACKGROUND_TASKS)
    public void performReceiveMailAndAddComments() {
        if (store == null) {
            log.error("performReceiveMailAndAddComments(): fail to get store");
            return;
        }

        if (!portalConfig.data().getMailCommentConfig().isEnable()) {
            log.info("performReceiveMailAndAddComments(): not enable");
            return;
        }

        try {
            log.info("performReceiveMailAndAddComments(): connect to store");
            connectImap(store, portalConfig.data().getMailCommentConfig());

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);
            Message[] search = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
            if (search.length == 0) {
                log.info("performReceiveMailAndAddComments(): no messages");
                return;
            }
            inbox.fetch(search, createFetchProfile());
            Pattern blackListPattern = createBlackListPattern(portalConfig.data().getMailCommentConfig().getBlackList());
            List<Message> messagesForForward = new ArrayList<>();
            stream(search).forEach(message -> {
                log.info("performReceiveMailAndAddComments(): message service info = {}", parseServiceInfo(message));
                parseMessage(message, blackListPattern)
                        .filter(this::hasFullInfo)
                        .ifPresent(mail -> caseCommentService.addCommentReceivedByMail(mail)
                        .ifError(result -> {
                            if (result.getStatus() == En_ResultStatus.USER_NOT_FOUND) {
                                messagesForForward.add(message);
                            }
                        } ));
            });

            if (portalConfig.data().getMailCommentConfig().isEnableForwardMail()) {
                if (!messagesForForward.isEmpty()) {
                    forwardMail(messagesForForward);
                }
            } else {
                log.info("performReceiveMailAndAddComments(): not enable forward mail");
            }

            setSeen(inbox, search);
            inbox.close(false);
        } catch (MessagingException e) {
            log.error("performReceiveMailAndAddComments(): fail e={}", e.toString());
            throw new RuntimeException(e);
        } finally {
            if (store != null) {
                try {
                    store.close();
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void forwardMail(List<Message> messagesForForward) throws MessagingException {
        Session session = Session.getInstance(smtpProperties);
        List<Message> messages = new ArrayList<>();
        for (Message messageForForward : messagesForForward) {
            messages.add(createForwardMessage(session, messageForForward));
        }
        Transport transport = session.getTransport("smtp");
        connectSmtp(transport, portalConfig.data().getMailCommentConfig());
        try {
            for (Message message : messages) {
                log.info("performReceiveMailAndAddComments(): forwardMail mail subject = {}", message.getSubject());
                transport.sendMessage(message, message.getAllRecipients());
            }
        } finally {
            transport.close();
        }
    }

    private Message createForwardMessage(Session session, Message message) throws MessagingException {
        Message forward = new MimeMessage(session);

        forward.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(portalConfig.data().getMailCommentConfig().getForwardMail()));
        forward.setSubject("Fwd: " + message.getSubject());
        forward.setFrom(new InternetAddress(portalConfig.data().getMailCommentConfig().getUser()));

        MimeBodyPart messageBodyPart = new MimeBodyPart();
        Multipart multipart = new MimeMultipart();
        messageBodyPart.setContent(message, "message/rfc822");
        multipart.addBodyPart(messageBodyPart);

        forward.setContent(multipart);
        forward.saveChanges();

        return forward;
    }

    private boolean hasFullInfo(ReceivedMail receivedMail) {
        return receivedMail.getCaseNo() != null &&
                receivedMail.getSenderEmail() != null &&
                StringUtils.isNotEmpty(receivedMail.getContent());
    }

    private void setSeen(Folder folder, Message[] message) {
        try {
            folder.setFlags(message, new Flags(Flags.Flag.SEEN), true);
        } catch (MessagingException e) {
            log.error("setSeen(): fail e = {}", e.toString());
        }
    }

    private Properties createImapProperties() {
        Properties props = new Properties();
        props.put("mail.debug", "false");
        props.put("mail.store.protocol", "imaps");
        return props;
    }

    private Properties createSmtpProperties(PortalConfig portalConfig) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", portalConfig.data().smtp().getHost());
        props.put("mail.smtp.port", portalConfig.data().smtp().getPort());
        return props;
    }

    private FetchProfile createFetchProfile() {
        FetchProfile fetchProfile = new FetchProfile();
        fetchProfile.add(FetchProfile.Item.ENVELOPE);
        fetchProfile.add(FetchProfile.Item.CONTENT_INFO);
        fetchProfile.add(FetchProfile.Item.FLAGS);
        return fetchProfile;
    }

    private void connectImap(Store store, PortalConfigData.MailCommentConfig mailCommentConfig) throws MessagingException {
        if (!store.isConnected()) {
            store.connect(mailCommentConfig.getHost(), mailCommentConfig.getUser(), mailCommentConfig.getPass());
        }
    }

    private void connectSmtp(Transport transport, PortalConfigData.MailCommentConfig mailCommentConfig) throws MessagingException {
        if (!transport.isConnected()) {
            transport.connect(mailCommentConfig.getUser(), mailCommentConfig.getPass());
        }
    }

    private Optional<ReceivedMail> parseMessage(Message message, Pattern blackListPattern) {
        try {
            if (isInBlackList(message, blackListPattern)) {
                return Optional.empty();
            }
            Long caseNo = parseCaseNo(message);
            String senderEmail = parseSenderEmail(message);
            List<MailContent> mailContents = (caseNo != null && senderEmail != null) ? parseContent(message) : new ArrayList<>();
            String mailContent = stream(mailContents)
                        .filter(Objects::nonNull)
                        .map(content -> getCleanedContent(content.getContentType(), content.getContent()))
                        .collect(Collectors.joining("\n"));
            return Optional.of(new ReceivedMail(caseNo, senderEmail, mailContent));

        } catch (MessagingException | IOException e) {
            log.error("parseMessage(): fail, e = {}", e.getMessage());
            return Optional.empty();
        }
    }

    private Pattern createBlackListPattern(List<String> blackList) {
        return Pattern.compile(blackList.stream()
                        .map(Pattern::quote)
                        .map(item -> String.format(".*%s.*", item))
                        .collect(Collectors.joining("|")));
    }
}
