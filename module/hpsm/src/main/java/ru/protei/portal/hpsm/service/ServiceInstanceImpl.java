package ru.protei.portal.hpsm.service;

import org.springframework.integration.mail.MailReceivingMessageSource;
import ru.protei.portal.hpsm.api.HpsmMessageFactory;
import ru.protei.portal.hpsm.api.MailSendChannel;
import ru.protei.portal.hpsm.config.HpsmEnvConfig;
import ru.protei.portal.hpsm.utils.CompanyBranchMap;

/**
 * Created by michael on 15.05.17.
 */
public class ServiceInstanceImpl extends AbstractServiceInstanceImpl {

    public ServiceInstanceImpl(HpsmEnvConfig.ServiceConfig config, CompanyBranchMap companyBranchMap, MailReceivingMessageSource inboundSource, MailSendChannel outChannel, HpsmMessageFactory messageFactory) {
        super(config, companyBranchMap, inboundSource, outChannel, messageFactory);
    }
}
