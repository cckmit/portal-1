package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.config.PortalConfigData;
import ru.protei.portal.core.model.struct.receivedmail.ReceivedMail;
import ru.protei.portal.core.service.events.EventPublisherService;

import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
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
            connect(store, portalConfig.data().getMailReceiver());

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);
            Message[] search = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
            if (search.length == 0) {
                log.info("performReceiveMailAndAddComments(): no messages");
                return;
            }
            inbox.fetch(search, createFetchProfile());
            List<ReceivedMail> receivedMails = stream(search)
                    .map(this::parseMessage)
                    .filter(mail ->
                            mail.filter(receivedMail -> receivedMail.getCaseNo() != null && receivedMail.getSenderEmail() != null)
                                    .isPresent())
                    .map(Optional::get)
                    .collect(Collectors.toList());

            receivedMails.forEach(caseCommentService::addCommentReceivedByMail);
        } catch (MessagingException e) {
            log.error("performReceiveMailAndAddComments(): fail");
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

    private Optional<ReceivedMail> parseMessage(Message message) {
        try {
            return Optional.of(
                    new ReceivedMail(parseCaseNo(message), parseSenderEmail(message), parseContent(message))
            );
        } catch (MessagingException | IOException e) {
            log.error("parseMessage(): fail, e = {}", e.getMessage());
            return Optional.empty();
        } 
    }
}
