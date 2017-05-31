package ru.protei.portal.hpsm.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.mail.MailReceivingMessageSource;
import org.springframework.messaging.Message;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.hpsm.api.HpsmMessageFactory;
import ru.protei.portal.core.mail.MailSendChannel;
import ru.protei.portal.hpsm.api.HpsmSeverity;
import ru.protei.portal.hpsm.config.HpsmEnvConfig;
import ru.protei.portal.hpsm.struct.HpsmMessage;
import ru.protei.portal.hpsm.struct.HpsmMessageHeader;
import ru.protei.portal.hpsm.struct.HpsmPingMessage;
import ru.protei.portal.hpsm.utils.CompanyBranchMap;
import ru.protei.portal.hpsm.utils.HpsmUtils;

import javax.mail.internet.MimeMessage;

/**
 * Created by michael on 15.05.17.
 */
public class ServiceInstanceImpl implements ServiceInstance {

    private static Logger logger = LoggerFactory.getLogger(ServiceInstanceImpl.class);

    private HpsmEnvConfig.ServiceConfig serviceConfig;

    private MailReceivingMessageSource inboundSource;
    protected MailSendChannel sendChannel;
    private CompanyBranchMap companyBranchMap;
    private HpsmMessageFactory messageFactory;


    public ServiceInstanceImpl(HpsmEnvConfig.ServiceConfig config,
                               CompanyBranchMap companyBranchMap,
                               MailReceivingMessageSource inboundSource,
                               MailSendChannel outChannel,
                               HpsmMessageFactory messageFactory) {
        this.serviceConfig = config;
        this.sendChannel = outChannel;
        this.messageFactory = messageFactory;
        this.companyBranchMap = companyBranchMap;
        this.inboundSource = inboundSource;//createInboundSource(config);
    }

    //protected abstract MailReceivingMessageSource createInboundSource (HpsmEnvConfig.ServiceConfig serviceConfig);


    @Override
    public HpsmEnvConfig.ServiceConfig config() {
        return serviceConfig;
    }

    @Override
    public boolean acceptCase(CaseObject object) {
        return HpsmUtils.testBind(object, this);
    }

    @Override
    public String id() {
        return serviceConfig.getId();
    }

    @Override
    public MimeMessage read() {
        if (this.inboundSource != null) {
            Message<Object> msg = this.inboundSource.receive();
            if (msg != null && msg.getPayload() instanceof MimeMessage)
                return (MimeMessage) msg.getPayload();
        }

        return  null;
    }

    @Override
    public Company getCompanyByBranch(String branchName) {
        return companyBranchMap.getCompanyByBranch(branchName);
    }

    @Override
    public void sendReject(HpsmEvent request, String reason) throws Exception {
        sendReject(request.getEmailSourceAddr(), request, reason);
    }

    @Override
    public void sendReject(String to, HpsmEvent request, String reason) throws Exception {
        this.sendChannel.send(messageFactory.createRejectMessage(to, serviceConfig.getOutboundChannel().getSenderAddress(), request.getSubject(), reason));
    }

    @Override
    public void sendReject(String to, HpsmMessageHeader subject, String reason) throws Exception {
        this.sendChannel.send(messageFactory.createRejectMessage(to, serviceConfig.getOutboundChannel().getSenderAddress(), subject, reason));
    }

    @Override
    public void sendReply(HpsmMessageHeader replyHeader, HpsmMessage replyMessage) throws Exception {
        sendReply(this.serviceConfig.getOutboundChannel().getSendTo(), replyHeader, replyMessage);
    }

    @Override
    public void sendReply(String to, HpsmMessageHeader replyHeader, HpsmMessage replyMessage) throws Exception {
        this.sendChannel.send(messageFactory.makeReplyMessage(to, serviceConfig.getOutboundChannel().getSenderAddress(), replyHeader, replyMessage));
    }

    @Override
    public void sendReply(String replyTo, HpsmPingMessage msg) throws Exception {
        this.sendChannel.send(messageFactory.makePingMessgae(replyTo, serviceConfig.getOutboundChannel().getSenderAddress(), msg));
    }



    public void fillReplyMessageAttributes(HpsmMessage message, CaseObject object) {
        message.severity(HpsmSeverity.find(object.importanceLevel()));
        message.setProductName(object.getProduct() != null ? object.getProduct().getName() : "");
        message.setShortDescription(object.getName());
        message.setDescription(object.getInfo());

        if (object.getManager() != null) {
            Person ourManager = object.getManager();

            message.setOurManager(HelperFunc.nvlt(ourManager.getDisplayName(), ourManager.getDisplayShortName()));
            if (HelperFunc.isEmpty(message.getOurManager())) {
                logger.debug("unable to get display name for employee-contact, id={}", ourManager.getId());

                String autoDisplayName = HelperFunc.joinNotEmpty(ourManager.getLastName(), ourManager.getFirstName(), ourManager.getSecondName());
                if (HelperFunc.isEmpty(autoDisplayName))
                    autoDisplayName = serviceConfig.getOutboundChannel().getDefaultContactName();

                message.setOurManager(autoDisplayName);
            }

            PlainContactInfoFacade contactInfoFacade = new PlainContactInfoFacade(ourManager.getContactInfo());
            message.setOurManagerEmail(contactInfoFacade.getEmail());

            if (HelperFunc.isEmpty(message.getOurManagerEmail())) {
                logger.debug("Our manager with id={} has no contact e-mail", ourManager.getId());
                message.setOurManagerEmail(serviceConfig.getOutboundChannel().getDefaultContactEmail());
            }
        }
    }

}
