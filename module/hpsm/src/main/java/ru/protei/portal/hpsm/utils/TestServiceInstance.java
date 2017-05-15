package ru.protei.portal.hpsm.utils;

import org.springframework.integration.mail.MailReceivingMessageSource;
import ru.protei.portal.hpsm.api.HpsmMessageFactory;
import ru.protei.portal.hpsm.config.HpsmEnvConfig;
import ru.protei.portal.hpsm.service.AbstractServiceInstanceImpl;

import javax.mail.internet.MimeMessage;

/**
 * Created by michael on 15.05.17.
 */
public class TestServiceInstance extends AbstractServiceInstanceImpl {

    public TestServiceInstance(HpsmEnvConfig.ServiceConfig config, CompanyBranchMap companyBranchMap, HpsmMessageFactory messageFactory) {
        super(config, companyBranchMap, new VirtualMailSendChannel(), messageFactory);
    }

    public MimeMessage getSentMessage () {
        return ((VirtualMailSendChannel)this.sendChannel).get();
    }


    @Override
    protected MailReceivingMessageSource createInboundSource(HpsmEnvConfig.ServiceConfig serviceConfig) {
        return null;
    }
}
