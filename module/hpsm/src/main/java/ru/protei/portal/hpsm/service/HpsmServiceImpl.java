package ru.protei.portal.hpsm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.mail.ImapMailReceiver;
import org.springframework.integration.mail.MailReceiver;
import org.springframework.integration.mail.MailReceivingMessageSource;
import org.springframework.mail.javamail.MimeMessageHelper;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.hpsm.api.MailMessageFactory;
import ru.protei.portal.hpsm.api.MailSendChannel;
import ru.protei.portal.hpsm.config.HpsmEnvConfig;
import ru.protei.portal.hpsm.logic.HpsmEvent;
import ru.protei.portal.hpsm.logic.ServiceInstance;
import ru.protei.portal.hpsm.struct.HpsmMessage;
import ru.protei.portal.hpsm.struct.HpsmMessageHeader;
import ru.protei.portal.hpsm.struct.HpsmPingMessage;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;

/**
 * Created by michael on 27.04.17.
 */
public class HpsmServiceImpl implements HpsmService {

    private static Logger logger = LoggerFactory.getLogger(HpsmService.class);

    @Autowired
    CompanyDAO companyDAO;

    @Autowired
    MailSendChannel outboundChannel;

    @Autowired
    MailMessageFactory messageFactory;


    @Autowired
    HpsmEnvConfig config;

    private HashMap<String,Company> branchToCompanyIdMap;

    public HpsmServiceImpl() {
        this.branchToCompanyIdMap = new HashMap<>();
    }

    @PostConstruct
    private void postConstruct () {

        config.getCompanyMapEntries().forEach(e -> addCompany(e.getBranchName(), e.getCompanyId()));
        config.getInstanceList().forEach(cfg -> addService(cfg));


//        addCompany("TT Mobile", 230L);
//        addCompany("Аквафон GSM", 766L);
//        addCompany("Дальневосточный Филиал", 546L);
//        addCompany("Кавказский Филиал", 2366L);
//        addCompany("Поволжский Филиал", 151L);
//
//        addCompany("Северо-Западный Филиал", 231L);
//        addCompany("Столичный Филиал", 550L);
//        addCompany("Сибирский Филиал", 553L);
//        addCompany("Уральский Филиал", 549L);
//        addCompany("Центральный Филиал", 260L);
    }

    private void addService (HpsmEnvConfig.ServiceConfig instCfg) {

    }


    private String branchKey (String branchName) {
        return branchName.toUpperCase();
    }

    private void addCompany (String branchName, Long companyId) {

        Company company = companyDAO.get(companyId);
        if (company == null) {
            logger.error("Company with id {} not found", companyId);
            return;
        }

        this.branchToCompanyIdMap.put(branchKey(branchName), company);
    }

    @Override
    public Company getCompanyByBranchName(String branchName) {
        return branchName == null ? null : branchToCompanyIdMap.get(branchKey(branchName));
    }


    public class ServiceInstanceImpl implements ServiceInstance {

        HpsmEnvConfig.ServiceConfig serviceConfig;

        private MailReceivingMessageSource inboundSource;


        public ServiceInstanceImpl (HpsmEnvConfig.ServiceConfig config) {

            ImapMailReceiver imapMailReceiver = new ImapMailReceiver(config.getInboundChannel().getUrl());
            imapMailReceiver.setShouldMarkMessagesAsRead(false);
            imapMailReceiver.setShouldDeleteMessages(false);
            imapMailReceiver.setEmbeddedPartsAsBytes(false);

            inboundSource = new MailReceivingMessageSource(imapMailReceiver);
        }


        @Override
        public String id() {
            return serviceConfig.getId();
        }

        @Override
        public MimeMessage read() {
            return null;
        }

        @Override
        public void sendReject(HpsmEvent request, String reason) {

        }

        @Override
        public void sendReply(HpsmEvent request, HpsmMessageHeader replyHeader, HpsmMessage replyMessage) {

        }

        @Override
        public void sendReply(HpsmPingMessage msg) {

        }

        public MimeMessage makeMessgae (String to, HpsmPingMessage cmd) throws MessagingException {

            MimeMessage msg = messageFactory.createMailMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, false);

            helper.setSubject(cmd.toString());
            helper.setTo(to);
            helper.setFrom(serviceConfig.getOutboundChannel().getSenderAddress());

            return msg;
        }

    }
}
