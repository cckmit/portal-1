package ru.protei.portal.hpsm.service;

import com.thoughtworks.xstream.XStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.mail.ImapMailReceiver;
import org.springframework.integration.mail.MailReceiver;
import org.springframework.integration.mail.MailReceivingMessageSource;
import org.springframework.mail.javamail.MimeMessageHelper;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.hpsm.api.HpsmStatus;
import ru.protei.portal.hpsm.api.MailMessageFactory;
import ru.protei.portal.hpsm.api.MailSendChannel;
import ru.protei.portal.hpsm.config.HpsmEnvConfig;
import ru.protei.portal.hpsm.logic.HpsmEvent;
import ru.protei.portal.hpsm.logic.HpsmHandler;
import ru.protei.portal.hpsm.logic.ServiceInstance;
import ru.protei.portal.hpsm.struct.HpsmMessage;
import ru.protei.portal.hpsm.struct.HpsmMessageHeader;
import ru.protei.portal.hpsm.struct.HpsmPingMessage;
import ru.protei.portal.hpsm.utils.EventMsgInputStreamSource;
import ru.protei.portal.hpsm.utils.HpsmUtils;

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
    @Qualifier("hpsmSendChannel")
    MailSendChannel outboundChannel;

    @Autowired
    @Qualifier("hpsmMessageFactory")
    MailMessageFactory messageFactory;

    @Autowired
    @Qualifier("hpsmSerializer")
    private XStream xstream;


    @Autowired
    HpsmEnvConfig config;


    private HpsmHandler[] inboundHandlers;

    private HashMap<String,Company> branchToCompanyIdMap;

    private HashMap<String, ServiceInstanceImpl> serviceMap;

    public HpsmServiceImpl(HpsmHandler...handlers) {
        this.branchToCompanyIdMap = new HashMap<>();
        this.inboundHandlers = handlers;
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
        serviceMap.put(instCfg.getId(), new ServiceInstanceImpl(instCfg, outboundChannel));
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


    private Company findCompanyByBranchName(String branchName) {
        return branchName == null ? null : branchToCompanyIdMap.get(branchKey(branchName));
    }


    public class ServiceInstanceImpl implements ServiceInstance {

        HpsmEnvConfig.ServiceConfig serviceConfig;

        private MailReceivingMessageSource inboundSource;
        private MailSendChannel sendChannel;



        public ServiceInstanceImpl (HpsmEnvConfig.ServiceConfig config, MailSendChannel outChannel) {

            ImapMailReceiver imapMailReceiver = new ImapMailReceiver(config.getInboundChannel().getUrl());
            imapMailReceiver.setShouldMarkMessagesAsRead(false);
            imapMailReceiver.setShouldDeleteMessages(false);
            imapMailReceiver.setEmbeddedPartsAsBytes(false);

            this.inboundSource = new MailReceivingMessageSource(imapMailReceiver);
            this.sendChannel = outChannel;
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
        public Company getCompanyByBranch(String branchName) {
            return findCompanyByBranchName (branchName);
        }

        @Override
        public void sendReject(HpsmEvent request, String reason) throws Exception {
            sendReject(request.getEmailSourceAddr(), request, reason);
        }

        @Override
        public void sendReject(String to, HpsmEvent request, String reason) throws Exception {
            this.sendChannel.send(createRejectMessage(to, serviceConfig.getOutboundChannel().getSenderAddress(), request.getSubject(), reason));
        }

        @Override
        public void sendReject(String to, HpsmMessageHeader subject, String reason) throws Exception {
            this.sendChannel.send(createRejectMessage(to, serviceConfig.getOutboundChannel().getSenderAddress(), subject, reason));
        }

        @Override
        public void sendReply(HpsmEvent request, HpsmMessageHeader replyHeader, HpsmMessage replyMessage) throws Exception {
            this.sendChannel.send(createReplyMessage(serviceConfig.getOutboundChannel().getSenderAddress(), request, replyHeader, replyMessage));
        }

        @Override
        public void sendReply(String replyTo, HpsmPingMessage msg) throws Exception {
            this.sendChannel.send(makeMessgae(replyTo, serviceConfig.getOutboundChannel().getSenderAddress(), msg));
        }
    }

    private MimeMessage createReplyMessage (String from, HpsmEvent request, HpsmMessageHeader subject, HpsmMessage hpsmMessage) throws Exception {

        MimeMessage response = messageFactory.createMailMessage();
        MimeMessageHelper helper = new MimeMessageHelper(response, true);

        helper.setSubject(subject.toString());
        helper.setTo(request.getEmailSourceAddr());
        helper.setFrom(from);
        helper.addAttachment(HpsmUtils.RTTS_HPSM_XML, new EventMsgInputStreamSource(xstream).attach(hpsmMessage), "application/xml");

        return response;
    }

    private MimeMessage makeMessgae (String to, String from, HpsmPingMessage cmd) throws MessagingException {

        MimeMessage msg = messageFactory.createMailMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, false);

        helper.setSubject(cmd.toString());
        helper.setTo(to);
        helper.setFrom(from);

        return msg;
    }

//    private MimeMessage createRejectMessage (String from, HpsmEvent request, String messageText) throws Exception {
//        return createRejectMessage(request.getEmailSourceAddr(), from, request.getSubject(), messageText);
//    }

    private MimeMessage createRejectMessage (String replyTo, String from, HpsmMessageHeader subject, String messageText) throws Exception {
        MimeMessage response = messageFactory.createMailMessage();

        HpsmMessageHeader respSubject = new HpsmMessageHeader(subject.getHpsmId(), subject.getOurId(), HpsmStatus.REJECTED);

        MimeMessageHelper helper = new MimeMessageHelper(response, false);

        helper.setSubject(respSubject.toString());
        helper.setTo(replyTo);
        helper.setFrom(from);

        if (messageText != null)
            helper.setText(messageText);

        return response;
    }

}
