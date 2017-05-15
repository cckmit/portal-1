package ru.protei.portal.hpsm.service;

import org.springframework.integration.mail.MailReceivingMessageSource;
import org.springframework.messaging.Message;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Company;
import ru.protei.portal.hpsm.api.HpsmMessageFactory;
import ru.protei.portal.hpsm.api.MailSendChannel;
import ru.protei.portal.hpsm.config.HpsmEnvConfig;
import ru.protei.portal.hpsm.logic.HpsmEvent;
import ru.protei.portal.hpsm.logic.ServiceInstance;
import ru.protei.portal.hpsm.struct.HpsmMessage;
import ru.protei.portal.hpsm.struct.HpsmMessageHeader;
import ru.protei.portal.hpsm.struct.HpsmPingMessage;
import ru.protei.portal.hpsm.utils.CompanyBranchMap;
import ru.protei.portal.hpsm.utils.HpsmUtils;

import javax.mail.internet.MimeMessage;

/**
 * Created by michael on 15.05.17.
 */
public abstract class AbstractServiceInstanceImpl implements ServiceInstance {

    private HpsmEnvConfig.ServiceConfig serviceConfig;

    private MailReceivingMessageSource inboundSource;
    protected MailSendChannel sendChannel;
    private CompanyBranchMap companyBranchMap;
    private HpsmMessageFactory messageFactory;


    public AbstractServiceInstanceImpl(HpsmEnvConfig.ServiceConfig config,
                                       CompanyBranchMap companyBranchMap,
                                       MailSendChannel outChannel,
                                       HpsmMessageFactory messageFactory) {
        this.serviceConfig = config;
        this.sendChannel = outChannel;
        this.messageFactory = messageFactory;
        this.companyBranchMap = companyBranchMap;
        this.inboundSource = createInboundSource(config);
    }

    protected abstract MailReceivingMessageSource createInboundSource (HpsmEnvConfig.ServiceConfig serviceConfig);


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
        this.sendChannel.send(messageFactory.makeMessage(to, serviceConfig.getOutboundChannel().getSenderAddress(), replyHeader, replyMessage));
    }

    @Override
    public void sendReply(String replyTo, HpsmPingMessage msg) throws Exception {
        this.sendChannel.send(messageFactory.makeMessgae(replyTo, serviceConfig.getOutboundChannel().getSenderAddress(), msg));
    }
}
