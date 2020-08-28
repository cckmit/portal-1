package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.config.PortalConfigData;
import ru.protei.portal.core.model.struct.MailReceiveInfo;
import ru.protei.portal.core.service.events.EventPublisherService;

import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static ru.protei.portal.util.MailReceiverUtils.*;

public class MailReceiverServiceImpl implements MailReceiverService {

    private static Logger log = LoggerFactory.getLogger(MailReceiverServiceImpl.class);
    private Store store;
    private FetchProfile fetchProfile = createFetchProfile();

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
    public void mailForComment() {
        if (!connect(store, portalConfig.data().getMailReceiver())) {
            return;
        }

        try {
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);
            Message[] search = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
            if (search.length == 0) {
                return;
            }
            inbox.fetch(search, fetchProfile);
            List<MailReceiveInfo> mailReceiveInfos = new ArrayList<>();
            for (Message message : search) {
                mailReceiveInfos.add(parseMessage(message));
            }
            inbox.close(false);
            store.close();

            caseCommentService.addMailComments(mailReceiveInfos);

        } catch (MessagingException | IOException e) {
            log.error("mailForComment(): fail, e = ", e);
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

    private boolean connect(Store store, PortalConfigData.MailReceiverConfig mailReceiverConfig) {
        try {
            if (!store.isConnected()) {
                store.connect(mailReceiverConfig.getHost(), mailReceiverConfig.getUser(), mailReceiverConfig.getPass());
            }
            return true;
        } catch (MessagingException e) {
            log.error("connect(): fail to connect user={}, host={}", mailReceiverConfig.getUser(), mailReceiverConfig.getHost());
            return false;
        }
    }

    private MailReceiveInfo parseMessage(Message message) throws IOException, MessagingException {
        return new MailReceiveInfo(
                getCaseNo(message),
                getSenderEmail(message),
                extractText(message));
    }
}
