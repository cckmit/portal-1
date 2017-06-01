package ru.protei.portal.hpsm.utils;

import ru.protei.portal.core.mail.VirtualMailSendChannel;
import ru.protei.portal.hpsm.api.HpsmMessageFactory;
import ru.protei.portal.hpsm.config.HpsmEnvConfig;
import ru.protei.portal.hpsm.logic.ServiceInstanceImpl;

import javax.mail.internet.MimeMessage;

/**
 * Created by michael on 15.05.17.
 */
public class TestServiceInstance extends ServiceInstanceImpl {

    public TestServiceInstance(HpsmEnvConfig.ServiceConfig config, CompanyBranchMap companyBranchMap, HpsmMessageFactory messageFactory) {
        super(config, companyBranchMap, null, new VirtualMailSendChannel(), messageFactory);
    }

    public MimeMessage getSentMessage () {
        return ((VirtualMailSendChannel)this.sendChannel).get();
    }
}
