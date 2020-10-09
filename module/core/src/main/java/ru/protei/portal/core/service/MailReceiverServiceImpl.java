package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.config.PortalConfigData;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.struct.receivedmail.ReceivedMail;
import ru.protei.portal.core.service.events.EventPublisherService;

import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static ru.protei.portal.config.MainConfiguration.BACKGROUND_TASKS;
import static ru.protei.portal.util.MailReceiverParsers.*;

public class MailReceiverServiceImpl implements MailReceiverService {

    private static Logger log = LoggerFactory.getLogger(MailReceiverServiceImpl.class);
    private Store store;

    @Autowired
    PortalConfig portalConfig;

    @Autowired
    EventPublisherService publisherService;

    @Autowired
    CaseCommentService caseCommentService;

    @Autowired
    public void onInit() {
        try {
            log.info("onInit(): init store");
            store = Session.getInstance(createProperties()).getStore();
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
        try {
            log.info("performReceiveMailAndAddComments(): connect to store");
            connect(store, portalConfig.data().getMailReceiver());

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);
            Message[] search = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
            if (search.length == 0) {
                log.info("performReceiveMailAndAddComments(): no messages");
                return;
            }
            inbox.fetch(search, createFetchProfile());

            Pattern blackListPattern = createBlackListPattern(portalConfig.data().getMailReceiver().getBlackList());
            stream(search).forEach(message -> {
                log.info("performReceiveMailAndAddComments(): message service info = {}", parseServiceInfo(message));
                parseMessage(message, blackListPattern)
                        .filter(this::hasFullInfo)
                        .ifPresent(mail -> caseCommentService.addCommentReceivedByMail(mail));
                setSeen(inbox, message);
            });

            inbox.close(false);
        } catch (MessagingException e) {
            log.error("performReceiveMailAndAddComments(): fail e={}", e.toString());
            throw new RuntimeException();
        } finally {
            if (store != null) {
                try {
                    store.close();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean hasFullInfo(ReceivedMail receivedMail) {
        return receivedMail.getCaseNo() != null &&
                receivedMail.getSenderEmail() != null &&
                StringUtils.isNotEmpty(receivedMail.getContent());
    }

    private void setSeen(Folder folder, Message message) {
        try {
            folder.setFlags(message.getMessageNumber(), message.getMessageNumber(), new Flags(Flags.Flag.SEEN), true);
        } catch (MessagingException e) {
            log.error("setSeen(): fail message = {}", message);
        }
    }

    private Properties createProperties() {
        Properties props = new Properties();
        props.put("mail.debug", "false");
        props.put("mail.store.protocol", "imaps");
        return props;
    }

    private FetchProfile createFetchProfile() {
        FetchProfile fetchProfile = new FetchProfile();
        fetchProfile.add(FetchProfile.Item.ENVELOPE);
        fetchProfile.add(FetchProfile.Item.CONTENT_INFO);
        fetchProfile.add(FetchProfile.Item.FLAGS);
        return fetchProfile;
    }

    private void connect(Store store, PortalConfigData.MailReceiverConfig mailReceiverConfig) throws MessagingException {
        if (!store.isConnected()) {
            store.connect(mailReceiverConfig.getHost(), mailReceiverConfig.getUser(), mailReceiverConfig.getPass());
        }
    }

    private Optional<ReceivedMail> parseMessage(Message message, Pattern blackListPattern) {
        try {
            if (isInBlackList(message, blackListPattern)) {
                return Optional.empty();
            }
            Long caseNo = parseCaseNo(message);
            String senderEmail = parseSenderEmail(message);
            MailContent content = (caseNo != null && senderEmail != null) ? parseContent(message) : null;
            return Optional.of(new ReceivedMail(caseNo, senderEmail,
                    content != null ? content.getContent() : null,
                    content != null ? content.getContentType() : null));

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
